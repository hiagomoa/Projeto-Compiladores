package compilador;

import compilador.models.Token;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class MainController {
    @FXML
    private Button btn;

    @FXML
    private TextArea textArea;
    @FXML
    private TextArea errorTextArea;
    @FXML
    public void onBtnLoadPath(){
        System.out.println(fileTextField.getText());
        String text;
        textArea.clear();
        try (BufferedReader buffReader = new BufferedReader(new FileReader(fileTextField.getText()))) {
            while ((text = buffReader.readLine()) != null) {
                textArea.appendText(text + "\n");
            }
        } catch (IOException e) {
            System.out.println("ERRO");
        }
    }

    @FXML
    public void onClickToCompile() throws IOException {
        Path path = Paths.get(fileTextField.getText());
        byte[] file = textArea.getText().getBytes(StandardCharsets.UTF_8);
                //Files.readAllBytes(path);
        try{
            LinkedList<Token> listToken = new LexicalAnalizer(file).lexical();
            new SyntaticAnalyzer(listToken).Syntatic();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informação do compilador");
            alert.setHeaderText(null);
            alert.setContentText("Código compilado com sucesso");

            alert.showAndWait();
        }catch (Exception e){
            System.out.println("\n\n\n\nASDFGHJKLÇSDFGHJKLDFGHJKL\n");
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
}