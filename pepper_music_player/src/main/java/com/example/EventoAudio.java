package com.example;

public class EventoAudio {
    String nota;
    int instrumento;
    int oitava;
    int volume;

    // Construtor EventoAudio
    public EventoAudio(String nota, int instrumento, int oitava, int volume) {
        this.nota = nota;
        this.instrumento = instrumento;
        this.oitava = oitava;
        this.volume = volume;
    }

    public String getNota() {
        return nota;
    }

    public int getInstrumento() {
        return instrumento;
    }

    public int getOitava() {
        return oitava;
    }

    public int getVolume() {
        return volume;
    }

}
