package compilador.models;

import java.util.Optional;

public class SymbolTable {

    private String Lexeme;
    private String Type;
    private String Level;
    private Integer Label;

    public SymbolTable(String lexeme, String type, String level, Integer label) {
        Lexeme = lexeme;
        Type = type;
        Level = level;
        Label = label;
    }

    public SymbolTable(String lexeme, String type) {
        Lexeme = lexeme;
        Type = type;
    }

    public String getLexeme() {
        return Lexeme;
    }

    public void setLexeme(String lexeme) {
        Lexeme = lexeme;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public Integer getLabel() {
        return Label;
    }

    public void setLabel(Integer label) {
        Label = label;
    }
}
