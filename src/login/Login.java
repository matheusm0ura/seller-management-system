package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Login extends Application {

    private double xOffset;
    private double yOffset;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("../guiLogin/login.fxml"));

        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image("/icons/icons8-logo-java-coffee-cup-64.png"));


        Scene scene = new Scene(root);
        stage.setScene(scene);

        //Permite arrastar a janela de login para qualquer ponto(x,y) da tela
        scene.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        stage.setTitle("");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
