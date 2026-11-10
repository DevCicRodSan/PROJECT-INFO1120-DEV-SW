package pepper;

import java.util.ArrayList;
import java.util.List;

public class TradutorInstrucao {
    private static final String PAUSA = "R";
    private static final int PASSO_BPM = 10; // '>' acelera e '<' desacelera

    // Configurações base das 4 vozes
    private static final int[] OITAVAS_BASE = {6, 5, 4, 3};
    private static final int[] VOLUMES_BASE = {100, 80, 60, 40};
    // Os instrumentos base vêm do ControladorAudio (escolhidos na interface)

    private int instrumentoAtual;
    private int oitavaAtual;
    private int volumeAtual;
    private int oitavaBaseDaLinha;
    private int variacaoBpm;
    private String ultimaNota;
    private boolean anteriorEraNota;

    // instrumentosBase: número GM (1 a 128) do instrumento inicial de cada voz
    public List<List<EventoAudio>> traduzir(String texto, int[] instrumentosBase) {
        List<List<EventoAudio>> trilhas = new ArrayList<>();
        if (texto == null || texto.isEmpty()) {
            return trilhas;
        }

        String[] linhas = texto.split("\n");

        for (int i = 0; i < linhas.length; i++) {
            int idVoz = i % 4; // Cicla entre 0 e 3
            resetarEstado(instrumentosBase[idVoz], OITAVAS_BASE[idVoz], VOLUMES_BASE[idVoz]);
            trilhas.add(processarLinha(linhas[i]));
        }
        return trilhas;
    }

    private List<EventoAudio> processarLinha(String linha) {
        List<EventoAudio> eventos = new ArrayList<>();
        int i = 0;

        // Processa delay inicial [n] nas linhas
        if (linha.startsWith("[")) {
            int fechaColchete = linha.indexOf(']');
            if (fechaColchete != -1) {
                try {
                    int delay = Integer.parseInt(linha.substring(1, fechaColchete).trim());
                    // 1 beat = semínima (duração 3)
                    for (int b = 0; b < delay; b++) {
                        eventos.add(new EventoAudio(PAUSA, instrumentoAtual, oitavaAtual, volumeAtual, 3, variacaoBpm));
                    }
                } catch (NumberFormatException ignored) {}
                i = fechaColchete + 1;
            }
        }

        while (i < linha.length()) {
            char c = linha.charAt(i);
            String instrucao = String.valueOf(c);

            if (c >= 'A' && c <= 'G') {
                int duracao = 3; // Padrão: semínima
                
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

                eventos.add(new EventoAudio(instrucao, instrumentoAtual, oitavaAtual, volumeAtual, duracao, variacaoBpm));
                ultimaNota = instrucao;
                anteriorEraNota = true;
            } else if (c == '?') {
                // Sobe uma oitava relativa à oitava base da voz
                oitavaAtual = oitavaBaseDaLinha + 1;
                anteriorEraNota = false;
            } else if (c == 'V') {
                // Desce uma oitava relativa à oitava base da voz
                oitavaAtual = Math.max(1, oitavaBaseDaLinha - 1);
                anteriorEraNota = false;
            } else if (c == ' ') {
                // Dobra volume
                volumeAtual = Math.min(127, volumeAtual * 2);
                anteriorEraNota = false;
            } else if (c == '>') {
                // Acelera o BPM global
                variacaoBpm += PASSO_BPM;
                anteriorEraNota = false;
            } else if (c == '<') {
                // Desacelera o BPM global
                variacaoBpm -= PASSO_BPM;
                anteriorEraNota = false;
            } else {
                // Outras letras/consoantes (incluindo H): se anterior era nota, repete; senão pausa
                if (anteriorEraNota && instrucao.matches("[^OUIoui0-9?!;,\n<> ]")) {
                    eventos.add(new EventoAudio(ultimaNota, instrumentoAtual, oitavaAtual, volumeAtual, 3, variacaoBpm));
                } else {
                    anteriorEraNota = false;
                }
            }
            i++;
        }
        return eventos;
    }

    private void resetarEstado(int instrumento, int oitava, int volume) {
        this.instrumentoAtual = instrumento;
        this.oitavaAtual = oitava;
        this.oitavaBaseDaLinha = oitava;
        this.volumeAtual = volume;
        this.variacaoBpm = 0;
        this.anteriorEraNota = false;
        this.ultimaNota = PAUSA;
    }
}
