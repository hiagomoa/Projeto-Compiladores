package compilador;

import compilador.Consts.Symbols;
import compilador.models.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConversionPosFixed {
    public ConversionPosFixed() {
    }

    public List<Token> InFixedToPosFixed(List<Token> expr) throws Exception{
        int i = 0;
        List<Token> Exit = new ArrayList<Token>();
        Token currentValue;
        LinkedList<Token> stack = new LinkedList<Token>();

        do {
            currentValue = expr.get(i);
            i++;
            if (currentValue.getSimbol().equals(Symbols.SIDENTIFICADOR) || currentValue.getSimbol().equals(Symbols.SNUMERO)||currentValue.getSimbol().equals(Symbols.SVERDADEIRO)||currentValue.getSimbol().equals(Symbols.SFALSO)) {
                Exit.add(currentValue);
            } else if (currentValue.getSimbol().equals(Symbols.SABRE_PARENTESES)) {
                stack.push(currentValue);
            } else if (currentValue.getSimbol().equals(Symbols.SFECHA_PARENTESES)) {
                while (!stack.peek().getSimbol().equals(Symbols.SABRE_PARENTESES)) {
                    Exit.add(stack.pop());
                }
                stack.pop();
            } else if (currentValue.getSimbol().equals(Symbols.SMAIS) || currentValue.getSimbol().equals(Symbols.SMENOS) ||
                    currentValue.getSimbol().equals(Symbols.SMULT) || currentValue.getSimbol().equals(Symbols.SDIV) ||
                    currentValue.getSimbol().equals(Symbols.SMAIOR) || currentValue.getSimbol().equals(Symbols.SMENOR) ||
                    currentValue.getSimbol().equals(Symbols.SMAIORIG) || currentValue.getSimbol().equals(Symbols.SMENORIG) ||
                    currentValue.getSimbol().equals(Symbols.SDIF) || currentValue.getSimbol().equals(Symbols.SE) ||
                    currentValue.getSimbol().equals(Symbols.SOU) || currentValue.getSimbol().equals(Symbols.SPOSITIVO) ||
                    currentValue.getSimbol().equals(Symbols.SNEGATIVO) ||   currentValue.getSimbol().equals(Symbols.SNAO)||
                    currentValue.getSimbol().equals(Symbols.SIG)
            ) {
                while (true) {
                    if (!stack.isEmpty()) {
                        if (Priority(currentValue, stack.peek())) {
                            stack.push(currentValue);
                            break;
                        } else {
                            Exit.add(stack.pop());
                            stack.push(currentValue);
                            break;
                        }
                    } else {
                        stack.push(currentValue);
                        break;
                    }
                }
            }
        } while (expr.size() > i);
        while (!stack.isEmpty()) {
            Exit.add(stack.pop());
        }

        return Exit;
    }

    private boolean Priority(Token current, Token top) {
        int pc = 0, pt = 0;


         if (current.getSimbol().equals(Symbols.SE))
            pc = 8;
        else if (current.getSimbol().equals(Symbols.SNAO))
            pc = 7;
        else if (current.getSimbol().equals(Symbols.SMAIOR) || current.getSimbol().equals(Symbols.SMENOR) || current.getSimbol().equals(Symbols.SDIF)||
                current.getSimbol().equals(Symbols.SMAIORIG) || current.getSimbol().equals(Symbols.SMENORIG) || current.getSimbol().equals(Symbols.SIG))
            pc = 6;
        else if (current.getSimbol().equals(Symbols.SOU))
            pc = 5;
        else if (current.getSimbol().equals(Symbols.SPOSITIVO) || current.getSimbol().equals(Symbols.SNEGATIVO))
            pc = 4;
        else if (current.getSimbol().equals(Symbols.SMULT) || current.getSimbol().equals(Symbols.SDIV))
            pc = 3;
        else if (current.getSimbol().equals(Symbols.SMAIS) || current.getSimbol().equals(Symbols.SMENOS))
            pc = 2;
        else if (current.getSimbol().equals(Symbols.SABRE_PARENTESES))
            pc = 0;


        if (top.getSimbol().equals(Symbols.SE))
            pt = 8;
        else if (top.getSimbol().equals(Symbols.SNAO))
            pt = 7;
        else if (top.getSimbol().equals(Symbols.SMAIOR) || top.getSimbol().equals(Symbols.SMENOR) || top.getSimbol().equals(Symbols.SDIF) ||
                top.getSimbol().equals(Symbols.SMAIORIG) || top.getSimbol().equals(Symbols.SMENORIG)||top.getSimbol().equals(Symbols.SIG))
            pt = 6;
        else if (top.getSimbol().equals(Symbols.SOU))
            pt = 5;
        else if (top.getSimbol().equals(Symbols.SPOSITIVO) || top.getSimbol().equals(Symbols.SNEGATIVO))
            pt = 4;
        else if (top.getSimbol().equals(Symbols.SMULT) || top.getSimbol().equals(Symbols.SDIV))
            pt = 3;
        else if (top.getSimbol().equals(Symbols.SMAIS) || top.getSimbol().equals(Symbols.SMENOS))
            pt = 2;
        else if (top.getSimbol().equals(Symbols.SABRE_PARENTESES))
            pt = 0;

        return (pc > pt);
    }
}
