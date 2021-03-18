package guiCadastro;

import guiLogin.LoginController;
import guiUtil.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.entities.Department;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MainViewController implements Initializable {

    @FXML
    private MenuItem menuItemVendedor;

    @FXML
    private MenuItem menuItemDepartamento;

    @FXML
    private MenuItem menuItemSobre;

    @FXML
    public void onMenuItemVendedorAction(){
        System.out.println("vendedor");
    }

    @FXML
    public void onMenuItemDepartmentAction(){
        loadView("/guiCadastro/ListaDepartamento.fxml", (ListaDepartamentoController controller) -> {
            controller.setDepartmentService(new DepartmentService());
            controller.updateTableView();

        });
    }

    @FXML
    public void onMenuItemSobreAction(){
        loadView("/guiCadastro/About.fxml", x -> {});
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private synchronized <T> void loadView(String absoluteName, Consumer<T> inicializingAction){
        //O synchronized vai fazer com que a aplicação gráfica rode sem interrupção.
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVbox = loader.load();

            Scene cadastroScene = LoginController.getCadastroScene();
            VBox cadastroVbox = (VBox) ((ScrollPane) cadastroScene.getRoot()).getContent();

            Node cadastroMenu = cadastroVbox.getChildren().get(0);

            cadastroVbox.getChildren().clear();
            cadastroVbox.getChildren().add(cadastroMenu);
            cadastroVbox.getChildren().addAll(newVbox.getChildren());

            T controller =  loader.getController();
            inicializingAction.accept(controller);

        }catch (IOException e){
            Alerts.showAlert("IOException", "Erro ao carregar a página.", e.getMessage(), Alert.AlertType.ERROR,"UNDECORATED", 350, 50);
        }

    }
}
