package com.example.sistemadecomandas.Modelos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArchivosAdjuntosPorComanda {
    private Map<String, String> urlsImagenes;

    public ArchivosAdjuntosPorComanda() {}

    public Map<String, String> getUrlsImagenes() {
        return urlsImagenes;
    }

    public void setUrlsImagenes(Map<String, String> urlsImagenes) {
        this.urlsImagenes = urlsImagenes;
    }

    public List<String> getListaUrls() {
        return new ArrayList<>(urlsImagenes.values());
    }
}
