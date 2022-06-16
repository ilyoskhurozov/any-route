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
import uz.ilyoskhurozov.anyroute.component.ConPropsDialog;
import uz.ilyoskhurozov.anyroute.component.Connection;
import uz.ilyoskhurozov.anyroute.component.Router;
import uz.ilyoskhurozov.anyroute.component.SaveTopologyDialog;
import uz.ilyoskhurozov.anyroute.util.FindRoute;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


public class Controller {
    private int r = 0;
    private TreeMap<String, TreeMap<String, Connection>> connectionsTable;
    private TreeMap<String, Router> routersMap;
    private Connection currentConnection;
    private final ConPropsDialog conPropsDialog = new ConPropsDialog();

    @FXML
    private AnchorPane desk;

    @FXML
    private ToggleButton routerBtn;

    @FXML
    private ToggleButton cableBtn;

    @FXML
    private ToggleButton deleteBtn;

    @FXML
    private ToggleGroup toggles;

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
        algorithms.getItems().addAll("Dijskstra", "Floyd", "Bellman-Ford");
        algorithms.getSelectionModel().selectFirst();

        connectionsTable = new TreeMap<>();
        routersMap = new TreeMap<>();
    }

    public void bindKeys() {
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
                new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
                this::findRoute
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.CONTROL_ANY),
                this::cancel
        );
    }

    private void cancel() {
        if (currentConnection == null) {
            Toggle selectedBtn = toggles.getSelectedToggle();
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

                        currentConnection.setSource(router);
                        currentConnection.setEndXY(
                                router.getLayoutX() + mouseEvent.getX(),
                                router.getLayoutY() + mouseEvent.getY()
                        );

                        desk.setOnMouseMoved(event -> currentConnection.setEndXY(
                                event.getX() + Math.signum(currentConnection.getStartX() - currentConnection.getEndX()),
                                event.getY() + Math.signum(currentConnection.getStartY() - currentConnection.getEndY())
                        ));
                    } else {
                        if (
                                connectionsTable.get(currentConnection.getSource()).get(routerName) != null ||
                                        connectionsTable.get(routerName).get(currentConnection.getSource()) != null
                        ) {
                            //forbid doubling connection
                            return;
                        }
                        currentConnection.setTarget(router);

                        if (currentConnection.getEndX() == currentConnection.getStartX() &&
                                currentConnection.getEndY() == currentConnection.getStartY()
                        ) {
                            desk.getChildren().remove(currentConnection);
                        } else {
                            connectionsTable.get(currentConnection.getSource()).put(currentConnection.getTarget(), currentConnection);

                            currentConnection.setOnMouseClicked(mEvent -> {
                                Connection connection = (Connection) mEvent.getSource();
                                if (connection.isSendingData()) {
                                    return;
                                }
                                if (deleteBtn.isSelected()) {
                                    connectionsTable.get(connection.getSource()).put(connection.getTarget(), null);
                                    desk.getChildren().remove(connection);
                                } else if (mEvent.getClickCount() == 2 && currentConnection == null) {
                                    String[] names = new String[]{connection.getSource(), connection.getTarget()};
                                    Arrays.sort(names);
                                    conPropsDialog.setTitle(names[0] + " - " + names[1]);
                                    conPropsDialog.setProps(connection.getMetrics(), connection.getCableCount());

                                    conPropsDialog.showAndWait().ifPresent(
                                            conProps -> {
                                                connection.setProps(conProps.metrics);
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

                    routersMap.keySet().forEach(r -> {
                        Connection connection = connectionsTable.get(r).remove(routerName);
                        if (connection == null){
                            connection = connectionsTable.get(routerName).remove(r);
                        }

                        if (connection != null) {
                            desk.getChildren().remove(connection);
                        }
                    });
                    connectionsTable.remove(routerName);

                    findRouteBtn.setDisable(connectionsTable.size() < 2);
                }
            });

            desk.getChildren().add(router);
            routersMap.put(routerName, router);

            connectionsTable.put(routerName, new TreeMap<>());

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
        Set<String> routers = routersMap.keySet();
        choiceDialog.getItems().addAll(routers);
        choiceDialog.setTitle("Source");
        choiceDialog.setSelectedItem(choiceDialog.getItems().get(0));
        choiceDialog.showAndWait().ifPresent(r1 -> {
            stopAnimation();
            resultsPane.setVisible(false);

            choiceDialog.getItems().remove(r1);
            choiceDialog.setTitle("Target");
            choiceDialog.setSelectedItem(choiceDialog.getItems().get(0));
            choiceDialog.showAndWait().ifPresent(r2 -> Platform.runLater(() -> {
                Thread thread = new Thread(() -> {
                    findRouteBtn.setDisable(true);

                    String algo = algorithms.getValue();
                    List<String> route = null;
                    AtomicLong begin = new AtomicLong(), end = new AtomicLong();

                    try {
                        switch (algo) {
                            case "Dijskstra": {
                                begin.set(System.nanoTime());
                                route = FindRoute.withDijkstra(getMetricsTable(true), r1, r2);
                                end.set(System.nanoTime());
                            }
                            break;
                            case "Floyd": {
                                begin.set(System.nanoTime());
                                route = FindRoute.withFloyd(getMetricsTable(true), r1, r2);
                                end.set(System.nanoTime());
                            }
                            break;
                            case "Bellman-Ford": {
                                begin.set(System.nanoTime());
                                route = FindRoute.withBellmanFord(getMetricsTable(false), r1, r2);
                                end.set(System.nanoTime());
                            }
                            break;
                        }
                    } catch (RuntimeException e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setContentText(e.getMessage());
                            alert.showAndWait();
                        });
                        return;
                    }

                    if (route == null) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setHeaderText(null);
                            alert.setContentText("Couldn't find route! Make sure to all cables are connected correctly.");
                            alert.showAndWait();
                        });
                    } else {
                        String p1;
                        String p2 = route.get(0);

                        AtomicLong dis = new AtomicLong(0);

                        for (int i = 1; i < route.size(); i++) {
                            p1 = p2;
                            p2 = route.get(i);

                            Connection connection = connectionsTable.get(p1).get(p2);
                            if (connection == null) {
                                connection = connectionsTable.get(p2).get(p1);
                            }
                            connection.startSendingData(connection.getSource().equals(p1));
                            dis.addAndGet(connection.getMetrics());
                        }
                        stopBtn.setDisable(false);

                        Platform.runLater(() -> {
                            long t = end.get() - begin.get();
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
                            distance.setText(dis.get() + "");
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

    private TreeMap<String, TreeMap<String, Integer>> getMetricsTable(boolean isUndirected) {
        TreeMap<String, TreeMap<String, Integer>> table = new TreeMap<>();

        connectionsTable.forEach((r, cableMap) -> table.put(r, new TreeMap<>()));

        connectionsTable.forEach((r1, row) -> row.forEach((r2, con) -> {
            if (con != null) {

                table.get(r1).put(r2, con.getMetrics());

                if (isUndirected) {
                    if (con.getMetrics() < 0) throw new IllegalArgumentException("Negative metrics on undirected connection");
                    table.get(r2).put(r1, con.getMetrics());
                }
            }
        }));

        return table;
    }

    @FXML
    void stopAnimation() {
        desk.getChildren()
                .parallelStream()
                .filter(node -> node instanceof Connection)
                .map(node -> (Connection) node)
                .forEach(Connection::stopSendingData);

        stopBtn.setDisable(true);
    }

    //Menus

    @FXML
    void graphByTopology() {

    }

    @FXML
    void graphByCableCount() {

    }

    @FXML
    void saveTopology() {
        if (routersMap.size() < 3) return;
        Optional<Map<String, String>> stringStringMap = new SaveTopologyDialog(routersMap.keySet(), 1).showAndWait();
        stringStringMap.ifPresent(System.out::println);
        //TODO save data to cache
    }

    @FXML
    void clearCache() {

    }

    @FXML
    void close() {
        Platform.exit();
    }
}