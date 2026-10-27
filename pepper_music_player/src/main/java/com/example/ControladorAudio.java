package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ControladorAudio {
    private TradutorInstrucao tradutor;
    private TocadorAudio tocador;

    // Valores globais padrão (podem ser alterados pela interface futuramente)
    private int bpmGlobal = 120;

    // Construtor
    public ControladorAudio() {
        this.tradutor = new TradutorInstrucao();
        this.tocador = new TocadorAudio();
        this.tocador.definirBpm(this.bpmGlobal);
    }

    public void tocarTexto(String texto) {
        if (texto == null || texto.isEmpty())
            return;
        List<List<EventoAudio>> trilhas = tradutor.traduzir(texto);
        tocador.tocarSequencia(trilhas);
    }

    public void exportarMidi(String texto, String caminhoArquivo) {
        if (texto == null || texto.isEmpty())
            return;
        List<List<EventoAudio>> trilhas = tradutor.traduzir(texto);
        tocador.exportarMidi(trilhas, caminhoArquivo);
    }

    public String carregarArquivoTxt(File arquivo) throws IOException {
        return Files.readString(arquivo.toPath());
    }

    public void salvarArquivoTxt(String texto, File arquivo) throws IOException {
        Files.writeString(arquivo.toPath(), texto);
    }
}