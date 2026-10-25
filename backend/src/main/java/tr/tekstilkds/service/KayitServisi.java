package tr.tekstilkds.service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.tekstilkds.dto.Dtolar.*;
import tr.tekstilkds.entity.*;
import tr.tekstilkds.entity.Enumlar.Periyot;
import tr.tekstilkds.repository.*;

/**
 * Firma, atık türü, ilan ve talep kayıtlarının temel işlemleri (listele / getir / oluştur).
 * TODO (Öğrenci 1): güncelleme, silme (durum=KAPALI), filtreleme ve sayfalama eklenecek.
 */
@Service
@Transactional(readOnly = true)
public class KayitServisi {

    private final FirmaRepository firmalar;
    private final AtikTuruRepository atikTurleri;
    private final AtikIlaniRepository ilanlar;
    private final TalepRepository talepler;

    public KayitServisi(FirmaRepository firmalar, AtikTuruRepository atikTurleri,
                        AtikIlaniRepository ilanlar, TalepRepository talepler) {
        this.firmalar = firmalar;
        this.atikTurleri = atikTurleri;
        this.ilanlar = ilanlar;
        this.talepler = talepler;
    }

    // ---- Firma ----

    public List<FirmaDto> firmalar() {
        return firmalar.findAll().stream().map(FirmaDto::from).toList();
    }

    public Firma firma(long id) {
        return firmalar.findById(id).orElseThrow(() -> new EntityNotFoundException("Firma bulunamadı: " + id));
    }

    @Transactional
    public FirmaDto firmaOlustur(FirmaIstek istek) {
        var f = new Firma();
        f.setAd(istek.ad());
        f.setRol(istek.rol());
        f.setSektor(istek.sektor());
        f.setIl(istek.il());
        f.setIlce(istek.ilce());
        f.setBolge(istek.bolge());
        f.setEnlem(istek.enlem());
        f.setBoylam(istek.boylam());
        f.setTelefon(istek.telefon());
        f.setEposta(istek.eposta());
        return FirmaDto.from(firmalar.save(f));
    }

    // ---- Atık türü ----

    public List<AtikTuruDto> atikTurleri() {
        return atikTurleri.findAll().stream().map(AtikTuruDto::from).toList();
    }

    private AtikTuru atikTuru(int id) {
        return atikTurleri.findById(id).orElseThrow(() -> new EntityNotFoundException("Atık türü bulunamadı: " + id));
    }

    // ---- İlan (arz) ----

    public List<IlanDto> ilanlar() {
        return ilanlar.findAll().stream().map(IlanDto::from).toList();
    }

    @Transactional
    public IlanDto ilanOlustur(IlanIstek istek) {
        var i = new AtikIlani();
        i.setFirma(firma(istek.firmaId()));
        i.setAtikTuru(atikTuru(istek.atikTuruId()));
        i.setMiktarKg(istek.miktarKg());
        i.setPeriyot(istek.periyot() != null ? istek.periyot() : Periyot.TEK_SEFERLIK);
        i.setAciklama(istek.aciklama());
        return IlanDto.from(ilanlar.save(i));
    }

    // ---- Talep ----

    public List<TalepDto> talepler() {
        return talepler.findAll().stream().map(TalepDto::from).toList();
    }

    @Transactional
    public TalepDto talepOlustur(TalepIstek istek) {
        var t = new Talep();
        t.setFirma(firma(istek.firmaId()));
        t.setAtikTuru(atikTuru(istek.atikTuruId()));
        t.setMiktarKg(istek.miktarKg());
        t.setMaxMesafeKm(istek.maxMesafeKm() != null ? istek.maxMesafeKm() : BigDecimal.valueOf(300));
        t.setAciklama(istek.aciklama());
        return TalepDto.from(talepler.save(t));
    }

    // ---- İstatistik ----

    public List<YogunlukDto> yogunluk(Integer atikTuruId) {
        return ilanlar.yogunluk(atikTuruId).stream()
                .map(n -> new YogunlukDto(n.getEnlem(), n.getBoylam(), n.getToplamKg()))
                .toList();
    }
}
