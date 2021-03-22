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
import jdk.jshell.execution.Util;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;

import model.services.DepartmentService;
import model.services.SellerService;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class VendedorFormController implements Initializable {

    private Seller entidade;

    private SellerService service;

    private DepartmentService departmentService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtNome;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private DatePicker dpNascimento;

    @FXML
    private JFXTextField txtSalario;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label labelNomeErro;

    @FXML
    private Label labelErroEmail;

    @FXML
    private Label labelErroDate;

    @FXML
    private Label labelErroSalario;

    @FXML
    private JFXButton btSalvar;

    @FXML
    private JFXButton btCancelar;

    private ObservableList<Department> obsList;

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

    private Seller getFormData() {
        Seller obj = new Seller();

        ValidationException exception = new ValidationException("Erro de validação.");

        obj.setId(Utils.tryParseToInt(txtId.getText()));

        if (txtNome.getText() == null || txtNome.getText().trim().equals("")){
            exception.addError("nome", "O nome não pode ser vazio");
        }
        obj.setName(txtNome.getText());

        if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")){
            exception.addError("email", "O email não pode ser vazio");
        }
        obj.setEmail(txtEmail.getText());

        if (dpNascimento.getValue() == null){
            exception.addError("nascimento", "O campo não pode ser nulo");
        }
        else {
            Instant instant = Instant.from(dpNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
            obj.setBirthDate(Date.from(instant));
        }

        if (txtSalario.getText() == null || txtSalario.getText().trim().equals("")){
            exception.addError("salario", "O salário não pode ser vazio");
        }
        obj.setBaseSalary(Utils.tryParseToDouble(txtSalario.getText()));
        obj.setDepartment(comboBoxDepartment.getValue());


        if (exception.getErros().size() > 0){
            throw exception;
        }


        return obj;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event){
        Utils.currentStage(event).close();

    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtNome, 30);
        Constraints.setTextFieldDouble(txtSalario);
        Constraints.setTextFieldMaxLength(txtEmail, 60);
        Utils.formatDatePicker(dpNascimento, "dd/MM/yyyy");
        initializeComboBoxDepartment();

    }

    public void setSeller(Seller entidade){
        this.entidade = entidade;

    }

    public void setServices(SellerService service, DepartmentService departmentService){
        this.service = service;
        this.departmentService = departmentService;
    }


    public void updateFormData(){
        if (entidade == null){
            throw new IllegalStateException("Entidade estava nula.");
        }
        txtId.setText(String.valueOf(entidade.getId()));
        txtNome.setText(entidade.getName());
        txtEmail.setText(entidade.getEmail());
        Locale.setDefault(Locale.US);
        txtSalario.setText(String.format("%.2f", entidade.getBaseSalary()));

        if (entidade.getBirthDate() != null){
            dpNascimento.setValue(LocalDate.ofInstant(entidade.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }

        if (entidade.getDepartment() == null){
            comboBoxDepartment.getSelectionModel().selectFirst();
        }
        else {
            comboBoxDepartment.setValue(entidade.getDepartment());
        }

    }

    public void loadAssociatedObjects(){
        List<Department> list = departmentService.findAll();
        obsList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(obsList);
    }

    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        if (fields.contains("nome")){
            labelNomeErro.setText(errors.get("nome"));
        }
        else {
            labelNomeErro.setText("");
        }

        if (fields.contains("email")){
            labelErroEmail.setText(errors.get("email"));
        }
        else {
           labelErroEmail.setText("");
        }

        if (fields.contains("salario")){
            labelErroSalario.setText(errors.get("salario"));
        }
        else {
            labelErroSalario.setText("");
        }
        if (fields.contains("nascimento")){
            labelErroDate.setText(errors.get("nascimento"));
        }
        else {
            labelErroDate.setText("");
        }


    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }


}
