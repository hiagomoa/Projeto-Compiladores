package compilador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//responsavel por fazer a abertura e a leitura do arquivo
public class FileReader {
    public byte[] reader() throws IOException {
        Path path = Paths.get("src/Arquivos/finalTest.txt");
        byte[] data = Files.readAllBytes(path);
        return data;
    }
}
