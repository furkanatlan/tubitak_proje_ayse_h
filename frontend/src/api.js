// Backend REST API çağrıları. Uç noktaların tamamı: http://localhost:18080/swagger-ui.html

async function getir(yol) {
  const cevap = await fetch(`/api${yol}`)
  if (!cevap.ok) throw new Error(`${yol} -> HTTP ${cevap.status}`)
  return cevap.json()
}

export const api = {
  firmalar: () => getir('/firmalar'),
  atikTurleri: () => getir('/atik-turleri'),
  ilanlar: () => getir('/ilanlar'),
  talepler: () => getir('/talepler'),
  eslestir: (ilanId, enFazla = 3) => getir(`/eslestirme/ilan/${ilanId}?enFazla=${enFazla}`),
  rota: (kaynakFirmaId, hedefFirmaId) => getir(`/rota?kaynakFirmaId=${kaynakFirmaId}&hedefFirmaId=${hedefFirmaId}`),
  yogunluk: (atikTuruId) => getir(`/istatistik/yogunluk${atikTuruId ? `?atikTuruId=${atikTuruId}` : ''}`),
}
