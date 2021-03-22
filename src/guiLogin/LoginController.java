package guiLogin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dbUtil.DB;
import guiCadastro.ListaVendedorController;
import guiUtil.Alerts;
import guiUtil.Constraints;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import login.LoginModel;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    LoginModel loginModel = new LoginModel();

    @FXML
    private JFXButton loginButton;

    @FXML
    private JFXTextField usuario;

    @FXML
    private JFXPasswordField senha;

    @FXML
    private Label statusDB;

    @FXML
    private JFXButton sairButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Constraints.setTextFieldMaxLength(usuario, 20);
        Constraints.setPasswordFieldMaxLenght(senha, 20);

        if (this.loginModel.isDataBaseConnected()){
            this.statusDB.setText("conectado");
        }
        else {
            this.statusDB.setStyle("-fx-text-fill: red;");
            this.statusDB.setText("desconectado");
        }

    }

    @FXML
    public void login(ActionEvent event){
        try {
            if (this.loginModel.isLogin(this.usuario.getText(), this.senha.getText())){

                loginButton.setDefaultButton(true);

                Stage stage = (Stage) this.loginButton.getScene().getWindow();
                stage.close();
                cadastro();

            }

            if (this.senha.getText().isEmpty() || this.usuario.getText().isEmpty()){
                /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().setPrefSize(350, 50);

                alert.setTitle("");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, insira suas credenciais.");

                alert.showAndWait();
                */
                Alerts.showAlert("", "", "Por favor, insira suas credenciais.", Alert.AlertType.INFORMATION, "UNDECORATED", 350, 50);

            }

            else if (!this.loginModel.isLogin(this.usuario.getText(), this.senha.getText())){
                /*Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().setPrefSize(350, 50);

                alert.setTitle("");
                alert.setHeaderText(null);
                alert.setContentText("Credenciais incorretas.");

                alert.showAndWait();
                */
                Alerts.showAlert("", "", "Credenciais incorretas", Alert.AlertType.ERROR, "UNDECORATED", 350, 50);
                clearFields();

            }

        }catch (Exception localException){

        }
    }

    private static Scene cadastroScene;
    public void cadastro(){
        try {

            Stage controlStage = new Stage();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/guiCadastro/MainView.fxml"));

            ScrollPane scrollPane = loader.load();

            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);

            cadastroScene = new Scene(scrollPane);

            controlStage.setScene(cadastroScene);
            controlStage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Scene getCadastroScene(){
        return cadastroScene;
    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) sairButton.getScene().getWindow();
        stage.close();
    }


    private void clearFields(){
        this.usuario.setText("");
        this.senha.setText("");
    }


}
