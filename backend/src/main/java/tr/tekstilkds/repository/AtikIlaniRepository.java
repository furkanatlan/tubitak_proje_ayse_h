package tr.tekstilkds.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tr.tekstilkds.entity.AtikIlani;

public interface AtikIlaniRepository extends JpaRepository<AtikIlani, Long> {

    interface YogunlukNoktasi {
        double getEnlem();
        double getBoylam();
        double getToplamKg();
    }

    /** Isı haritası için: firma konumu başına aktif ilanlardaki toplam atık miktarı. */
    @Query(nativeQuery = true, value = """
            SELECT f.enlem AS enlem, f.boylam AS boylam, SUM(i.miktar_kg) AS toplamKg
            FROM atik_ilani i JOIN firma f ON f.id = i.firma_id
            WHERE i.durum = 'AKTIF'
              AND (CAST(:atikTuruId AS INTEGER) IS NULL OR i.atik_turu_id = :atikTuruId)
            GROUP BY f.id, f.enlem, f.boylam
            """)
    List<YogunlukNoktasi> yogunluk(@Param("atikTuruId") Integer atikTuruId);
}
