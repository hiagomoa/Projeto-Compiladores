package compilador;

import Consts.Symbols;
import compilador.models.Token;

import java.util.LinkedList;

public class SyntaticAnalyzer {
    int label;
    int i = 0;
    LinkedList<Token> listToken;

    public SyntaticAnalyzer(LinkedList<Token> data) throws Exception {
        this.listToken = data;
    }

    public void Syntatic() throws Exception {
        label = 0;
//        for (Token token : listToken) {
//            System.out.printf("%s # %s\n", token.getLexema(), token.getSimbol());
//        }
        for (i = 0; i < listToken.size(); i++) {
            if (listToken.get(i).getSimbol().equals(Symbols.SPROGRAMA)) {
                i++;//LEXICO(TOKEN)
                if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                    i++;//LEXICO(TOKEN)
                    if (listToken.get(i).getSimbol().equals(Symbols.SPONTO_VIRGULA)) {
                        BlockAnalyzer();
                        if (listToken.get(i).getSimbol().equals(Symbols.SPONTO)) {
                            if (listToken.size() - 1 < i) {//TODO: como é comentário?
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
        }
    }

    private void BlockAnalyzer() throws Exception {
        i++;
        AnalyzeVariablesDeclaration();
        AnalyzeSubRoutine();
        AnalyzeCommands();

    }
    //TODO: analisar ponto e virgula antes do fim, teste sint9.txt
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
                    throw new Exception("[Error] inesperado: "+listToken.get(i).getSimbol());
                }
            }
            i++;
        } else {
            throw new Exception("[Error] -- esperado um identificador de inicio no lugar de: "+listToken.get(i).getSimbol());
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
        do {
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                //boolean isDuplicated = DuplicateVarTableSearch(listToken.get(i).getLexema());
                i++;
                if (listToken.get(i).getSimbol().equals(Symbols.SVIRGULA) || listToken.get(i).getSimbol().equals(Symbols.SDOIS_PONTOS)) {
                    if (listToken.get(i).getSimbol().equals(Symbols.SVIRGULA)) {
                        i++;
                        if (listToken.get(i).getSimbol().equals(Symbols.SDOIS_PONTOS)) {
                            throw new Exception("[Error] -- simbolo não esperado: "+listToken.get(i).getSimbol());
                        }
                    }
                } else {
                    throw new Exception("[Error] -- simbolo errado esperado \":\" ou \",\"");
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador para variavel");
            }
        } while (!listToken.get(i).getSimbol().equals(Symbols.SDOIS_PONTOS));
        i++;
        AnalyzeType();
    }

    private void AnalyzeType() throws Exception {
        if (!listToken.get(i).getSimbol().equals(Symbols.SINTEIRO) && !listToken.get(i).getSimbol().equals(Symbols.SBOOLEANO)) {
            throw new Exception("[Error] -- tipo não esperado");
        } else {
            //colocar tipo tabela ()
        }
        i++;
    }

    private void ChProcedureAtributeAnalyzer() throws Exception {
        i++;
        if (listToken.get(i).getSimbol().equals(Symbols.SATRIBUICAO)) {
            i++;
            ExpressionAnalyzer();
        } else {
            //Chamada Procedimento
           // i++;
        }
    }

    private void ReadAnalyzer() throws Exception {
        i++;
        if (listToken.get(i).getSimbol().equals(Symbols.SABRE_PARENTESES)) {
            i++;
            if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
                i++;
                if (listToken.get(i).getSimbol().equals(Symbols.SFECHA_PARENTESES)) {
                    i++;
                } else {
                    throw new Exception("[Error] -- esperado )");
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
                i++;
                if (listToken.get(i).getSimbol().equals(Symbols.SFECHA_PARENTESES)) {
                    i++;
                } else {
                    throw new Exception("[Error] -- esperado um )");
                }
            } else {
                throw new Exception("[Error] -- esperado um identificador");
            }
        } else {
            throw new Exception("[Error] -- esperado um (");
        }
    }

    private void WhileAnalyzer() throws Exception {
        i++;
        ExpressionAnalyzer();
        if (listToken.get(i).getSimbol().equals(Symbols.SFACA)) {
            i++;
            SimpleCommandAnalyser();
        } else {
            throw new Exception("[Error] -- esperado uma palavra reservada faça");
        }
    }

    private void IfAnalyzer() throws Exception {
        i++;
        ExpressionAnalyzer();
        if (listToken.get(i).getSimbol().equals(Symbols.SENTAO)) {
            i++;
            SimpleCommandAnalyser();
            if (listToken.get(i).getSimbol().equals(Symbols.SSENAO)) {
                i++;
                SimpleCommandAnalyser();
            }
        } else {
            throw new Exception("[ERROR] Esperado \"então\"");
        }
    }

    private void AnalyzeSubRoutine() throws Exception {
        int flag = 0;
        if (listToken.get(i).getSimbol().equals(Symbols.SPROCEDIMENTO) || listToken.get(i).getSimbol().equals(Symbols.SFUNCAO)) {

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
                throw new Exception("[Error] -- esperado um ;3");
            }
        }
        if (flag == 1) {
            //Gera
        }

    }

    private void ProcedureDeclarationAnalyzer() throws Exception {
        i++;
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
            i++;
            if (listToken.get(i).getSimbol().equals(Symbols.SPONTO_VIRGULA)) {
                BlockAnalyzer();
            } else {
                throw new Exception("[Error] -- esperado ;");
            }
        } else {
            throw new Exception("[Error] -- esperado um identificador");
        }
    }

    private void FunctionDeclarationAnalyzer() throws Exception {
        i++;
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
            i++;
            if (listToken.get(i).getSimbol().equals(Symbols.SDOIS_PONTOS)) {
                i++;
                if (listToken.get(i).getSimbol().equals(Symbols.SINTEIRO) || listToken.get(i).getSimbol().equals(Symbols.SBOOLEANO)) {
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
            throw new Exception("[Error] -- esperado um identificador valido");
        }
    }

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

    private void SimpleExpressionAnalyzer() throws Exception {
        if (listToken.get(i).getSimbol().equals(Symbols.SMAIS) || listToken.get(i).getSimbol().equals(Symbols.SMENOS)) {
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

    private void FactorAnalyzer() throws Exception {
        if (listToken.get(i).getSimbol().equals(Symbols.SIDENTIFICADOR)) {
            i++;
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
                throw new Exception("[Error] -- esperado um )");
            }
        } else if (listToken.get(i).getLexema().equals(true) || listToken.get(i).getLexema().equals(false)) {
            i++;
        } else {
            throw new Exception("[Error] -- token invalido: "+(listToken.get(i).getLexema()));
        }
    }

}
