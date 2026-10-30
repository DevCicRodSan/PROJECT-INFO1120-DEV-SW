package com.example;

public class EventoAudio {
    private String nota;
    private int instrumento;
    private int oitava;
    private int volume;
    private int duracao;
    private int variacaoBpm; // soma dos '>' e '<' já lidos na linha

    public EventoAudio(String nota, int instrumento, int oitava, int volume, int duracao, int variacaoBpm) {
        this.nota = nota;
        this.instrumento = instrumento;
        this.oitava = oitava;
        this.volume = volume;
        this.duracao = duracao;
        this.variacaoBpm = variacaoBpm;
    }

    public String getNota() { return nota; }
    public int getInstrumento() { return instrumento; }
    public int getOitava() { return oitava; }
    public int getVolume() { return volume; }
    public int getDuracao() { return duracao; }
    public int getVariacaoBpm() { return variacaoBpm; }
}
