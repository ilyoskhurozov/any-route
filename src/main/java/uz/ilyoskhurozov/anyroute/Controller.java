package uz.ilyoskhurozov.anyroute;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class Controller {

    @FXML
    private ToggleButton routerBtn;

    @FXML
    private ToggleButton cableBtn;

    @FXML
    private ToggleButton removeBtn;

    @FXML
    private ChoiceBox<String> algorithms;

    @FXML
    private void initialize() {
        ToggleGroup btns = new ToggleGroup();
        btns.getToggles().addAll(routerBtn, cableBtn, removeBtn);

        algorithms.getItems().addAll("Dijskstra", "algorithm 2", "algorithm 3", "algorithm 4", "algorithm 5");
        algorithms.getSelectionModel().selectFirst();
    }
}