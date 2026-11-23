package pepper;

/**
 * Uma nota (ou pausa "R") pronta para tocar: o que tocar (nota e duração)
 * e em que estado de voz tocar (EstadoVoz).
 * Os getters de instrumento, oitava, volume e variação de BPM continuam
 * disponíveis e consultam o EstadoVoz.
 */
public class EventoAudio {
    private final String nota;
    private final int duracao;
    private final EstadoVoz estado;

    public EventoAudio(String nota, int duracao, EstadoVoz estado) {
        this.nota = nota;
        this.duracao = duracao;
        this.estado = estado;
    }

    public String getNota() { return nota; }
    public int getDuracao() { return duracao; }
    public EstadoVoz getEstado() { return estado; }

    public int getInstrumento() { return estado.getInstrumento(); }
    public int getOitava() { return estado.getOitava(); }
    public int getVolume() { return estado.getVolume(); }
    public int getVariacaoBpm() { return estado.getVariacaoBpm(); }
}
