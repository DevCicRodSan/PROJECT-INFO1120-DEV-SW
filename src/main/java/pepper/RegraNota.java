package pepper;

/**
 * Notas A a G, com acidente opcional (# ou b) e duração opcional (:1 a :9).
 * Exemplos: C, A#, Bb:2, G#:5
 */
public class RegraNota implements RegraInstrucao {

    @Override
    public boolean reconhece(String linha, int posicao) {
        char c = linha.charAt(posicao);
        return c >= 'A' && c <= 'G';
    }

    @Override
    public int executar(String linha, int posicao, ContextoTraducao contexto) {
        String nota = String.valueOf(linha.charAt(posicao));
        int duracao = ContextoTraducao.DURACAO_PADRAO;
        int consumidos = 1;

        // Acidente: # ou b logo depois da letra
        int posAcidente = posicao + consumidos;
        if (posAcidente < linha.length()) {
            char acidente = linha.charAt(posAcidente);
            if (acidente == '#' || acidente == 'b') {
                nota += acidente;
                consumidos++;
            }
        }

        // Duração: ":" seguido de um dígito
        int posDoisPontos = posicao + consumidos;
        int posDigito = posDoisPontos + 1;
        if (posDigito < linha.length()
                && linha.charAt(posDoisPontos) == ':'
                && Character.isDigit(linha.charAt(posDigito))) {
            duracao = Character.getNumericValue(linha.charAt(posDigito));
            consumidos += 2;
        }

        contexto.adicionarNota(nota, duracao);
        return consumidos;
    }
}
