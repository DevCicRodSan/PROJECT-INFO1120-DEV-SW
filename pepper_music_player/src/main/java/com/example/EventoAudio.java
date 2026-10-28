package com.example;

public class EventoAudio {
    private String nota;
    private int instrumento;
    private int oitava;
    private int volume;
    private int duracao;

    public EventoAudio(String nota, int instrumento, int oitava, int volume, int duracao) {
        this.nota = nota;
        this.instrumento = instrumento;
        this.oitava = oitava;
        this.volume = volume;
        this.duracao = duracao;
    }

    public String getNota() { return nota; }
    public int getInstrumento() { return instrumento; }
    public int getOitava() { return oitava; }
    public int getVolume() { return volume; }
    public int getDuracao() { return duracao; }
}
