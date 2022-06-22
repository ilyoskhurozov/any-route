package uz.ilyoskhurozov.anyroute;

import javafx.scene.control.Alert;

public enum Message {
    RUNTIME_ERROR(Alert.AlertType.ERROR, "Runtime error happened!"),
    ROUTE_NOT_FOUND(Alert.AlertType.ERROR, "Couldn't find route! Make sure all cables are connected correctly."),
    AT_LEAST_TWO_TOPOLOGIES(Alert.AlertType.WARNING, "Make sure there's at least 2 topologies."),
    AT_LEAST_TWO_ROUTER(Alert.AlertType.WARNING, "Make sure there's at least 2 routers."),
    TOPOLOGY_NAME_EXISTS(Alert.AlertType.WARNING, "There is topology with this name.");

    public final Alert.AlertType type;
    public final String text;

    Message(Alert.AlertType type, String text){
        this.type = type;
        this.text = text;
    }
}
