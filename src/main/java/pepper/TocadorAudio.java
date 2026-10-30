package pepper;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.Player;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TocadorAudio {
    private static final int BPM_MINIMO = 20;

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

    // Duração em beats (semínima = 1 beat): semibreve = 4, mínima = 2, ..., semifusa = 1/16
    private double duracaoEmBeats(int idDuracao) {
        if (idDuracao < 1 || idDuracao > 7) {
            return 1.0; // mesmo padrão de mapearDuracao (semínima)
        }
        return 4.0 / Math.pow(2, idDuracao - 1);
    }

    // O BPM é global: soma os '>' e '<' de todas as vozes na ordem em que acontecem no tempo.
    // Devolve, para cada instante (em beats) com mudança, o BPM resultante da peça inteira.
    private TreeMap<Double, Integer> calcularBpmPorInstante(List<List<EventoAudio>> trilhas) {
        TreeMap<Double, Integer> variacaoPorInstante = new TreeMap<>();
        for (List<EventoAudio> voz : trilhas) {
            double instante = 0;
            int ultimaVariacao = 0;
            for (EventoAudio evento : voz) {
                if (evento.getVariacaoBpm() != ultimaVariacao) {
                    int mudanca = evento.getVariacaoBpm() - ultimaVariacao;
                    variacaoPorInstante.merge(instante, mudanca, Integer::sum);
                    ultimaVariacao = evento.getVariacaoBpm();
                }
                instante += duracaoEmBeats(evento.getDuracao());
            }
        }

        TreeMap<Double, Integer> bpmPorInstante = new TreeMap<>();
        int variacaoAcumulada = 0;
        for (Map.Entry<Double, Integer> entrada : variacaoPorInstante.entrySet()) {
            variacaoAcumulada += entrada.getValue();
            bpmPorInstante.put(entrada.getKey(), Math.max(BPM_MINIMO, bpmGlobal + variacaoAcumulada));
        }
        return bpmPorInstante;
    }

    public Pattern construirPadraoMusical(List<List<EventoAudio>> trilhas) {
        Pattern padraoCompleto = new Pattern();
        padraoCompleto.add("T" + bpmGlobal + " ");

        for (int i = 0; i < trilhas.size(); i++) {
            StringBuilder stringVoz = new StringBuilder();
            stringVoz.append("V").append(i % 16).append(" "); // JFugue Voices (V0, V1...)

            for (EventoAudio evento : trilhas.get(i)) {
                // Instrumento vem em GM 1 a 128; o JFugue conta de 0 a 127
                stringVoz.append("I").append(evento.getInstrumento() - 1).append(" ");
                
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

        // O sequenciador só obedece a mudanças de andamento na faixa 0 (voz V0).
        // Por isso todas vão para a V0, posicionadas com @ (unidade: semibreve = 4 beats).
        TreeMap<Double, Integer> bpmPorInstante = calcularBpmPorInstante(trilhas);
        for (Map.Entry<Double, Integer> entrada : bpmPorInstante.entrySet()) {
            double instanteEmSemibreves = entrada.getKey() / 4.0;
            padraoCompleto.add("V0 @" + instanteEmSemibreves + " T" + entrada.getValue() + " ");
        }
        return padraoCompleto;
    }

    // Toca em background; aoTerminar roda quando a música acaba ou é interrompida por parar()
    public void tocarSequencia(List<List<EventoAudio>> trilhas, Runnable aoTerminar) {
        Pattern padrao = construirPadraoMusical(trilhas);
        new Thread(() -> {
            try {
                player.play(padrao); // bloqueia até o fim da música ou até parar()
            } finally {
                aoTerminar.run();
            }
        }).start();
    }

    public void parar() {
        ManagedPlayer reproducao = player.getManagedPlayer();
        if (reproducao.isStarted() && !reproducao.isFinished()) {
            reproducao.finish(); // faz o player.play() acima retornar
        }
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
