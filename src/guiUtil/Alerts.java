package guiUtil;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class Alerts {
    public static void showAlert(String title, String header, String content, Alert.AlertType type, String style, int v, int v1) {
        Alert alert = new Alert(type);

        alert.initStyle(StageStyle.valueOf(style));
        alert.getDialogPane().setPrefSize(v, v1);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
