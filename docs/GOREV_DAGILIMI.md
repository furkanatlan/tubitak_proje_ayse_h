# Görev Dağılımı

> **Kendi görevinize geçmeden önce aşağıdaki "Adım 0"ı eksiksiz tamamlayın.** Bu adımlar dört öğrenci için de aynıdır.
> Bir adımda takılırsanız bir sonrakine geçmeyin. Hata mesajının ekran görüntüsüyle gruba yazın.

## Adım 0: Herkes için ilk kurulum

Bu adımlar bir kez yapılır ve yaklaşık 30-45 dakika sürer (çoğu indirme beklemesidir).

### 0.1 Programları kurun

1. **Docker Desktop:** https://www.docker.com/products/docker-desktop/ adresinden indirip kurun.
   Kurulumda **"Use WSL 2"** seçeneği işaretli olsun. Kurulumdan sonra bilgisayarı yeniden başlatın.
   Docker Desktop'ı açın ve sol alttaki durum **"Engine running"** (yeşil) olana kadar bekleyin.
2. **Git:** https://git-scm.com/downloads adresinden kurun. Bütün seçenekleri varsayılan bırakabilirsiniz.
3. **VS Code:** https://code.visualstudio.com/ adresinden kurun.

Kontrol etmek için bir terminal açın (Windows'ta **Git Bash** ya da **PowerShell**) ve şunları yazın:

```bash
docker --version
git --version
```

İkisi de bir sürüm numarası yazdırmalıdır.

### 0.2 GitHub erişimi

1. GitHub hesabınızın kullanıcı adını hocaya iletin. Size repoya katılma daveti gelecek.
2. E-postanıza gelen daveti (ya da https://github.com/notifications adresindeki daveti) **kabul edin**.
3. Git'e kim olduğunuzu tanıtın (kendi bilgilerinizle):

```bash
git config --global user.name "Adınız Soyadınız"
git config --global user.email "github-epostaniz@ornek.com"
```

### 0.3 Projeyi klonlayın

Projeyi koymak istediğiniz klasörde terminal açın (örneğin `Belgeler`) ve şunları yazın:

```bash
git clone https://github.com/furkanatlan/tubitak_proje_ayse_h.git
cd tubitak_proje_ayse_h
```

İlk `git push` işleminde tarayıcıda GitHub girişi istenecek. Bu normaldir, giriş yapmanız yeterli.
Klasörü VS Code'da açmak için: `code .`

### 0.4 Sistemi Docker ile başlatın

Docker Desktop'ın açık olduğundan emin olun. Ardından proje klasöründe şunu çalıştırın:

```bash
docker compose up -d
```

İlk seferde Java, Node ve veritabanı paketleri indirildiği için **5-10 dakika** sürer. Backend'in hazır olup olmadığını görmek için:

```bash
docker compose logs -f backend
```

Ekranda **`Started TekstilKdsApplication`** yazısı göründüğünde backend hazırdır. `Ctrl + C` ile log ekranından çıkın
(bu komut sistemi durdurmaz). Çalışan servisleri görmek için:

```bash
docker compose ps
```

Listede `backend`, `db`, `frontend` ve `pgadmin` görünmeli, hepsi `running` durumunda olmalıdır.

### 0.5 Çalıştığını kontrol edin

Tarayıcıda şu adresleri açın:

| Adres | Ne görmelisiniz |
|---|---|
| http://localhost:15173 | Solda atık ilanları, sağda Bursa haritası ve renkli firma noktaları |
| http://localhost:18080/swagger-ui.html | API dokümantasyonu |
| http://localhost:15050 | pgAdmin (veritabanı arayüzü). Şifre sorarsa: `kds_sifre` |

Haritada sol taraftaki bir ilana tıklayın, altında "Önerilen alıcılar" kartları çıkmalı.
Bu kartlardan birine tıklayınca haritada rota çizilir. **OSRM kurulmadıysa rota düz kesikli çizgidir.**
Gerçek yol rotası için 0.6'ya geçin.

### 0.6 Gerçek yol haritası (OSRM) kurulumu

OSRM, iki firma arasındaki **gerçek karayolu mesafesini ve süresini** hesaplar. Projenin lojistik ve karbon
hesapları bu mesafeye dayanır. Bu adım bir kez yapılır.

**Gereken:** Docker'ın en az **8 GB RAM** kullanabilmesi ve yaklaşık **3 GB boş disk**.
RAM'iniz yeterli değilse aşağıdaki **"B planı"**na geçin.

**1. Docker'a yeterli RAM verin.** Dosya Gezgini'nin adres çubuğuna `%UserProfile%` yazın, açılan klasörde
`.wslconfig` adında bir dosya oluşturun (yoksa) ve içine şunu yazın:

```ini
[wsl2]
memory=10GB
```

Sonra Docker Desktop'ı kapatıp yeniden açın. 16 GB RAM'li bir bilgisayarda `10GB` uygundur.

**2. Türkiye haritasını indirip işleyin.** Proje klasöründe şunu çalıştırın:

```bash
docker compose --profile osrm-setup run --rm osrm-hazirla
```

Bu komut önce Türkiye haritasını (~600 MB) indirir, sonra rota hesaplamaya uygun hale getirir.
İnternet ve bilgisayar hızına göre **5-20 dakika** sürer. Ekranda çok sayıda `[info]` satırı akar, bu normaldir.
İşlem bittiğinde şu satır görünür:

```
>> Tamamlandı. Sunucuyu başlatmak için: docker compose --profile osrm up -d
```

Komut `Killed` yazıp durursa Docker'a yeterli RAM verilmemiştir. 1. adıma dönün.

**3. OSRM'nin her seferinde otomatik başlamasını sağlayın.** Proje klasöründeki `.env.example` dosyasını
kopyalayın ve kopyanın adını `.env` yapın. Sonra `.env` dosyasını VS Code'da açın ve şu satırın başındaki `#` işaretini silin:

```
COMPOSE_PROFILES=osrm
```

Ardından sistemi yeniden başlatın:

```bash
docker compose up -d
```

Artık her `docker compose up -d` komutu OSRM'yi de başlatır.

**4. Kontrol edin.** Tarayıcıda şu adresi açın (Bursa Demirtaş'tan Uşak'a rota):

http://localhost:15000/route/v1/driving/29.08,40.268;29.36,38.648?overview=false

Sayfada `"code":"Ok"` ve `"distance":285...` (metre cinsinden) görmelisiniz. Uygulamada (http://localhost:15173)
öneri kartlarında mesafenin yanında **(OSRM)** yazmalı ve rota haritada yolları takip eden mavi bir çizgi olmalıdır.
Hâlâ **(TAHMIN)** yazıyorsa `docker compose ps` çıktısında `osrm` satırının `running` olup olmadığına bakın.

**B planı (RAM yetersizse):** `.env.example` dosyasını `.env` adıyla kopyalayın ve şu satırın başındaki `#` işaretini silin:

```
OSRM_URL=https://router.project-osrm.org
```

Ardından `docker compose up -d` çalıştırın. Bu, internetteki herkese açık bir OSRM demo sunucusudur ve gerçek rota verir.
Ancak sadece geliştirme içindir, çok sık istekte geçici olarak engelleyebilir. Raporda kullanılacak sonuçlar yerel OSRM'li bir bilgisayarda üretilmelidir.

### 0.7 Günlük çalışma

```bash
docker compose up -d        # çalışmaya başlarken (Docker Desktop açık olmalı)
docker compose stop         # bitirince (veriler kaybolmaz)
git pull                    # her gün işe başlamadan önce, arkadaşlarınızın değişikliklerini almak için
```

Kod değiştirince ne yapılacağı, testler ve sık karşılaşılan sorunlar için: [KURULUM.md](KURULUM.md).
Kendi dalınızda çalışma ve Pull Request kuralları için: [GIT_AKISI.md](GIT_AKISI.md).

---

## İş paketleri

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

**İlk gün (Adım 0'dan sonra):** http://localhost:18080/swagger-ui.html adresinde `GET /api/firmalar` uç noktasını
"Try it out" ile çalıştırın. Ardından `ApiController.java` içinde bir şeyi değiştirin (örneğin bir `@Operation` açıklamasını),
`docker compose restart backend` çalıştırın ve değişikliği Swagger'da görün. Java'yı IDE'de çalıştırmak (debug) için: [KURULUM.md bölüm 7](KURULUM.md#7-docker-kullanmadan-ideden-backend-çalıştırma).

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

**İlk gün (Adım 0'dan sonra):** OSRM'yi (Adım 0.6) mutlaka yerelde kurun, çünkü bakımı sizdedir. pgAdmin'de (http://localhost:15050)
*Tekstil KDS (docker) > tekstil_kds > Schemas > public > Tables* altındaki tabloları inceleyin. Query Tool'da şunu çalıştırın:
`SELECT ad, ST_AsText(konum::geometry) FROM firma;`. Sonra `V1__sema.sql` ve `V2__ornek_veri.sql` dosyalarını okuyun ve
[KURULUM.md bölüm 6](KURULUM.md#6-veritabanı-kuralları-önemli)'daki migration kurallarını öğrenin.

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

**İlk gün (Adım 0'dan sonra):** Gerçek mesafelerle çalışacağınız için OSRM'yi (Adım 0.6) kurun. Testleri çalıştırın:
`docker compose run --rm --no-deps backend mvn test` (sonunda `BUILD SUCCESS` görmelisiniz). Sonra tarayıcıda
http://localhost:18080/api/eslestirme/ilan/4 adresini açın ve `EslestirmeServisi.java`'yı okuyarak her sayının nasıl hesaplandığını çıkarın.
Dikkat edin: Uşak'taki alıcının net faydası daha yüksek, ama skoru daha düşük. Çok kriterli modelin neden gerektiğini bu örnek gösteriyor.

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

**İlk gün (Adım 0'dan sonra):** Rotaların gerçek yolları izlediğini görmek için OSRM'yi (Adım 0.6) kurun; RAM yetmezse B planını kullanın.
`frontend/src/App.jsx` dosyasındaki başlık metnini değiştirip kaydedin. Tarayıcı kendiliğinden yenilenmeli, yeniden başlatma gerekmez.
Yeni bir npm paketi eklemek için: `docker compose exec frontend npm install paket-adi`.

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
