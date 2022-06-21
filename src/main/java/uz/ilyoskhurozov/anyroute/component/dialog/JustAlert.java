package uz.ilyoskhurozov.anyroute.component.dialog;

import javafx.scene.control.Alert;

public class JustAlert extends Alert {
    public JustAlert(AlertType alertType, String msg) {
        super(alertType, msg);
        setHeaderText(null);
    }
}
