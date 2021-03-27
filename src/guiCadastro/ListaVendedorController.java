package guiCadastro;

import dbUtil.DbIntegrityException;
import guiCadastro.listeners.DataChangeListener;
import guiLogin.LoginController;
import guiUtil.Alerts;
import guiUtil.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.entities.Department;
import model.entities.Seller;

import model.services.DepartmentService;
import model.services.SellerService;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListaVendedorController implements Initializable, DataChangeListener {

    private SellerService service;
    private ObservableList<Seller> obsList;

    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller, Integer> tableColumnId;

    @FXML
    private TableColumn<Seller, String> tableColumnNome;

    @FXML
    private TableColumn<Seller, String> tableColumnEmail;

    @FXML
    private TableColumn<Seller, Date> tableColumnNascimento;

    @FXML
    private TableColumn<Seller, Double> tableColumnSalario;

    @FXML
    private TableColumn<Seller, Seller> tableColumnEDIT;

    @FXML
    private TableColumn<Seller, Seller> tableColumnREMOVE;

    @FXML
    private Button btNovo;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
        tableColumnId.setStyle( "-fx-alignment: CENTER;");
        tableColumnNome.setStyle( "-fx-alignment: CENTER;");
        tableColumnEmail.setStyle( "-fx-alignment: CENTER;");
        tableColumnSalario.setStyle( "-fx-alignment: CENTER;");
        tableColumnNascimento.setStyle( "-fx-alignment: CENTER;");
        tableColumnEDIT.setStyle( "-fx-alignment: CENTER;");
        tableColumnREMOVE.setStyle( "-fx-alignment: CENTER;");
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnNascimento.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        Utils.formatTableColumnDate(tableColumnNascimento, "dd/MM/yyyy");
        tableColumnSalario.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnSalario, 2);

        Stage stage = (Stage) LoginController.getCadastroScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    @FXML
    public void onBtNovoAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Seller obj = new Seller();

        createDialogForm(obj, "/guiCadastro/FormVendedor.fxml", parentStage);
    }

    public void setSellerService(SellerService service){
        this.service = service;
    }

    public void updateTableView(){
        if (service == null){
            throw new IllegalStateException("Service estava nulo");
        }
        List<Seller> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewSeller.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Seller obj, String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            VendedorFormController controller = loader.getController();
            controller.setServices(new SellerService(), new DepartmentService());
            controller.loadAssociatedObjects();
            controller.setSeller(obj);
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Coloque os dados do vendedor");
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
        tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("editar");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/guiCadastro/FormVendedor.fxml", Utils.currentStage(event)));
            }
        });
    }



    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("remover");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
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

    private void removeEntity(Seller obj){
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
