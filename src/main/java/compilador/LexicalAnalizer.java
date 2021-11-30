package compilador;

import compilador.Consts.Symbols;
import compilador.models.Token;

import java.util.LinkedList;

/**
 * Class <b>LexicalAnalizer</b> responsavel por toda analize lexica do compilador
 */
public class LexicalAnalizer {
    public int i = 0;
    public byte[] data;
    public LinkedList<Token> listToken = new LinkedList<Token>();
    int line = 0;
    int col = 0;
    public LexicalAnalizer(byte[] data) {
        this.data = data;
    }

    /**
     * método <b>lexical</b>, principal do analizador lexico,
     * percorrendo o array de bytes data,setado globalmente,
     * validando chaves ('{','}') e espaços em brancos,
     * até encontrar algo diferente de chaves e espaços vazios.
     * @throws Exception
     * @return <b>listToken</b> um LinkedList com Lexema e simbolo
     * @see Token
     */
    public LinkedList<Token> lexical() throws Exception {
        int length = data.length;
        for (i = 0; i < length; i++) {
            while ((i < length ) && (((char) data[i] == '{') || Character.isSpace((char) data[i]))) {
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
            }
        }
        return listToken;
    }

    /**
     * método <b>getToken</b> responsável por checar se o token lido se enquadra
     * em qual categoria (digito, palavra, atribuição
     * operador aritimetico e relacional ou pontuação)
     * @throws Exception
     */
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

    /**
     * método <b>managerDigit</b> responsável por salvar na lista de token, o token de numero, com seu Lexema("digit")
     * e símbolo("Symbols.SNUMERO")
     */
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

    /**
     * método <b>managerIdentifyAndReservedWord</b> responsável por identificar e administrar palavras reservadas
     * adicionando-as a lista de token
     */
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

    /**
     * método <b>managerAttribution</b> responsável por identificar uma atribuição ou dois pontos e
     * salvar na lista de token, com Lexema e simbolo
     */
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

    /**
     * método <b>managerOperatorArithmetic</b> responsável por identificar e salvar na lista de tokens
     * operadores aritmeticos
     */
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

    /**
     * método <b>managerRelationalOperator</b> identifica e insere na lista de tokens os operadores
     * relacionais (!, !=, <, <=, >, >= ou =)
     * @throws Exception
     */
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
            throw new Exception("[Error] -- "+(line+1)+" : "+(i-col)+" ||=> "+ (char)data[i] + " não é !=");

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

    /**
     * método <b>managerPontuation</b> identifica pontuação e insere na lista de tokens o lexema da pontuação e o simbolo
     */
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
            if(i < data.length-1) {
                if (Character.isSpace((char)data[i+1])) {
                    i++;
                }
            }
            return;
        }
    }

    /**
     * método <b>verifyIdentifyAndReservedWord</b> identifica palavra reservada e retorna seu simbolo.
     * @param id identificador da palavra reservada
     * @return simbolo String indentificada.
     * @see Symbols
     */
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
