package tr.tekstilkds.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import tr.tekstilkds.entity.AtikIlani;
import tr.tekstilkds.entity.AtikTuru;
import tr.tekstilkds.entity.Enumlar.FirmaRol;
import tr.tekstilkds.entity.Enumlar.KayitDurumu;
import tr.tekstilkds.entity.Enumlar.Periyot;
import tr.tekstilkds.entity.Firma;
import tr.tekstilkds.entity.Talep;

/**
 * API'nin dışarıya verdiği / dışarıdan aldığı veri yapıları.
 * Entity'ler doğrudan JSON olarak döndürülmez; böylece veritabanı yapısı değişse de API sabit kalır.
 */
public final class Dtolar {

    private Dtolar() {}

    // ---------- Çıktılar ----------

    public record FirmaDto(Long id, String ad, FirmaRol rol, String sektor, String il, String ilce,
                           String bolge, double enlem, double boylam) {
        public static FirmaDto from(Firma f) {
            return new FirmaDto(f.getId(), f.getAd(), f.getRol(), f.getSektor(), f.getIl(), f.getIlce(),
                    f.getBolge(), f.getEnlem(), f.getBoylam());
        }
    }

    public record AtikTuruDto(Integer id, String kod, String ad, String aciklama,
                              BigDecimal bertarafMaliyetiTlTon, BigDecimal hammaddeDegeriTlTon,
                              BigDecimal co2TasarrufKgTon) {
        public static AtikTuruDto from(AtikTuru a) {
            return new AtikTuruDto(a.getId(), a.getKod(), a.getAd(), a.getAciklama(),
                    a.getBertarafMaliyetiTlTon(), a.getHammaddeDegeriTlTon(), a.getCo2TasarrufKgTon());
        }
    }

    public record IlanDto(Long id, FirmaDto firma, AtikTuruDto atikTuru, BigDecimal miktarKg,
                          Periyot periyot, KayitDurumu durum, String aciklama) {
        public static IlanDto from(AtikIlani i) {
            return new IlanDto(i.getId(), FirmaDto.from(i.getFirma()), AtikTuruDto.from(i.getAtikTuru()),
                    i.getMiktarKg(), i.getPeriyot(), i.getDurum(), i.getAciklama());
        }
    }

    public record TalepDto(Long id, FirmaDto firma, AtikTuruDto atikTuru, BigDecimal miktarKg,
                           BigDecimal maxMesafeKm, KayitDurumu durum, String aciklama) {
        public static TalepDto from(Talep t) {
            return new TalepDto(t.getId(), FirmaDto.from(t.getFirma()), AtikTuruDto.from(t.getAtikTuru()),
                    t.getMiktarKg(), t.getMaxMesafeKm(), t.getDurum(), t.getAciklama());
        }
    }

    /** kaynak: "OSRM" (gerçek yol ağı) veya "TAHMIN" (kuş uçuşu x yol katsayısı). geometri: [enlem, boylam] listesi. */
    public record RotaDto(double mesafeKm, double sureDk, String kaynak, List<double[]> geometri) {}

    public record EslestirmeOnerisiDto(
            TalepDto talep,
            double skor,
            double kusUcusuKm,
            double yolMesafeKm,
            double sureDk,
            String mesafeKaynagi,
            double tasinanTon,
            double tasimaMaliyetiTl,
            double bertarafTasarrufuTl,
            double hammaddeTasarrufuTl,
            double netFaydaTl,
            double netCo2TasarrufuKg) {}

    public record YogunlukDto(double enlem, double boylam, double toplamKg) {}

    // ---------- Girdiler ----------

    public record FirmaIstek(
            @NotBlank String ad,
            @NotNull FirmaRol rol,
            String sektor,
            @NotBlank String il,
            String ilce,
            String bolge,
            @DecimalMin("35.0") @DecimalMax("43.0") double enlem,   // Türkiye sınırları
            @DecimalMin("25.0") @DecimalMax("45.0") double boylam,
            String telefon,
            @Email String eposta) {}

    public record IlanIstek(
            @NotNull Long firmaId,
            @NotNull Integer atikTuruId,
            @NotNull @Positive BigDecimal miktarKg,
            Periyot periyot,
            String aciklama) {}

    public record TalepIstek(
            @NotNull Long firmaId,
            @NotNull Integer atikTuruId,
            @NotNull @Positive BigDecimal miktarKg,
            @Positive BigDecimal maxMesafeKm,
            String aciklama) {}
}
