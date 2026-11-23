package pepper;

import java.util.ArrayList;
import java.util.List;

/**
 * Estado de leitura de UMA linha (voz) do texto: instrumento, oitava, volume,
 * variação de BPM, última nota lida e os eventos já gerados.
 * Quem interpreta o texto (as regras) manda o contexto fazer as mudanças,
 * em vez de ler e alterar os campos por fora.
 */
public class ContextoTraducao {
    public static final String PAUSA = "R";
    public static final int DURACAO_PADRAO = 3; // semínima

    private static final int PASSO_BPM = 10; // '>' acelera e '<' desacelera
    private static final int VOLUME_MAXIMO = 127;
    private static final int OITAVA_MINIMA = 1;

    private int instrumentoAtual;
    private int oitavaAtual;
    private int oitavaBaseDaLinha;
    private int volumeAtual;
    private int variacaoBpm;
    private String ultimaNota;
    private boolean anteriorEraNota;
    private List<EventoAudio> eventos = new ArrayList<>();

    // Começa uma nova linha: volta ao estado inicial da voz e zera os eventos
    public void iniciarLinha(int instrumento, int oitava, int volume) {
        this.instrumentoAtual = instrumento;
        this.oitavaAtual = oitava;
        this.oitavaBaseDaLinha = oitava;
        this.volumeAtual = volume;
        this.variacaoBpm = 0;
        this.anteriorEraNota = false;
        this.ultimaNota = PAUSA;
        this.eventos = new ArrayList<>();
    }

    public List<EventoAudio> getEventos() {
        return eventos;
    }

    // --- Gerar eventos ---

    public void adicionarNota(String nota, int duracao) {
        eventos.add(criarEvento(nota, duracao));
        ultimaNota = nota;
        anteriorEraNota = true;
    }

    public void adicionarPausa(int duracao) {
        eventos.add(criarEvento(PAUSA, duracao));
    }

    public void repetirUltimaNota() {
        eventos.add(criarEvento(ultimaNota, DURACAO_PADRAO));
    }

    // --- Mudar o estado da voz (toda mudança "esquece" a última nota) ---

    public void subirOitava() {
        oitavaAtual = oitavaBaseDaLinha + 1;
        anteriorEraNota = false;
    }

    public void descerOitava() {
        oitavaAtual = Math.max(OITAVA_MINIMA, oitavaBaseDaLinha - 1);
        anteriorEraNota = false;
    }

    public void dobrarVolume() {
        volumeAtual = Math.min(VOLUME_MAXIMO, volumeAtual * 2);
        anteriorEraNota = false;
    }

    public void acelerar() {
        variacaoBpm += PASSO_BPM;
        anteriorEraNota = false;
    }

    public void desacelerar() {
        variacaoBpm -= PASSO_BPM;
        anteriorEraNota = false;
    }

    public void esquecerUltimaNota() {
        anteriorEraNota = false;
    }

    public boolean ultimaInstrucaoFoiNota() {
        return anteriorEraNota;
    }

    // Tira uma "foto" do estado atual e monta o evento
    private EventoAudio criarEvento(String nota, int duracao) {
        EstadoVoz estado = new EstadoVoz(instrumentoAtual, oitavaAtual, volumeAtual, variacaoBpm);
        return new EventoAudio(nota, duracao, estado);
    }
}
