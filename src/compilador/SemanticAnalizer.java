package compilador;

import Consts.SymbolTableType;
import Consts.Symbols;
import compilador.models.SymbolTable;
import compilador.models.Token;

import java.util.LinkedList;
import java.util.List;

public class SemanticAnalizer {

    public SemanticAnalizer() {
    }

    public void semantic(List<Token> Exit, LinkedList<SymbolTable> symbolTable){

    }
    private String analizeType(String param1,String param2, String operator){
        if(operator.equals("+")||operator.equals("-")||operator.equals("*")||operator.equals("div")){
            if(param1.equals(Symbols.SINTEIRO) && param2.equals(Symbols.SINTEIRO)){
                return Symbols.SINTEIRO;
            }
        }

        if(operator.equals("u")){
            if(param1.equals(Symbols.SINTEIRO))
                return  Symbols.SINTEIRO;
        }


        return "i";
    }
    private String searchType(LinkedList<SymbolTable> symbolTable, Token element){
        int i=0;
        while(symbolTable.size()>i) {
            if(symbolTable.get(i).equals(element.getLexema())){
                return symbolTable.get(i).getType();
            }
        }
        return "";
    }
}
