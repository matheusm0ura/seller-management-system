package guiCadastro;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dbUtil.DbException;
import guiCadastro.listeners.DataChangeListener;
import guiUtil.Alerts;
import guiUtil.Constraints;
import guiUtil.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Phone;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentService;
import model.services.PhoneService;
import model.services.SellerService;

import java.net.URL;
import java.util.*;

public class TelefoneFormController implements Initializable {

    private Phone entidade;
    private PhoneService service;
    private SellerService sellerService;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtNumero;

    @FXML
    private Label labelErroNumero;

    @FXML
    private JFXButton btSalvar;

    @FXML
    private JFXButton btCancelar;

    @FXML
    private ComboBox<Seller> comboBoxSeller;

    private ObservableList<Seller> obsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    @FXML
    public void onBtSaveAction(ActionEvent event){
        if (entidade == null){
            throw new IllegalStateException("A entidade estava nula.");
        }
        if (service == null){
            throw new IllegalStateException("O serviço estva nulo.");
        }

        try {
            entidade = getFormData();
            service.saveOrUpdate(entidade);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();

        }catch (DbException e){
            Alerts.showAlert("Erro ao salvar o objeto", "", e.getMessage(), Alert.AlertType.ERROR, "UNDECORATED", 150, 50);

        }catch (ValidationException e){
            setErrorMessages(e.getErros());
        }
    }

    private void notifyDataChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }

    private Phone getFormData() {
        Phone obj = new Phone();
        ValidationException exception = new ValidationException("Erro de validação.");

        obj.setId(Utils.tryParseToInt(txtId.getText()));

        if (txtNumero.getText() == null || txtNumero.getText().trim().equals("")){
            exception.addError("numero", "O numero não pode ser vazio");
        }
        obj.setNumber(txtNumero.getText());

        obj.setSeller(comboBoxSeller.getValue());

        if (exception.getErros().size() > 0){
            throw exception;
        }

        return obj;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event){
        Utils.currentStage(event).close();

    }

    public void initializeNodes(){
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtNumero, 20);
        Constraints.setTextFieldInteger(txtNumero);
        initializeComboBoxSeller();

    }

    public void loadAssociatedObjects(){
        List<Seller> list = sellerService.findAll();
        obsList = FXCollections.observableArrayList(list);
        comboBoxSeller.setItems(obsList);
    }
    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    public void setPhone(Phone entidade){
        this.entidade = entidade;

    }

    public void setServices(PhoneService service, SellerService sellerService){
        this.service = service;
        this.sellerService = sellerService;
    }

    public void updateFormData(){
        if (entidade == null){
            throw new IllegalStateException("Entidade estava nula.");
        }

        txtId.setText(String.valueOf(entidade.getId()));
        txtNumero.setText(String.valueOf(entidade.getNumber()));

        if (entidade.getSeller() == null){
            comboBoxSeller.getSelectionModel().selectFirst();
        }
        else {
            comboBoxSeller.setValue(entidade.getSeller());
        }
    }

    private void initializeComboBoxSeller() {
        Callback<ListView<Seller>, ListCell<Seller>> factory = lv -> new ListCell<Seller>() {
            @Override
            protected void updateItem(Seller item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxSeller.setCellFactory(factory);
        comboBoxSeller.setButtonCell(factory.call(null));
    }

    private void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();

        if (fields.contains("numero")) {
            labelErroNumero.setText(errors.get("numero"));
        } else {
            labelErroNumero.setText("");

        }
    }




}
