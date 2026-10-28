package com.example;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TocadorAudio {
    private Player player;
    private int bpmGlobal = 120; // BPM base

    public TocadorAudio() {
        this.player = new Player();
    }

    public void definirBpm(int bpm) {
        this.bpmGlobal = bpm;
    }

    // Mapeamento das durações 1 a 7 para as letras do JFugue
    private String mapearDuracao(int idDuracao) {
        return switch (idDuracao) {
            case 1 -> "w"; // Semibreve
            case 2 -> "h"; // Mínima
            case 3 -> "q"; // Semínima
            case 4 -> "i"; // Colcheia
            case 5 -> "s"; // Semicolcheia
            case 6 -> "t"; // Fusa
            case 7 -> "x"; // Semifusa
            default -> "q";
        };
    }

    public Pattern construirPadraoMusical(List<List<EventoAudio>> trilhas) {
        Pattern padraoCompleto = new Pattern();
        padraoCompleto.add("T" + bpmGlobal + " ");

        for (int i = 0; i < trilhas.size(); i++) {
            StringBuilder stringVoz = new StringBuilder();
            stringVoz.append("V").append(i % 16).append(" "); // JFugue Voices (V0, V1...)

            for (EventoAudio evento : trilhas.get(i)) {
                stringVoz.append("I").append(evento.getInstrumento()).append(" ");
                
                if (evento.getNota().equals("R")) {
                    stringVoz.append("R").append(mapearDuracao(evento.getDuracao())).append(" ");
                } else {
                    stringVoz.append(evento.getNota())
                             .append(evento.getOitava())
                             .append(mapearDuracao(evento.getDuracao()))
                             .append("a").append(evento.getVolume()) // 'a' no JFugue = attack velocity (volume)
                             .append(" ");
                }
            }
            padraoCompleto.add(stringVoz.toString());
        }
        return padraoCompleto;
    }

    public void tocarSequencia(List<List<EventoAudio>> trilhas) {
        Pattern padrao = construirPadraoMusical(trilhas);
        new Thread(() -> player.play(padrao)).start(); // Roda em background
    }

    // Exportação MIDI (Requisito Fase 2)
    public void exportarMidi(List<List<EventoAudio>> trilhas, String caminhoArquivo) {
        Pattern padrao = construirPadraoMusical(trilhas);
        try {
            File arquivo = new File(caminhoArquivo);
            MidiFileManager.savePatternToMidi(padrao, arquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo MIDI: " + e.getMessage());
        }
    }
}
