package compilador;

import compilador.models.Token;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainController  implements Initializable {
    @FXML
    private Button btn;
    @FXML
    private TextArea textArea;
    @FXML
    private TextArea errorTextArea;
    @FXML
    public void onBtnLoadPath(){
        textArea.clear();
        Path path = Paths.get(fileTextField.getText());
        try {
            String teste =Files.readString(path);
            textArea.setText(teste);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickToCompile() throws IOException {
        byte[] file = new FileReaderCompiler().reader(fileTextField.getText()); //

        try{
            LinkedList<Token> listToken = new LexicalAnalizer(file).lexical();
            new SyntaticAnalyzer(listToken).Syntatic();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informação do compilador");
            alert.setHeaderText(null);
            alert.setContentText("Código compilado com sucesso");
            alert.showAndWait();
        }catch (Exception e){
            errorTextArea.appendText(e.getMessage());
        }
    }

    @FXML
    private MenuItem menuItemOpenFile;

    @FXML
    public void menuItemOpenFileAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File", "*.txt"));
        File f = fileChooser.showOpenDialog(null);
        if (f != null) {
            fileTextField.setText(f.getAbsolutePath());
        }
    }

    @FXML
    private TextField fileTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textArea.setEditable(false);

    }
}