package tr.tekstilkds.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import tr.tekstilkds.entity.Enumlar.FirmaRol;

/** "konum" (PostGIS geography) sütunu veritabanında enlem/boylamdan otomatik üretilir, burada eşlenmez. */
@Entity
@Table(name = "firma")
public class Firma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ad;

    @Enumerated(EnumType.STRING)
    private FirmaRol rol;

    private String sektor;
    private String il;
    private String ilce;
    private String bolge;
    private double enlem;
    private double boylam;
    private String telefon;
    private String eposta;

    @Column(name = "olusturma_zamani", updatable = false)
    private OffsetDateTime olusturmaZamani;

    @PrePersist
    void olustururken() { olusturmaZamani = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public FirmaRol getRol() { return rol; }
    public void setRol(FirmaRol rol) { this.rol = rol; }
    public String getSektor() { return sektor; }
    public void setSektor(String sektor) { this.sektor = sektor; }
    public String getIl() { return il; }
    public void setIl(String il) { this.il = il; }
    public String getIlce() { return ilce; }
    public void setIlce(String ilce) { this.ilce = ilce; }
    public String getBolge() { return bolge; }
    public void setBolge(String bolge) { this.bolge = bolge; }
    public double getEnlem() { return enlem; }
    public void setEnlem(double enlem) { this.enlem = enlem; }
    public double getBoylam() { return boylam; }
    public void setBoylam(double boylam) { this.boylam = boylam; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public String getEposta() { return eposta; }
    public void setEposta(String eposta) { this.eposta = eposta; }
    public OffsetDateTime getOlusturmaZamani() { return olusturmaZamani; }
}
