package compilador;

import Consts.SymbolTableType;
import Consts.Symbols;
import compilador.models.SymbolTable;
import compilador.models.Token;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SemanticAnalizer {
    LinkedList<Token> stack = new LinkedList<Token>();
    LinkedList<String> generatedCode = new LinkedList<String>();
    FileWriter file;
    PrintWriter writer;

    public SemanticAnalizer() throws IOException {
        file = new FileWriter("./Commanders.txt");
        writer = new PrintWriter(file);
    }

    public String Semantic(List<Token> Exit, LinkedList<SymbolTable> symbolTable) {
        int i = 0;
        Token element;
        String param1;
        String param2;

        while (i < Exit.size()) {
            element = Exit.get(i);
            if (element.getSimbol().equals(Symbols.SNUMERO) || element.getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                stack.push(element);
            } else if (analizeType(element).equals("OpArithmetic")) {
                param1 = searchType(symbolTable, stack.pop());
                param2 = searchType(symbolTable, stack.pop());
                if (param1.equals(Symbols.SINTEIRO) && param2.equals(Symbols.SINTEIRO)) {
                    stack.push(new Token("", Symbols.SINTEIRO));
                } else {
                    System.out.println("ERRO OPERAÇÃO ARITMETICA");
                    //TODO: ERROR
                }
            } else if (analizeType(element).equals("OpArithmeticUnity")) {
                param1 = searchType(symbolTable, stack.pop());
                if (param1.equals(Symbols.SINTEIRO)) {
                    stack.push(new Token("", Symbols.SINTEIRO));
                } else {
                    System.out.println("ERRO SINALIZAÇÃO");
                    //TODO: ERROR
                }
            } else if (analizeType(element).equals("OpRelational")) {
                param1 = searchType(symbolTable, stack.pop());
                param2 = searchType(symbolTable, stack.pop());
                if (param1.equals(Symbols.SINTEIRO) && param2.equals(Symbols.SINTEIRO)) {
                    stack.push(new Token("", Symbols.SBOOLEANO));
                } else {
                    System.out.println("ERRO OPERADOR RELACIONAL");
                    //TODO: ERROR
                }
            } else if (analizeType(element).equals("OpLogic")) {
                param1 = searchType(symbolTable, stack.pop());
                param2 = searchType(symbolTable, stack.pop());
                if (param1.equals(Symbols.SBOOLEANO) && param2.equals(Symbols.SBOOLEANO)) {
                    stack.push(new Token("", Symbols.SBOOLEANO));
                } else {
                    System.out.println("ERRO OPERADOR LOGICO");
                    //TODO: ERROR
                }
            } else if (analizeType(element).equals("OpLogicUnity")) {
                param1 = searchType(symbolTable, stack.pop());
                if (param1.equals(Symbols.SBOOLEANO)) {
                    stack.push(new Token("", Symbols.SBOOLEANO));
                } else {
                    System.out.println("ERRO OPERADOR LOGICO NÃO");
                    //TODO: ERROR
                }
            }
            i++;
        }

        while (!stack.isEmpty()) {
            Token lastElement = stack.pop();
            if (lastElement.getSimbol().equals(Symbols.SNUMERO)) {
                return Symbols.SINTEIRO;
            } else return lastElement.getSimbol();
        }
        return "";
    }

    private String analizeType(Token operator) {
        if (operator.getSimbol().equals(Symbols.SMAIS) || operator.getSimbol().equals(Symbols.SMENOS) ||
                operator.getSimbol().equals(Symbols.SMULT) || operator.getSimbol().equals(Symbols.SDIV)) {
            return "OpArithmetic";
        }
        if (operator.getSimbol().equals(Symbols.SPOSITIVO) || operator.getSimbol().equals(Symbols.SNEGATIVO)) {
            return "OpArithmeticUnity";
        }
        if (operator.getSimbol().equals(Symbols.SMAIOR) || operator.getSimbol().equals(Symbols.SMENOR) ||
                operator.getSimbol().equals(Symbols.SMAIORIG) || operator.getSimbol().equals(Symbols.SMENORIG) ||
                operator.getSimbol().equals(Symbols.SDIF)) {
            return "OpRelational";
        }
        if (operator.getSimbol().equals(Symbols.SE) || operator.getSimbol().equals(Symbols.SOU)) {
            return "OpLogic";
        }
        if (operator.getSimbol().equals(Symbols.SNAO)) {//TODO: COLOCAR ISSO NA CONVERSÃO POS FIXA
            return "OpLogicUnity";
        }
        return "i";
    }

    private String searchType(LinkedList<SymbolTable> symbolTable, Token element) {
        int i = 0;

        if (element.getSimbol().equals(Symbols.SINTEIRO) || element.getSimbol().equals(Symbols.SNUMERO)) {
            return Symbols.SINTEIRO;
        }
        if (element.getSimbol().equals(Symbols.SBOOLEANO)) {
            return Symbols.SBOOLEANO;
        }
        while (symbolTable.size() > i) {
            if (symbolTable.get(i).getLexeme().equals(element.getLexema())) {
                return symbolTable.get(i).getType();
            }
            i++;
        }
        return "";
    }

    public void GenerationCode(String label_1, String label_2, String label_3, String label_4) throws IOException {
        writer.println(CompleteWithSpaces(label_1) + CompleteWithSpaces(label_2) + CompleteWithSpaces(label_3) + CompleteWithSpaces(label_4));
    }

    private String CompleteWithSpaces(String label) {
        while (label.length() < 8) {
            label += " ";
        }
        return label;
    }

    public void CloseFile() throws IOException {
        file.close();
    }

    public String FindLabel(LinkedList<SymbolTable> symbolTables, String lexeme){
        Optional<SymbolTable> a = symbolTables.stream().filter(element->element.getLexeme().equals(lexeme)).findFirst();
        if(a.isPresent()){
            return  String.format("%d",a.get().getLabel());
        }
        return "";
    }
}
