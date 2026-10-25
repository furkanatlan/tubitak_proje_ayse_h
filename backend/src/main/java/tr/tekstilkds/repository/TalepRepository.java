package tr.tekstilkds.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tr.tekstilkds.entity.Talep;

public interface TalepRepository extends JpaRepository<Talep, Long> {

    interface Aday {
        Long getTalepId();
        double getKusUcusuKm();
    }

    /**
     * Bir ilan için ön eleme: aynı atık türünü isteyen, aktif ve talep sahibinin
     * kabul ettiği yarıçap içinde kalan talepler (kuş uçuşu, PostGIS GIST indeksi ile hızlı).
     */
    @Query(nativeQuery = true, value = """
            SELECT t.id AS talepId, ST_Distance(fi.konum, ft.konum) / 1000.0 AS kusUcusuKm
            FROM atik_ilani i
            JOIN firma fi ON fi.id = i.firma_id
            JOIN talep t  ON t.atik_turu_id = i.atik_turu_id AND t.durum = 'AKTIF' AND t.firma_id <> i.firma_id
            JOIN firma ft ON ft.id = t.firma_id
            WHERE i.id = :ilanId
              AND ST_DWithin(fi.konum, ft.konum, t.max_mesafe_km * 1000)
            ORDER BY kusUcusuKm
            LIMIT :enFazla
            """)
    List<Aday> ilanIcinAdaylar(@Param("ilanId") long ilanId, @Param("enFazla") int enFazla);
}
