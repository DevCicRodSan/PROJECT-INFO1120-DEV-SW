package pepper;

import java.util.List;

/**
 * Contrato de quem traduz o texto digitado em eventos de áudio (uma lista por voz).
 * O ControladorAudio depende desta interface, e não da classe concreta,
 * o que permite trocar o tradutor (ou usar um falso nos testes).
 */
public interface TradutorMusical {

    // instrumentosBase: número GM (1 a 128) do instrumento inicial de cada voz
    List<List<EventoAudio>> traduzir(String texto, int[] instrumentosBase);
}
