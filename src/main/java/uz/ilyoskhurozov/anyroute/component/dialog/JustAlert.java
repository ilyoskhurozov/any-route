package uz.ilyoskhurozov.anyroute.component.dialog;

import javafx.scene.control.Alert;
import uz.ilyoskhurozov.anyroute.Message;

public class JustAlert extends Alert {
    public JustAlert(Message msg) {
        super(msg.type, msg.text);
        setHeaderText(null);
    }
}
