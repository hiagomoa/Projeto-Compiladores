package compilador.models;

public class SignalNumbers {
    private String Lexema;
    private String Simbol;
    private Integer Position;

    public SignalNumbers(String lexema, String simbol, Integer position) {
        Lexema = lexema;
        Simbol = simbol;
        Position = position;
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

    public Integer getPosition() {
        return Position;
    }

    public void setPosition(Integer position) {
        Position = position;
    }
}
