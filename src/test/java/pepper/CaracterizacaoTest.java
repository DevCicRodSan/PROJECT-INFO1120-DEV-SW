package pepper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.jfugue.pattern.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Teste de caracterização ("golden master"): guarda o padrão musical gerado
 * para algumas entradas e confere que mudanças futuras não alteraram o resultado.
 *
 * Se o arquivo esperado ainda não existir, o teste o cria e falha de propósito.
 */
public class CaracterizacaoTest {

    private static final int[] INSTRUMENTOS_BASE = {7, 20, 1, 71};
    private static final int BPM = 120;
    private static final String PASTA_ESPERADO = "src/test/resources/esperado/";
    private static final String EXEMPLO_FASE2 = "docs/example1_fase2.txt";

    private String gerarPadrao(String texto) {
        TradutorMusical tradutor = new TradutorInstrucao();
        List<List<EventoAudio>> trilhas = tradutor.traduzir(texto, INSTRUMENTOS_BASE);

        TocadorAudio tocador = new TocadorAudio();
        tocador.definirBpm(BPM);
        Pattern padrao = tocador.construirPadraoMusical(trilhas);
        return padrao.toString();
    }

    private void conferir(String nome, String texto) throws IOException {
        String obtido = gerarPadrao(texto).strip();
        Path arquivo = Path.of(PASTA_ESPERADO + nome + ".txt");

        if (!Files.exists(arquivo)) {
            Files.createDirectories(arquivo.getParent());
            Files.writeString(arquivo, obtido);
            fail("Arquivo esperado criado: " + arquivo
                    + ". Confira o conteúdo e rode o teste novamente.");
        }

        String esperado = Files.readString(arquivo).strip();
        assertEquals(esperado, obtido);
    }

    @Test
    public void exemploDaFase2() throws IOException {
        String bruto = Files.readString(Path.of(EXEMPLO_FASE2));
        String texto = bruto.replace("\r\n", "\n");
        conferir("exemplo_fase2", texto);
    }

    @Test
    public void acidentesEDuracoes() throws IOException {
        conferir("acidentes_duracoes", "A#:2 Bb:5 C:7 G#:1");
    }

    @Test
    public void mudancasDeOitava() throws IOException {
        conferir("oitavas", "C?CVC?C");
    }

    @Test
    public void dobraDoVolume() throws IOException {
        conferir("volume", "C C  C   C");
    }

    @Test
    public void delayInicial() throws IOException {
        conferir("delay", "[2]C\n[3]D");
    }

    @Test
    public void mudancasDeAndamento() throws IOException {
        conferir("andamento", "C>C<<C\nD>>D");
    }

    @Test
    public void repeticaoDeNotaEPausa() throws IOException {
        conferir("repeticao", "HCHJKLOHC");
    }

    @Test
    public void quatroVozesECicloDeLinhas() throws IOException {
        conferir("vozes", "C\nD\nE\nF\nG\nA");
    }

    @Test
    public void linhaVaziaNoMeio() throws IOException {
        conferir("linha_vazia", "C\n\nD");
    }
}