package compilador;

import compilador.models.Token;

import java.util.LinkedList;

public class LexicalAnalizer {
    public void analizer(byte[] data) {
//        LinkedList<Token> listToken = new LinkedList<Token>();
        int length = data.length;
        for (int i = 0; i < length; i++) {
            char currentChar = (char) data[i];
            while (((currentChar == '{') || Character.isWhitespace(currentChar)) && (i < length-1)) {//Faça {Enquanto ((caractere = “{“) ou (caractere = espaço)) e (não acabou o arquivo fonte)
                if (currentChar == '{') {// Se caractere = “{“
                    while ((currentChar != '}') && (i < length-1)) {//Enquanto (caractere != “}” ) e(não acabou o arquivo fonte)
                        currentChar = (char) data[++i];
                    }
                    currentChar = (char) data[++i];
                }
                while (Character.isWhitespace(currentChar) && (i < length-1)) {
                    currentChar = (char) data[++i];
                }
            }
            if(currentChar != (char)data[length-1]){
                System.out.println(currentChar);
                //TODO: pegar token
                //listToken.add(new Token(currentChar,"bla"));
            }
        }
    }
}
