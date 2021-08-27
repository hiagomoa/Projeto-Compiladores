package compilador.models;

public class Token {
    private String Lexema;
    private String Simbol;

    public Token(String lexema, String simbol) {
        Lexema = lexema;
        Simbol = simbol;
    }

    public String getLexema() {
        return Lexema;
    }

    public void setLexema(String lexema) {
        Lexema = lexema;
    }

    public String getSimbol() {
        return Simbol;
    }

    public void setSimbol(String simbol) {
        Simbol = simbol;
    }
}
