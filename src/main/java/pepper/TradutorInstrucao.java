package pepper;

import java.util.ArrayList;
import java.util.List;

public class TradutorInstrucao {

    // Configurações base das 4 vozes
    private static final int[] OITAVAS_BASE = {6, 5, 4, 3};
    private static final int[] VOLUMES_BASE = {100, 80, 60, 40};
    // Os instrumentos base vêm do ControladorAudio (escolhidos na interface)

    private final ContextoTraducao contexto = new ContextoTraducao();

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
        int i = processarDelayInicial(linha);

        while (i < linha.length()) {
            char c = linha.charAt(i);
            String instrucao = String.valueOf(c);

            if (c >= 'A' && c <= 'G') {
                int duracao = ContextoTraducao.DURACAO_PADRAO;

                // Look-ahead para acidente (# ou b)
                if (i + 1 < linha.length() && (linha.charAt(i + 1) == '#' || linha.charAt(i + 1) == 'b')) {
                    instrucao += linha.charAt(i + 1);
                    i++;
                }

                // Look-ahead para duração (:X)
                if (i + 2 < linha.length() && linha.charAt(i + 1) == ':' && Character.isDigit(linha.charAt(i + 2))) {
                    duracao = Character.getNumericValue(linha.charAt(i + 2));
                    i += 2;
                }

                contexto.adicionarNota(instrucao, duracao);
            } else if (c == '?') {
                contexto.subirOitava();
            } else if (c == 'V') {
                contexto.descerOitava();
            } else if (c == ' ') {
                contexto.dobrarVolume();
            } else if (c == '>') {
                contexto.acelerar();
            } else if (c == '<') {
                contexto.desacelerar();
            } else {
                // Outras letras/consoantes (incluindo H): se anterior era nota, repete; senão pausa
                if (contexto.ultimaInstrucaoFoiNota() && instrucao.matches("[^OUIoui0-9?!;,\n<> ]")) {
                    contexto.repetirUltimaNota();
                } else {
                    contexto.esquecerUltimaNota();
                }
            }
            i++;
        }
        return contexto.getEventos();
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
        }
        return fechaColchete + 1;
    }
}
