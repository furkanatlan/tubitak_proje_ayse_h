package tr.tekstilkds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import tr.tekstilkds.entity.Enumlar.KayitDurumu;
import tr.tekstilkds.entity.Enumlar.Periyot;

/** Arz tarafı: bir firmanın elden çıkarmak istediği atık. */
@Entity
@Table(name = "atik_ilani")
public class AtikIlani {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "firma_id")
    private Firma firma;

    @ManyToOne(optional = false)
    @JoinColumn(name = "atik_turu_id")
    private AtikTuru atikTuru;

    @Column(name = "miktar_kg")
    private BigDecimal miktarKg;

    @Enumerated(EnumType.STRING)
    private Periyot periyot = Periyot.TEK_SEFERLIK;

    @Enumerated(EnumType.STRING)
    private KayitDurumu durum = KayitDurumu.AKTIF;

    private String aciklama;

    @Column(name = "olusturma_zamani", updatable = false)
    private OffsetDateTime olusturmaZamani;

    @PrePersist
    void olustururken() { olusturmaZamani = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public Firma getFirma() { return firma; }
    public void setFirma(Firma firma) { this.firma = firma; }
    public AtikTuru getAtikTuru() { return atikTuru; }
    public void setAtikTuru(AtikTuru atikTuru) { this.atikTuru = atikTuru; }
    public BigDecimal getMiktarKg() { return miktarKg; }
    public void setMiktarKg(BigDecimal miktarKg) { this.miktarKg = miktarKg; }
    public Periyot getPeriyot() { return periyot; }
    public void setPeriyot(Periyot periyot) { this.periyot = periyot; }
    public KayitDurumu getDurum() { return durum; }
    public void setDurum(KayitDurumu durum) { this.durum = durum; }
    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }
    public OffsetDateTime getOlusturmaZamani() { return olusturmaZamani; }
}
