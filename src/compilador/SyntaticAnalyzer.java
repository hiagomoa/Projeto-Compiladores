package compilador;

import Consts.SymbolTableType;
import Consts.Symbols;
import compilador.models.SignalNumbers;
import compilador.models.SymbolTable;
import compilador.models.Token;
import jdk.jshell.spi.ExecutionControlProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
                    throw new Exception("[Error] -- esperado um ;1");
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador");
            }
        }

        int countVariable = 0;
        SymbolTable firstElement = symbolTable.peek();
        SymbolTable elementAux = symbolTable.peek();
        int flag = 0;
        int size = symbolTable.size();//TODO: pq aqui precisa?
        for (int i = 0; i < size; i++) {
                elementAux = symbolTable.pop();
                if (elementAux.getType().equals(Symbols.SINTEIRO) || elementAux.getType().equals(Symbols.SBOOLEANO) || elementAux.getType().equals(Symbols.SVAR)) {
                    if (flag == 0) {
                        firstElement = elementAux;
                        flag = 1;
                    }
                    countVariable++;
                }
        }
        if (countVariable > 0) {
            variablesMemory = variablesMemory - countVariable;
            SemanticAnalizer.GenerationCode("", "DALLOC", String.format("%d", variablesMemory), String.format("%d", countVariable));
        }
        SemanticAnalizer.GenerationCode("", "DALLOC", String.format("%d", variablesMemory - 1), "1");
        SemanticAnalizer.GenerationCode("", "HLT", "", "");//TODO: TALVEZ SEJA DENTRO
        for (SymbolTable element : symbolTable) {
            System.out.println(element.getLevel() + " " + element.getLexeme() + " " + element.getType());
        }
        SemanticAnalizer.CloseFile();
    }

    private void Unstack() throws Exception {//TODO: TA ERRADO TEM Q SER SALVAR PRA VOLTAR NO ALLOC
        int countVariable = 0;
        SymbolTable element = symbolTable.peek();
        //int size = symbolTable.size();
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

    private boolean SearchDeclarationVariableOnTable(String lexeme) throws Exception {
        for (SymbolTable element : symbolTable) {
            if (element.getLexeme().equals(lexeme)) {
                return true;
            }
        }
        return false;
    }

    private boolean SearchDeclarationFunctionOnTable(String lexeme) throws Exception {
        for (SymbolTable element : symbolTable) {
            if (element.getLexeme().equals(lexeme)) {
                return true;
            }
        }
        return false;
    }

    private boolean SearchDeclarationProcedureOnTable(String lexeme) throws Exception {
        for (SymbolTable element : symbolTable) {
            if (element.getLexeme().equals(lexeme)) {
                return true;
            }
        }
        return false;
    }

    private void InsertTable(String lexeme, String type, String level, Integer label) throws Exception {
        if (label != null) {
            symbolTable.push(new SymbolTable(lexeme, type, level, label));
            return;
        }
        symbolTable.push(new SymbolTable(lexeme, type));
    }

    private void BlockAnalyzer() throws Exception {
        i++;
        AnalyzeVariablesDeclaration();
        AnalyzeSubRoutine();
        AnalyzeCommands();
    }

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
                    throw new Exception("[Error] inesperado: " + listToken.get(i).getSimbol());
                }
            }
            i++;
        } else {
            throw new Exception("[Error] -- esperado \"inicio\" no lugar de: " + listToken.get(i).getSimbol());
        }
    }

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

    private void AnalyzeVariablesDeclaration() throws Exception {//Analisa_et_variáveis
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
        SemanticAnalizer.GenerationCode("", "ALLOC", String.format("%d", auxVariablesMemory), String.format("%d", variablesMemory - auxVariablesMemory));//TODO: talvez tenha q juntar os allocs
        i++;
        AnalyzeType();
    }

    private void AnalyzeType() throws Exception {
        if (!listToken.get(i).getSimbol().equals(Symbols.SINTEIRO) && !listToken.get(i).getSimbol().equals(Symbols.SBOOLEANO)) {
            throw new Exception("[Error] -- tipo não esperado");
        } else {
            PushTypeIntoTheTable(listToken.get(i).getLexema());
        }
        i++;
    }

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

    private void ChProcedureAtributeAnalyzer() throws Exception {
        i++;
        if (listToken.get(i).getSimbol().equals(Symbols.SATRIBUICAO)) {
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
            String returnExitExpression = new SemanticAnalizer().Semantic(Exit, symbolTable);

            String lexemaOnPosition = listToken.get(initExpression - 2).getLexema();
            int positionOnTableVariable = searchTableVariable(lexemaOnPosition);
            ScrollExpressionToGenerationCode(Exit);
            if(positionOnTableVariable==-1){
                int positionOnTableFunction = searchTableFunction(lexemaOnPosition);
                if(positionOnTableFunction!=-1){
                    SemanticAnalizer.GenerationCode("", "STR", "0", "");
                }else{
                    throw new Exception("[Error] -- Erro de atribuição" );
                }
            }else{
//                if (!returnExitExpression.equals(symbolTable.get(positionOnTableVariable).getType())) {
//                    throw new Exception("[Error] -- Erro de atribuição");
//                }
               // ScrollExpressionToGenerationCode(Exit);
                Token tokenCurrent = listToken.get(initExpression - 2);
                int positionCurrent = searchTable(tokenCurrent.getLexema(),null);
                if(symbolTable.get(positionCurrent-1).getType().equals(Symbols.SBOOLEANO)){
                    throw new Exception("[Error] -- Não é possivel salvar um booleano");
                }else{
                    SemanticAnalizer.GenerationCode("", "STR", SemanticAnalizer.FindLabel(symbolTable, tokenCurrent.getLexema()), "");
                }
            }
        } else {
            //Chamada Procedimento
            SemanticAnalizer.GenerationCode("", "CALL", SemanticAnalizer.FindLabel(symbolTable, listToken.get(i - 1).getLexema()), "");
        }
    }

    private void ReadAnalyzer() throws Exception {
        i++;
        SemanticAnalizer.GenerationCode("", "RD", "", "");
        //label+=1;
        if (listToken.get(i).getSimbol().equals(Symbols.SABRE_PARENTESES)) {
            i++;
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                if (SearchDeclarationVariableOnTable(listToken.get(i).getLexema())) {
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

    private void WriteAnalyzer() throws Exception {
        i++;

        if (listToken.get(i).getSimbol().equals(Symbols.SABRE_PARENTESES)) {
            i++;
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                if (SearchDeclarationFunctionOnTable(listToken.get(i).getLexema())) {
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
                        throw new Exception("[Error] -- esperado um )1");
                    }
                } else {
                    throw new Exception("[Error] -- 2");
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador");
            }
        } else {
            throw new Exception("[Error] -- esperado um (");
        }
    }

    private void WhileAnalyzer() throws Exception {
        int auxLabel_1 = label, auxLabel_2;
        i++;
        inFixedList = new LinkedList<SignalNumbers>();
        int initExpression = i;
        ExpressionAnalyzer();
        int finishExpression = i;

        SemanticAnalizer.GenerationCode(String.format("%d", label), "NULL", "", "");
        label = label + 1;


        List<Token> sliceInFixed = listToken.subList(initExpression, finishExpression);
        inFixedList.forEach(element -> {
            sliceInFixed.get(element.getPosition() - initExpression).setLexema(element.getLexema());
            sliceInFixed.get(element.getPosition() - initExpression).setSimbol(element.getSimbol());
        });

        List<Token> Exit = new ConversionPosFixed().InFixedToPosFixed(sliceInFixed);
        ScrollExpressionToGenerationCode(Exit);
        String returnExitExpretion = new SemanticAnalizer().Semantic(Exit, symbolTable);
        System.out.println("returnExitExpretion:" + returnExitExpretion);
        if (!returnExitExpretion.equals(Symbols.SBOOLEANO)) {
            System.out.println("ERROR");
            //TODO:ERROR
        }
        //TODO: chama posfixo
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

        System.out.println("returnExitExpretion:" + returnExitExpretion);
        if (!returnExitExpretion.equals(Symbols.SBOOLEANO)) {
            System.out.println("ERROR");
            //TODO:ERROR
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
        SemanticAnalizer.GenerationCode( String.format("%d",finalLabel), "NULL", "", "");//TODO VERIFICAR ESSE NULL tem q adicionar
    }

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

    private void ProcedureDeclarationAnalyzer() throws Exception {
        i++;
        level = "L";
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {

            if (!SearchDeclarationProcedureOnTable(listToken.get(i).getLexema())) {
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
                throw new Exception("[Error] -- 3");
            }

        } else {
            throw new Exception("[Error] -- esperado um identificador");
        }
        // DESEMPILHA OU VOLTA NÍVEL
        Unstack();
        level = "";
    }

    private void FunctionDeclarationAnalyzer() throws Exception {
        i++;
        level = "L";
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
            if (!SearchDeclarationFunctionOnTable(listToken.get(i).getLexema())) {
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
                throw new Exception("[Error] -- 4");
            }
        } else {
            throw new Exception("[Error] -- esperado um identificador valido");
        }
        Unstack();
        level = "";
    }

    private void ExpressionAnalyzer() throws Exception { //TODO: CHAMAR POS FIXICO APOS chamada desta função

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

    private void TermAnalyzer() throws Exception {
        FactorAnalyzer();
        while (listToken.get(i).getSimbol().equals(Symbols.SMULT) ||
                listToken.get(i).getSimbol().equals(Symbols.SDIV) ||
                listToken.get(i).getSimbol().equals(Symbols.SE)) {
            i++;
            FactorAnalyzer();
        }
    }

    private int searchTable(String lexeme, String level) {
        int index = 0;
        for (SymbolTable element : symbolTable) {
            index++;
            //   if (element.getLevel().equals(level)) {
            if (element.getLexeme().equals(lexeme)) {
                return index;
            }
            // }
        }
        return -1;
    }

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

    private void FactorAnalyzer() throws Exception {
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
            int ind = searchTable(listToken.get(i).getLexema(), level);
            if (ind != -1) {
                if (symbolTable.get(ind).getType().equals(SymbolTableType.STINTFUNCTION) || symbolTable.get(ind).getType().equals(SymbolTableType.STBOOLFUNCTION)) {
                    i++;//TODO: ANALISA CHAMADA DE FUNÇÃO
                } else {
                    i++;
                }
            } else {
                throw new Exception("[Error] -- 5");
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
        } else if (listToken.get(i).getLexema().equals(true) || listToken.get(i).getLexema().equals(false)) {
            i++;
        } else {
            throw new Exception("[Error] -- token invalido: " + (listToken.get(i).getLexema()));
        }
    }

}
