package uz.ilyoskhurozov.anyroute;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import uz.ilyoskhurozov.anyroute.component.Cable;
import uz.ilyoskhurozov.anyroute.component.Router;
import uz.ilyoskhurozov.anyroute.util.FindRoute;

import java.util.*;


public class Controller {
    private int r = 0;
    private LinkedHashMap<String, LinkedHashMap<String, Cable>> cablesTable;
    private Cable currentCable;
    private final int DEFAULT_CABLE_LENGTH = 1;
    private final TextInputDialog sizeDialog = new TextInputDialog();

    @FXML
    private AnchorPane desk;

    @FXML
    private ToggleButton routerBtn;

    @FXML
    private ToggleButton cableBtn;

    @FXML
    private ToggleButton removeBtn;

    @FXML
    private ToggleGroup btns;

    @FXML
    private ChoiceBox<String> algorithms;

    @FXML
    private Button findRouteBtn;

    @FXML
    private void initialize() {
        algorithms.getItems().addAll("Dijskstra", "algorithm 2", "algorithm 3", "algorithm 4", "algorithm 5");
        algorithms.getSelectionModel().selectFirst();

        cablesTable = new LinkedHashMap<>();

        sizeDialog.setHeaderText(null);
        sizeDialog.setContentText(null);
        sizeDialog.setGraphic(null);

        sizeDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(Bindings.not(Bindings.createBooleanBinding(() -> {
            try {
                return Integer.parseInt(sizeDialog.getEditor().getText()) != 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }, sizeDialog.getEditor().textProperty())));
    }

    @FXML
    void mouseClickOnDesk(MouseEvent e) {
        if (routerBtn.isSelected() && e.getTarget().equals(desk)) {
            Router router = new Router(++r, e.getX(), e.getY());

            String routerName = router.getName();
            router.setOnMouseClicked(mouseEvent -> {
                if (cableBtn.isSelected()) {
                    if (currentCable == null) {
                        currentCable = new Cable(DEFAULT_CABLE_LENGTH);
                        desk.getChildren().add(currentCable);

                        currentCable.setBegin(router);
                        currentCable.setEndX(router.getLayoutX() + mouseEvent.getX());
                        currentCable.setEndY(router.getLayoutY() + mouseEvent.getY());

                        desk.setOnMouseMoved(event -> {
                            currentCable.setEndX(event.getX() + Math.signum(currentCable.getStartX() - currentCable.getEndX()));
                            currentCable.setEndY(event.getY() + Math.signum(currentCable.getStartY() - currentCable.getEndY()));
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
                                    desk.getChildren().remove(cable);
                                } else if (mEvent.getClickCount() == 2) {
                                    String[] names = new String[]{cable.getBegin(), cable.getEnd()};
                                    Arrays.sort(names);
                                    sizeDialog.setTitle(names[0] + " - " + names[1]);
                                    sizeDialog.getEditor().setText(Integer.toString(cable.getLength()));

                                    sizeDialog.showAndWait().ifPresent(lengthStr -> cable.setLength(Integer.parseInt(lengthStr)));
                                }
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
                        if (removedCable != null) {
                            desk.getChildren().remove(removedCable);
                        }
                    });

                    findRouteBtn.setDisable(cablesTable.size() < 3);
                }
            });

            desk.getChildren().add(router);

            cablesTable.put(routerName, new LinkedHashMap<>(cablesTable.size() + 1));
            cablesTable.keySet().forEach(rName -> {
                cablesTable.get(rName).put(routerName, null);
                cablesTable.get(routerName).put(rName, null);
            });

            findRouteBtn.setDisable(cablesTable.size() < 3);
        }
    }

    @FXML
    void clearDesk() {
        cablesTable.clear();
        desk.getChildren().clear();
        findRouteBtn.setDisable(true);
        r = 0;
    }

    @FXML
    void findRoute() {
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>();
        choiceDialog.setGraphic(null);
        choiceDialog.setHeaderText(null);
        Set<String> routers = cablesTable.keySet();
        choiceDialog.getItems().addAll(routers);
        choiceDialog.setTitle("Start");
        choiceDialog.setSelectedItem(choiceDialog.getItems().get(0));
        choiceDialog.showAndWait().ifPresent(r1 -> {
            choiceDialog.getItems().remove(r1);
            choiceDialog.setTitle("End");
            choiceDialog.setSelectedItem(choiceDialog.getItems().get(0));
            choiceDialog.showAndWait().ifPresent(r2 -> {
                System.out.println("Find path from " + r1 + " to " + r2);

                Platform.runLater(() -> {
                    Thread thread = new Thread(() -> {
                        findRouteBtn.setDisable(true);

                        String algo = algorithms.getValue();
                        switch (algo) {
                            case "Dijskstra": {
                                List<String> route = FindRoute.withDijkstra(getLengthTable(), r1, r2);
                                if (route == null) {
                                    System.out.println("route not found");
                                    //TODO route not found
                                } else {
                                    System.out.println(route);
                                }
                            }
                            break;
                        }

                        findRouteBtn.setDisable(false);
                    });

                    thread.setDaemon(true);
                    thread.start();
                });
            });
        });

    }

    private LinkedHashMap<String, LinkedHashMap<String, Integer>> getLengthTable() {
        LinkedHashMap<String, LinkedHashMap<String, Integer>> table = new LinkedHashMap<>();

        cablesTable.forEach((r1, cableMap) -> {
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

            cableMap.forEach((r2, cable) -> map.put(r2, cable != null ? cable.getLength() : null));

            table.put(r1, map);
        });

        return table;
    }

    //Menus

    @FXML
    void cancel() {
        if (currentCable == null) {
            Toggle selectedBtn = btns.getSelectedToggle();
            if (selectedBtn != null) {
                selectedBtn.setSelected(false);
            }
        } else {
            desk.getChildren().remove(currentCable);
            desk.setOnMouseMoved(null);
            currentCable = null;
        }
    }

    @FXML
    void close() {
        Platform.exit();
    }
}