package com.example;

import java.util.List;

public class TradutorInstrucao {
    String ultimaNota;
    int volumeAtual;
    int oitavaAtual;
    boolean anteriorEraNota;

    public void traduzir(List<EventoAudio> eventos) {
            EventoAudio evento = new EventoAudio();
            traduzirCaractere()
    }

    private void traduzirCaractere(char c) {

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
