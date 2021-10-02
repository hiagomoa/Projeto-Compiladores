package compilador;

import compilador.models.Token;

import java.io.IOException;
import java.util.LinkedList;


public class Main {
    public static void main(String[] args) throws Exception {
        byte[] file = new FileReader().reader();
        LinkedList<Token> listToken = new LexicalAnalizer(file).lexical();
//        for (Token token : listToken) {
//            System.out.printf("%s # %s\n", token.getLexema(), token.getSimbol());
//        }
        new SyntaticAnalyzer(listToken).Syntatic();
    }
}
