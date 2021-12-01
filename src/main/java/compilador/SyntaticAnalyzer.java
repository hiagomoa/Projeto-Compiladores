package compilador;

import compilador.Consts.SymbolTableType;
import compilador.Consts.Symbols;
import compilador.models.Token;
import compilador.models.SignalNumbers;
import compilador.models.SymbolTable;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class <b>SyntaticAnalizer</b> responsavel por toda analize sintatica do compilador
 */
public class SyntaticAnalyzer {
    int label;
    int i = 0;
    Integer variablesMemory = 0;
    LinkedList<Token> listToken;
    LinkedList<SymbolTable> symbolTable = new LinkedList<SymbolTable>();
    LinkedList<SignalNumbers> inFixedList;
    String level;
    SemanticAnalizer SemanticAnalizer = new SemanticAnalizer();

    public SyntaticAnalyzer(LinkedList<Token> data) throws Exception {
        this.listToken = data;
    }

    /**
     * método <b>Syntatic</b> valida simbolos reservados de estrura de código necessário
     * @throws Exception
     */
    public void Syntatic() throws Exception {
        label = 1;
        if (listToken.get(i).getSimbol().equals(Symbols.SPROGRAMA)) {
            SemanticAnalizer.GenerationCode("", "START", "", "");
            i++;//LEXICO(TOKEN)
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                InsertTable(listToken.get(i).getLexema(), SymbolTableType.STPROGRAMNAME, null, null);
                SemanticAnalizer.GenerationCode("", "ALLOC", String.format("%d", variablesMemory), String.format("%d", ++variablesMemory));

                i++;//LEXICO(TOKEN)
                if (listToken.get(i).getSimbol().equals(Symbols.SPONTO_VIRGULA)) {
                    BlockAnalyzer();
                    if (i < listToken.size() && listToken.get(i).getSimbol().equals(Symbols.SPONTO)) {
                        if (listToken.size() - 1 < i) {
                            throw new Exception("[Error] -- Arquivo não terminou com erro");
                        }
                    } else {
                        throw new Exception("[Error] -- esperado um .");
                    }
                } else {
                    throw new Exception("[Error] -- esperado um ;");
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador");
            }
        }

        int countVariable = 0;
        SymbolTable elementAux;
        int size = symbolTable.size();
        for (int i = 0; i < size; i++) {
                elementAux = symbolTable.pop();
                if (elementAux.getType().equals(Symbols.SINTEIRO) || elementAux.getType().equals(Symbols.SBOOLEANO) || elementAux.getType().equals(Symbols.SVAR)) {
                    countVariable++;
                }
        }
        if (countVariable > 0) {
            variablesMemory = variablesMemory - countVariable;
            SemanticAnalizer.GenerationCode("", "DALLOC", String.format("%d", variablesMemory), String.format("%d", countVariable));
        }
        SemanticAnalizer.GenerationCode("", "DALLOC", String.format("%d", variablesMemory - 1), "1");
        SemanticAnalizer.GenerationCode("", "HLT", "", "");

        SemanticAnalizer.CloseFile();
    }

    /**
     * Método <b>Unstack</b> é responsável por desempilhar e verificar a quantidade de variáveis para ser desalocada
     */
    private void Unstack() throws Exception {
        int countVariable = 0;
        SymbolTable element;
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.peek().getLevel() == null) {
                element = symbolTable.pop();
                if (element.getType().equals(Symbols.SINTEIRO) || element.getType().equals(Symbols.SBOOLEANO) || element.getType().equals(Symbols.SVAR)) {
                    countVariable++;
                }
            } else {
                if (symbolTable.peek().getType().equals("procedimento")) {
                    symbolTable.peek().setLevel(null);
                }
                break;
            }
        }
        if (countVariable > 0) {
            variablesMemory = variablesMemory - countVariable;
            SemanticAnalizer.GenerationCode("", "DALLOC", String.format("%d", variablesMemory), String.format("%d", countVariable));
        }
        SemanticAnalizer.GenerationCode("", "RETURN","", "");
    }

    /**
     * Método <b>SearchDuplicatedVarInTable</b> é responsável por verificar se a variavel já foi declarada
     * dentro do nivel.
     * @param lexeme String
     * @return boolean true para encontrado variavel duplicada, false para o caso contrario
     * @throws Exception
     */
    private boolean SearchDuplicatedVarInTable(String lexeme) throws Exception {
        for (SymbolTable element : symbolTable) {
            if (element.getLevel() == null) {
                if (element.getLexeme().equals(lexeme)) {
                    return true;
                }
            } else if (element.getLevel().equals("L")) {
                return false;
            }
        }
        return false;
    }

    /**
     * Método <b>SearchGlobalDeclaration</b> é responsável por verificar se o identificador
     * está presente na tabela de simbolos
     * @param lexeme String
     * @return boolean true caso o lexema exista na tabela de simbolos falso caso não seja encontrado
     * @throws Exception
     */
    private boolean SearchGlobalDeclaration(String lexeme) throws Exception{
        for (SymbolTable element : symbolTable) {
            if (element.getLexeme().equals(lexeme)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método <b>InsertTable</b> é a é responsavel por inserir na tabela de simbolos,
     * Caso o label seja null significa que esta sendo inserido um nome de programa
     * Nos demais casos são inseridos variaveis, funcoes, e procedimentos
     * @param lexeme String
     * @param type String é um SymbolTableType
     * @see SymbolTableType
     * @param level String marcador de nivel de procedimento ou função, é utilizado o caractere "L"
     * @param label Integer posição da memória ou posicação de função/procedimento
     * @throws Exception
     */
    private void InsertTable(String lexeme, String type, String level, Integer label) throws Exception {
        if (label != null) {
            symbolTable.push(new SymbolTable(lexeme, type, level, label));
            return;
        }
        symbolTable.push(new SymbolTable(lexeme, type));
    }

    /**
     * método <b>BlockAnalyzer</b> é responsavel por analizar uma declaração, SubRotina ou Comando
     * @throws Exception
     */
    private void BlockAnalyzer() throws Exception {
        i++;
        AnalyzeVariablesDeclaration();
        AnalyzeSubRoutine();
        AnalyzeCommands();
    }

    /**
     * método <b>AnalyzeCommands</b> é responsavel por validar a estrutura necessária
     * para o corpo de um comando.
     * @throws Exception
     */
    private void AnalyzeCommands() throws Exception {
        if (listToken.get(i).getSimbol().equals(Symbols.SINICIO)) {
            i++;
            SimpleCommandAnalyser();
            while (!listToken.get(i).getSimbol().equals(Symbols.SFIM)) {
                if (listToken.get(i).getSimbol().equals(Symbols.SPONTO_VIRGULA)) {
                    i++;
                    if (!listToken.get(i).getSimbol().equals(Symbols.SFIM)) {
                        SimpleCommandAnalyser();
                    }
                } else {
                    throw new Exception("[Error] inesperado: " + listToken.get(i).getLexema());
                }
            }
            i++;
        } else {
            throw new Exception("[Error] -- esperado \"inicio\" no lugar de: " + listToken.get(i).getLexema());
        }
    }

    /**
     * Metódo <b>SimpleCommandAnalyser</b> é responsável por verificar palavras reservadas
     * de comandos.
     * @throws Exception
     */
    private void SimpleCommandAnalyser() throws Exception {
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
            ChProcedureAtributeAnalyzer();
        } else if (listToken.get(i).getSimbol().equals(Symbols.SSE)) {
            IfAnalyzer();
        } else if (listToken.get(i).getSimbol().equals(Symbols.SENQUANTO)) {
            WhileAnalyzer();
        } else if (listToken.get(i).getSimbol().equals(Symbols.SLEIA)) {
            ReadAnalyzer();
        } else if (listToken.get(i).getSimbol().equals(Symbols.SESCREVA)) {
            WriteAnalyzer();
        } else {
            AnalyzeCommands();
        }
    }

    /**
     * Metódo <b>AnalyzeVariableDeclaration</b> é responsavel por verificar
     * declaração de variaveis, consultando a existencia de indentificador.
     * @throws Exception
     */
    private void AnalyzeVariablesDeclaration() throws Exception {
        if (listToken.get(i).getSimbol().equals(Symbols.SVAR)) {
            i++;//LEXICO(TOKEN)
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                while (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                    AnalyzeVariables();
                    if (listToken.get(i).getSimbol().equals(Symbols.SPONTO_VIRGULA)) {
                        i++;//LEXICO(TOKEN)
                    } else {
                        throw new Exception("[Error] -- esperado ;");
                    }
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador");
            }
        }
    }

    /**
     * Metódo <b>AnalyzeVariable</b> é responsavel por validar as variaveis que estão sendo criadas
     * verificando seu identificador e inserindo na tabela de simbolos
     * @throws Exception
     */
    private void AnalyzeVariables() throws Exception {
        int auxVariablesMemory = variablesMemory;
        do {
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                boolean isDuplicated = SearchDuplicatedVarInTable(listToken.get(i).getLexema());
                if (!isDuplicated) {
                    InsertTable(listToken.get(i).getLexema(), SymbolTableType.STVARIABLE, null, variablesMemory++);
                    i++;
                    if (listToken.get(i).getSimbol().equals(Symbols.SVIRGULA) || listToken.get(i).getSimbol().equals(Symbols.SDOIS_PONTOS)) {
                        if (listToken.get(i).getSimbol().equals(Symbols.SVIRGULA)) {
                            i++;
                            if (listToken.get(i).getSimbol().equals(Symbols.SDOIS_PONTOS)) {
                                throw new Exception("[Error] -- simbolo não esperado: " + listToken.get(i).getSimbol());
                            }
                        }
                    } else {
                        throw new Exception("[Error] -- simbolo errado esperado \":\" ou \",\"");
                    }
                } else {
                    throw new Exception("[Error] -- simbolo duplicado");
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador para variavel");
            }
        } while (!listToken.get(i).getSimbol().equals(Symbols.SDOIS_PONTOS));
        SemanticAnalizer.GenerationCode("", "ALLOC", String.format("%d", auxVariablesMemory), String.format("%d", variablesMemory - auxVariablesMemory));
        i++;
        AnalyzeType();
    }

    /**
     * Metódo <b>AnalyzeType</b> é responsavel por restringir variaveis a inteiros e booleanos
     * @throws Exception
     */
    private void AnalyzeType() throws Exception {
        if (!listToken.get(i).getSimbol().equals(Symbols.SINTEIRO) && !listToken.get(i).getSimbol().equals(Symbols.SBOOLEANO)) {
            throw new Exception("[Error] -- tipo não esperado");
        } else {
            PushTypeIntoTheTable(listToken.get(i).getLexema());
        }
        i++;
    }

    /**
     * Metódo <b>PushIntoTheTable</b> é responsavel por colocar o tipo corretamente da variavel na tabela de simbolos
     * @throws Exception
     */
    private void PushTypeIntoTheTable(String lexeme) throws Exception {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).getType().equals(SymbolTableType.STVARIABLE)) {
                if (lexeme.equals("inteiro")) {
                    symbolTable.get(i).setType(Symbols.SINTEIRO);
                } else if (lexeme.equals("booleano")) {
                    symbolTable.get(i).setType(Symbols.SBOOLEANO);
                } else {
                    symbolTable.get(i).setType(lexeme);
                }
            } else {
                break;
            }
        }
    }

    /**
     * Metódo <b>ChProcedureAtributeAnalyzer</b> é responsavel por toda lógica de atribuição
     * validando sintaticamente e semanticamente sua corretude.
     * @throws Exception
     */
    private void ChProcedureAtributeAnalyzer() throws Exception {
        i++;
        if (listToken.get(i).getSimbol().equals(Symbols.SATRIBUICAO)) {
            i++;
            inFixedList = new LinkedList<SignalNumbers>();
            int initExpression = i;
            ExpressionAnalyzer();
            int finishExpression = i;

            // slice da expressão para ser enviada para a conversão para pós fixa
            List<Token> sliceInFixed = listToken.subList(initExpression, finishExpression);

            //corrigi o identificador positivo e negatico (+u ,-u) na lista auxiliar
            inFixedList.forEach(element -> {
                sliceInFixed.get(element.getPosition() - initExpression).setLexema(element.getLexema());
                sliceInFixed.get(element.getPosition() - initExpression).setSimbol(element.getSimbol());
            });

            // chamada da conversão pós fixa passando o slice
            List<Token> Exit = new ConversionPosFixed().InFixedToPosFixed(sliceInFixed);
            //chamada da verificação de semantica da saido do pós fixo
            String returnExitExpression = new SemanticAnalizer().Semantic(Exit, symbolTable);
            //pega lexema que será atribuido o retorno da expressão
            String lexemaOnPosition = listToken.get(initExpression - 2).getLexema();
            //verifica existencia de uma possivel variavel na tabela de simbolos retornando posição
            int positionOnTableVariable = searchTableVariable(lexemaOnPosition);
            ScrollExpressionToGenerationCode(Exit);
            if(positionOnTableVariable==-1){
                //caso não exista variavel verifica a existencia de uma posivel função
                int positionOnTableFunction = searchTableFunction(lexemaOnPosition);
                if(positionOnTableFunction!=-1){
                    SemanticAnalizer.GenerationCode("", "STR", "0", "");
                }else{
                    throw new Exception("[Error] -- Erro de atribuição");
                }
            }else{
                Token tokenCurrent = listToken.get(initExpression - 2);
                int positionCurrent = searchTable(tokenCurrent.getLexema(),null);
                //caso exista a variavel verifica o tipo do retorno da expressão e da variavel
                if(!symbolTable.get(positionCurrent-1).getType().equals(returnExitExpression)){
                    throw new Exception("[Error] -- Erro de atribuição");
                }else{
                    SemanticAnalizer.GenerationCode("", "STR", SemanticAnalizer.FindLabel(symbolTable, tokenCurrent.getLexema()), "");
                }
            }
        } else {
            SemanticAnalizer.GenerationCode("", "CALL", SemanticAnalizer.FindLabel(symbolTable, listToken.get(i - 1).getLexema()), "");
        }
    }

    /**
     * Metódo <b>ReadAnalyzer</b> é responsável por gerar o código de máquina que indica a leitura de um dado,
     * verificando a varivel em que sera gravada.
     * Ex: leia(v1) se transforma em:
     * RD
     * STR V1
     * @throws Exception
     */
    private void ReadAnalyzer() throws Exception {
        i++;
        SemanticAnalizer.GenerationCode("", "RD", "", "");
        if (listToken.get(i).getSimbol().equals(Symbols.SABRE_PARENTESES)) {
            i++;
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                if (SearchGlobalDeclaration(listToken.get(i).getLexema())) {
                    Token tokenCurrent = listToken.get(i);
                    String labelFinded = SemanticAnalizer.FindLabel(symbolTable, tokenCurrent.getLexema());
                    int positionCurrent = searchTable(tokenCurrent.getLexema(),null);
                    if(symbolTable.get(positionCurrent-1).getType().equals(Symbols.SBOOLEANO)){
                        throw new Exception("[Error] -- Não é possivel salvar um booleano");
                    }else{
                        SemanticAnalizer.GenerationCode("", "STR", labelFinded, "");
                    }
                    i++;
                    if (listToken.get(i).getSimbol().equals(Symbols.SFECHA_PARENTESES)) {
                        i++;
                    } else {
                        throw new Exception("[Error] -- esperado )");
                    }
                } else {
                    throw new Exception("[Error] -- Esperado uma declaração");
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador");
            }
        } else {
            throw new Exception("[Error] -- esperado um (");
        }
    }

    /**
     * Metódo <b>WriteAnalyzer</b> é responsavel por verificar a escrita de um dado,
     * limitando-o a inteiros.
     * @throws Exception
     */
    private void WriteAnalyzer() throws Exception {
        i++;

        if (listToken.get(i).getSimbol().equals(Symbols.SABRE_PARENTESES)) {
            i++;
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                if (SearchGlobalDeclaration(listToken.get(i).getLexema())) {
                    Token tokenCurrent = listToken.get(i);
                    String labelFinded = SemanticAnalizer.FindLabel(symbolTable, tokenCurrent.getLexema());
                    int positionCurrent = searchTable(tokenCurrent.getLexema(),null);

                    if(symbolTable.get(positionCurrent-1).getType().equals(Symbols.SBOOLEANO)){
                        throw new Exception("[Error] -- Não é possivel printar um booleano");
                    }else{
                        SemanticAnalizer.GenerationCode("", "LDV", labelFinded, "");
                        SemanticAnalizer.GenerationCode("", "PRN", "", "");
                    }

                    i++;
                    if (listToken.get(i).getSimbol().equals(Symbols.SFECHA_PARENTESES)) {
                        i++;
                    } else {
                        throw new Exception("[Error] -- esperado um )");
                    }
                } else {
                    throw new Exception("[Error] -- variavel não encontrada");
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador");
            }
        } else {
            throw new Exception("[Error] -- esperado um (");
        }
    }

    /**
     * Metódo <b>WhileAnalyzer</b> é responsavel por verificar a estrutura de repetição while do código,
     * validando sua expressão.
     * @throws Exception
     */
    private void WhileAnalyzer() throws Exception {
        int auxLabel_1 = label, auxLabel_2;
        i++;
        inFixedList = new LinkedList<SignalNumbers>();
        int initExpression = i;
        ExpressionAnalyzer();
        int finishExpression = i;

        SemanticAnalizer.GenerationCode(String.format("%d", label), "NULL", "", "");
        label = label + 1;

        // slice da expressão para ser enviada para a conversão para pós fixa
        List<Token> sliceInFixed = listToken.subList(initExpression, finishExpression);
        //chamada da verificação de semantica da saida do pós fixo
        inFixedList.forEach(element -> {
            sliceInFixed.get(element.getPosition() - initExpression).setLexema(element.getLexema());
            sliceInFixed.get(element.getPosition() - initExpression).setSimbol(element.getSimbol());
        });
        // chamada da conversão pós fixa passando o slice
        List<Token> Exit = new ConversionPosFixed().InFixedToPosFixed(sliceInFixed);
        ScrollExpressionToGenerationCode(Exit);
        //chamada da verificação de semantica da saido do pós fixo
        String returnExitExpretion = new SemanticAnalizer().Semantic(Exit, symbolTable);
        //espera-se que o retorno seja um booleano
        if (!returnExitExpretion.equals(Symbols.SBOOLEANO)) {
            throw new Exception("[Error] -- Esperado um booleano");
        }
        if (listToken.get(i).getSimbol().equals(Symbols.SFACA)) {
            auxLabel_2 = label;
            SemanticAnalizer.GenerationCode("", "JMPF", String.format("%d", label), "");
            label = label + 1;
            i++;
            SimpleCommandAnalyser();
            SemanticAnalizer.GenerationCode("", "JMP", String.format("%d", auxLabel_1), "");
            SemanticAnalizer.GenerationCode(String.format("%d", auxLabel_2), "NULL", "", "");
        } else {
            throw new Exception("[Error] -- esperado uma palavra reservada faça");
        }
    }

    /**
     * Metódo <b>IfAnalyzer</b> é responsavel por verificar as estruturas condicionais do código,
     * validando a sua expressão.
     * @throws Exception
     */
    private void IfAnalyzer() throws Exception {
        i++;
        inFixedList = new LinkedList<SignalNumbers>();
        int initExpression = i;
        ExpressionAnalyzer();
        int finishExpression = i;

        List<Token> sliceInFixed = listToken.subList(initExpression, finishExpression);

        inFixedList.forEach(element -> {
            sliceInFixed.get(element.getPosition() - initExpression).setLexema(element.getLexema());
            sliceInFixed.get(element.getPosition() - initExpression).setSimbol(element.getSimbol());
        });

        List<Token> Exit = new ConversionPosFixed().InFixedToPosFixed(sliceInFixed);
        ScrollExpressionToGenerationCode(Exit);
        String returnExitExpretion = new SemanticAnalizer().Semantic(Exit, symbolTable);

        if (!returnExitExpretion.equals(Symbols.SBOOLEANO)) {
            throw new Exception("[ERROR] Esperado um booleano");
        }
        int falseLabel = label, finalLabel = label;
        if (listToken.get(i).getSimbol().equals(Symbols.SENTAO)) {
            i++;
            SemanticAnalizer.GenerationCode("", "JMPF",  String.format("%d", falseLabel), "");
            label++;
            SimpleCommandAnalyser();
            if (listToken.get(i).getSimbol().equals(Symbols.SSENAO)) {
                finalLabel = label;
                SemanticAnalizer.GenerationCode("", "JMP",  String.format("%d", finalLabel), "");
                label++;
                i++;
                SemanticAnalizer.GenerationCode(String.format("%d", falseLabel), "NULL", "" , "");
                SimpleCommandAnalyser();
            }
        } else {
            throw new Exception("[ERROR] Esperado \"então\"");
        }
        SemanticAnalizer.GenerationCode( String.format("%d",finalLabel), "NULL", "", "");
    }

    /**
      Metódo <b>ScrollExpressionToGenerationCode</b> é responsavel por carregar da lista
     * constantes, variáveis ou instruções que executam uma operação e assim gerar o código de máquina
     * @param list é a lista que contém Tokens
     */
    private void ScrollExpressionToGenerationCode(List<Token> list) {
        try {
            for (Token token : list) {
                switch (token.getSimbol()) {
                    case Symbols.SNUMERO:
                        SemanticAnalizer.GenerationCode("", "LDC", token.getLexema(), "");
                        break;
                    case Symbols.SIDENTIFICADOR:
                        if (symbolTable.get(searchTable(token.getLexema(), null)-1).getType().equals(SymbolTableType.STBOOLFUNCTION) ||
                            symbolTable.get(searchTable(token.getLexema(), null)-1).getType().equals(SymbolTableType.STINTFUNCTION)) {
                            SemanticAnalizer.GenerationCode("", "CALL", SemanticAnalizer.FindLabel(symbolTable, token.getLexema()), "");
                            SemanticAnalizer.GenerationCode("", "LDV", "0", "");
                            break;
                        }
                        SemanticAnalizer.GenerationCode("", "LDV", SemanticAnalizer.FindLabel(symbolTable, token.getLexema()), "");
                        break;
                    case Symbols.SVERDADEIRO:
                        SemanticAnalizer.GenerationCode("", "LDC", "1", "");
                        break;
                    case Symbols.SFALSO:
                        SemanticAnalizer.GenerationCode("", "LDC", "0", "");
                        break;
                    case Symbols.SMAIS:
                        SemanticAnalizer.GenerationCode("", "ADD", "", "");
                        break;
                    case Symbols.SMENOS:
                        SemanticAnalizer.GenerationCode("", "SUB", "", "");
                        break;
                    case Symbols.SMULT:
                        SemanticAnalizer.GenerationCode("", "MULT", "", "");
                        break;
                    case Symbols.SDIV:
                        SemanticAnalizer.GenerationCode("", "DIVI", "", "");
                        break;
                    case Symbols.SNEGATIVO:
                        SemanticAnalizer.GenerationCode("", "INV", "", "");
                        break;
                    case Symbols.SMAIOR:
                        SemanticAnalizer.GenerationCode("", "CMA", "", "");
                        break;
                    case Symbols.SMENOR:
                        SemanticAnalizer.GenerationCode("", "CME", "", "");
                        break;
                    case Symbols.SMAIORIG:
                        SemanticAnalizer.GenerationCode("", "CMAQ", "", "");
                        break;
                    case Symbols.SMENORIG:
                        SemanticAnalizer.GenerationCode("", "CMEQ", "", "");
                        break;
                    case Symbols.SDIF:
                        SemanticAnalizer.GenerationCode("", "CDIF", "", "");
                        break;
                    case Symbols.SE:
                        SemanticAnalizer.GenerationCode("", "AND", "", "");
                        break;
                    case Symbols.SOU:
                        SemanticAnalizer.GenerationCode("", "OR", "", "");
                        break;
                    case Symbols.SNAO:
                        SemanticAnalizer.GenerationCode("", "NEG", "", "");
                        break;
                    case Symbols.SIG:
                        SemanticAnalizer.GenerationCode("", "CEQ", "", "");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metódo <b>AnalyzeSubRoutine</b> é responsavel por
     * @throws Exception
     */
    private void AnalyzeSubRoutine() throws Exception {
        Integer auxLabel = null, flag = 0;

        if (listToken.get(i).getSimbol().equals(Symbols.SPROCEDIMENTO) || listToken.get(i).getSimbol().equals(Symbols.SFUNCAO)) {
            auxLabel = label;
            SemanticAnalizer.GenerationCode("", "JMP", String.format("%d", label), "");
            label = label + 1;
            flag = 1;
        }

        while (listToken.get(i).getSimbol().equals(Symbols.SPROCEDIMENTO) || listToken.get(i).getSimbol().equals(Symbols.SFUNCAO)) {
            if (listToken.get(i).getSimbol().equals(Symbols.SPROCEDIMENTO)) {
                ProcedureDeclarationAnalyzer();
            } else {
                FunctionDeclarationAnalyzer();
            }
            if (listToken.get(i).getSimbol().equals(Symbols.SPONTO_VIRGULA)) {
                i++;
            } else {
                throw new Exception("[Error] -- inesperado: " + listToken.get(i).getSimbol());
            }
        }
        if (flag == 1) {
            SemanticAnalizer.GenerationCode(String.format("%d", auxLabel), "NULL", "", "");
        }

    }

    /**
     * Metódo <b>ProcedureDeclarationAnalyzer</b> é responsavel por inserir o token na tabela de simbolos
     * e gerar o código de procedimentos.
     * @throws Exception
     */
    private void ProcedureDeclarationAnalyzer() throws Exception {
        i++;
        level = "L";
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {

            if (!SearchGlobalDeclaration(listToken.get(i).getLexema())) {
                InsertTable(listToken.get(i).getLexema(), SymbolTableType.STPROCEDURE, level, label);
                SemanticAnalizer.GenerationCode(String.format("%d", label), "NULL", "", "");
                label++;
                i++;
                if (listToken.get(i).getSimbol().equals(Symbols.SPONTO_VIRGULA)) {
                    BlockAnalyzer();
                } else {
                    throw new Exception("[Error] -- esperado ;");
                }
            } else {
                throw new Exception("[Error] -- procedimento já declarado");
            }

        } else {
            throw new Exception("[Error] -- esperado um identificador");
        }
        // DESEMPILHA OU VOLTA NÍVEL
        Unstack();
        level = "";
    }

    /**
     * Metódo <b>FunctionDeclarationAnalyzer</b> é responsavel por inserir o token na tabela de simbolos
     * e gerar o codigo das funções.
     * @throws Exception
     */
    private void FunctionDeclarationAnalyzer() throws Exception {
        i++;
        level = "L";
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
            if (!SearchGlobalDeclaration(listToken.get(i).getLexema())) {
                InsertTable(listToken.get(i).getLexema(), "", level, label);
                SemanticAnalizer.GenerationCode(String.format("%d", label), "NULL", "", "");
                label++;
                i++;
                if (listToken.get(i).getSimbol().equals(Symbols.SDOIS_PONTOS)) {
                    i++;
                    if (listToken.get(i).getSimbol().equals(Symbols.SINTEIRO) || listToken.get(i).getSimbol().equals(Symbols.SBOOLEANO)) {
                        SymbolTable element = symbolTable.pop();
                        if (listToken.get(i).getSimbol().equals(Symbols.SINTEIRO)) {
                            element.setType(SymbolTableType.STINTFUNCTION);
                            symbolTable.push(element);
                        } else {
                            element.setType(SymbolTableType.STBOOLFUNCTION);
                            symbolTable.push(element);
                        }
                        i++;
                        if (listToken.get(i).getSimbol().equals(Symbols.SPONTO_VIRGULA)) {
                            BlockAnalyzer();
                        }
                    } else {
                        throw new Exception("[Error] -- esperado um tipo valido");
                    }
                } else {
                    throw new Exception("[Error] -- esperado :");
                }
            } else {
                throw new Exception("[Error] -- Função já declarada");
            }
        } else {
            throw new Exception("[Error] -- esperado um identificador valido");
        }
        Unstack();
        level = "";
    }

    /**
     * Metódo <b><ExpressionAnalyzer/b> é responsavel por validar operadores relacional.
     * @throws Exception
     */
    private void ExpressionAnalyzer() throws Exception {

        SimpleExpressionAnalyzer();
        if (listToken.get(i).getSimbol().equals(Symbols.SMAIOR) ||
                listToken.get(i).getSimbol().equals(Symbols.SMAIORIG) ||
                listToken.get(i).getSimbol().equals(Symbols.SIG) ||
                listToken.get(i).getSimbol().equals(Symbols.SMENOR) ||
                listToken.get(i).getSimbol().equals(Symbols.SMENORIG) ||
                listToken.get(i).getSimbol().equals(Symbols.SDIF)) {
            i++;
            SimpleExpressionAnalyzer();
        }

    }

    /**
     * Metódo <b>SimpleExpressionAnalyzer</b> analisa uma expressão simples e também identica positivos e negativos
     * na expressão, adicionando-os a uma lista auxiliar que é validade após nas funções, "WhileAnalizer",
     * "ifAnalizer" e "ChProcedureAtributeAnalyzer".
     * @throws Exception
     */
    private void SimpleExpressionAnalyzer() throws Exception {
        if (listToken.get(i).getSimbol().equals(Symbols.SMAIS) || listToken.get(i).getSimbol().equals(Symbols.SMENOS)) {
            if (listToken.get(i).getSimbol().equals(Symbols.SMAIS)) {
                SignalNumbers t = new SignalNumbers("+u", Symbols.SPOSITIVO, i);
                inFixedList.add(t);
            }
            if (listToken.get(i).getSimbol().equals(Symbols.SMENOS)) {
                SignalNumbers t = new SignalNumbers("-u", Symbols.SNEGATIVO, i);
                inFixedList.add(t);
            }
            i++;
        }
        TermAnalyzer();
        while (listToken.get(i).getSimbol().equals(Symbols.SMAIS) ||
                listToken.get(i).getSimbol().equals(Symbols.SMENOS) ||
                listToken.get(i).getSimbol().equals(Symbols.SOU)) {
            i++;
            TermAnalyzer();
        }
    }

    /**
     * Metódo <b>TermAnalyzer</b> é responsavel por verificar se é uma multiplicação, divisão ou and.
     * @throws Exception
     */
    private void TermAnalyzer() throws Exception {
        FactorAnalyzer();
        while (listToken.get(i).getSimbol().equals(Symbols.SMULT) ||
                listToken.get(i).getSimbol().equals(Symbols.SDIV) ||
                listToken.get(i).getSimbol().equals(Symbols.SE)) {
            i++;
            FactorAnalyzer();
        }
    }

    /**
     * Metódo <b>searchTable</b> provura simbolo na tabela de simbolos atravez do lexema, sem restrição de level.
     */
    private int searchTable(String lexeme, String level) {
        int index = 0;
        for (SymbolTable element : symbolTable) {
            index++;
            if (element.getLexeme().equals(lexeme)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Metódo <b>searchTableVariable</b> é responsavel por procurar variaveis na tebela de simbolos atravez do lexema.
     */
    private int searchTableVariable(String lexeme) {
        int index = 0;
        for (SymbolTable element : symbolTable) {
            index++;
            if (element.getLexeme().equals(lexeme)) {
                if(element.getType().equals(Symbols.SINTEIRO)||element.getType().equals(Symbols.SBOOLEANO)) {
                    return index;
                }
            }
        }
        return -1;
    }

    /**
     * Metódo <b>searchTableFunction</b> é responsavel por procurar uma função na tabela de simbolos atráves do lexema.
     */
    private int searchTableFunction(String lexeme) {
        int index = 0;
        for (SymbolTable element : symbolTable) {
            index++;
            if (element.getLexeme().equals(lexeme)) {
                if(element.getType().equals(SymbolTableType.STBOOLFUNCTION)||element.getType().equals(SymbolTableType.STINTFUNCTION)) {
                    return index;
                }
            }

        }
        return -1;
    }

    /**
     * Metódo <b>FactorAnalyzer</b> é responsável por verificar um trecho de código e rediricionar o fluxo para
     * as devidos métodos sintaticos.
     * @throws Exception
     */
    private void FactorAnalyzer() throws Exception {
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
            int ind = searchTable(listToken.get(i).getLexema(), level);
            if (ind != -1) {
                if (symbolTable.get(ind).getType().equals(SymbolTableType.STINTFUNCTION) || symbolTable.get(ind).getType().equals(SymbolTableType.STBOOLFUNCTION)) {
                    i++;
                } else {
                    i++;
                }
            } else {
                throw new Exception("[Error] -- Simbolo não declarado");
            }
        } else if (listToken.get(i).getSimbol().equals(Symbols.SNUMERO)) {
            i++;
        } else if (listToken.get(i).getSimbol().equals(Symbols.SNAO)) {
            i++;
            FactorAnalyzer();
        } else if (listToken.get(i).getSimbol().equals(Symbols.SABRE_PARENTESES)) {
            i++;
            ExpressionAnalyzer();
            if (listToken.get(i).getSimbol().equals(Symbols.SFECHA_PARENTESES)) {
                i++;
            } else {
                throw new Exception("[Error] -- esperado um ) em vez de: " + listToken.get(i).getSimbol());
            }
        } else if (listToken.get(i).getSimbol().equals(Symbols.SVERDADEIRO) || listToken.get(i).getSimbol().equals(Symbols.SFALSO)) {
            i++;
        } else {
            throw new Exception("[Error] -- token invalido: " + (listToken.get(i).getLexema()));
        }
    }
}
