#!/bin/sh
# OSM verisini OSRM'nin MLD algoritması için işler (extract -> partition -> customize).
# Türkiye verisi için ~8 GB RAM ve 10-20 dk sürer. Sadece bir kez çalıştırılır.
set -e

if [ -f /data/harita.osrm.mldgr ]; then
  echo "OSRM verisi zaten hazır, atlanıyor. Yeniden üretmek için osrm_data volume'unu silin."
  exit 0
fi

echo ">> osrm-extract (araç profili)..."
osrm-extract -p /opt/car.lua /data/harita.osm.pbf
echo ">> osrm-partition..."
osrm-partition /data/harita.osrm
echo ">> osrm-customize..."
osrm-customize /data/harita.osrm
echo ">> Tamamlandı. Sunucuyu başlatmak için: docker compose --profile osrm up -d"
