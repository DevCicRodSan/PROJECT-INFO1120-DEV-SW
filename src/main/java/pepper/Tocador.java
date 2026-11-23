package pepper;

import java.util.List;

/**
 * Contrato de quem toca ou exporta as trilhas. O ControladorAudio depende desta
 * interface; a implementação real (TocadorAudio) é a única que conhece o JFugue.
 */
public interface Tocador {

    void definirBpm(int bpm);

    // Só retorna quando a música termina
    void tocarSequencia(List<List<EventoAudio>> trilhas);

    // Lança FalhaExportacaoMidi se não conseguir gravar o arquivo
    void exportarMidi(List<List<EventoAudio>> trilhas, String caminhoArquivo);
}
