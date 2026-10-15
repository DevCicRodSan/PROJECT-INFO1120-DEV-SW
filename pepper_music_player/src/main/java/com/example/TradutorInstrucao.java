package com.example;

import java.util.ArrayList;
import java.util.List;

public class TradutorInstrucao {
    // Pausa no padrão do JFugue
    private static final String PAUSA = "R";

    // Mapeamento de valores
    private static final int VOLUME_MAXIMO = 100;
    private static final int OITAVA_PADRAO = 5;
    private static final int OITAVA_MAXIMA = 9;

    // Mapear instrumentos que possuem instrucoes proprias
    private static final int TOTAL_INSTRUMENTOS = 128;
    private static final int ONDAS_DO_MAR = 123;
    private static final int TUBULAR_BELLS = 15;
    private static final int AGOGO = 114;
    private static final int BANDONEON = 24;
    private static final int GAITA_DE_FOLES = 110;

    // Mudar isso para uma Classe EstadoAtual?!
    String ultimaNota;
    int volumeAtual;
    int oitavaAtual;
    int instrumentoAtual;
    boolean anteriorEraNota;

    // Cria uma lista de EventosAudio para serem tocados
    public List<EventoAudio> traduzir(String texto, int instrumentoInicial, int oitava, int volume) {
        resetarEstado(instrumentoInicial, oitava, volume);
        List<EventoAudio> listaEventos = new ArrayList<>();
        EventoAudio evento;
        for (char c : texto.toCharArray()) {
            String instrucao = String.valueOf(c);
            evento = traduzirInstrucao(instrucao);
            // null = a instrução só alterou o estado, não gera som
            if (evento != null) {
                listaEventos.add(evento);
            }
        }

        return listaEventos;
    }

    // Recebe a string que simboliza a instrucao para criar um evento de audio
    private EventoAudio traduzirInstrucao(String instrucao) {
        EventoAudio evento = null;
        boolean instrucaoEraNota = false;

        switch (instrucao) {
            case "A":
            case "B":
            case "C":
            case "D":
            case "E":
            case "F":
            case "G":
                evento = criarEventoNota(instrucao);
                instrucaoEraNota = true;
                break;

            case "H":
                evento = criarEventoNota("Bb");
                instrucaoEraNota = true;
                break;

            case "a":
            case "b":
            case "c":
            case "d":
            case "e":
            case "f":
            case "g":
            case "h":
                evento = criarEventoNota(PAUSA);
                break;

            case " ":
                dobrarVolume();
                break;

            case "!":
                trocarInstrumento(BANDONEON);
                break;

            case "O":
            case "o":
            case "I":
            case "i":
            case "U":
            case "u":
                trocarInstrumento(GAITA_DE_FOLES);
                break;

            case "0":
            case "2":
            case "4":
            case "6":
            case "8":
                int digito = Integer.parseInt(instrucao);
                trocarInstrumento(instrumentoAtual + digito);
                break;

            case "?":
            case ".":
                subirOitava();
                break;

            case "\n":
                trocarInstrumento(ONDAS_DO_MAR);
                break;

            case ";":
            case "1":
            case "3":
            case "5":
            case "7":
            case "9":
                trocarInstrumento(TUBULAR_BELLS);
                break;

            case ",":
                trocarInstrumento(AGOGO);
                break;

            // Outras consoantes e qualquer outro caractere
            default:
                if (anteriorEraNota) {
                    evento = criarEventoNota(ultimaNota);
                    instrucaoEraNota = true;
                } else {
                    evento = criarEventoNota(PAUSA);
                }
                break;
        }

        anteriorEraNota = instrucaoEraNota;
        return evento;
    }

    private EventoAudio criarEventoNota(String nota) {
        EventoAudio evento = new EventoAudio(nota, instrumentoAtual, oitavaAtual, volumeAtual);
        if (!nota.equals(PAUSA)) {
            ultimaNota = nota;
        }

        return evento;
    }

    private void resetarEstado(int instrumento, int oitava, int volume) {
        instrumentoAtual = instrumento;
        oitavaAtual = oitava;
        volumeAtual = volume;
        anteriorEraNota = false;
        ultimaNota = null;
    }

    // Dobra o volume; se passar do máximo, fica no máximo
    private void dobrarVolume() {
        int novoVolume = volumeAtual * 2;
        if (novoVolume > VOLUME_MAXIMO) {
            novoVolume = VOLUME_MAXIMO;
        }
        volumeAtual = novoVolume;
    }

    // Sobe uma oitava; se não puder, volta à oitava padrão
    private void subirOitava() {
        if (oitavaAtual < OITAVA_MAXIMA) {
            oitavaAtual = oitavaAtual + 1;
        } else {
            oitavaAtual = OITAVA_PADRAO;
        }
    }

    // Se passar do último instrumento, recomeça do primeiro
    private void trocarInstrumento(int numero) {
        if (numero > TOTAL_INSTRUMENTOS) {
            numero = numero - TOTAL_INSTRUMENTOS;
        }
        instrumentoAtual = numero;
    }

    public String getUltimaNota() {
        return ultimaNota;
    }

    public void setUltimaNota(String ultimaNota) {
        this.ultimaNota = ultimaNota;
    }

    public int getVolumeAtual() {
        return volumeAtual;
    }

    public void setVolumeAtual(int volumeAtual) {
        this.volumeAtual = volumeAtual;
    }

    public int getOitavaAtual() {
        return oitavaAtual;
    }

    public void setOitavaAtual(int oitavaAtual) {
        this.oitavaAtual = oitavaAtual;
    }

    public boolean isAnteriorEraNota() {
        return anteriorEraNota;
    }

    public void setAnteriorEraNota(boolean anteriorEraNota) {
        this.anteriorEraNota = anteriorEraNota;
    }

}
