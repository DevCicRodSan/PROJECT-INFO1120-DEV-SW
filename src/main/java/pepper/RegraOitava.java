package pepper;

/** '?' sobe uma oitava e 'V' desce uma, sempre relativas à oitava base da voz. */
public class RegraOitava implements RegraInstrucao {

    @Override
    public boolean reconhece(String linha, int posicao) {
        char c = linha.charAt(posicao);
        return c == '?' || c == 'V';
    }

    @Override
    public int executar(String linha, int posicao, ContextoTraducao contexto) {
        char c = linha.charAt(posicao);
        if (c == '?') {
            contexto.subirOitava();
        } else {
            contexto.descerOitava();
        }
        return 1;
    }
}
