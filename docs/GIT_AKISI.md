# Git ve GitHub Çalışma Düzeni

Dört kişi aynı depoda çalışırken birbirimizin kodunu bozmamak için şu kurallara uyuyoruz.

## Altın kurallar

1. **`main` dalına doğrudan push yapılmaz.** Her iş kendi dalında yapılır ve Pull Request (PR) ile birleştirilir.
2. Her PR'ı **en az bir başka öğrenci** inceler ve onaylar.
3. Küçük ve sık commit. "Bir haftalık iş tek commit" olmasın.
4. İşe başlamadan önce `main`'i güncelleyin.

## Bir işin baştan sona akışı

```bash
# 1) main'i güncelle
git checkout main
git pull

# 2) Yeni dal aç. Ad: <öğrenci-no>/<kısa-açıklama>
git checkout -b ogr1/firma-guncelleme

# 3) Kodla, test et, commit'le
git add .
git commit -m "Firma güncelleme uç noktası eklendi"

# 4) GitHub'a gönder
git push -u origin ogr1/firma-guncelleme
```

5) GitHub'da **"Compare & pull request"** butonuna basın. Açıklamaya ne yaptığınızı ve nasıl test edilebileceğini yazın, bir arkadaşınızı *Reviewer* olarak ekleyin.
6) Onay gelince **"Squash and merge"** ile birleştirin ve dalı silin.

### Dal adı örnekleri

| Öğrenci | Örnek |
|---|---|
| Öğrenci 1 | `ogr1/jwt-giris`, `ogr1/ilan-sayfalama` |
| Öğrenci 2 | `ogr2/bursa-osb-verisi`, `ogr2/bolgesel-yogunluk-sorgusu` |
| Öğrenci 3 | `ogr3/ahp-agirliklar`, `ogr3/coklu-eslestirme` |
| Öğrenci 4 | `ogr4/ilan-formu`, `ogr4/dashboard` |

## Çakışma (conflict) olursa

```bash
git checkout main && git pull
git checkout benim-dalim
git merge main            # çakışan dosyaları VS Code'da düzeltin
git add . && git commit
git push
```

Emin değilseniz çakışan kodun sahibine sorun.

## İş takibi

- Görevler GitHub **Issues** olarak açılır ve [GOREV_DAGILIMI.md](GOREV_DAGILIMI.md)'deki maddelerden gelir.
- GitHub **Projects** panosunda üç sütun kullanılır: *Yapılacak, Yapılıyor, Bitti*.
- PR açıklamasına `Closes #12` yazarsanız PR birleşince ilgili issue otomatik kapanır.

## Repoya girmemesi gerekenler

`.gitignore` bunları zaten dışarıda tutar, ama yine de dikkat edin: `.env` dosyası (şifreler),
`node_modules/`, `target/`, harita dosyaları (`*.osm.pbf`) ve kişisel veriler (gerçek firma iletişim bilgileri).
