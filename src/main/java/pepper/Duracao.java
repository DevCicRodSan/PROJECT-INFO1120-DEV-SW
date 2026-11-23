package pepper;

/**
 * Duração de uma nota: código usado no texto (1 a 7), letra da notação do JFugue
 * e tamanho em beats (semínima = 1 beat).
 */
public enum Duracao {
    SEMIBREVE(1, "w", 4.0),
    MINIMA(2, "h", 2.0),
    SEMINIMA(3, "q", 1.0),
    COLCHEIA(4, "i", 0.5),
    SEMICOLCHEIA(5, "s", 0.25),
    FUSA(6, "t", 0.125),
    SEMIFUSA(7, "x", 0.0625);

    private final int codigo;
    private final String letra;
    private final double beats;

    Duracao(int codigo, String letra, double beats) {
        this.codigo = codigo;
        this.letra = letra;
        this.beats = beats;
    }

    public int getCodigo() { return codigo; }
    public String getLetra() { return letra; }
    public double getBeats() { return beats; }

    // Código fora de 1 a 7 vale semínima, a duração padrão do programa
    public static Duracao porCodigo(int codigo) {
        for (Duracao duracao : values()) {
            if (duracao.codigo == codigo) {
                return duracao;
            }
        }
        return SEMINIMA;
    }
}
