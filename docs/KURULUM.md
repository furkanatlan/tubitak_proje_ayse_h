# Kurulum ve Günlük Kullanım Rehberi

Tüm sistem **Docker** üzerinde çalışır. Bilgisayarınıza Java, Maven, Node.js veya PostgreSQL
**kurmanız gerekmez**. Hepsi konteynerlerin içinde gelir.

## 1. Gerekenler (bir kez)

| Program | Neden | Not |
|---|---|---|
| [Docker Desktop](https://www.docker.com/products/docker-desktop/) | Tüm servisleri çalıştırır | Windows'ta kurulumda **WSL 2** seçili olsun |
| [Git](https://git-scm.com/downloads) | Kodu indirmek ve göndermek | |
| [VS Code](https://code.visualstudio.com/) | Kod editörü | Önerilen eklentiler aşağıda |

**VS Code eklentileri:** *Extension Pack for Java* (Öğrenci 1 ve 3), *ES7+ React snippets* (Öğrenci 4),
*Docker*, *GitLens*. Java için **IntelliJ IDEA Community** de kullanılabilir.

**Docker Desktop bellek ayarı:** Yerel OSRM için Docker'ın en az **8 GB** RAM kullanabilmesi gerekir.
WSL 2 kullanıyorsanız `C:\Users\<kullanıcı>\.wslconfig` dosyasına şunu yazın ve Docker Desktop'ı yeniden başlatın:

```ini
[wsl2]
memory=10GB
```

> Bilgisayarınızda 8 GB'tan az RAM varsa OSRM'yi kurmayın, aşağıdaki "OSRM olmadan çalışmak" bölümüne bakın.

## 2. Projeyi indirip başlatma (bir kez)

```bash
git clone https://github.com/furkanatlan/tubitak_proje_ayse_h.git
cd tubitak_proje_ayse_h
docker compose up -d
```

İlk açılış **5-10 dakika** sürebilir, çünkü Maven ve npm paketleri indirilir. Sonraki açılışlar birkaç saniye sürer.
Durumu izlemek için:

```bash
docker compose logs -f backend      # "Started TekstilKdsApplication" yazısını bekleyin, Ctrl+C ile çıkın
```

## 3. Adresler

| Ne | Adres | Giriş bilgisi |
|---|---|---|
| **Uygulama (harita)** | http://localhost:15173 | |
| Backend API | http://localhost:18080/api/firmalar | |
| **API dokümantasyonu (Swagger)** | http://localhost:18080/swagger-ui.html | |
| Veritabanı arayüzü (pgAdmin) | http://localhost:15050 | Sunucu hazır gelir; şifre sorarsa: `kds_sifre` |
| PostgreSQL (DBeaver vb. için) | `localhost:15432`, veritabanı `tekstil_kds` | kullanıcı `kds`, şifre `kds_sifre` |
| OSRM rota sunucusu | http://localhost:15000 | |

Port başka bir programla çakışıyorsa `.env.example` dosyasını `.env` adıyla kopyalayıp portu değiştirin.

## 4. OSRM (gerçek yol mesafesi) kurulumu (bir kez, isteğe bağlı)

OSRM, iki firma arasındaki **gerçek karayolu** mesafesini ve süresini hesaplar. Türkiye haritasını
(~600 MB) indirip işler. İşlem yaklaşık 7 GB RAM ister ve 5-20 dakika sürer:

```bash
docker compose --profile osrm-setup run --rm osrm-hazirla
```

Hazırlık bittikten sonra `.env.example` dosyasını `.env` adıyla kopyalayın ve `COMPOSE_PROFILES=osrm` satırının başındaki `#` işaretini silin.
Bundan sonra her `docker compose up -d` komutu OSRM'yi de başlatır:

```bash
docker compose up -d
```

Kontrol için tarayıcıda http://localhost:15000/route/v1/driving/29.08,40.268;29.36,38.648?overview=false adresini açın,
`"code":"Ok"` görmelisiniz. Uygulamadaki öneri kartlarında mesafenin yanında **(OSRM)** yazar.
Adım adım anlatım için: [GOREV_DAGILIMI.md, Adım 0.6](GOREV_DAGILIMI.md#06-gerçek-yol-haritası-osrm-kurulumu).

### OSRM olmadan çalışmak

OSRM kapalıyken backend çökmez. Mesafeyi *kuş uçuşu × 1,3* ile **tahmin eder**: API'de
`"mesafeKaynagi": "TAHMIN"` döner, haritada rota kesikli düz çizgi olarak görünür.
Gerçek rota görmek isteyen ama RAM'i yetmeyen biri `.env` dosyasına şunu yazabilir
(herkese açık demo sunucusu, sadece geliştirme amaçlı):

```
OSRM_URL=https://router.project-osrm.org
```

## 5. Günlük kullanım

```bash
docker compose up -d                 # sistemi başlat (.env'de COMPOSE_PROFILES=osrm varsa OSRM de başlar)
docker compose ps                    # neler çalışıyor?
docker compose logs -f backend       # backend loglarını izle
docker compose stop                  # durdur (veriler korunur)
```

### Kod değiştirdiğinizde

| Ne değişti | Ne yapmalı |
|---|---|
| `frontend/` içindeki dosyalar | Hiçbir şey. Tarayıcı otomatik yenilenir. |
| `backend/` içindeki Java dosyaları | `docker compose restart backend` (yaklaşık 20 sn) |
| Yeni veritabanı migration'ı (`V3__...sql`) | `docker compose restart backend`. Flyway otomatik uygular. |
| `pom.xml` (yeni Java kütüphanesi) | `docker compose restart backend` |
| Yeni npm paketi | `docker compose exec frontend npm install paket-adi` |

### Testleri çalıştırma

```bash
docker compose run --rm --no-deps backend mvn test
```

### Veritabanına komut satırından bağlanma

```bash
docker compose exec db psql -U kds -d tekstil_kds
# örnek: SELECT ad, ST_AsText(konum::geometry) FROM firma;
```

## 6. Veritabanı kuralları (önemli!)

- Şema **sadece Flyway migration dosyalarıyla** değişir: `backend/src/main/resources/db/migration/`.
- Daha önce çalışmış bir migration dosyası (`V1__`, `V2__`, ...) **asla düzenlenmez**. Değişiklik için yeni dosya eklenir:
  `V3__talep_tablosuna_oncelik_ekle.sql`.
- İki kişi aynı anda aynı numarayı kullanırsa çakışır. Numara almadan önce gruba yazın (ya da Öğrenci 2'ye sorun).
- Veritabanını tamamen sıfırlamak gerekirse (bütün veriler silinir!):

```bash
docker compose down
docker volume rm tekstil-kds_db_data
docker compose up -d
```

> `docker compose down -v` komutunu kullanmayın, çünkü OSRM verisini de siler ve yeniden hazırlamanız gerekir.

## 7. Docker kullanmadan (IDE'den) backend çalıştırma

İsteyen, Java 21 kurup backend'i IntelliJ veya VS Code'dan doğrudan çalıştırabilir (debug için rahattır):

```bash
docker compose up -d db            # sadece veritabanı Docker'da
docker compose stop backend        # Docker'daki backend'i durdur (port çakışmasın)
cd backend && ./mvnw spring-boot:run   # veya IDE'de TekstilKdsApplication'ı çalıştır
```

`application.yml`'deki varsayılanlar zaten `localhost:15432` veritabanına ve `localhost:15000` OSRM'ye bağlanır.
Frontend'i Docker dışında çalıştırmak için de `cd frontend && npm install && npm run dev` yeterlidir
(istekleri `localhost:18080`'e yönlendirir).

## 8. Sık karşılaşılan sorunlar

| Belirti | Çözüm |
|---|---|
| `port is already allocated` | `.env` dosyasında ilgili portu değiştirin |
| Backend açılmıyor | `docker compose logs backend`. Genelde Java derleme hatası veya hatalı migration'dır. |
| `Migration checksum mismatch` | Eski bir migration dosyası değiştirilmiş. Değişikliği geri alıp yeni migration yazın. |
| Haritada firma yok | Backend çalışıyor mu? http://localhost:18080/api/firmalar adresini deneyin |
| Rota düz kesikli çizgi | OSRM çalışmıyor (bkz. bölüm 4) |
| OSRM hazırlığı `Killed` ile bitiyor | Docker'a yeterli RAM verilmemiş (bkz. bölüm 1) |
| Docker çok yavaş | Docker Desktop > Settings > Resources. Projeyi WSL dosya sistemine klonlamak da hızlandırır. |
