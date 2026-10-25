-- ÖRNEK (HAYALİ) VERİ. Firma adları uydurmadır, koordinatlar bölgeleri yaklaşık gösterir.
-- Maliyet / CO2 katsayıları yer tutucudur; Öğrenci 2 literatür ve saha verisiyle güncelleyecek (yeni migration ile).

INSERT INTO atik_turu (kod, ad, aciklama, bertaraf_maliyeti_tl_ton, hammadde_degeri_tl_ton, co2_tasarruf_kg_ton) VALUES
('PAMUK_KIRPINTI',  'Pamuk kırpıntı',            'Konfeksiyon kesim artığı, %100 pamuk',      1500, 9000, 3000),
('POLYESTER_KIRP',  'Polyester kırpıntı',        'Sentetik kumaş kesim artığı',               1500, 6500, 2500),
('KARISIK_KIRP',    'Karışık elyaf kırpıntı',    'Pamuk/polyester karışım',                   1500, 4000, 1800),
('DENIM_KIRP',      'Denim kırpıntı',            'İndigo boyalı pamuk',                       1500, 8000, 2800),
('IPLIK_ATIGI',     'İplik / bobin atığı',       'İplikhane ve dokuma iplik artıkları',       1200, 7000, 2600),
('KUMAS_TOP_SONU',  'Top sonu / defolu kumaş',   'Satılamayan top sonları',                   1000, 12000, 3500);

INSERT INTO firma (ad, rol, sektor, il, ilce, bolge, enlem, boylam) VALUES
('Örnek Konfeksiyon A.Ş.',      'ATIK_VEREN', 'Hazır giyim',        'Bursa', 'Nilüfer',  'Nilüfer OSB',   40.2260, 28.8700),
('Deneme Dokuma San.',          'ATIK_VEREN', 'Dokuma',             'Bursa', 'Osmangazi','Bursa OSB',     40.2330, 28.9930),
('Mavi Denim Tekstil',          'ATIK_VEREN', 'Denim',              'Bursa', 'Osmangazi','Demirtaş OSB',  40.2680, 29.0800),
('Kayapa İplik Ltd.',           'HER_IKISI',  'İplik',              'Bursa', 'Nilüfer',  'Kayapa',        40.2200, 28.8350),
('İnegöl Ev Tekstili',          'ATIK_VEREN', 'Ev tekstili',        'Bursa', 'İnegöl',   'İnegöl OSB',    40.0980, 29.4700),
('Gürsu Geri Dönüşüm İplik',    'ALICI',      'Rejenere iplik',     'Bursa', 'Gürsu',    NULL,            40.2180, 29.1800),
('Gemlik Yalıtım Malzemeleri',  'ALICI',      'Yalıtım',            'Bursa', 'Gemlik',   NULL,            40.4300, 29.1600),
('Uşak Rejenere Elyaf',         'ALICI',      'Rejenere elyaf',     'Uşak',  'Merkez',   'Uşak OSB',      38.6480, 29.3600),
('Gebze Otomotiv Keçe',         'ALICI',      'Otomotiv keçe',      'Kocaeli','Gebze',   'Gebze OSB',     40.8200, 29.4500);

INSERT INTO atik_ilani (firma_id, atik_turu_id, miktar_kg, periyot) VALUES
(1, (SELECT id FROM atik_turu WHERE kod='PAMUK_KIRPINTI'), 500,  'HAFTALIK'),
(1, (SELECT id FROM atik_turu WHERE kod='KARISIK_KIRP'),   800,  'AYLIK'),
(2, (SELECT id FROM atik_turu WHERE kod='POLYESTER_KIRP'), 1200, 'AYLIK'),
(3, (SELECT id FROM atik_turu WHERE kod='DENIM_KIRP'),     2000, 'AYLIK'),
(4, (SELECT id FROM atik_turu WHERE kod='IPLIK_ATIGI'),    700,  'HAFTALIK'),
(5, (SELECT id FROM atik_turu WHERE kod='KUMAS_TOP_SONU'), 300,  'TEK_SEFERLIK'),
(5, (SELECT id FROM atik_turu WHERE kod='PAMUK_KIRPINTI'), 900,  'AYLIK');

INSERT INTO talep (firma_id, atik_turu_id, miktar_kg, max_mesafe_km) VALUES
(6, (SELECT id FROM atik_turu WHERE kod='PAMUK_KIRPINTI'), 2000, 100),
(6, (SELECT id FROM atik_turu WHERE kod='DENIM_KIRP'),     1500, 100),
(7, (SELECT id FROM atik_turu WHERE kod='KARISIK_KIRP'),   3000, 150),
(7, (SELECT id FROM atik_turu WHERE kod='POLYESTER_KIRP'), 2000, 150),
(8, (SELECT id FROM atik_turu WHERE kod='PAMUK_KIRPINTI'), 10000, 500),
(8, (SELECT id FROM atik_turu WHERE kod='DENIM_KIRP'),     8000, 500),
(9, (SELECT id FROM atik_turu WHERE kod='KARISIK_KIRP'),   5000, 250),
(9, (SELECT id FROM atik_turu WHERE kod='POLYESTER_KIRP'), 4000, 250),
(4, (SELECT id FROM atik_turu WHERE kod='PAMUK_KIRPINTI'), 600,  50);
