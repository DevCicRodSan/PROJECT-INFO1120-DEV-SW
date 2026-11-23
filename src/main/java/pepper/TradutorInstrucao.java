package pepper;

import java.util.ArrayList;
import java.util.List;

public class TradutorInstrucao {

    // Configurações base das 4 vozes
    private static final int[] OITAVAS_BASE = {6, 5, 4, 3};
    private static final int[] VOLUMES_BASE = {100, 80, 60, 40};
    // Os instrumentos base vêm do ControladorAudio (escolhidos na interface)

    private final ContextoTraducao contexto = new ContextoTraducao();
    private final List<RegraInstrucao> regras = criarRegras();

    // A ordem importa: a primeira regra que reconhece o caractere é a escolhida.
    // RegraRepeticaoOuPausa reconhece tudo, por isso fica por último.
    private static List<RegraInstrucao> criarRegras() {
        List<RegraInstrucao> lista = new ArrayList<>();
        lista.add(new RegraNota());
        lista.add(new RegraOitava());
        lista.add(new RegraVolume());
        lista.add(new RegraAndamento());
        lista.add(new RegraRepeticaoOuPausa());
        return lista;
    }

    // instrumentosBase: número GM (1 a 128) do instrumento inicial de cada voz
    public List<List<EventoAudio>> traduzir(String texto, int[] instrumentosBase) {
        List<List<EventoAudio>> trilhas = new ArrayList<>();
        if (texto == null || texto.isEmpty()) {
            return trilhas;
        }

        String[] linhas = texto.split("\n");

        for (int i = 0; i < linhas.length; i++) {
            int idVoz = i % 4; // Cicla entre 0 e 3
            contexto.iniciarLinha(instrumentosBase[idVoz], OITAVAS_BASE[idVoz], VOLUMES_BASE[idVoz]);
            trilhas.add(processarLinha(linhas[i]));
        }
        return trilhas;
    }

    private List<EventoAudio> processarLinha(String linha) {
        int posicao = processarDelayInicial(linha);

        while (posicao < linha.length()) {
            RegraInstrucao regra = escolherRegra(linha, posicao);
            int consumidos = regra.executar(linha, posicao, contexto);
            posicao += consumidos;
        }
        return contexto.getEventos();
    }

    private RegraInstrucao escolherRegra(String linha, int posicao) {
        for (RegraInstrucao regra : regras) {
            if (regra.reconhece(linha, posicao)) {
                return regra;
            }
        }
        throw new IllegalStateException("Nenhuma regra para o caractere na posição " + posicao);
    }

    // Delay inicial "[n]": n pausas de 1 beat. Devolve a posição onde a música começa.
    private int processarDelayInicial(String linha) {
        if (!linha.startsWith("[")) {
            return 0;
        }
        int fechaColchete = linha.indexOf(']');
        if (fechaColchete == -1) {
            return 0;
        }
        try {
            int delay = Integer.parseInt(linha.substring(1, fechaColchete).trim());
            // 1 beat = semínima (duração 3)
            for (int b = 0; b < delay; b++) {
                contexto.adicionarPausa(ContextoTraducao.DURACAO_PADRAO);
            }
        } catch (NumberFormatException ignored) {
            // O que está entre colchetes não é um número: o delay é ignorado
        }
        return fechaColchete + 1;
    }
}
