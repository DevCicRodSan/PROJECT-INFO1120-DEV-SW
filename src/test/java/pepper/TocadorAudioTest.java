package pepper;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class TocadorAudioTest {

    @Test
    public void exportarParaPastaInexistenteLancaFalhaExportacaoMidi() {
        final TocadorAudio tocador = new TocadorAudio();
        final List<List<EventoAudio>> trilhas = new ArrayList<List<EventoAudio>>();
        Executable acao = new Executable() {
            @Override
            public void execute() {
                tocador.exportarMidi(trilhas, "pasta_que_nao_existe_xyz/saida.mid");
            }
        };
        assertThrows(FalhaExportacaoMidi.class, acao);
    }
}
