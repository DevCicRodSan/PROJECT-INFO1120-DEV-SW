package pepper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class ControladorAudioTest {

    // Tradutor falso: só conta quantas vezes foi chamado
    private static class TradutorFalso implements TradutorMusical {
        int chamadas = 0;

        @Override
        public List<List<EventoAudio>> traduzir(String texto, int[] instrumentosBase) {
            chamadas++;
            return new ArrayList<List<EventoAudio>>();
        }
    }

    // Tocador falso: não toca nada, só registra o que o controlador pediu
    private static class TocadorFalso implements Tocador {
        int bpm = -1;
        int vezesQueTocou = 0;
        int vezesQueExportou = 0;
        String ultimoCaminho = null;

        @Override
        public void definirBpm(int novoBpm) {
            bpm = novoBpm;
        }

        @Override
        public void tocarSequencia(List<List<EventoAudio>> trilhas) {
            vezesQueTocou++;
        }

        @Override
        public void exportarMidi(List<List<EventoAudio>> trilhas, String caminhoArquivo) {
            vezesQueExportou++;
            ultimoCaminho = caminhoArquivo;
        }
    }

    @Test
    public void textoEmBrancoNaoTocaNada() {
        TradutorFalso tradutor = new TradutorFalso();
        TocadorFalso tocador = new TocadorFalso();
        ControladorAudio controlador = new ControladorAudio(tradutor, tocador);

        controlador.tocarTexto("   ");
        controlador.tocarTexto(null);

        assertEquals(0, tradutor.chamadas);
        assertEquals(0, tocador.vezesQueTocou);
    }

    @Test
    public void textoValidoTocaComOBpmDoControlador() {
        TradutorFalso tradutor = new TradutorFalso();
        TocadorFalso tocador = new TocadorFalso();
        ControladorAudio controlador = new ControladorAudio(tradutor, tocador);

        controlador.setBpm(90);
        controlador.tocarTexto("C");

        assertEquals(1, tradutor.chamadas);
        assertEquals(1, tocador.vezesQueTocou);
        assertEquals(90, tocador.bpm);
    }

    @Test
    public void exportarTextoEmBrancoNaoExporta() {
        TradutorFalso tradutor = new TradutorFalso();
        TocadorFalso tocador = new TocadorFalso();
        ControladorAudio controlador = new ControladorAudio(tradutor, tocador);

        controlador.exportarMidi("  \n ", "saida.mid");

        assertEquals(0, tocador.vezesQueExportou);
    }

    @Test
    public void exportarTextoValidoPassaOCaminhoAoTocador() {
        TradutorFalso tradutor = new TradutorFalso();
        TocadorFalso tocador = new TocadorFalso();
        ControladorAudio controlador = new ControladorAudio(tradutor, tocador);

        controlador.exportarMidi("C", "saida.mid");

        assertEquals(1, tocador.vezesQueExportou);
        assertEquals("saida.mid", tocador.ultimoCaminho);
    }

    @Test
    public void bpmAbaixoDoMinimoLancaExcecao() {
        final ControladorAudio controlador = new ControladorAudio(new TradutorFalso(), new TocadorFalso());
        Executable acao = new Executable() {
            @Override
            public void execute() {
                controlador.setBpm(ControladorAudio.BPM_MINIMO - 1);
            }
        };
        assertThrows(IllegalArgumentException.class, acao);
    }

    @Test
    public void bpmAcimaDoMaximoLancaExcecao() {
        final ControladorAudio controlador = new ControladorAudio(new TradutorFalso(), new TocadorFalso());
        Executable acao = new Executable() {
            @Override
            public void execute() {
                controlador.setBpm(ControladorAudio.BPM_MAXIMO + 1);
            }
        };
        assertThrows(IllegalArgumentException.class, acao);
    }

    @Test
    public void instrumentoNuloLancaExcecao() {
        final ControladorAudio controlador = new ControladorAudio(new TradutorFalso(), new TocadorFalso());
        Executable acao = new Executable() {
            @Override
            public void execute() {
                controlador.setInstrumentoVoz(0, null);
            }
        };
        assertThrows(IllegalArgumentException.class, acao);
    }

    @Test
    public void vozInexistenteLancaExcecao() {
        final ControladorAudio controlador = new ControladorAudio(new TradutorFalso(), new TocadorFalso());
        Executable acao = new Executable() {
            @Override
            public void execute() {
                controlador.setInstrumentoVoz(99, Instrumento.PIANO);
            }
        };
        assertThrows(IndexOutOfBoundsException.class, acao);
    }
}
