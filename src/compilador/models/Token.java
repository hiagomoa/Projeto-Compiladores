package compilador.models;

public class Token {
    private char Lexema;
    private String Simbol;

    public Token(char lexema, String simbol) {
        Lexema = lexema;
        Simbol = simbol;
    }

    public char getLexema() {
        return Lexema;
    }

    public void setLexema(char lexema) {
        Lexema = lexema;
    }

    public String getSimbol() {
        return Simbol;
    }

    public void setSimbol(String simbol) {
        Simbol = simbol;
    }
}
