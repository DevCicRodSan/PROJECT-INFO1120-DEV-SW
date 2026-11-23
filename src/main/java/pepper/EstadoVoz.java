package pepper;

/**
 * "Foto" do estado de uma voz no momento em que uma nota é lida:
 * instrumento (GM 1 a 128), oitava, volume e variação acumulada de BPM.
 * Imutável: depois de criado, nunca muda.
 */
public class EstadoVoz {
    private final int instrumento;
    private final int oitava;
    private final int volume;
    private final int variacaoBpm; // soma dos '>' e '<' já lidos na linha

    public EstadoVoz(int instrumento, int oitava, int volume, int variacaoBpm) {
        this.instrumento = instrumento;
        this.oitava = oitava;
        this.volume = volume;
        this.variacaoBpm = variacaoBpm;
    }

    public int getInstrumento() { return instrumento; }
    public int getOitava() { return oitava; }
    public int getVolume() { return volume; }
    public int getVariacaoBpm() { return variacaoBpm; }
}
