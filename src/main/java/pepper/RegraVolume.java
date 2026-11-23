package pepper;

/** Espaço dobra o volume da voz (limitado ao máximo). */
public class RegraVolume implements RegraInstrucao {

    @Override
    public boolean reconhece(String linha, int posicao) {
        return linha.charAt(posicao) == ' ';
    }

    @Override
    public int executar(String linha, int posicao, ContextoTraducao contexto) {
        contexto.dobrarVolume();
        return 1;
    }
}
