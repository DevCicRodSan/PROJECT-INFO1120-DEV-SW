package pepper;

/**
 * Erro ao gravar o arquivo MIDI (pasta sem permissão, disco cheio etc.).
 * É uma exceção não verificada: quem chama decide onde tratá-la (a tela mostra a mensagem).
 */
public class FalhaExportacaoMidi extends RuntimeException {

    public FalhaExportacaoMidi(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
