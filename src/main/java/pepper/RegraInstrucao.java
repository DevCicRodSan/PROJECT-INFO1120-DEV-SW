package pepper;

/**
 * Uma regra de interpretação do texto (ex.: "A a G tocam nota", "? sobe oitava").
 * Para criar uma instrução nova, basta criar uma classe que implementa esta
 * interface e registrá-la no TradutorInstrucao (princípio aberto/fechado).
 */
public interface RegraInstrucao {

    // Diz se esta regra sabe tratar a instrução que começa em "posicao"
    boolean reconhece(String linha, int posicao);

    // Executa a regra e devolve quantos caracteres consumiu (no mínimo 1)
    int executar(String linha, int posicao, ContextoTraducao contexto);
}
