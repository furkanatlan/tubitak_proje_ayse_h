# Görev Dağılımı

Proje dört iş paketine (İP) bölünmüştür. Her öğrenci bir iş paketinin **sahibidir**, yani o alandaki
kararlardan ve kodun kalitesinden sorumludur. Bu, başka alanlara dokunamayacağı anlamına gelmez.
Arayüzler (API sözleşmesi, veritabanı şeması) değişecekse ilgili kişiyle konuşulur.

| | İş paketi | Ana teknolojiler | Kodun yeri |
|---|---|---|---|
| **Öğrenci 1** | Backend ve REST API | Java 21, Spring Boot, Spring Security, JPA | `backend/.../controller`, `service/KayitServisi`, `dto` |
| **Öğrenci 2** | Veri, veritabanı ve coğrafi altyapı | PostgreSQL, PostGIS, Flyway, OSRM | `backend/src/main/resources/db/migration`, `repository`, `infra/` |
| **Öğrenci 3** | Karar destek algoritmaları | Java, çok kriterli karar verme (AHP/TOPSIS), optimizasyon | `service/EslestirmeServisi`, `service/RotaServisi`, testler |
| **Öğrenci 4** | Arayüz ve görselleştirme | React, Leaflet.js, Vite | `frontend/` |

> Proje yürütücüsü öğrenci, kendi iş paketine ek olarak koordinasyonu da üstlenir: haftalık toplantı,
> GitHub Projects panosu ve hocaya giden ara raporlar.

---

## Öğrenci 1: Backend ve REST API

Sistemin iskeletinden sorumludur: veriler doğru, güvenli ve tutarlı şekilde API'den sunulur.

- [ ] Mevcut `ApiController`'ı kaynaklara göre böl (`FirmaController`, `IlanController`, `TalepController`, ...)
- [ ] Firma, ilan ve talep için güncelleme ve silme (silme = `durum=KAPALI`) uç noktaları
- [ ] Listeleme uç noktalarına filtre (il, atık türü, durum) ve sayfalama (`Pageable`)
- [ ] Kullanıcı ve rol yapısı: Spring Security + JWT. Roller: `ADMIN`, `FIRMA` (firma sadece kendi kayıtlarını düzenler)
- [ ] Eşleştirme kararını kaydetme: `POST /api/eslestirme` (KABUL / RED). `eslestirme` tablosu hazır.
- [ ] Hata mesajlarının Türkçe ve tutarlı olması (`HataYakalayici`)
- [ ] Entegrasyon testleri: Testcontainers ile gerçek PostGIS üzerinde
- [ ] Swagger dokümantasyonunun açıklamalarını tamamla (`@Operation`, örnek istekler)

**Rapora katkısı:** Sistem mimarisi, API tasarımı, güvenlik.

## Öğrenci 2: Veri, veritabanı ve coğrafi altyapı

Projenin "gerçek" olmasını sağlar. Algoritma ne kadar iyi olursa olsun, sonuç verinin kalitesine bağlıdır.

- [ ] **Veri toplama:** Bursa OSB'leri (Nilüfer, Demirtaş, BOSB, İnegöl...) ve tekstil firmalarının konumları, TÜİK atık istatistikleri
- [ ] Atık türü katsayılarının literatürden doğrulanması (`V2__ornek_veri.sql`'deki sayılar **yer tutucudur**):
      bertaraf maliyeti (TL/ton), hammadde değeri (TL/ton), geri dönüşümün CO2 tasarrufu (kg/ton), kamyon emisyon faktörü (kg CO2e/ton-km).
      Kaynakları `docs/kaynaklar.md` dosyasına yaz.
- [ ] Gerçekçi bir test veri seti: en az 50 firma, 150 ilan/talep (yeni migration: `V3__...sql`)
- [ ] Isı haritası için bölgesel toplama sorguları: OSB/ilçe bazlı ya da `ST_SnapToGrid`/`ST_ClusterDBSCAN` ile
- [ ] Ayrıca belediye/OSB bazlı rapor sorguları: "Hangi bölgede ne kadar atık var, ne kadarı eşleşiyor?"
- [ ] OSRM kurulumunun bakımı; ileride kamyon profili (`truck.lua`) ile daha gerçekçi rota
- [ ] Veritabanı yedekleme/geri yükleme betiği (`pg_dump`)

**Rapora katkısı:** Veri kaynakları, veri modeli (ER diyagramı), coğrafi analiz yöntemi.

## Öğrenci 3: Karar destek algoritmaları

Projenin "akıllı" kısmıdır ve bilimsel katkının merkezindedir.

- [ ] Mevcut basit skoru (miktar uyumu %50 + mesafe %50) **çok kriterli karar verme** modeline dönüştür:
      kriterler mesafe, süre, miktar uyumu, net fayda (TL), net CO2. Ağırlıkları **AHP** ile belirle, sıralamayı **TOPSIS** ile yap.
- [ ] **Çoklu eşleştirme:** Bir ilan birden fazla alıcıya bölünebilsin (ör. 2 ton denim: 1,5 t Gürsu + 0,5 t Uşak).
      Bu bir *ulaştırma problemi*dir. Önce açgözlü (greedy) çözüm, sonra doğrusal programlama (Google OR-Tools Java).
- [ ] **Maliyet ve tasarruf hesaplayıcı:** Taşıma maliyeti modelini (yakıt, araç kapasitesi, dönüş yükü) geliştir
- [ ] **Karbon emisyonu modeli:** Taşıma emisyonu ile geri dönüşüm tasarrufu karşılaştırması (Öğrenci 2'nin katsayılarıyla)
- [ ] Duyarlılık analizi: Ağırlıklar değişince sıralama nasıl değişiyor? (Raporda grafik olarak kullanılacak)
- [ ] Her hesap fonksiyonu için birim test (`EslestirmeServisiTest` örneğini genişlet)
- [ ] OSRM `table` servisi ile çok sayıda aday için toplu mesafe matrisi (performans)

**Rapora katkısı:** Yöntem bölümü (matematiksel model), bulgular, duyarlılık analizi.

## Öğrenci 4: Arayüz ve görselleştirme

Jürinin ve kullanıcının göreceği yüz. Karar destek sisteminin anlaşılır olması burada belirlenir.

- [ ] Firma, ilan ve talep ekleme formları (haritaya tıklayarak konum seçme dahil)
- [ ] Eşleştirme sonuçları ekranı: önerileri karşılaştırma, rotayı haritada gösterme, "Kabul / Red" butonları
- [ ] **Isı haritası:** Atık türü filtresi, zaman filtresi, OSB sınırları katmanı (GeoJSON)
- [ ] **Gösterge paneli (dashboard):** Toplam yönlendirilen atık (ton), toplam tasarruf (TL), önlenen CO2 (kg). Grafikler için Recharts.
- [ ] Harita lejantı (renkler: turuncu = atık veren, yeşil = alıcı, mor = ikisi de)
- [ ] Giriş ekranı (Öğrenci 1'in JWT yapısı ile)
- [ ] Mobil uyum ve basit kullanılabilirlik testi (3-5 kişiyle, rapora eklenir)

**Rapora katkısı:** Kullanıcı arayüzü, ekran görüntüleri, kullanılabilirlik testi sonuçları.

---

## Ortak sorumluluklar

- Her öğrenci kendi kodunun **testini** yazar ve kendi bölümünün **dokümantasyonunu** günceller.
- Her Pull Request'i **en az bir başka öğrenci** inceler (bkz. [GIT_AKISI.md](GIT_AKISI.md)).
- Haftalık kısa toplantı: geçen hafta ne yapıldı, bu hafta ne yapılacak, nerede takılındı?

## Önerilen zaman planı

| Dönem | Hedef | Kim |
|---|---|---|
| 1. ay | Herkes ortamı kurar, mevcut iskeleti çalıştırır ve kodu okur. Literatür taraması, veri kaynakları. | Hepsi |
| 2.–3. ay | CRUD + güvenlik (Ö1), gerçek veri seti (Ö2), AHP ağırlıkları (Ö3), formlar (Ö4) | Paralel |
| 4.–6. ay | TOPSIS + çoklu eşleştirme (Ö3), bölgesel analiz (Ö2), dashboard + ısı haritası (Ö4), testler (Ö1) | Paralel |
| 7.–8. ay | Entegrasyon, senaryo testleri (Bursa vaka çalışması), duyarlılık analizi | Hepsi |
| 9.–10. ay | Kullanılabilirlik testi, hata düzeltme, sonuç raporu, poster/sunum | Hepsi |

## Ek özellik değerlendirmesi

Bilgi notundaki üç ek önerinin **üçü de** projeye alınmalı. Üçü de mevcut altyapıyla uyumlu ve
iskelette ilk sürümleri zaten çalışıyor:

1. **Maliyet / tasarruf hesaplayıcı** (Öğrenci 3): Kararın gerekçesini TL ile ortaya koyar. *Önceliği en yüksek.*
2. **Alternatif sektör öneri motoru (çoklu eşleştirme)** (Öğrenci 3): Sistemi "akıllı" kılan kısımdır. Tek en iyi eşleşme yerine sıralı alternatifler ve miktarın bölünmesi.
3. **Coğrafi ısı haritası** (Öğrenci 2 + 4): Az emekle güçlü görsel etki sağlar; belediye/OSB ölçeğinde kullanılabilirliği gösterir.

Karbon emisyonu hesabı zaten ana kapsamda (OSRM mesafesi üzerinden). Tasarruf hesaplayıcı ile
birlikte "ekonomik + çevresel" iki boyutlu bir sonuç sunar. Bu da projenin döngüsel ekonomi iddiasını güçlendirir.
