package compilador;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws Exception {
        byte[] file = new FileReader().reader();
        new LexicalAnalizer(file).lexical();
    }
}
