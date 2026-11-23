package pepper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Leitura e gravação de arquivos de texto (.txt) com a música.
 * Separada do ControladorAudio, que só cuida de áudio.
 */
public class ArquivoTextoService {

    public String carregar(File arquivo) throws IOException {
        return Files.readString(arquivo.toPath());
    }

    public void salvar(String texto, File arquivo) throws IOException {
        Files.writeString(arquivo.toPath(), texto);
    }
}
