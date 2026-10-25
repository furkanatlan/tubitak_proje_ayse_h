package tr.tekstilkds.entity;

/** Veritabanında VARCHAR olarak saklanan sabit değer listeleri (V1__sema.sql CHECK kısıtlarıyla aynı olmalı). */
public final class Enumlar {

    private Enumlar() {}

    public enum FirmaRol { ATIK_VEREN, ALICI, HER_IKISI }

    public enum Periyot { TEK_SEFERLIK, HAFTALIK, AYLIK }

    public enum KayitDurumu { AKTIF, ESLESTI, KAPALI }
}
