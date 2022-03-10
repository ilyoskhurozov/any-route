package uz.ilyoskhurozov.anyroute;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import uz.ilyoskhurozov.anyroute.component.Cable;
import uz.ilyoskhurozov.anyroute.component.Router;

import java.util.LinkedHashMap;


public class Controller {
    private int r = 0;
    private LinkedHashMap<String, LinkedHashMap<String, Cable>> cablesTable;
    private Cable currentCable;
    private final int DEFAULT_CABLE_LENGTH = 1;

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

        cablesTable = new LinkedHashMap<>();
    }

    @FXML
    void mouseClickOnDesk(MouseEvent e){
        if (routerBtn.isSelected() && e.getTarget().equals(desk)){
            Router router = new Router(++r, e.getX(), e.getY());

            router.setOnMouseClicked(mouseEvent -> {
                if (cableBtn.isSelected()) {
                    if (currentCable == null) {
                        currentCable = new Cable(DEFAULT_CABLE_LENGTH);
                        desk.getChildren().add(currentCable);

                        currentCable.setBegin(router);
                        currentCable.setEndX(router.getLayoutX()+mouseEvent.getX());
                        currentCable.setEndY(router.getLayoutY()+mouseEvent.getY());

                        desk.setOnMouseMoved(event -> {
                            currentCable.setEndX(event.getX()+Math.signum(currentCable.getStartX()- currentCable.getEndX()));
                            currentCable.setEndY(event.getY()+Math.signum(currentCable.getStartY()- currentCable.getEndY()));
                        });
                    } else {
                        if (cablesTable.get(currentCable.getBegin()).get(router.getName()) != null) {
                            return;
                        }
                        currentCable.setEnd(router);

                        if (currentCable.getEndX() == currentCable.getStartX() &&
                                currentCable.getEndY() == currentCable.getStartY()
                        ) {
                            desk.getChildren().remove(currentCable);
                        } else {
                            cablesTable.get(currentCable.getBegin()).put(currentCable.getEnd(), currentCable);
                            cablesTable.get(currentCable.getEnd()).put(currentCable.getBegin(), currentCable);
                        }

                        currentCable = null;
                        desk.setOnMouseMoved(null);

                    }
                }
            });

            desk.getChildren().add(router);

            String name = router.getName();
            cablesTable.put(name, new LinkedHashMap<>(cablesTable.size()+1));
            cablesTable.keySet().forEach(rName -> {
                cablesTable.get(rName).put(name, null);
                cablesTable.get(name).put(rName, null);
            });
        }
    }

    @FXML
    void clearDesk() {
        cablesTable.clear();
        desk.getChildren().clear();
        r = 0;
    }
}