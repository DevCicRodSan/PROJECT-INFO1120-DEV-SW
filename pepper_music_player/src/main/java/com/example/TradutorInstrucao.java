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
    private int oitavaBaseDaLinha;
    private String ultimaNota;
    private boolean anteriorEraNota;

    public List<List<EventoAudio>> traduzir(String texto) {
        List<List<EventoAudio>> trilhas = new ArrayList<>();
        if (texto == null || texto.isEmpty()) {
            return trilhas;
        }

        String[] linhas = texto.split("\n");

        for (int i = 0; i < linhas.length; i++) {
            int idVoz = i % 4; // Cicla entre 0 e 3
            resetarEstado(INSTRUMENTOS_BASE[idVoz], OITAVAS_BASE[idVoz], VOLUMES_BASE[idVoz]);
            trilhas.add(processarLinha(linhas[i]));
        }
        return trilhas;
    }

    private List<EventoAudio> processarLinha(String linha) {
        List<EventoAudio> eventos = new ArrayList<>();
        int i = 0;

        while (i < linha.length()) {
            char c = linha.charAt(i);
            String instrucao = String.valueOf(c);

            if (c >= 'A' && c <= 'G') {
                int duracao = 3; // Padrão: semínima
                
                // Look-ahead para acidente (# ou b)
                if (i + 1 < linha.length() && (linha.charAt(i + 1) == '#' || linha.charAt(i + 1) == 'b')) {
                    instrucao += linha.charAt(i + 1);
                    i++;
                }
                
                // Look-ahead para duração (:X)
                if (i + 2 < linha.length() && linha.charAt(i + 1) == ':' && Character.isDigit(linha.charAt(i + 2))) {
                    duracao = Character.getNumericValue(linha.charAt(i + 2));
                    i += 2;
                }

                eventos.add(new EventoAudio(instrucao, instrumentoAtual, oitavaAtual, volumeAtual, duracao));
                ultimaNota = instrucao;
                anteriorEraNota = true;
            } else if (c == '?') {
                // Sobe uma oitava relativa à oitava base da voz
                oitavaAtual = oitavaBaseDaLinha + 1;
                anteriorEraNota = false;
            } else if (c == 'V') {
                // Desce uma oitava relativa à oitava base da voz
                oitavaAtual = Math.max(1, oitavaBaseDaLinha - 1);
                anteriorEraNota = false;
            } else if (c == ' ') {
                // Dobra volume
                volumeAtual = Math.min(127, volumeAtual * 2);
                anteriorEraNota = false;
            } else {
                // Outras letras/consoantes (incluindo H): se anterior era nota, repete; senão pausa
                if (anteriorEraNota && instrucao.matches("[^OUIoui0-9?!;,\n<> ]")) {
                    eventos.add(new EventoAudio(ultimaNota, instrumentoAtual, oitavaAtual, volumeAtual, 3));
                } else {
                    anteriorEraNota = false;
                }
            }
            i++;
        }
        return eventos;
    }

    private void resetarEstado(int instrumento, int oitava, int volume) {
        this.instrumentoAtual = instrumento;
        this.oitavaAtual = oitava;
        this.oitavaBaseDaLinha = oitava;
        this.volumeAtual = volume;
        this.anteriorEraNota = false;
        this.ultimaNota = PAUSA;
    }
}
