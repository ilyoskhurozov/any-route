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
import java.util.Set;


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

            String routerName = router.getName();
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
                        if (cablesTable.get(currentCable.getBegin()).get(routerName) != null) {
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

                            currentCable.setOnMouseClicked(mEvent -> {
                                Cable cable = (Cable) mEvent.getSource();
                                if (removeBtn.isSelected()) {
                                    cablesTable.get(cable.getBegin()).put(cable.getEnd(), null);
                                    cablesTable.get(cable.getEnd()).put(cable.getBegin(), null);
                                }

                                desk.getChildren().remove(cable);
                            });
                        }

                        currentCable = null;
                        desk.setOnMouseMoved(null);

                    }
                } else if (removeBtn.isSelected()) {
                    desk.getChildren().remove(router);

                    Set<String> names = cablesTable.remove(routerName).keySet();
                    names.remove(routerName);
                    names.forEach(name -> {
                        Cable removedCable = cablesTable.get(name).remove(routerName);
                        if (removedCable != null){
                            desk.getChildren().remove(removedCable);
                        }
                    });
                }
            });

            desk.getChildren().add(router);

            cablesTable.put(routerName, new LinkedHashMap<>(cablesTable.size()+1));
            cablesTable.keySet().forEach(rName -> {
                cablesTable.get(rName).put(routerName, null);
                cablesTable.get(routerName).put(rName, null);
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