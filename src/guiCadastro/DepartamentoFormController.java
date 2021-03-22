package guiCadastro;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dbUtil.DbException;
import guiCadastro.listeners.DataChangeListener;
import guiUtil.Alerts;
import guiUtil.Constraints;
import guiUtil.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleAction;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import model.entities.Department;
import model.exception.ValidationException;
import model.services.DepartmentService;

import java.net.URL;
import java.util.*;

public class DepartamentoFormController implements Initializable {

    private Department entidade;

    private DepartmentService service;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtNome;

    @FXML
    private Label labelNomeErro;

    @FXML
    private JFXButton btSalvar;

    @FXML
    private JFXButton btCancelar;

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

    private Department getFormData() {
        Department obj = new Department();

        ValidationException exception = new ValidationException("Erro de validação.");

        obj.setId(Utils.tryParseToInt(txtId.getText()));

        if (txtNome.getText() == null || txtNome.getText().trim().equals("")){
            exception.addError("nome", "O nome não pode ser vazio");
        }

        if (exception.getErros().size() > 0){
            throw exception;
        }

        obj.setName(txtNome.getText());

        return obj;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event){
        Utils.currentStage(event).close();

    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtNome, 30);

    }

    public void setDepartamento(Department entidade){
        this.entidade = entidade;

    }

    public void setDepartmentService(DepartmentService service){
        this.service = service;
    }

    public void updateFormData(){
        if (entidade == null){
            throw new IllegalStateException("Entidade estava nula.");
        }
        txtId.setText(String.valueOf(entidade.getId()));
        txtNome.setText(entidade.getName());
    }

    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        if (fields.contains("nome")){
            labelNomeErro.setText(errors.get("nome"));
        }
    }


}
