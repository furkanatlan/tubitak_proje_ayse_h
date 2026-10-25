package tr.tekstilkds.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tr.tekstilkds.dto.Dtolar.*;
import tr.tekstilkds.service.EslestirmeServisi;
import tr.tekstilkds.service.KayitServisi;
import tr.tekstilkds.service.RotaServisi;

/**
 * REST API. Tüm uç noktalar /api altındadır; belgeler: http://localhost:18080/swagger-ui.html
 * TODO (Öğrenci 1): Kaynak sayısı arttıkça bu sınıf FirmaController, IlanController ... olarak bölünecek.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final KayitServisi kayit;
    private final EslestirmeServisi eslestirme;
    private final RotaServisi rota;

    public ApiController(KayitServisi kayit, EslestirmeServisi eslestirme, RotaServisi rota) {
        this.kayit = kayit;
        this.eslestirme = eslestirme;
        this.rota = rota;
    }

    @GetMapping("/firmalar")
    public List<FirmaDto> firmalar() {
        return kayit.firmalar();
    }

    @GetMapping("/firmalar/{id}")
    public FirmaDto firma(@PathVariable long id) {
        return FirmaDto.from(kayit.firma(id));
    }

    @PostMapping("/firmalar")
    @ResponseStatus(HttpStatus.CREATED)
    public FirmaDto firmaOlustur(@Valid @RequestBody FirmaIstek istek) {
        return kayit.firmaOlustur(istek);
    }

    @GetMapping("/atik-turleri")
    public List<AtikTuruDto> atikTurleri() {
        return kayit.atikTurleri();
    }

    @GetMapping("/ilanlar")
    public List<IlanDto> ilanlar() {
        return kayit.ilanlar();
    }

    @PostMapping("/ilanlar")
    @ResponseStatus(HttpStatus.CREATED)
    public IlanDto ilanOlustur(@Valid @RequestBody IlanIstek istek) {
        return kayit.ilanOlustur(istek);
    }

    @GetMapping("/talepler")
    public List<TalepDto> talepler() {
        return kayit.talepler();
    }

    @PostMapping("/talepler")
    @ResponseStatus(HttpStatus.CREATED)
    public TalepDto talepOlustur(@Valid @RequestBody TalepIstek istek) {
        return kayit.talepOlustur(istek);
    }

    @Operation(summary = "Bir atık ilanı için en uygun alıcı firmaları skorlayarak önerir")
    @GetMapping("/eslestirme/ilan/{ilanId}")
    public List<EslestirmeOnerisiDto> eslestir(@PathVariable long ilanId,
                                               @RequestParam(defaultValue = "3") int enFazla) {
        return eslestirme.oner(ilanId, enFazla);
    }

    @Operation(summary = "İki firma arasındaki karayolu rotası (harita çizimi için geometri içerir)")
    @GetMapping("/rota")
    public RotaDto rota(@RequestParam long kaynakFirmaId, @RequestParam long hedefFirmaId) {
        return rota.hesapla(kayit.firma(kaynakFirmaId), kayit.firma(hedefFirmaId), true);
    }

    @Operation(summary = "Isı haritası için firma konumlarındaki aktif atık miktarları")
    @GetMapping("/istatistik/yogunluk")
    public List<YogunlukDto> yogunluk(@RequestParam(required = false) Integer atikTuruId) {
        return kayit.yogunluk(atikTuruId);
    }
}
