package tr.tekstilkds.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Veritabanı gerektirmeyen saf hesap testleri. Çalıştırma: bkz. docs/KURULUM.md "Testler". */
class EslestirmeServisiTest {

    @Test
    void ayniMiktarVeSifirMesafeTamSkorAlir() {
        assertEquals(1.0, EslestirmeServisi.skorHesapla(1000, 1000, 0, 100), 1e-9);
    }

    @Test
    void maksimumMesafeyiAsanAdayinMesafeSkoruSifirdir() {
        // miktar uyumu 0.5, mesafe skoru 0 -> 0.25
        assertEquals(0.25, EslestirmeServisi.skorHesapla(500, 1000, 150, 100), 1e-9);
    }

    @Test
    void bursaIleUsakArasiKusUcusuMakul() {
        double km = RotaServisi.kusUcusuKm(40.1885, 29.0610, 38.6823, 29.4082);
        assertTrue(km > 160 && km < 180, "Bulunan: " + km);
    }
}
