package compilador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {
    public byte[] reader() throws IOException {
        Path path = Paths.get("src/Arquivos/Syntatic/sint9.txt");
        byte[] data = Files.readAllBytes(path);
        return data;
    }
}
