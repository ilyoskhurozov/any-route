package uz.ilyoskhurozov.anyroute;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import uz.ilyoskhurozov.anyroute.component.Router;


public class Controller {
    private int r=0;

    @FXML
    private AnchorPane desk;

    @FXML
    private ToggleButton routerBtn;

    @FXML
    private ToggleButton cableBtn;

    @FXML
    private ToggleButton removeBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private ChoiceBox<String> algorithms;

    @FXML
    private void initialize() {
        ToggleGroup btns = new ToggleGroup();
        btns.getToggles().addAll(routerBtn, cableBtn, removeBtn);

        algorithms.getItems().addAll("Dijskstra", "algorithm 2", "algorithm 3", "algorithm 4", "algorithm 5");
        algorithms.getSelectionModel().selectFirst();
    }

    @FXML
    void mouseClickOnDesk(MouseEvent e){
        if (routerBtn.isSelected() && e.getTarget().equals(desk)){
            Router router = new Router(++r, e.getX(), e.getY());
            desk.getChildren().add(router);
        }
    }
}