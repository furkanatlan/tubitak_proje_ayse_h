import { useEffect, useState } from 'react'
import { api } from './api.js'
import Harita from './components/Harita.jsx'
import IlanPaneli from './components/IlanPaneli.jsx'

export default function App() {
  const [firmalar, setFirmalar] = useState([])
  const [ilanlar, setIlanlar] = useState([])
  const [seciliIlan, setSeciliIlan] = useState(null)
  const [oneriler, setOneriler] = useState([])
  const [seciliOneri, setSeciliOneri] = useState(null)
  const [rota, setRota] = useState(null)
  const [isiHaritasi, setIsiHaritasi] = useState(false)
  const [yogunluk, setYogunluk] = useState([])
  const [hata, setHata] = useState(null)

  useEffect(() => {
    Promise.all([api.firmalar(), api.ilanlar(), api.yogunluk()])
      .then(([f, i, y]) => { setFirmalar(f); setIlanlar(i); setYogunluk(y) })
      .catch((e) => setHata(e.message))
  }, [])

  async function ilanSec(ilan) {
    setSeciliIlan(ilan)
    setSeciliOneri(null)
    setRota(null)
    try {
      setOneriler(await api.eslestir(ilan.id))
    } catch (e) {
      setHata(e.message)
    }
  }

  async function oneriSec(oneri) {
    setSeciliOneri(oneri)
    try {
      setRota(await api.rota(seciliIlan.firma.id, oneri.talep.firma.id))
    } catch (e) {
      setHata(e.message)
    }
  }

  return (
    <div className="sayfa">
      <header>
        <h1>Tekstil Atıkları Akıllı Karar Destek Sistemi</h1>
        <label>
          <input type="checkbox" checked={isiHaritasi} onChange={(e) => setIsiHaritasi(e.target.checked)} />
          Atık yoğunluğu ısı haritası
        </label>
      </header>
      {hata && <div className="hata" onClick={() => setHata(null)}>Hata: {hata} (kapatmak için tıklayın)</div>}
      <main>
        <IlanPaneli
          ilanlar={ilanlar}
          seciliIlan={seciliIlan}
          onIlanSec={ilanSec}
          oneriler={oneriler}
          seciliOneri={seciliOneri}
          onOneriSec={oneriSec}
          rota={rota}
        />
        <Harita
          firmalar={firmalar}
          seciliIlan={seciliIlan}
          oneriler={oneriler}
          rota={rota}
          yogunluk={isiHaritasi ? yogunluk : null}
        />
      </main>
    </div>
  )
}
