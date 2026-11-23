package pepper;

/** '>' acelera e '<' desacelera o andamento (BPM). */
public class RegraAndamento implements RegraInstrucao {

    @Override
    public boolean reconhece(String linha, int posicao) {
        char c = linha.charAt(posicao);
        return c == '>' || c == '<';
    }

    @Override
    public int executar(String linha, int posicao, ContextoTraducao contexto) {
        char c = linha.charAt(posicao);
        if (c == '>') {
            contexto.acelerar();
        } else {
            contexto.desacelerar();
        }
        return 1;
    }
}
