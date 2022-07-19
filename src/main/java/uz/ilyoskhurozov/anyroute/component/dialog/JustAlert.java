package uz.ilyoskhurozov.anyroute.component.dialog;

import javafx.scene.control.Alert;

public class JustAlert extends Alert {
    public JustAlert(Message msg) {
        super(msg.type, msg.text);
        setHeaderText(null);
    }

    public enum Message {
        RUNTIME_ERROR(AlertType.ERROR, "Runtime error happened!"),
        ROUTE_NOT_FOUND(AlertType.ERROR, "Couldn't find route! Make sure all cables are connected correctly."),
        AT_LEAST_TWO_TOPOLOGIES(AlertType.WARNING, "Make sure there's at least 2 topologies."),
        AT_LEAST_TWO_ROUTERS(AlertType.WARNING, "Make sure there's at least 2 routers."),
        TOPOLOGY_NAME_EXISTS(AlertType.WARNING, "There is topology with this name."),
        DELETE_TOPOLOGY_CACHE_CONFIRMATION(AlertType.CONFIRMATION, "Do you want to delete topology cache"),
        SOURCE_CODE(AlertType.INFORMATION, "Source code: https://github.com/ilyoskhurozov/any-route");

        public final AlertType type;
        public final String text;

        Message(AlertType type, String text){
            this.type = type;
            this.text = text;
        }
    }
}
