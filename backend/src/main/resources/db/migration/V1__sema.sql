-- Temel şema. Bu dosya bir kez çalıştırıldıktan sonra DEĞİŞTİRİLMEZ;
-- yeni değişiklikler için V3__..., V4__... şeklinde yeni migration eklenir.

CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE atik_turu (
    id                      SERIAL PRIMARY KEY,
    kod                     VARCHAR(40)  NOT NULL UNIQUE,
    ad                      VARCHAR(120) NOT NULL,
    aciklama                TEXT,
    bertaraf_maliyeti_tl_ton NUMERIC(12,2) NOT NULL DEFAULT 0, -- atık verenin çöpe/yakmaya ödediği
    hammadde_degeri_tl_ton   NUMERIC(12,2) NOT NULL DEFAULT 0, -- alıcının ham madde yerine kullandığında kazandığı
    co2_tasarruf_kg_ton      NUMERIC(12,2) NOT NULL DEFAULT 0  -- geri dönüşümün sağladığı emisyon tasarrufu
);

CREATE TABLE firma (
    id          BIGSERIAL PRIMARY KEY,
    ad          VARCHAR(200) NOT NULL,
    rol         VARCHAR(20)  NOT NULL CHECK (rol IN ('ATIK_VEREN', 'ALICI', 'HER_IKISI')),
    sektor      VARCHAR(120),
    il          VARCHAR(60)  NOT NULL,
    ilce        VARCHAR(60),
    bolge       VARCHAR(120),           -- örn. Nilüfer OSB
    enlem       DOUBLE PRECISION NOT NULL,
    boylam      DOUBLE PRECISION NOT NULL,
    konum       GEOGRAPHY(Point, 4326)
                GENERATED ALWAYS AS (ST_SetSRID(ST_MakePoint(boylam, enlem), 4326)::geography) STORED,
    telefon     VARCHAR(30),
    eposta      VARCHAR(120),
    olusturma_zamani TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX firma_konum_gix ON firma USING GIST (konum);

-- Arz: bir firmanın elindeki atık
CREATE TABLE atik_ilani (
    id           BIGSERIAL PRIMARY KEY,
    firma_id     BIGINT  NOT NULL REFERENCES firma(id),
    atik_turu_id INTEGER NOT NULL REFERENCES atik_turu(id),
    miktar_kg    NUMERIC(12,2) NOT NULL CHECK (miktar_kg > 0),
    periyot      VARCHAR(20) NOT NULL DEFAULT 'TEK_SEFERLIK'
                 CHECK (periyot IN ('TEK_SEFERLIK', 'HAFTALIK', 'AYLIK')),
    durum        VARCHAR(20) NOT NULL DEFAULT 'AKTIF' CHECK (durum IN ('AKTIF', 'ESLESTI', 'KAPALI')),
    aciklama     TEXT,
    olusturma_zamani TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Talep: bir firmanın ham madde olarak almak istediği atık
CREATE TABLE talep (
    id            BIGSERIAL PRIMARY KEY,
    firma_id      BIGINT  NOT NULL REFERENCES firma(id),
    atik_turu_id  INTEGER NOT NULL REFERENCES atik_turu(id),
    miktar_kg     NUMERIC(12,2) NOT NULL CHECK (miktar_kg > 0),
    max_mesafe_km NUMERIC(8,1)  NOT NULL DEFAULT 300,
    durum         VARCHAR(20) NOT NULL DEFAULT 'AKTIF' CHECK (durum IN ('AKTIF', 'ESLESTI', 'KAPALI')),
    aciklama      TEXT,
    olusturma_zamani TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Kullanıcının onayladığı / reddettiği eşleştirme kararları (karar destek geçmişi)
CREATE TABLE eslestirme (
    id               BIGSERIAL PRIMARY KEY,
    ilan_id          BIGINT NOT NULL REFERENCES atik_ilani(id),
    talep_id         BIGINT NOT NULL REFERENCES talep(id),
    mesafe_km        NUMERIC(10,2),
    sure_dk          NUMERIC(10,1),
    skor             NUMERIC(6,4),
    net_fayda_tl     NUMERIC(14,2),
    net_co2_kg       NUMERIC(14,2),
    durum            VARCHAR(20) NOT NULL DEFAULT 'ONERILDI' CHECK (durum IN ('ONERILDI', 'KABUL', 'RED')),
    olusturma_zamani TIMESTAMPTZ NOT NULL DEFAULT now()
);
