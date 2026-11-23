package pepper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TradutorInstrucaoTest {

    private static final int[] INSTRUMENTOS_BASE = {7, 20, 1, 71};

    private List<List<EventoAudio>> traduzir(String texto) {
        TradutorInstrucao tradutor = new TradutorInstrucao();
        return tradutor.traduzir(texto, INSTRUMENTOS_BASE);
    }

    // Texto simples com o que importa de cada evento, para comparar resultados
    private String descrever(List<List<EventoAudio>> trilhas) {
        StringBuilder texto = new StringBuilder();
        for (List<EventoAudio> voz : trilhas) {
            texto.append("|");
            for (EventoAudio evento : voz) {
                texto.append(evento.getNota()).append(":")
                     .append(evento.getDuracao()).append(":")
                     .append(evento.getOitava()).append(":")
                     .append(evento.getVolume()).append(":")
                     .append(evento.getInstrumento()).append(":")
                     .append(evento.getVariacaoBpm()).append(" ");
            }
        }
        return texto.toString();
    }

    @Test
    public void notaComAcidenteEDuracao() {
        List<List<EventoAudio>> trilhas = traduzir("A#:2");
        assertEquals(1, trilhas.size());
        assertEquals(1, trilhas.get(0).size());
        EventoAudio evento = trilhas.get(0).get(0);
        assertEquals("A#", evento.getNota());
        assertEquals(2, evento.getDuracao());
    }

    @Test
    public void delayInicialGeraPausas() {
        List<EventoAudio> voz = traduzir("[2]C").get(0);
        assertEquals(3, voz.size());
        assertEquals("R", voz.get(0).getNota());
        assertEquals("R", voz.get(1).getNota());
        assertEquals("C", voz.get(2).getNota());
    }

    @Test
    public void delayInvalidoEhIgnorado() {
        List<EventoAudio> voz = traduzir("[x]C").get(0);
        assertEquals(1, voz.size());
        assertEquals("C", voz.get(0).getNota());
    }

    @Test
    public void consoanteRepeteAUltimaNota() {
        List<EventoAudio> voz = traduzir("CH").get(0);
        assertEquals(2, voz.size());
        assertEquals("C", voz.get(0).getNota());
        assertEquals("C", voz.get(1).getNota());
    }

    @Test
    public void consoanteSemNotaAntesNaoToca() {
        List<EventoAudio> voz = traduzir("HC").get(0);
        assertEquals(1, voz.size());
    }

    @Test
    public void vogalInterrompeARepeticao() {
        List<EventoAudio> voz = traduzir("COH").get(0);
        assertEquals(1, voz.size());
    }

    @Test
    public void interrogacaoSobeUmaOitavaEmRelacaoABase() {
        List<EventoAudio> voz = traduzir("C?C").get(0);
        assertEquals(6, voz.get(0).getOitava());
        assertEquals(7, voz.get(1).getOitava());
    }

    @Test
    public void linhasCiclamEntreAsQuatroVozes() {
        List<List<EventoAudio>> trilhas = traduzir("C\nC\nC\nC\nC");
        assertEquals(5, trilhas.size());
        assertEquals(7, trilhas.get(0).get(0).getInstrumento());
        assertEquals(20, trilhas.get(1).get(0).getInstrumento());
        assertEquals(7, trilhas.get(4).get(0).getInstrumento());
    }

    @Test
    public void quebraDeLinhaDoWindowsEIgualAoUnix() {
        String unix = descrever(traduzir("CH\nD"));
        String windows = descrever(traduzir("CH\r\nD"));
        assertEquals(unix, windows);
    }

    @Test
    public void retornoDeCarroNoFimDaLinhaNaoRepeteANota() {
        List<EventoAudio> voz = traduzir("C\r").get(0);
        assertEquals(1, voz.size());
    }
}
