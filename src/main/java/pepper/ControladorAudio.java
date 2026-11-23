package pepper;

import java.util.List;

public class ControladorAudio {
    // Faixa de BPM aceita pelo programa (a interface usa as mesmas constantes)
    public static final int BPM_MINIMO = 20;
    public static final int BPM_MAXIMO = 300;

    private final TradutorMusical tradutor;
    private final Tocador tocador;

    // BPM inicial global, definido pelo usuário na interface
    private int bpmGlobal = 120;

    // Instrumento base de cada voz (sugestão barroca do enunciado), definido pelo usuário na interface
    private Instrumento[] instrumentosPorVoz = {
        Instrumento.CRAVO, Instrumento.ORGAO_DE_IGREJA, Instrumento.PIANO, Instrumento.FAGOTE
    };


    // Construtor usado pelo programa: cria as implementações reais
    public ControladorAudio() {
        this(new TradutorInstrucao(), new TocadorAudio());
    }

    // Construtor que recebe as dependências (usado nos testes com versões falsas)
    public ControladorAudio(TradutorMusical tradutor, Tocador tocador) {
        this.tradutor = tradutor;
        this.tocador = tocador;
        this.tocador.definirBpm(this.bpmGlobal);
    }

    public int getBpm() {
        return bpmGlobal;
    }

    public void setBpm(int bpm) {
        if (bpm < BPM_MINIMO || bpm > BPM_MAXIMO) {
            throw new IllegalArgumentException(
                    "O BPM deve estar entre " + BPM_MINIMO + " e " + BPM_MAXIMO + ".");
        }
        this.bpmGlobal = bpm;
    }

    public int getTotalVozes() {
        return instrumentosPorVoz.length;
    }

    public Instrumento getInstrumentoVoz(int voz) {
        return instrumentosPorVoz[voz];
    }

    public void setInstrumentoVoz(int voz, Instrumento instrumento) {
        if (voz < 0 || voz >= instrumentosPorVoz.length) {
            throw new IndexOutOfBoundsException("Voz inexistente: " + voz);
        }
        if (instrumento == null) {
            throw new IllegalArgumentException("O instrumento não pode ser nulo.");
        }
        this.instrumentosPorVoz[voz] = instrumento;
    }

    // Números GM (1 a 128) dos instrumentos base, na ordem das vozes
    private int[] numerosInstrumentosBase() {
        int[] numeros = new int[instrumentosPorVoz.length];
        for (int i = 0; i < instrumentosPorVoz.length; i++) {
            numeros[i] = instrumentosPorVoz[i].getNumeroGM();
        }
        return numeros;
    }

    // Só retorna quando a música termina (a interface fica bloqueada enquanto toca)
    public void tocarTexto(String texto) {
        if (texto == null || texto.isBlank())
            return;
        List<List<EventoAudio>> trilhas = prepararTrilhas(texto);
        tocador.tocarSequencia(trilhas);
    }

    public void exportarMidi(String texto, String caminhoArquivo) {
        if (texto == null || texto.isBlank())
            return;
        List<List<EventoAudio>> trilhas = prepararTrilhas(texto);
        tocador.exportarMidi(trilhas, caminhoArquivo);
    }

    // Traduz o texto em trilhas e configura o BPM do tocador (passo comum a tocar e exportar)
    private List<List<EventoAudio>> prepararTrilhas(String texto) {
        List<List<EventoAudio>> trilhas = tradutor.traduzir(texto, numerosInstrumentosBase());
        tocador.definirBpm(bpmGlobal);
        return trilhas;
    }
}
