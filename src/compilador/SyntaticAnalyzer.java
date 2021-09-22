    package compilador;

    import compilador.models.Token;

    import java.util.LinkedList;

public class SyntaticAnalyzer {
    int label;
    public byte[] data;
    int i = 0;
    LinkedList<Token> listToken;

    public SyntaticAnalyzer(byte[] data) throws Exception {
        this.data = data;
        this.listToken = new LexicalAnalizer(data).lexical();
    }


    public void Syntatic() throws Exception {
        label = 0;


        for (i = 0; i < listToken.size(); i++) {
            if (listToken.get(i).getSimbol().equals("programa")) {
                i++;//LEXICO(TOKEN)
                if (listToken.get(i).getSimbol().equals("sidentificador")) {
                    i++;//LEXICO(TOKEN)
                    if (listToken.get(i).getSimbol().equals("spontovirgula")) {
                        BlockAnalyzer();
                        if (listToken.get(i).getSimbol().equals("sponto")) {
                            if (listToken.size() != i) {//TODO: como é comentário?
                                //ERROR
                            }
                        } else {
                            //ERROR
                        }
                    } else {
                        //ERROR
                    }
                } else {
                    //ERROR
                    //ERROR
                }
            }
        }
    }

    private void BlockAnalyzer () {
        i++;
        AnalyzeVariablesDeclaration();
        AnalyzeSubRoutine();
        AnalyzeCommands();

    }

    private void AnalyzeCommands(){
        if(listToken.get(i).getSimbol().equals("sinicio")){
            i++;
            SimpleCommandAnalyser();
            while(!listToken.get(i).getSimbol().equals("sfim")){
                if(listToken.get(i).getSimbol().equals("spontovirgula")){
                    i++;
                    if(!listToken.get(i).getSimbol().equals("sfim")){
                        SimpleCommandAnalyser();
                    }
                }else{
                    //ERROR
                }
                i++;
            }
        }else{
            //ERROR
        }
    }

    private void SimpleCommandAnalyser(){
        if(listToken.get(i).getSimbol().equals("sidentificador")){
            ChProcedureAtributeAnalyzer();
        }else if(listToken.get(i).getSimbol().equals("sse")){
            IfAnalyzer();
        }
        else if(listToken.get(i).getSimbol().equals("senquanto")){
            WhileAnalyzer();
        }
        else if(listToken.get(i).getSimbol().equals("sleia")){
            ReadAnalyzer();
        }
        else if(listToken.get(i).getSimbol().equals("sescreva")){
            WriteAnalyzer();
        }else{
            AnalyzeCommands();
        }
    }

    private void AnalyzeVariablesDeclaration () {//Analisa_et_variáveis
            if (listToken.get(i).getSimbol().equals("svar")) {
                i++;//LEXICO(TOKEN)
                if (listToken.get(i).getSimbol().equals("sidentificador")) {
                    while (listToken.get(i).getSimbol().equals("sidentificador")) {
                        AnalyzeVariables();
                        if (listToken.get(i).getSimbol().equals("spontvirg")) {
                            i++;//LEXICO(TOKEN)
                        } else {
                            //ERROR
                        }
                    }
                } else {
                    //ERROR
                }
            }
        }

    private void AnalyzeVariables () {
        do {
            if (listToken.get(i).getSimbol().equals("sidentificador")) {
                //boolean isDuplicated = DuplicateVarTableSearch(listToken.get(i).getLexema());
                i++;
                if (listToken.get(i).getSimbol().equals("svirgula") || listToken.get(i).getSimbol().equals("sdoispontos")) {
                    if (listToken.get(i).getSimbol().equals("svirgula")) {
                        i++;
                        if (listToken.get(i).getSimbol().equals("sdoispontos")) {
                            //ERRO
                        }
                    }
                } else {
                    //ERROR
                }
            } else {
                //ERROR
            }
        } while (listToken.get(i).getSimbol().equals("sidentificador"));
        i++;
        this.AnalyzeType();
    }

    private void AnalyzeType () {
        if (!listToken.get(i).getSimbol().equals("sinteiro") && !listToken.get(i).getSimbol().equals("sbooleano")) {
            //ERROR
        } else {
            //colocar tipo tabela ()
            i++;
        }
    }

    private  void ChProcedureAtributeAnalyzer(){
            i++;
            if(listToken.get(i).getSimbol().equals("satribuicao")){
                //attributionAnalyzer();
            }else{
                //Chamada Procedimento
            }
        }

    private  void ReadAnalyzer(){
        i++;
        if(listToken.get(i).getSimbol().equals("sabre_parenteses")){
            i++;
            if(listToken.get(i).getSimbol().equals("sidentificador")){
                i++;
                if(listToken.get(i).getSimbol().equals("sfecha_parenteses")){
                    i++;
                }else{
                    //ERROR
                }
            }else{
                //ERROR
            }
        }else{
            //ERROR
        }
    }

    private void WriteAnalyzer(){
        i++;
        if(listToken.get(i).getSimbol().equals("sabre_parenteses")){
            i++;
            if(listToken.get(i).getSimbol().equals("sidentificador")) {
                i++;
                if (listToken.get(i).getSimbol().equals("sfecha_parenteses")) {
                    i++;
                } else {
                    //ERRO
                }
            }else{
                //ERRO
            }
        }else{
            //ERRO
        }
    }

    private  void WhileAnalyzer(){
        i++;
        ExpressionAnalyzer();
        if(listToken.get(i).getSimbol().equals("sfaca")){
            i++;
            SimpleCommandAnalyser();
        }else{
            //ERROR
        }
    }

    private  void IfAnalyzer(){
        i++;
        ExpressionAnalyzer();
        if(listToken.get(i).getSimbol().equals("sentao")){
            i++;
            SimpleCommandAnalyser();
            if(listToken.get(i).getSimbol().equals("ssenao")){
                i++;
                SimpleCommandAnalyser();
            }
        }
    }

    private void AnalyzeSubRoutine () {
            int flag=0;
            if(listToken.get(i).getSimbol().equals("sprocedimento")||listToken.get(i).getSimbol().equals("sfuncao")) {
                while (listToken.get(i).getSimbol().equals("sprocedimento") && listToken.get(i).getSimbol().equals("sfuncao")) {
                    if (listToken.get(i).getSimbol().equals("sprocedimento")) {
                        ProcedureDeclarationAnalyzer();
                    } else {
                        FunctionDeclarationAnalyzer();
                    }
                    if(listToken.get(i).getSimbol().equals("sponto-virgula")){
                        i++;
                    }else{
                        //ERROR
                    }
                }
                if(flag==1){
                    //Gera
                }
            }
    }

    private void ProcedureDeclarationAnalyzer(){
        i++;
        if(listToken.get(i).getSimbol().equals("sidentificador")){
                 i++;
                 if(listToken.get(i).getSimbol().equals("sponto-virgula")){
                     BlockAnalyzer();
                 }else{
                     //ERROR
                 }
        }else{
            //ERROR
        }
    }

    private void FunctionDeclarationAnalyzer(){
        i++;
        if(listToken.get(i).getSimbol().equals("sidentificador")){
            i++;
            if(listToken.get(i).getSimbol().equals("sdoispontos")){
                i++;
                if(listToken.get(i).getSimbol().equals("sinteiro")|| listToken.get(i).getSimbol().equals("sbooleano")){
                    i++;
                    if(listToken.get(i).getSimbol().equals("sponto_vírgula")){
                        BlockAnalyzer();
                    }
                }else{
                    //ERRO
                }
            }else{
                //ERRO
            }
        }else{
            //ERRO
        }
    }

    private void ExpressionAnalyzer(){
        SimpleExpressionAnalyzer();
        if(listToken.get(i).getSimbol().equals("smaior") ||
                listToken.get(i).getSimbol().equals("smaiorig") ||
                listToken.get(i).getSimbol().equals("sig") ||
                listToken.get(i).getSimbol().equals("smenor") ||
                listToken.get(i).getSimbol().equals("smenorig")||
                listToken.get(i).getSimbol().equals("sdif") ){
            i++;
            SimpleExpressionAnalyzer();
        }

    }

    private void SimpleExpressionAnalyzer(){
        if(listToken.get(i).getSimbol().equals("smais")||listToken.get(i).getSimbol().equals("smenos")){
            i++;
            TermAnalyzer();
            while(listToken.get(i).getSimbol().equals("smais")||
                    listToken.get(i).getSimbol().equals("smenos")||
                    listToken.get(i).getSimbol().equals("sou")){
                i++;
                TermAnalyzer();
            }
        }
    }

    private void TermAnalyzer(){
        FactorAnalyzer();
        while (listToken.get(i).getSimbol().equals("smult") ||
                listToken.get(i).getSimbol().equals("sdiv") ||
                listToken.get(i).getSimbol().equals("se")){
            i++;
            FactorAnalyzer();
        }
    }

    private void FactorAnalyzer(){
        if(listToken.get(i).getSimbol().equals("sidentificador")){

        }else if(listToken.get(i).getSimbol().equals("snumero")){
            i++;

        }else if(listToken.get(i).getSimbol().equals("snao")){
            i++;
            FactorAnalyzer();
        }else if(listToken.get(i).getSimbol().equals("sabre_parenteses")){
            i++;
            ExpressionAnalyzer();
            if(listToken.get(i).getSimbol().equals("sfecha_parenteses")){
                i++;
            }else{
                //ERRO
            }
        }else if(listToken.get(i).getLexema().equals(true)||listToken.get(i).getLexema().equals(false)){
            i++;
        } else{
            //ERRO
        }
    }

    }
