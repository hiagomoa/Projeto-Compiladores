package compilador;

import Consts.Symbols;
import compilador.models.Token;

import java.util.LinkedList;

public class LexicalAnalizer {
    public int i = 0;
    public byte[] data;
    public LinkedList<Token> listToken = new LinkedList<Token>();
    int line = 0;
    int col = 0;
    public LexicalAnalizer(byte[] data) {
        this.data = data;
    }

    public LinkedList<Token> lexical() throws Exception {
        int length = data.length;

        for (i = 0; i < length; i++) {
            while ((i < length - 1) && (((char) data[i] == '{') || Character.isSpace((char) data[i]))) {
                if ((char) data[i] == '{') {
                    while (((char) data[i] != '}') && (i < length - 1)) {
                        if (i == length - 1) {
                            throw new Exception("[Error] -- not found \"}\"");
                        }
                        i++;
                    }
                    i++;
                }
                while ((i < length ) && Character.isSpace((char) data[i])) {
                    if((char) data[i]=='\n'){
                        line++;
                        col=i;
                    }
                    i++;
                }
            }

            if (i < length) {
                getToken();
                //i++;
            }
        }
//        for (Token token : listToken) {
//            System.out.printf("%s # %s\n", token.getLexema(), token.getSimbol());
//        }
        return listToken;
    }

    private void getToken() throws Exception {
        if (Character.isDigit(data[i])) {
            managerDigit();
            return;
        }
        if (Character.isLetter(data[i])) {
            managerIdentifyAndReservedWord();
            return;
        }
        if (data[i] == ':') {
            managerAttribution();
            return;
        }
        if (data[i] == '+' || data[i] == '-' || data[i] == '*') {
            managerOperatorArithmetic();
            return;
        }
        if (data[i] == '!' || data[i] == '<' || data[i] == '>' || data[i] == '=') {
            managerRelationalOperator();
            return;
        }
        if (data[i] == ';' || data[i] == ',' || data[i] == '(' || data[i] == ')' || data[i] == '.') {
            managerPontuation();
            return;
        }
        throw new Exception("[Error] -- "+(line+1)+":"+(i-col)+"||=> "+ (char)data[i] + " é inválido!");
    }

    private void managerDigit() {
        String digit = new String();
        digit += (char) data[i];
        i++;
        while (Character.isDigit((char) data[i])) {
            digit += (char) data[i];
            i++;
        }
        i--;
        listToken.add(new Token(digit, Symbols.SNUMERO));
    }

    private void managerIdentifyAndReservedWord() {
        String id = new String();
        id += (char) data[i];
        i++;
        while (Character.isLetter((char) data[i]) || Character.isDigit((char) data[i]) || (char) data[i] == '_') {
            id += (char) data[i];
            i++;
        }
        i--;
        String symbol = verifyIdentifyAndReservedWord(id);
        listToken.add(new Token(id, symbol));
    }

    private void managerAttribution() {
        String operatorAttribution = new String();
        operatorAttribution += (char) data[i];
        i++;
        if ((char) data[i] == '=') {
            operatorAttribution += (char) data[i];
            listToken.add(new Token(operatorAttribution, Symbols.SATRIBUICAO));
        } else {
            listToken.add(new Token(operatorAttribution, Symbols.SDOIS_PONTOS));
            i--;

        }
    }

    private void managerOperatorArithmetic() {
        String operatorArithmetic = new String();
        operatorArithmetic += (char) data[i];
        if ((char) data[i] == '+') {
            listToken.add(new Token(operatorArithmetic, Symbols.SMAIS));
            return;
        }
        if ((char) data[i] == '-') {
            listToken.add(new Token(operatorArithmetic,  Symbols.SMENOS));
            return;
        }
        if ((char) data[i] == '*') {
            listToken.add(new Token(operatorArithmetic, Symbols.SMULT));
            return;
        }
    }

    private void managerRelationalOperator() throws Exception {
        String relationalOperator = new String();
        relationalOperator += (char) data[i];
        if ((char) data[i] == '!') {
            i++;
            if ((char) data[i] == '=') {
                relationalOperator += (char) data[i];
                listToken.add(new Token(relationalOperator, Symbols.SDIF));
                return;
            }
            throw new Exception("[Error] -- "+(line+1)+":"+(i-col)+"||=> "+ (char)data[i] + " is not a !=");

        }
        if ((char) data[i] == '<') {
            i++;
            if ((char) data[i] == '=') {
                relationalOperator += (char) data[i];
                listToken.add(new Token(relationalOperator, Symbols.SMENORIG));
                return;
            }
            listToken.add(new Token(relationalOperator, Symbols.SMENOR));
            i--;
            return;
        }
        if ((char) data[i] == '>') {
            i++;
            if ((char) data[i] == '=') {
                relationalOperator += (char) data[i];
                listToken.add(new Token(relationalOperator, Symbols.SMAIORIG));
                return;
            }
            listToken.add(new Token(relationalOperator, Symbols.SMAIOR));
            i--;
            return;
        }
        if ((char) data[i] == '=') {
            listToken.add(new Token(relationalOperator,Symbols.SIG));
            return;
        }
    }

    private void managerPontuation() {
        String pontuation = new String();
        pontuation += (char) data[i];
        if ((char) data[i] == ';') {
            listToken.add(new Token(pontuation, Symbols.SPONTO_VIRGULA));
            return;
        }
        if ((char) data[i] == ',') {
            listToken.add(new Token(pontuation, Symbols.SVIRGULA));
            return;
        }
        if ((char) data[i] == '(') {
            listToken.add(new Token(pontuation, Symbols.SABRE_PARENTESES));
            return;
        }
        if ((char) data[i] == ')') {
            listToken.add(new Token(pontuation, Symbols.SFECHA_PARENTESES));
            return;
        }
        if ((char) data[i] == '.') {
            listToken.add(new Token(pontuation, Symbols.SPONTO));
            return;
        }
    }

    private String verifyIdentifyAndReservedWord(String id) {
        switch (id) {
            case "programa":
                return Symbols.SPROGRAMA;

            case "se":
                return Symbols.SSE;

            case "entao":
                return Symbols.SENTAO;

            case "senao":
                return Symbols.SSENAO;

            case "enquanto":
                return Symbols.SENQUANTO;

            case "faca":
                return Symbols.SFACA;

            case "inicio":
                return Symbols.SINICIO;

            case "fim":
                return Symbols.SFIM;

            case "escreva":
                return Symbols.SESCREVA;

            case "leia":
                return Symbols.SLEIA;

            case "var":
                return Symbols.SVAR;

            case "inteiro":
                return Symbols.SINTEIRO;

            case "booleano":
                return Symbols.SBOOLEANO;

            case "verdadeiro":
                return Symbols.SVERDADEIRO;

            case "falso":
                return Symbols.SFALSO;

            case "procedimento":
                return Symbols.SPROCEDIMENTO;

            case "funcao":
                return Symbols.SFUNCAO;

            case "div":
                return Symbols.SDIV;

            case "e":
                return Symbols.SE;

            case "ou":
                return Symbols.SOU;

            case "nao":
                return Symbols.SNAO;

            default:
                return Symbols.SIDENTIFICADOR;
        }
    }


}
