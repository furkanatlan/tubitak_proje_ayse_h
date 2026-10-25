package tr.tekstilkds.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** application.yml içindeki "kds.*" ayarları. */
@ConfigurationProperties(prefix = "kds")
public record KdsAyarlari(Osrm osrm, Hesap hesap) {

    public record Osrm(String url, int timeoutMs) {}

    public record Hesap(double yolKatsayisi, double tasimaMaliyetiTlTonKm, double tasimaEmisyonKgTonKm) {}
}
