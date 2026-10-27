package com.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class TelaPrincipal extends JFrame {
    private JTextArea areaTexto;
    private ControladorAudio controlador;
    private File arquivoTxtAtual;

    public TelaPrincipal() {
        this.controlador = new ControladorAudio();

        setTitle("Pepper Music Player");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Área Central (Texto)
        areaTexto = new JTextArea();
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        add(scrollPane, BorderLayout.CENTER);

        // Painel Superior (Botões de Arquivo)
        JPanel painelArquivos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAbrirTxt = new JButton("Abrir TXT");
        JButton btnSalvarTxt = new JButton("Salvar TXT");
        JButton btnExportarMidi = new JButton("Exportar MIDI");

        painelArquivos.add(btnAbrirTxt);
        painelArquivos.add(btnSalvarTxt);
        painelArquivos.add(btnExportarMidi);
        add(painelArquivos, BorderLayout.NORTH);

        // Painel Inferior (Botões de Reprodução)
        JPanel painelReproducao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnTocar = new JButton("Tocar (JFugue)");

        painelReproducao.add(btnTocar);
        add(painelReproducao, BorderLayout.SOUTH);

        // Ações dos Botões
        btnTocar.addActionListener(e -> controlador.tocarTexto(areaTexto.getText()));

        btnAbrirTxt.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto", "txt"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    arquivoTxtAtual = chooser.getSelectedFile();
                    areaTexto.setText(controlador.carregarArquivoTxt(arquivoTxtAtual));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao abrir: " + ex.getMessage());
                }
            }
        });

        btnSalvarTxt.addActionListener(e -> {
            if (arquivoTxtAtual != null) {
                try {
                    controlador.salvarArquivoTxt(areaTexto.getText(), arquivoTxtAtual);
                    JOptionPane.showMessageDialog(this, "Arquivo salvo com sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Abra um arquivo TXT primeiro.");
            }
        });

        btnExportarMidi.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Arquivos MIDI", "mid"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File arquivoMidi = chooser.getSelectedFile();
                if (!arquivoMidi.getName().toLowerCase().endsWith(".mid")) {
                    arquivoMidi = new File(arquivoMidi.getParentFile(), arquivoMidi.getName() + ".mid");
                }
                controlador.exportarMidi(areaTexto.getText(), arquivoMidi.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "MIDI exportado com sucesso!");
            }
        });
    }
}