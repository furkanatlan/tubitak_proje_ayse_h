import { useEffect } from 'react'
import { CircleMarker, MapContainer, Polyline, Popup, TileLayer, useMap } from 'react-leaflet'
import IsiKatmani from './IsiKatmani.jsx'

const BURSA = [40.19, 29.06]

const ROL_RENGI = {
  ATIK_VEREN: '#d9480f', // turuncu: atık veren
  ALICI: '#2b8a3e',      // yeşil: geri dönüşümcü / alıcı
  HER_IKISI: '#5f3dc4',  // mor
}

function RotayaOdaklan({ rota }) {
  const harita = useMap()
  useEffect(() => {
    if (rota?.geometri?.length) harita.fitBounds(rota.geometri, { padding: [40, 40] })
  }, [rota, harita])
  return null
}

export default function Harita({ firmalar, seciliIlan, oneriler, rota, yogunluk }) {
  const oneriFirmaIdleri = new Set(oneriler.map((o) => o.talep.firma.id))

  return (
    <MapContainer center={BURSA} zoom={9} className="harita">
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> katkıcıları'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      {firmalar.map((f) => {
        const vurgulu = f.id === seciliIlan?.firma.id || oneriFirmaIdleri.has(f.id)
        return (
          <CircleMarker
            key={f.id}
            center={[f.enlem, f.boylam]}
            radius={vurgulu ? 11 : 7}
            pathOptions={{ color: ROL_RENGI[f.rol], weight: vurgulu ? 4 : 2, fillOpacity: 0.7 }}
          >
            <Popup>
              <b>{f.ad}</b><br />
              {f.sektor} — {f.ilce}/{f.il}<br />
              {f.bolge && <>{f.bolge}<br /></>}
              Rol: {f.rol}
            </Popup>
          </CircleMarker>
        )
      })}

      {rota && (
        <Polyline
          positions={rota.geometri}
          pathOptions={{ color: '#1971c2', weight: 5, dashArray: rota.kaynak === 'TAHMIN' ? '8 8' : null }}
        />
      )}
      <RotayaOdaklan rota={rota} />

      {yogunluk && <IsiKatmani noktalar={yogunluk} />}
    </MapContainer>
  )
}
