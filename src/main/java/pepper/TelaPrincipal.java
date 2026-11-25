package pepper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TelaPrincipal extends JFrame {
    private JTextArea areaTexto;
    private ControladorAudio controlador;
    private ArquivoTextoService arquivoService = new ArquivoTextoService();
    private File arquivoTxtAtual;
    private JSpinner spinnerBpm;
    private JComboBox<Instrumento>[] combosInstrumento;

    private JButton btnAbrirTxt;
    private JButton btnSalvarTxt;
    private JButton btnExportarMidi;
    private JButton btnTocar;

    public TelaPrincipal() {
        this.controlador = new ControladorAudio();

        setTitle("Pepper Music Player");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inicializarComponentes();
    }

    // Monta a janela juntando as partes; cada parte é criada por um método próprio
    private void inicializarComponentes() {
        add(criarEditorTexto(), BorderLayout.CENTER);
        add(criarPainelArquivos(), BorderLayout.NORTH);
        add(criarPainelReproducao(), BorderLayout.SOUTH);
        add(criarPainelLateral(), BorderLayout.EAST);
        configurarAcoes();
    }

    // Área central (texto)
    private JScrollPane criarEditorTexto() {
        areaTexto = new JTextArea();
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        return new JScrollPane(areaTexto);
    }

    // Painel superior (botões de arquivo)
    private JPanel criarPainelArquivos() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAbrirTxt = new JButton("Abrir TXT");
        btnSalvarTxt = new JButton("Salvar TXT");
        btnExportarMidi = new JButton("Exportar MIDI");

        painel.add(btnAbrirTxt);
        painel.add(btnSalvarTxt);
        painel.add(btnExportarMidi);
        return painel;
    }

    // Painel inferior (botão de reprodução)
    private JPanel criarPainelReproducao() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnTocar = new JButton("Tocar");
        painel.add(btnTocar);
        return painel;
    }

    // Painel lateral: BPM inicial e instrumento base de cada voz
    private JPanel criarPainelLateral() {
        JPanel painelLateral = new JPanel(new BorderLayout());
        painelLateral.add(criarPainelBpm(), BorderLayout.NORTH);
        painelLateral.add(criarPainelInstrumentos(), BorderLayout.CENTER);

        // Envolve em outro painel para os componentes não esticarem até o fim da janela
        JPanel painelLateralTopo = new JPanel(new BorderLayout());
        painelLateralTopo.add(painelLateral, BorderLayout.NORTH);
        return painelLateralTopo;
    }

    private JPanel criarPainelBpm() {
        JPanel painelBpm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBpm.setBorder(BorderFactory.createTitledBorder("Andamento"));
        // Valor inicial 120, passo 5; limites vêm do controlador
        SpinnerNumberModel modeloBpm = new SpinnerNumberModel(
                controlador.getBpm(), ControladorAudio.BPM_MINIMO, ControladorAudio.BPM_MAXIMO, 5);
        spinnerBpm = new JSpinner(modeloBpm);
        painelBpm.add(new JLabel("BPM:"));
        painelBpm.add(spinnerBpm);
        return painelBpm;
    }

    private JPanel criarPainelInstrumentos() {
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
        return painelInstrumentos;
    }

    // Liga cada botão ao método que trata o clique
    private void configurarAcoes() {
        btnTocar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aoClicarTocar();
            }
        });
        btnAbrirTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aoClicarAbrirTxt();
            }
        });
        btnSalvarTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aoClicarSalvarTxt();
            }
        });
        btnExportarMidi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aoClicarExportarMidi();
            }
        });
    }

    // A janela fica bloqueada até a música terminar (decisão de projeto: tudo em uma thread só)
    private void aoClicarTocar() {
        aplicarConfiguracao();
        controlador.tocarTexto(areaTexto.getText());
    }

    private void aoClicarAbrirTxt() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                arquivoTxtAtual = chooser.getSelectedFile();
                areaTexto.setText(arquivoService.carregar(arquivoTxtAtual));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir: " + ex.getMessage());
            }
        }
    }

    private void aoClicarSalvarTxt() {
        if (arquivoTxtAtual != null) {
            try {
                arquivoService.salvar(areaTexto.getText(), arquivoTxtAtual);
                JOptionPane.showMessageDialog(this, "Arquivo salvo com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Abra um arquivo TXT primeiro.");
        }
    }

    private void aoClicarExportarMidi() {
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
