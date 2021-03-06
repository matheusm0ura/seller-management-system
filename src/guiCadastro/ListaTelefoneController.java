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
import model.entities.Phone;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.PhoneService;
import model.services.SellerService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListaTelefoneController implements Initializable, DataChangeListener {

    private PhoneService service;
    private ObservableList<Phone> obsList;

    @FXML
    private TableView<Phone> tableViewPhone;

    @FXML
    private TableColumn<Phone, Integer> tableColumnId;

    @FXML
    private TableColumn<Phone, String> tableColumnNumero;

    @FXML
    private TableColumn<Phone, Phone> tableColumnEDIT;

    @FXML
    private TableColumn<Phone, Phone> tableColumnREMOVE;

    @FXML
    private Button btNovo;



    @Override
    public void onDataChanged() {
        updateTableView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
        tableColumnEDIT.setStyle( "-fx-alignment: CENTER;");
        tableColumnREMOVE.setStyle("-fx-alignment: CENTER;");
        tableColumnNumero.setStyle("-fx-alignment: CENTER;");
        tableColumnId.setStyle("-fx-alignment: CENTER;");
    }

    private void initializeNodes(){
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnNumero.setCellValueFactory(new PropertyValueFactory<>("number"));

        Stage stage = (Stage) LoginController.getCadastroScene().getWindow();
        tableViewPhone.prefHeightProperty().bind(stage.heightProperty());
    }



    @FXML
    public void onBtNovoAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Phone obj = new Phone();

        createDialogForm(obj, "/guiCadastro/FormTelefone.fxml", parentStage);
    }

    public void setPhoneService(PhoneService service){
        this.service = service;
    }

    public void updateTableView(){
        if (service == null){
            throw new IllegalStateException("Service estava nulo");
        }
        List<Phone> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewPhone.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Phone obj, String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            TelefoneFormController controller = loader.getController();
            controller.setServices(new PhoneService(), new SellerService());
            controller.loadAssociatedObjects();
            controller.setPhone(obj);
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Coloque o n??mero de telefone");
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



    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Phone, Phone>() {
            private final Button button = new Button("editar");
            @Override
            protected void updateItem(Phone obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/guiCadastro/FormTelefone.fxml", Utils.currentStage(event))); //ATUALIZAR PARA PHONE
            }
        });
    }



    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Phone, Phone>() {
            private final Button button = new Button("remover");
            @Override
            protected void updateItem(Phone obj, boolean empty) {
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

    private void removeEntity(Phone obj){
        Optional<ButtonType> resultado = Alerts.showConfirmation("Confirma????o", "Voc?? tem certeza que deseja deletar?");

        if (resultado.get() == ButtonType.OK){
            if (service == null){
                throw new IllegalStateException("O servi??o estava nulo");
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
