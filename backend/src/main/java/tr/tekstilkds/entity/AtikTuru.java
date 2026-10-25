package tr.tekstilkds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "atik_turu")
public class AtikTuru {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String kod;
    private String ad;
    private String aciklama;

    @Column(name = "bertaraf_maliyeti_tl_ton")
    private BigDecimal bertarafMaliyetiTlTon;

    @Column(name = "hammadde_degeri_tl_ton")
    private BigDecimal hammaddeDegeriTlTon;

    @Column(name = "co2_tasarruf_kg_ton")
    private BigDecimal co2TasarrufKgTon;

    public Integer getId() { return id; }
    public String getKod() { return kod; }
    public String getAd() { return ad; }
    public String getAciklama() { return aciklama; }
    public BigDecimal getBertarafMaliyetiTlTon() { return bertarafMaliyetiTlTon; }
    public BigDecimal getHammaddeDegeriTlTon() { return hammaddeDegeriTlTon; }
    public BigDecimal getCo2TasarrufKgTon() { return co2TasarrufKgTon; }
}
