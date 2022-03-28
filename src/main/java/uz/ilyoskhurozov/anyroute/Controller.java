package uz.ilyoskhurozov.anyroute;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import uz.ilyoskhurozov.anyroute.component.Connection;
import uz.ilyoskhurozov.anyroute.component.Router;
import uz.ilyoskhurozov.anyroute.util.ConPropsDialog;
import uz.ilyoskhurozov.anyroute.util.FindRoute;
import uz.ilyoskhurozov.anyroute.util.RouterPropsDialog;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


public class Controller {
    private int r = 0;
    private LinkedHashMap<String, LinkedHashMap<String, Connection>> connectionsTable;
    private HashMap<String, Router> routersMap;
    private Connection currentConnection;
    private final ConPropsDialog conPropsDialog = new ConPropsDialog();
    private final RouterPropsDialog routerPropsDialog = new RouterPropsDialog();
    private final Alert noRouteAlert = new Alert(Alert.AlertType.WARNING);
    private final ArrayList<Connection> animatingConnections = new ArrayList<>();

    @FXML
    private AnchorPane desk;

    @FXML
    private ToggleButton routerBtn;

    @FXML
    private ToggleButton cableBtn;

    @FXML
    private ToggleButton deleteBtn;

    @FXML
    private ToggleGroup btns;

    @FXML
    private ChoiceBox<String> algorithms;

    @FXML
    private Button findRouteBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private VBox resultsPane;

    @FXML
    private Label time;

    @FXML
    private Label distance;

    @FXML
    private void initialize() {
        algorithms.getItems().addAll("Dijskstra", "Floyd");
        algorithms.getSelectionModel().selectFirst();

        connectionsTable = new LinkedHashMap<>();
        routersMap = new LinkedHashMap<>();

        noRouteAlert.setHeaderText(null);
        noRouteAlert.setContentText("Couldn't find route! Make sure to all cables are connected correctly.");
    }

    public void bindKeys(){
        Scene scene = desk.getScene();
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN),
                () -> routerBtn.setSelected(true)
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN),
                () -> cableBtn.setSelected(true)
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN),
                () -> deleteBtn.setSelected(true)
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.CONTROL_ANY),
                this::cancel
        );
    }

    private void cancel() {
        if (currentConnection == null) {
            Toggle selectedBtn = btns.getSelectedToggle();
            if (selectedBtn != null) {
                selectedBtn.setSelected(false);
            }
        } else {
            desk.getChildren().remove(currentConnection);
            desk.setOnMouseMoved(null);
            currentConnection = null;
        }
    }

    @FXML
    void mouseClickOnDesk(MouseEvent e) {
        if (routerBtn.isSelected() && e.getTarget().equals(desk)) {
            Router router = new Router(++r, e.getX(), e.getY());

            String routerName = router.getName();
            router.setOnMouseClicked(mouseEvent -> {
                if (cableBtn.isSelected()) {
                    if (currentConnection == null) {
                        currentConnection = new Connection();
                        desk.getChildren().add(currentConnection);

                        currentConnection.setStart(router);
                        currentConnection.setEndXY(
                                router.getLayoutX() + mouseEvent.getX(),
                                router.getLayoutY() + mouseEvent.getY()
                        );

                        desk.setOnMouseMoved(event -> currentConnection.setEndXY(
                                event.getX() + Math.signum(currentConnection.getStartX() - currentConnection.getEndX()),
                                event.getY() + Math.signum(currentConnection.getStartY() - currentConnection.getEndY())
                        ));
                    } else {
                        if (connectionsTable.get(currentConnection.getStart()).get(routerName) != null) {
                            return;
                        }
                        currentConnection.setEnd(router);

                        if (currentConnection.getEndX() == currentConnection.getStartX() &&
                                currentConnection.getEndY() == currentConnection.getStartY()
                        ) {
                            desk.getChildren().remove(currentConnection);
                        } else {
                            connectionsTable.get(currentConnection.getStart()).put(currentConnection.getEnd(), currentConnection);
                            connectionsTable.get(currentConnection.getEnd()).put(currentConnection.getStart(), currentConnection);

                            currentConnection.setOnMouseClicked(mEvent -> {
                                if (!animatingConnections.isEmpty()) {
                                    return;
                                }
                                Connection connection = (Connection) mEvent.getSource();
                                if (deleteBtn.isSelected()) {
                                    connectionsTable.get(connection.getStart()).put(connection.getEnd(), null);
                                    connectionsTable.get(connection.getEnd()).put(connection.getStart(), null);
                                    desk.getChildren().remove(connection);
                                } else if (mEvent.getClickCount() == 2 && currentConnection == null) {
                                    String[] names = new String[]{connection.getStart(), connection.getEnd()};
                                    Arrays.sort(names);
                                    conPropsDialog.setTitle(names[0] + " - " + names[1]);
                                    conPropsDialog.setProps(connection.getMetrics(), connection.getReliability());

                                    conPropsDialog.showAndWait().ifPresent(
                                            conProps -> {
                                                connection.setProps(conProps.metrics, conProps.reliability);
                                                connection.setCableCount(conProps.count);
                                            }
                                    );
                                }
                            });
                        }

                        currentConnection = null;
                        desk.setOnMouseMoved(null);

                    }
                } else if (deleteBtn.isSelected()) {
                    desk.getChildren().remove(router);
                    routersMap.remove(routerName);

                    Set<String> names = connectionsTable.remove(routerName).keySet();
                    names.remove(routerName);
                    names.forEach(name -> {
                        Connection removedConnection = connectionsTable.get(name).remove(routerName);
                        if (removedConnection != null) {
                            desk.getChildren().remove(removedConnection);
                        }
                    });

                    findRouteBtn.setDisable(connectionsTable.size() < 2);
                } else if (mouseEvent.getClickCount() == 2) {
                    routerPropsDialog.setTitle(routerName);
                    routerPropsDialog.setProps(router.getReliability());

                    routerPropsDialog.showAndWait().ifPresent(
                            routerProps -> router.setProps(routerProps.reliability)
                    );
                }
            });

            desk.getChildren().add(router);
            routersMap.put(routerName, router);

            connectionsTable.put(routerName, new LinkedHashMap<>(connectionsTable.size() + 1));
            connectionsTable.keySet().forEach(rName -> {
                connectionsTable.get(rName).put(routerName, null);
                connectionsTable.get(routerName).put(rName, null);
            });

            findRouteBtn.setDisable(connectionsTable.size() < 2);
        }
    }

    @FXML
    void clearDesk() {
        stopAnimation();
        resultsPane.setVisible(false);
        connectionsTable.clear();
        desk.getChildren().clear();
        findRouteBtn.setDisable(true);
        r = 0;
    }

    @FXML
    void findRoute() {
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>();
        choiceDialog.setGraphic(null);
        choiceDialog.setHeaderText(null);
        Set<String> routers = connectionsTable.keySet();
        choiceDialog.getItems().addAll(routers);
        choiceDialog.setTitle("Start");
        choiceDialog.setSelectedItem(choiceDialog.getItems().get(0));
        choiceDialog.showAndWait().ifPresent(r1 -> {
            stopAnimation();
            resultsPane.setVisible(false);

            choiceDialog.getItems().remove(r1);
            choiceDialog.setTitle("End");
            choiceDialog.setSelectedItem(choiceDialog.getItems().get(0));
            choiceDialog.showAndWait().ifPresent(r2 -> Platform.runLater(() -> {
                Thread thread = new Thread(() -> {
                    findRouteBtn.setDisable(true);

                    String algo = algorithms.getValue();
                    List<String> route = null;
                    AtomicLong begin = new AtomicLong(), end = new AtomicLong();
                    LinkedHashMap<String, LinkedHashMap<String, Integer>> table = getMetricsTable();
                    switch (algo) {
                        case "Dijskstra": {
                            begin.set(System.nanoTime());
                            route = FindRoute.withDijkstra(table, r1, r2);
                            end.set(System.nanoTime());
                        } break;
                        case "Floyd": {
                            begin.set(System.nanoTime());
                            route = FindRoute.withFloyd(table, r1, r2);
                            end.set(System.nanoTime());
                        } break;
                    }

                    if (route == null) {
                        Platform.runLater(noRouteAlert::showAndWait);
                    } else {
                        String p1;
                        String p2 = route.get(0);

                        AtomicLong dis = new AtomicLong(0);

                        for (int i = 1; i < route.size(); i++) {
                            p1 = p2;
                            p2 = route.get(i);

                            Connection connection = connectionsTable.get(p1).get(p2);
                            connection.startSendingData(connection.getStart().equals(p1));
                            animatingConnections.add(connection);
                            dis.addAndGet(connection.getMetrics());
                        }
                        stopBtn.setDisable(false);
                        Platform.runLater(() -> {
                            long t = end.get()-begin.get();
                            long frac = 0;
                            String unit = "ns";
                            if (t > 1000) {
                                frac = t % 1000;
                                t /= 1000;
                                unit = "mks";

                                if (t > 1000) {
                                    frac = t % 1000;
                                    t /= 1000;
                                    unit = "ms";

                                    if (t > 1000) {
                                        frac = t % 1000;
                                        t /= 1000;
                                        unit = "s";
                                    }
                                }
                            }
                            time.setText(String.format("≈ %d.%03d %s", t, frac, unit));
                            distance.setText(dis.get()+"");
                        });
                        resultsPane.setVisible(true);
                    }

                    findRouteBtn.setDisable(false);
                });

                thread.setDaemon(true);
                thread.start();
            }));
        });
    }

    private LinkedHashMap<String, LinkedHashMap<String, Integer>> getMetricsTable() {
        LinkedHashMap<String, LinkedHashMap<String, Integer>> table = new LinkedHashMap<>();

        connectionsTable.forEach((r1, cableMap) -> {
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

            cableMap.forEach((r2, connection) -> map.put(r2, connection != null ? connection.getMetrics() : null));

            table.put(r1, map);
        });

        return table;
    }

    @FXML
    void stopAnimation() {
        animatingConnections.forEach(Connection::stopSendingData);
        animatingConnections.clear();

        stopBtn.setDisable(true);
    }

    //Menus

    @FXML
    void close() {
        Platform.exit();
    }
}