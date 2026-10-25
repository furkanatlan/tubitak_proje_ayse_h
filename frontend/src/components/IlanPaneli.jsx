const tl = (x) => x.toLocaleString('tr-TR', { maximumFractionDigits: 0 }) + ' ₺'
const sayi = (x, basamak = 1) => x.toLocaleString('tr-TR', { maximumFractionDigits: basamak })

export default function IlanPaneli({ ilanlar, seciliIlan, onIlanSec, oneriler, seciliOneri, onOneriSec, rota }) {
  return (
    <aside className="panel">
      <h2>Atık ilanları</h2>
      <ul className="liste">
        {ilanlar.map((i) => (
          <li key={i.id} className={i.id === seciliIlan?.id ? 'secili' : ''} onClick={() => onIlanSec(i)}>
            <b>{i.atikTuru.ad}</b> — {sayi(i.miktarKg, 0)} kg
            <small>{i.firma.ad} · {i.periyot}</small>
          </li>
        ))}
      </ul>

      {seciliIlan && (
        <>
          <h2>Önerilen alıcılar</h2>
          {oneriler.length === 0 && <p>Bu ilan için uygun talep bulunamadı.</p>}
          {oneriler.map((o, sira) => (
            <div
              key={o.talep.id}
              className={'kart' + (o === seciliOneri ? ' secili' : '')}
              onClick={() => onOneriSec(o)}
            >
              <div className="kart-baslik">
                <span>{sira + 1}. {o.talep.firma.ad}</span>
                <span className="skor">{sayi(o.skor * 100, 0)}</span>
              </div>
              <small>{o.talep.firma.sektor} · {o.talep.firma.ilce}/{o.talep.firma.il}</small>
              <table>
                <tbody>
                  <tr><td>Yol mesafesi</td><td>{sayi(o.yolMesafeKm)} km ({o.mesafeKaynagi})</td></tr>
                  <tr><td>Süre</td><td>{sayi(o.sureDk, 0)} dk</td></tr>
                  <tr><td>Taşınan</td><td>{sayi(o.tasinanTon, 2)} ton</td></tr>
                  <tr><td>Taşıma maliyeti</td><td>{tl(o.tasimaMaliyetiTl)}</td></tr>
                  <tr><td>Bertaraf tasarrufu</td><td>{tl(o.bertarafTasarrufuTl)}</td></tr>
                  <tr><td>Hammadde tasarrufu</td><td>{tl(o.hammaddeTasarrufuTl)}</td></tr>
                  <tr className="vurgu"><td>Net fayda</td><td>{tl(o.netFaydaTl)}</td></tr>
                  <tr className="vurgu"><td>Net CO₂ tasarrufu</td><td>{sayi(o.netCo2TasarrufuKg, 0)} kg</td></tr>
                </tbody>
              </table>
            </div>
          ))}
          {rota && seciliOneri && (
            <p className="not">
              Haritadaki rota: {sayi(rota.mesafeKm)} km, {sayi(rota.sureDk, 0)} dk
              {rota.kaynak === 'TAHMIN' && ' (OSRM kapalı — kesikli çizgi kuş uçuşu tahminidir)'}
            </p>
          )}
        </>
      )}
    </aside>
  )
}
