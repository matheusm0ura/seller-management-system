package guiCadastro;

import dbUtil.DbIntegrityException;
import guiCadastro.listeners.DataChangeListener;
import guiUtil.Alerts;
import guiUtil.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import model.entities.Department;
import guiLogin.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListaDepartamentoController implements Initializable, DataChangeListener {

    private DepartmentService service;
    private ObservableList<Department> obsList;

    @FXML
    private TableView<Department> tableViewDepartamento;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnNome;

    @FXML
    private TableColumn<Department, Department> tableColumnEDIT;

    @FXML
    private TableColumn<Department, Department> tableColumnREMOVE;

    @FXML
    private Button btNovo;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) LoginController.getCadastroScene().getWindow();
        tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());
    }

    @FXML
    public void onBtNovoAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Department obj = new Department();

        createDialogForm(obj, "/guiCadastro/FormDepartamento.fxml", parentStage);
    }

    public void setDepartmentService(DepartmentService service){
        this.service = service;
    }

    public void updateTableView(){
        if (service == null){
            throw new IllegalStateException("Service estava nulo");
        }
        List<Department> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewDepartamento.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Department obj, String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            DepartamentoFormController controller = loader.getController();
            controller.setDepartmentService(new DepartmentService());
            controller.setDepartamento(obj);
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Coloque o nome do departamento");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();


        }catch (IOException e){
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Erro ao carregar a view.", e.getMessage(), Alert.AlertType.ERROR, "UNDECORATED", 350, 50);

        }

    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("editar");
            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/guiCadastro/FormDepartamento.fxml", Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("remover");
            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Department obj){
        Optional<ButtonType> resultado = Alerts.showConfirmation("Confirmação", "Você tem certeza que deseja deletar?");

        if (resultado.get() == ButtonType.OK){
            if (service == null){
                throw new IllegalStateException("O serviço estava nulo");
            }
            try {
                service.remove(obj);
                updateTableView();

            }catch (DbIntegrityException e){
                Alerts.showAlert("Erro ao remover o objeto", "", e.getMessage(), Alert.AlertType.ERROR, "UNDECORATED", 350, 50);
            }
        }
    }


}
