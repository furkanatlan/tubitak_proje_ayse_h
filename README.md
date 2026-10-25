# Tekstil Atıkları Akıllı Karar Destek Sistemi

**TÜBİTAK 2209-A**: *Tekstil Atıklarının Döngüsel Ekonomiye Kazandırılması İçin Lojistik ve
Endüstriyel Simbiyoz Odaklı Akıllı Karar Destek Sistemi Geliştirilmesi*

Atık üreten tekstil firmalarını (arz) bu atığı ham madde olarak kullanabilecek firmalarla (talep)
eşleştirir. Her öneri için gerçek karayolu mesafesini, lojistik maliyeti, ekonomik tasarrufu ve
karbon etkisini hesaplar, sonuçları harita üzerinde gösterir.

## Hızlı başlangıç

```bash
git clone https://github.com/furkanatlan/tubitak_proje_ayse_h.git
cd tubitak_proje_ayse_h
docker compose up -d
```

Sonra tarayıcıda **http://localhost:15173** adresini açın.

**Öğrenciler:** Önce [docs/GOREV_DAGILIMI.md](docs/GOREV_DAGILIMI.md) dosyasındaki **Adım 0**'ı baştan sona uygulayın.
Program kurulumu, klonlama, Docker ve gerçek yol haritası (OSRM) kurulumu orada adım adım anlatılıyor.
Günlük komutlar ve sorun giderme için: [docs/KURULUM.md](docs/KURULUM.md)

## Dokümanlar

| Doküman | İçerik |
|---|---|
| [docs/KURULUM.md](docs/KURULUM.md) | Docker kurulumu, adresler, günlük komutlar, sorun giderme |
| [docs/GOREV_DAGILIMI.md](docs/GOREV_DAGILIMI.md) | 4 öğrencinin iş paketleri, zaman planı, ek özellik kararları |
| [docs/GIT_AKISI.md](docs/GIT_AKISI.md) | Dal / Pull Request kuralları |

## Mimari

```
 Tarayıcı (React + Leaflet)  :15173
          │  /api/*
          ▼
 Backend (Spring Boot 3, Java 21)  :18080 ──────► OSRM (yol ağı, Türkiye)  :15000
          │  JPA + native SQL
          ▼
 PostgreSQL 16 + PostGIS 3.4  :15432
```

## Teknoloji seçimleri

| Katman | Teknoloji | Sürüm | Neden |
|---|---|---|---|
| Backend | Java + Spring Boot | 21 / 3.5 | Kurumsal düzeyde kararlı, iş kuralları ve REST API için standart |
| API dokümantasyonu | springdoc-openapi (Swagger UI) | 2.8 | Frontend ile backend arasında ortak sözleşme |
| Veritabanı | PostgreSQL + PostGIS | 16 / 3.4 | İlişkisel veri ve coğrafi sorgular (yarıçap, mesafe, kümeleme) |
| Şema yönetimi | Flyway | | Şema değişikliklerinin sürüm kontrolü; 4 kişi aynı şemada çakışmadan çalışır |
| Rota / mesafe | OSRM (MLD algoritması) | 5.27 | Açık kaynak, ücretsiz, Türkiye OSM verisiyle yerelde çalışır |
| Arayüz | React + Vite | 18 / 5 | Bileşen tabanlı, hızlı geliştirme, geniş topluluk |
| Harita | Leaflet.js + react-leaflet + leaflet.heat | 1.9 | Hafif ve açık kaynak; ısı haritası eklentisi hazır |
| Altyapı | Docker Compose | | Tek komutla aynı ortam; kimsenin bilgisayarına Java/Postgres kurulmaz |

**İleride eklenecekler:** Spring Security + JWT (giriş), Testcontainers (entegrasyon testi),
Google OR-Tools (çoklu eşleştirme optimizasyonu), Recharts (dashboard grafikleri).

## Mevcut API (iskelet)

| Metot | Yol | Açıklama |
|---|---|---|
| GET/POST | `/api/firmalar` | Firmalar |
| GET | `/api/atik-turleri` | Atık türleri ve maliyet/CO2 katsayıları |
| GET/POST | `/api/ilanlar` | Atık ilanları (arz) |
| GET/POST | `/api/talepler` | Ham madde talepleri |
| GET | `/api/eslestirme/ilan/{id}?enFazla=3` | Bir ilan için skorlanmış alıcı önerileri (mesafe, TL, CO2) |
| GET | `/api/rota?kaynakFirmaId=&hedefFirmaId=` | Haritada çizilecek yol geometrisi |
| GET | `/api/istatistik/yogunluk?atikTuruId=` | Isı haritası verisi |

> Veritabanındaki firmalar ve katsayılar **örnek (hayali) verilerdir**. Gerçek veriler Öğrenci 2 tarafından eklenecektir.

## Klasör yapısı

```
backend/                       Spring Boot uygulaması
  src/main/java/tr/tekstilkds/
    controller/                REST uç noktaları
    service/                   İş kuralları: eşleştirme, rota, kayıt
    repository/                Veritabanı erişimi (PostGIS sorguları burada)
    entity/  dto/  config/
  src/main/resources/db/migration/   Flyway SQL dosyaları (V1, V2, ...)
frontend/                      React + Leaflet arayüzü
  src/components/              Harita, IlanPaneli, IsiKatmani
infra/                         OSRM hazırlık betiği, pgAdmin ayarı
docs/                          Kurulum, görev dağılımı, git akışı
docker-compose.yml
```
