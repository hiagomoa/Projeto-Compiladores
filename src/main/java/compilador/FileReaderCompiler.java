package compilador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReaderCompiler {
    public byte[] reader(String p) throws IOException {
        Path path = Paths.get(p);
        byte[] data = Files.readAllBytes(path);
        return data;
    }
}
