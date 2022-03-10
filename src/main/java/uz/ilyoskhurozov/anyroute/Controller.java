package uz.ilyoskhurozov.anyroute;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import uz.ilyoskhurozov.anyroute.component.Router;

import java.util.LinkedHashMap;


public class Controller {
    private int r = 0;
    private LinkedHashMap<String, LinkedHashMap<String, Integer>> siblingsTable;

    @FXML
    private AnchorPane desk;

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

        siblingsTable = new LinkedHashMap<>();
    }

    @FXML
    void mouseClickOnDesk(MouseEvent e){
        if (routerBtn.isSelected() && e.getTarget().equals(desk)){
            Router router = new Router(++r, e.getX(), e.getY());
            desk.getChildren().add(router);

            String name = router.getName();
            siblingsTable.put(name, new LinkedHashMap<>(siblingsTable.size()+1));
            siblingsTable.keySet().forEach(rName -> {
                siblingsTable.get(rName).put(name, null);
                siblingsTable.get(name).put(rName, null);
            });
        }
    }

    @FXML
    void clearDesk() {
        siblingsTable.clear();
        desk.getChildren().clear();
        r = 0;
    }
}