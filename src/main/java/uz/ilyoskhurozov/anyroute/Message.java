package uz.ilyoskhurozov.anyroute;

import javafx.scene.control.Alert;

public enum Message {
    RUNTIME_ERROR(Alert.AlertType.ERROR, "Runtime error happened!"),
    ROUTE_NOT_FOUND(Alert.AlertType.ERROR, "Couldn't find route! Make sure all cables are connected correctly.");

    public final Alert.AlertType type;
    public final String text;

    Message(Alert.AlertType type, String text){
        this.type = type;
        this.text = text;
    }
}
