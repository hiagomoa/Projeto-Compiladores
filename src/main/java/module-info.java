module com.example.compiladorinterface {
    requires javafx.controls;
    requires javafx.fxml;


    opens compilador to javafx.fxml;
    exports compilador;
}