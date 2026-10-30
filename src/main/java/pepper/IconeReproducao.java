package pepper;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// Ícone desenhado em código para o botão de reprodução. Mostra a ação disponível em cada estado:
// PARADO -> triângulo (tocar); TOCANDO -> quadrado (parar).
// Desenhar evita depender de arquivos de imagem ou de caracteres que nem toda fonte tem.
public class IconeReproducao implements Icon {
    private static final int TAMANHO = 14;

    private final EstadoReproducao estado;

    public IconeReproducao(EstadoReproducao estado) {
        this.estado = estado;
    }

    @Override
    public void paintIcon(Component componente, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (estado == EstadoReproducao.PARADO) {
            g2.setColor(new Color(0x2E7D32)); // verde
            int[] xs = {x + 2, x + TAMANHO - 1, x + 2};
            int[] ys = {y + 1, y + TAMANHO / 2, y + TAMANHO - 1};
            g2.fillPolygon(xs, ys, 3);
        } else {
            g2.setColor(new Color(0xC62828)); // vermelho
            g2.fillRect(x + 2, y + 2, TAMANHO - 4, TAMANHO - 4);
        }
        g2.dispose();
    }

    @Override
    public int getIconWidth() { return TAMANHO; }

    @Override
    public int getIconHeight() { return TAMANHO; }
}
