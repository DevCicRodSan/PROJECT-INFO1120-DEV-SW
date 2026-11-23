package pepper;

/**
 * Regra de qualquer outro caractere (deve ser a ÚLTIMA da lista).
 * Se a instrução anterior foi uma nota e o caractere é uma consoante ou
 * símbolo comum, repete a nota. Caso contrário, não toca nada (pausa).
 */
public class RegraRepeticaoOuPausa implements RegraInstrucao {

    // Caracteres que NÃO repetem a nota: vogais O, U, I, dígitos, ? ! ; , quebra de linha, < > e espaço
    private static final String NAO_REPETEM = "OUIoui0123456789?!;,\n<> ";

    @Override
    public boolean reconhece(String linha, int posicao) {
        return true;
    }

    @Override
    public int executar(String linha, int posicao, ContextoTraducao contexto) {
        char c = linha.charAt(posicao);
        boolean repete = NAO_REPETEM.indexOf(c) < 0;
        if (contexto.ultimaInstrucaoFoiNota() && repete) {
            contexto.repetirUltimaNota();
        } else {
            contexto.esquecerUltimaNota();
        }
        return 1;
    }
}
