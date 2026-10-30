package pepper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class TelaPrincipal extends JFrame {
    private JTextArea areaTexto;
    private ControladorAudio controlador;
    private File arquivoTxtAtual;
    private JSpinner spinnerBpm;
    private JComboBox<Instrumento>[] combosInstrumento;

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
        JButton btnTocar = new JButton("Tocar");
        painelReproducao.add(btnTocar);
        add(painelReproducao, BorderLayout.SOUTH);

        // Painel Lateral: BPM inicial
        JPanel painelBpm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBpm.setBorder(BorderFactory.createTitledBorder("Andamento"));
        // Valor inicial 120, mínimo 20, máximo 300, passo 5
        spinnerBpm = new JSpinner(new SpinnerNumberModel(controlador.getBpm(), 20, 300, 5));
        painelBpm.add(new JLabel("BPM:"));
        painelBpm.add(spinnerBpm);

        // Painel Lateral: instrumento base de cada voz
        int totalVozes = controlador.getTotalVozes();
        JPanel painelInstrumentos = new JPanel(new GridLayout(totalVozes, 2, 5, 5));
        painelInstrumentos.setBorder(BorderFactory.createTitledBorder("Instrumentos"));
        @SuppressWarnings("unchecked")
        JComboBox<Instrumento>[] combos = new JComboBox[totalVozes];
        combosInstrumento = combos;
        for (int i = 0; i < totalVozes; i++) {
            combosInstrumento[i] = new JComboBox<>(Instrumento.values());
            combosInstrumento[i].setSelectedItem(controlador.getInstrumentoVoz(i));
            painelInstrumentos.add(new JLabel("Voz " + i + ":"));
            painelInstrumentos.add(combosInstrumento[i]);
        }
        JPanel painelLateral = new JPanel(new BorderLayout());
        painelLateral.add(painelBpm, BorderLayout.NORTH);
        painelLateral.add(painelInstrumentos, BorderLayout.CENTER);
        // Envolve em outro painel para os componentes não esticarem até o fim da janela
        JPanel painelLateralTopo = new JPanel(new BorderLayout());
        painelLateralTopo.add(painelLateral, BorderLayout.NORTH);
        add(painelLateralTopo, BorderLayout.EAST);

        // Ações dos Botões
        btnTocar.addActionListener(this::aoClicarTocar);
        btnAbrirTxt.addActionListener(this::aoClicarAbrirTxt);
        btnSalvarTxt.addActionListener(this::aoClicarSalvarTxt);
        btnExportarMidi.addActionListener(this::aoClicarExportarMidi);
    }

    // A janela fica bloqueada até a música terminar (decisão de projeto: tudo em uma thread só)
    private void aoClicarTocar(ActionEvent e) {
        aplicarConfiguracao();
        controlador.tocarTexto(areaTexto.getText());
    }

    private void aoClicarAbrirTxt(ActionEvent e) {
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
    }

    private void aoClicarSalvarTxt(ActionEvent e) {
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
    }

    private void aoClicarExportarMidi(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos MIDI", "mid"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivoMidi = chooser.getSelectedFile();
            if (!arquivoMidi.getName().toLowerCase().endsWith(".mid")) {
                arquivoMidi = new File(arquivoMidi.getParentFile(), arquivoMidi.getName() + ".mid");
            }
            aplicarConfiguracao();
            controlador.exportarMidi(areaTexto.getText(), arquivoMidi.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "MIDI exportado com sucesso!");
        }
    }

    // Passa ao controlador o que o usuário configurou na tela (BPM e instrumento de cada voz)
    private void aplicarConfiguracao() {
        controlador.setBpm((Integer) spinnerBpm.getValue());
        for (int i = 0; i < combosInstrumento.length; i++) {
            controlador.setInstrumentoVoz(i, (Instrumento) combosInstrumento[i].getSelectedItem());
        }
    }
}