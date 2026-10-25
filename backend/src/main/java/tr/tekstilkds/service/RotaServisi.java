package tr.tekstilkds.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tr.tekstilkds.config.KdsAyarlari;
import tr.tekstilkds.dto.Dtolar.RotaDto;
import tr.tekstilkds.entity.Firma;

/**
 * İki firma arasındaki karayolu mesafesini ve süresini hesaplar.
 * Önce OSRM'ye sorar; OSRM kapalıysa kuş uçuşu mesafeyi yol katsayısıyla çarparak tahmin eder,
 * böylece sistem OSRM olmadan da çalışmaya devam eder.
 */
@Service
public class RotaServisi {

    private static final Logger log = LoggerFactory.getLogger(RotaServisi.class);
    private static final double DUNYA_YARICAPI_KM = 6371.0;
    private static final double TAHMINI_HIZ_KMSA = 60.0;

    private final RestClient osrm;
    private final KdsAyarlari ayarlar;

    public RotaServisi(KdsAyarlari ayarlar) {
        this.ayarlar = ayarlar;
        var fabrika = new SimpleClientHttpRequestFactory();
        fabrika.setConnectTimeout(Duration.ofMillis(ayarlar.osrm().timeoutMs()));
        fabrika.setReadTimeout(Duration.ofMillis(ayarlar.osrm().timeoutMs()));
        this.osrm = RestClient.builder().baseUrl(ayarlar.osrm().url()).requestFactory(fabrika).build();
    }

    public RotaDto hesapla(Firma kaynak, Firma hedef, boolean geometriIste) {
        return osrmdenSor(kaynak, hedef, geometriIste).orElseGet(() -> tahminEt(kaynak, hedef, geometriIste));
    }

    private Optional<RotaDto> osrmdenSor(Firma a, Firma b, boolean geometriIste) {
        // OSRM koordinat sırası: boylam,enlem
        String koordinatlar = String.format(Locale.ROOT, "%f,%f;%f,%f", a.getBoylam(), a.getEnlem(), b.getBoylam(), b.getEnlem());
        try {
            JsonNode cevap = osrm.get()
                    .uri("/route/v1/driving/{k}?overview={o}&geometries=geojson", koordinatlar, geometriIste ? "full" : "false")
                    .retrieve()
                    .body(JsonNode.class);
            if (cevap == null || !"Ok".equals(cevap.path("code").asText())) {
                return Optional.empty();
            }
            JsonNode rota = cevap.path("routes").get(0);
            List<double[]> geometri = new ArrayList<>();
            for (JsonNode nokta : rota.path("geometry").path("coordinates")) {
                geometri.add(new double[] {nokta.get(1).asDouble(), nokta.get(0).asDouble()}); // -> [enlem, boylam]
            }
            return Optional.of(new RotaDto(rota.path("distance").asDouble() / 1000.0,
                    rota.path("duration").asDouble() / 60.0, "OSRM", geometri));
        } catch (Exception e) {
            log.warn("OSRM'ye ulaşılamadı ({}), tahmini mesafe kullanılacak", e.getMessage());
            return Optional.empty();
        }
    }

    private RotaDto tahminEt(Firma a, Firma b, boolean geometriIste) {
        double km = kusUcusuKm(a.getEnlem(), a.getBoylam(), b.getEnlem(), b.getBoylam()) * ayarlar.hesap().yolKatsayisi();
        List<double[]> geometri = geometriIste
                ? List.of(new double[] {a.getEnlem(), a.getBoylam()}, new double[] {b.getEnlem(), b.getBoylam()})
                : List.of();
        return new RotaDto(km, km / TAHMINI_HIZ_KMSA * 60.0, "TAHMIN", geometri);
    }

    /** Haversine formülü. */
    static double kusUcusuKm(double enlem1, double boylam1, double enlem2, double boylam2) {
        double dEnlem = Math.toRadians(enlem2 - enlem1);
        double dBoylam = Math.toRadians(boylam2 - boylam1);
        double h = Math.sin(dEnlem / 2) * Math.sin(dEnlem / 2)
                + Math.cos(Math.toRadians(enlem1)) * Math.cos(Math.toRadians(enlem2))
                * Math.sin(dBoylam / 2) * Math.sin(dBoylam / 2);
        return 2 * DUNYA_YARICAPI_KM * Math.asin(Math.sqrt(h));
    }
}
