package compilador;

import compilador.models.Token;

import java.util.LinkedList;

public class LexicalAnalizer {
    public int i = 0;
    public byte[] data;
    public LinkedList<Token> listToken = new LinkedList<Token>();

    public LexicalAnalizer(byte[] data) {
        this.data = data;
    }

    public void analizer() throws Exception {
//        LinkedList<Token> listToken = new LinkedList<Token>();
        int length = data.length;
        for (i = 0; i < length; i++) {
            char currentChar = (char) data[i];
            while (((currentChar == '{') || Character.isWhitespace(currentChar)) && (i < length - 1)) {//Faça {Enquanto ((caractere = “{“) ou (caractere = espaço)) e (não acabou o arquivo fonte)
                if (currentChar == '{') {// Se caractere = “{“
                    while ((currentChar != '}') && (i < length - 1)) {//Enquanto (caractere != “}” ) e(não acabou o arquivo fonte)
                        currentChar = (char) data[++i];
                    }
                    currentChar = (char) data[++i];
                }
                while (Character.isWhitespace(currentChar) && (i < length - 1)) {
                    currentChar = (char) data[++i];
                }
            }
            if (currentChar != (char) data[length - 1]) {
                System.out.println(currentChar);
                getToken();
            }
        }
        for (Token token : listToken) {
            System.out.printf("%s # %s\n", token.getLexema(), token.getSimbol());
        }

//        for(int j=0;j<listToken.toArray().length;j++){
//            System.out.println(listToken.indexOf(j));
//        }
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
        throw new Exception("Error in Get Token");
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
        listToken.add(new Token(digit,"snumero"));
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
            listToken.add(new Token(operatorAttribution, "satribuicao"));
        } else {
            listToken.add(new Token(operatorAttribution, "sdoispontos"));// TODO: perguntar professor
        }
    }

    private void managerOperatorArithmetic() {
        String operatorArithmetic = new String();
        operatorArithmetic += (char) data[i];
        if ((char) data[i] == '+') {
            listToken.add(new Token(operatorArithmetic, "smais"));
            return;
        }
        if ((char) data[i] == '-') {
            listToken.add(new Token(operatorArithmetic, "smenos"));
            return;
        }
        if ((char) data[i] == '*') {
            listToken.add(new Token(operatorArithmetic, "smult"));
            return;
        }
    }

    private void managerRelationalOperator() {
        String relationalOperator = new String();
        relationalOperator += (char) data[i];
        if ((char) data[i] == '!') {
            i++;
            if ((char) data[i] == '=') {
                relationalOperator += (char) data[i];
                listToken.add(new Token(relationalOperator, "sdif"));
                return;
            }
            //todo:ERROr
            return;
        }
        if ((char) data[i] == '<') {
            i++;
            if ((char) data[i] == '=') {
                relationalOperator += (char) data[i];
                listToken.add(new Token(relationalOperator, "smenorig"));
                return;
            }
            listToken.add(new Token(relationalOperator, "smenor"));
            i--;
            return;
        }
        if ((char) data[i] == '>') {
            i++;
            if ((char) data[i] == '=') {
                relationalOperator += (char) data[i];
                listToken.add(new Token(relationalOperator, "smaiorig"));
                return;
            }
            listToken.add(new Token(relationalOperator, "smaior"));
            i--;
            return;
        }
        if ((char) data[i] == '=') {
            listToken.add(new Token(relationalOperator, "sig"));
            return;
        }
    }

    private void managerPontuation() {
        String pontuation = new String();
        pontuation += (char) data[i];
        if ((char) data[i] == ';') {
            listToken.add(new Token(pontuation, "sponto_virgula"));
            return;
        }
        if ((char) data[i] == ',') {
            listToken.add(new Token(pontuation, "svirgula"));
            return;
        }
        if ((char) data[i] == '(') {
            listToken.add(new Token(pontuation, "sabre_parenteses"));
            return;
        }
        if ((char) data[i] == ')') {
            listToken.add(new Token(pontuation, "sfecha_parenteses"));
            return;
        }
        if ((char) data[i] == '.') {
            listToken.add(new Token(pontuation, "sponto"));
            return;
        }
    }

    private String verifyIdentifyAndReservedWord(String id) {
        switch (id) {
            case "programa":
                return "sprograma";

            case "se":
                return "sse";

            case "entao":
                return "sentao";

            case "senao":
                return "ssenao";

            case "enquanto":
                return "senquanto";

            case "faca":
                return "sfaca";

            case "inicio":
                return "sinicio";

            case "fim":
                return "sfim";

            case "escreva":
                return "sescreva";

            case "leia":
                return "sleia";

            case "var":
                return "svar";

            case "inteiro":
                return "sinteiro";

            case "booleano":
                return "sbooleano";

            case "verdadeiro":
                return "sverdadeiro";

            case "falso":
                return "sfalso";

            case "procedimento":
                return "sprocedimento";

            case "funcao":
                return "sfuncao";

            case "div":
                return "sdiv";

            case "e":
                return "se";

            case "ou":
                return "sou";

            case "nao":
                return "snao";

            default:
                return "sidentificador";
        }
    }


}
