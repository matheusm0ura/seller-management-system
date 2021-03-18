package guiCadastro;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.entities.Department;
import guiLogin.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.services.DepartmentService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListaDepartamentoController implements Initializable {

    private DepartmentService service;
    private ObservableList<Department> obsList;

    @FXML
    private TableView<Department> tableViewDepartamento;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnNome;

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
    public void onBtNovoAction(){
        System.out.println("click");
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
    }
}
