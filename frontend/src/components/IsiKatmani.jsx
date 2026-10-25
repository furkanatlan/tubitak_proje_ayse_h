import { useEffect } from 'react'
import { useMap } from 'react-leaflet'
import L from 'leaflet'
import 'leaflet.heat'

/** leaflet.heat eklentisini React'e bağlayan küçük bileşen. Ağırlık = toplam atık miktarı (kg). */
export default function IsiKatmani({ noktalar }) {
  const harita = useMap()

  useEffect(() => {
    const enBuyuk = Math.max(1, ...noktalar.map((n) => n.toplamKg))
    const katman = L.heatLayer(
      noktalar.map((n) => [n.enlem, n.boylam, n.toplamKg / enBuyuk]),
      { radius: 35, blur: 25, maxZoom: 11 },
    ).addTo(harita)
    return () => { harita.removeLayer(katman) }
  }, [noktalar, harita])

  return null
}
