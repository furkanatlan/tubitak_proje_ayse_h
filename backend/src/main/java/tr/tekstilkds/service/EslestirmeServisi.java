package tr.tekstilkds.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.tekstilkds.config.KdsAyarlari;
import tr.tekstilkds.dto.Dtolar.EslestirmeOnerisiDto;
import tr.tekstilkds.dto.Dtolar.RotaDto;
import tr.tekstilkds.dto.Dtolar.TalepDto;
import tr.tekstilkds.entity.AtikIlani;
import tr.tekstilkds.entity.AtikTuru;
import tr.tekstilkds.entity.Talep;
import tr.tekstilkds.repository.AtikIlaniRepository;
import tr.tekstilkds.repository.TalepRepository;

/**
 * Arz-talep eşleştirme motoru (çoklu eşleştirme / alternatif öneri).
 *
 * 1) PostGIS ile aynı atık türünü isteyen ve yarıçap içindeki talepler ön elenir.
 * 2) Her aday için OSRM'den gerçek yol mesafesi alınır.
 * 3) Ekonomik fayda, karbon etkisi ve skor hesaplanır, en iyi N öneri döndürülür.
 *
 * TODO (Öğrenci 3): Skor şu an basit ağırlıklı toplam. AHP/TOPSIS gibi çok kriterli karar
 * yöntemiyle değiştirilecek, ağırlıklar kullanıcıdan alınabilir hale getirilecek ve
 * birim testlerle doğrulanacak.
 */
@Service
public class EslestirmeServisi {

    private static final int ON_ELEME_CARPANI = 3;

    private final AtikIlaniRepository ilanlar;
    private final TalepRepository talepler;
    private final RotaServisi rotaServisi;
    private final KdsAyarlari.Hesap hesap;

    public EslestirmeServisi(AtikIlaniRepository ilanlar, TalepRepository talepler,
                             RotaServisi rotaServisi, KdsAyarlari ayarlar) {
        this.ilanlar = ilanlar;
        this.talepler = talepler;
        this.rotaServisi = rotaServisi;
        this.hesap = ayarlar.hesap();
    }

    @Transactional(readOnly = true)
    public List<EslestirmeOnerisiDto> oner(long ilanId, int enFazla) {
        AtikIlani ilan = ilanlar.findById(ilanId)
                .orElseThrow(() -> new EntityNotFoundException("İlan bulunamadı: " + ilanId));

        return talepler.ilanIcinAdaylar(ilanId, enFazla * ON_ELEME_CARPANI).stream()
                .map(aday -> degerlendir(ilan, talepler.getReferenceById(aday.getTalepId()), aday.getKusUcusuKm()))
                .sorted(Comparator.comparingDouble(EslestirmeOnerisiDto::skor).reversed())
                .limit(enFazla)
                .toList();
    }

    private EslestirmeOnerisiDto degerlendir(AtikIlani ilan, Talep talep, double kusUcusuKm) {
        RotaDto rota = rotaServisi.hesapla(ilan.getFirma(), talep.getFirma(), false);
        AtikTuru tur = ilan.getAtikTuru();

        double arzKg = ilan.getMiktarKg().doubleValue();
        double talepKg = talep.getMiktarKg().doubleValue();
        double ton = Math.min(arzKg, talepKg) / 1000.0;
        double km = rota.mesafeKm();

        double tasimaMaliyeti = ton * km * hesap.tasimaMaliyetiTlTonKm();
        double bertarafTasarrufu = ton * tur.getBertarafMaliyetiTlTon().doubleValue();
        double hammaddeTasarrufu = ton * tur.getHammaddeDegeriTlTon().doubleValue();
        double netFayda = bertarafTasarrufu + hammaddeTasarrufu - tasimaMaliyeti;
        double netCo2 = ton * tur.getCo2TasarrufKgTon().doubleValue() - ton * km * hesap.tasimaEmisyonKgTonKm();

        double skor = skorHesapla(arzKg, talepKg, km, talep.getMaxMesafeKm().doubleValue());

        return new EslestirmeOnerisiDto(TalepDto.from(talep), yuvarla(skor, 4), yuvarla(kusUcusuKm, 2),
                yuvarla(km, 2), yuvarla(rota.sureDk(), 1), rota.kaynak(), yuvarla(ton, 3),
                yuvarla(tasimaMaliyeti, 2), yuvarla(bertarafTasarrufu, 2), yuvarla(hammaddeTasarrufu, 2),
                yuvarla(netFayda, 2), yuvarla(netCo2, 2));
    }

    /** 0-1 arası skor: miktar uyumu ve mesafe yakınlığının eşit ağırlıklı ortalaması. */
    static double skorHesapla(double arzKg, double talepKg, double yolKm, double maxKm) {
        double miktarUyumu = Math.min(arzKg, talepKg) / Math.max(arzKg, talepKg);
        double mesafeSkoru = Math.max(0.0, 1.0 - yolKm / maxKm);
        return 0.5 * miktarUyumu + 0.5 * mesafeSkoru;
    }

    private static double yuvarla(double deger, int basamak) {
        double carpan = Math.pow(10, basamak);
        return Math.round(deger * carpan) / carpan;
    }
}
