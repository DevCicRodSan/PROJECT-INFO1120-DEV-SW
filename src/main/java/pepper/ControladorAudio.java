package pepper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ControladorAudio {
    private TradutorInstrucao tradutor;
    private TocadorAudio tocador;

    // BPM inicial global, definido pelo usuário na interface
    private int bpmGlobal = 120;

    // Instrumento base de cada voz (sugestão barroca do enunciado), definido pelo usuário na interface
    private Instrumento[] instrumentosPorVoz = {
        Instrumento.CRAVO, Instrumento.ORGAO_DE_IGREJA, Instrumento.PIANO, Instrumento.FAGOTE
    };

    // volatile: muda na thread de reprodução e é lido pela thread da interface
    private volatile EstadoReproducao estado = EstadoReproducao.PARADO;

    // Construtor
    public ControladorAudio() {
        this.tradutor = new TradutorInstrucao();
        this.tocador = new TocadorAudio();
        this.tocador.definirBpm(this.bpmGlobal);
    }

    public int getBpm() {
        return bpmGlobal;
    }

    public void setBpm(int bpm) {
        this.bpmGlobal = bpm;
    }

    public int getTotalVozes() {
        return instrumentosPorVoz.length;
    }

    public Instrumento getInstrumentoVoz(int voz) {
        return instrumentosPorVoz[voz];
    }

    public void setInstrumentoVoz(int voz, Instrumento instrumento) {
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

    public EstadoReproducao getEstado() {
        return estado;
    }

    // Não faz nada se já estiver tocando ou se não houver texto.
    // aoTerminar roda (fora da thread da interface) quando a música acaba ou é parada,
    // depois que o estado já voltou a PARADO.
    public void tocarTexto(String texto, Runnable aoTerminar) {
        if (estado == EstadoReproducao.TOCANDO || texto == null || texto.isEmpty())
            return;
        List<List<EventoAudio>> trilhas = tradutor.traduzir(texto, numerosInstrumentosBase());
        tocador.definirBpm(bpmGlobal);
        estado = EstadoReproducao.TOCANDO;
        tocador.tocarSequencia(trilhas, () -> {
            estado = EstadoReproducao.PARADO;
            aoTerminar.run();
        });
    }

    public void parar() {
        if (estado == EstadoReproducao.TOCANDO) {
            tocador.parar();
        }
    }

    public void exportarMidi(String texto, String caminhoArquivo) {
        if (texto == null || texto.isEmpty())
            return;
        List<List<EventoAudio>> trilhas = tradutor.traduzir(texto, numerosInstrumentosBase());
        tocador.definirBpm(bpmGlobal);
        tocador.exportarMidi(trilhas, caminhoArquivo);
    }

    public String carregarArquivoTxt(File arquivo) throws IOException {
        return Files.readString(arquivo.toPath());
    }

    public void salvarArquivoTxt(String texto, File arquivo) throws IOException {
        Files.writeString(arquivo.toPath(), texto);
    }
}