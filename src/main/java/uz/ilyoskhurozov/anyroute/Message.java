package uz.ilyoskhurozov.anyroute;

import javafx.scene.control.Alert;

public enum Message {
    RUNTIME_ERROR(Alert.AlertType.ERROR, "Runtime error happened!"),
    ROUTE_NOT_FOUND(Alert.AlertType.ERROR, "Couldn't find route! Make sure all cables are connected correctly."),
    AT_LEAST_TWO_TOPOLOGIES(Alert.AlertType.WARNING, "Make sure there's at least 2 topologies."),
    AT_LEAST_TWO_ROUTERS(Alert.AlertType.WARNING, "Make sure there's at least 2 routers."),
    TOPOLOGY_NAME_EXISTS(Alert.AlertType.WARNING, "There is topology with this name."),
    DELETE_TOPOLOGY_CACHE_CONFIRMATION(Alert.AlertType.CONFIRMATION, "Do you want to delete topology cache"),
    SOURCE_CODE(Alert.AlertType.INFORMATION, "Source code: https://github.com/ilyoskhurozov/any-route");

    public final Alert.AlertType type;
    public final String text;

    Message(Alert.AlertType type, String text){
        this.type = type;
        this.text = text;
    }
}
