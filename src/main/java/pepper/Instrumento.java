package pepper;

// Catálogo de instrumentos do software (General MIDI, numeração de 1 a 128 como no enunciado).
// O TocadorAudio converte para a numeração do JFugue (0 a 127).
public enum Instrumento {
    PIANO(1, "Piano"),
    PIANO_ELETRICO(5, "Piano Elétrico"),
    CRAVO(7, "Cravo"),
    GLOCKENSPIEL(10, "Glockenspiel"),
    MARIMBA(13, "Marimba"),
    TUBULAR_BELLS(15, "Tubular Bells"),
    ORGAO_HAMMOND(17, "Órgão Hammond"),
    ORGAO_DE_IGREJA(20, "Órgão de Igreja"),
    ACORDEAO(22, "Acordeão"),
    BANDONEON(24, "Bandoneon"),
    VIOLAO_NYLON(25, "Violão de Nylon"),
    BAIXO_ACUSTICO(33, "Baixo Acústico"),
    VIOLINO(41, "Violino"),
    VIOLONCELO(43, "Violoncelo"),
    HARPA(47, "Harpa"),
    CORDAS(49, "Cordas"),
    CORO(53, "Coro"),
    TROMPETE(57, "Trompete"),
    TROMPA(61, "Trompa"),
    SAX_ALTO(66, "Sax Alto"),
    OBOE(69, "Oboé"),
    FAGOTE(71, "Fagote"),
    CLARINETE(72, "Clarinete"),
    FLAUTA(74, "Flauta"),
    FLAUTA_DE_PA(76, "Flauta de Pã"),
    BANJO(106, "Banjo"),
    GAITA_DE_FOLES(110, "Gaita de Foles"),
    AGOGO(114, "Agogô"),
    STEEL_DRUMS(115, "Steel Drums"),
    ONDAS_DO_MAR(123, "Ondas do Mar");

    private final int numeroGM;
    private final String nome;

    Instrumento(int numeroGM, String nome) {
        this.numeroGM = numeroGM;
        this.nome = nome;
    }

    public int getNumeroGM() { return numeroGM; }
    public String getNome() { return nome; }

    // Texto exibido na caixa de seleção da interface
    @Override
    public String toString() {
        return numeroGM + " - " + nome;
    }
}
