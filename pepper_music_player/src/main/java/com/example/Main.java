package com.example;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Runnable tarefaDeInicializacao = new Runnable() {
            @Override
            public void run() {
                TelaPrincipal tela = new TelaPrincipal();
                tela.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(tarefaDeInicializacao);
    }
}