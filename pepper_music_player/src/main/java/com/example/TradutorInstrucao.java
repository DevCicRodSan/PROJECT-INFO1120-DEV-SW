package com.example;

import java.util.ArrayList;
import java.util.List;

public class TradutorInstrucao {
    private static final String PAUSA = "R";
    
    // Configurações base das 4 vozes
    private static final int[] OITAVAS_BASE = {6, 5, 4, 3};
    private static final int[] VOLUMES_BASE = {100, 80, 60, 40};
    private static final int[] INSTRUMENTOS_BASE = {6, 20, 0, 70}; 

    private int instrumentoAtual;
    private int oitavaAtual;
    private int volumeAtual;
    private String ultimaNota;
    private boolean anteriorEraNota;

    public List<List<EventoAudio>> traduzir(String texto) {
        List<List<EventoAudio>> trilhas = new ArrayList<>();
        if (texto == null || texto.isEmpty()) {
            return trilhas;
        }

        String[] linhas = texto.split("\n");

        for (int i = 0; i < linhas.length; i++) {
            int idVoz = i % 4; // Cicla entre as 4 vozes base
            resetarEstado(INSTRUMENTOS_BASE[idVoz], OITAVAS_BASE[idVoz], VOLUMES_BASE[idVoz]);
            trilhas.add(processarLinha(linhas[i]));
        }
        return trilhas;
    }

    private List<EventoAudio> processarLinha(String linha) {
        List<EventoAudio> eventos = new ArrayList<>();
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            String instrucao = String.valueOf(c);

            if (c >= 'A' && c <= 'G') {
                eventos.add(new EventoAudio(instrucao, instrumentoAtual, oitavaAtual, volumeAtual, 3));
                ultimaNota = instrucao;
                anteriorEraNota = true;
            } else {
                if (anteriorEraNota && instrucao.matches("[^OUIoui0-9?!;,\n<> ]")) {
                    eventos.add(new EventoAudio(ultimaNota, instrumentoAtual, oitavaAtual, volumeAtual, 3));
                } else {
                    anteriorEraNota = false;
                }
            }
        }
        return eventos;
    }

    private void resetarEstado(int instrumento, int oitava, int volume) {
        this.instrumentoAtual = instrumento;
        this.oitavaAtual = oitava;
        this.volumeAtual = volume;
        this.anteriorEraNota = false;
        this.ultimaNota = PAUSA;
    }
}
