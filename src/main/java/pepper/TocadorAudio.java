package pepper;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TocadorAudio implements Tocador {
    private static final int BPM_MINIMO = 20;

    private Player player;
    private int bpmGlobal = 120; // BPM base

    public TocadorAudio() {
        this.player = new Player();
    }

    @Override
    public void definirBpm(int bpm) {
        this.bpmGlobal = bpm;
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
                    int variacaoNoInstante = variacaoPorInstante.getOrDefault(instante, 0) + mudanca;
                    variacaoPorInstante.put(instante, variacaoNoInstante);
                    ultimaVariacao = evento.getVariacaoBpm();
                }
                Duracao duracao = Duracao.porCodigo(evento.getDuracao());
                instante += duracao.getBeats();
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

                Duracao duracao = Duracao.porCodigo(evento.getDuracao());
                if (evento.getNota().equals("R")) {
                    stringVoz.append("R").append(duracao.getLetra()).append(" ");
                } else {
                    stringVoz.append(evento.getNota())
                             .append(evento.getOitava())
                             .append(duracao.getLetra())
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

    @Override
    public void tocarSequencia(List<List<EventoAudio>> trilhas) {
        Pattern padrao = construirPadraoMusical(trilhas);
        player.play(padrao); // bloqueia até o fim da música
    }

    // Exportação MIDI (Requisito Fase 2)
    @Override
    public void exportarMidi(List<List<EventoAudio>> trilhas, String caminhoArquivo) {
        Pattern padrao = construirPadraoMusical(trilhas);
        try {
            File arquivo = new File(caminhoArquivo);
            MidiFileManager.savePatternToMidi(padrao, arquivo);
        } catch (IOException e) {
            throw new FalhaExportacaoMidi(
                    "Não foi possível salvar o arquivo MIDI: " + e.getMessage(), e);
        }
    }
}
