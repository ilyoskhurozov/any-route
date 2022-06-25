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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import uz.ilyoskhurozov.anyroute.component.*;
import uz.ilyoskhurozov.anyroute.component.dialog.ComparingGraphDialog;
import uz.ilyoskhurozov.anyroute.component.dialog.ConPropsDialog;
import uz.ilyoskhurozov.anyroute.component.dialog.JustAlert;
import uz.ilyoskhurozov.anyroute.component.dialog.SaveTopologyDialog;
import uz.ilyoskhurozov.anyroute.util.ReliabilityGraphData;
import uz.ilyoskhurozov.anyroute.util.TopologyData;
import uz.ilyoskhurozov.anyroute.util.algo.Dijkstra;
import uz.ilyoskhurozov.anyroute.util.algo.RouteAlgorithm;
import uz.ilyoskhurozov.anyroute.util.algo.RouteUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


public class Controller {
    private int r = 0;
    private TreeMap<String, TreeMap<String, Connection>> connectionsTable;
    private TreeMap<String, Router> routersMap;
    private Connection currentConnection;
    private final ConPropsDialog conPropsDialog = new ConPropsDialog();
    private final Map<String, TopologyData> topologyDataCache = new LinkedHashMap<>();

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
        algorithms.getItems().addAll("Dijkstra", "Floyd", "Bellman-Ford");
        algorithms.getSelectionModel().selectFirst();

        connectionsTable = new TreeMap<>();
        routersMap = new TreeMap<>();
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

        if (toggles.getSelectedToggle() == null) return;
        toggles.getSelectedToggle().setSelected(false);
    }

    @FXML
    void findRoute() {
        stopAnimation();
        resultsPane.setVisible(false);

        Font font = new Font("JetBrainsMono Nerd Font", 16);

        SourceTargetPane stPane = new SourceTargetPane(routersMap.keySet(), font);
        stPane.getColumnConstraints().addAll(
                new ColumnConstraints(100,100,100),
                new ColumnConstraints(100,100,100)
        );

        Dialog<Map<String, String>> stDialog = new Dialog<>();
        stDialog.getDialogPane().setContent(stPane);
        stDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        stDialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return stPane.getValue();
            }
            return null;
        });

        stDialog.showAndWait().ifPresent(st -> Platform.runLater(() -> {
            String r1 = st.get("source");
            String r2 = st.get("target");

            Thread thread = new Thread(() -> {
                findRouteBtn.setDisable(true);

                String algoName = algorithms.getValue();
                List<String> route;
                AtomicLong begin = new AtomicLong(), end = new AtomicLong();

                try {
                    RouteAlgorithm algo = RouteUtil.getRouteAlgorithm(algoName);
                    begin.set(System.nanoTime());
                    route = algo.findRoute(getMetricsTable(!algoName.equals("Bellman-Ford")), r1, r2);
                    end.set(System.nanoTime());
                } catch (RuntimeException e) {
                    Platform.runLater(() -> new JustAlert(Message.RUNTIME_ERROR).showAndWait());
                    System.out.println(e.getMessage());
                    return;
                }

                if (route == null) {
                    Platform.runLater(() -> new JustAlert(Message.ROUTE_NOT_FOUND).showAndWait());
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
    @SuppressWarnings("unchecked")
    void graphByTopology() {
        if (topologyDataCache.size() < 2) {
            new JustAlert(Message.AT_LEAST_TWO_TOPOLOGIES).showAndWait();
            return;
        }

        Optional<Map<String, Object>> dataOpt = new ComparingGraphDialog(
                topologyDataCache.keySet(), true
        ).showAndWait();

        if (dataOpt.isEmpty()) return;

        Map<String, Object> data = dataOpt.get();

        double routerRel = ((double) data.get("routerRel"));
        List<String> topologyNames = (List<String>) data.get("topologies");
        List<TopologyData> topologies = topologyDataCache.values().stream()
                .filter(topologyData -> topologyNames.contains(topologyData.name))
                .collect(Collectors.toList());

        showComparingGraphView(
                ReliabilityGraphData.comparingTopologies(routerRel, topologies)
        );
    }

    @FXML
    void graphByCableCount() {
        if (routersMap.size() < 2) {
            new JustAlert(Message.AT_LEAST_TWO_ROUTERS).showAndWait();
            return;
        }

        Optional<Map<String, Object>> dataOpt = new ComparingGraphDialog(
                routersMap.keySet(), false
        ).showAndWait();

        if (dataOpt.isEmpty()) return;

        Map<String, Object> data = dataOpt.get();

        double routerRel = ((double) data.get("routerRel"));
        String source = ((String) data.get("source"));
        String target = ((String) data.get("target"));
        Integer cableCountFrom = ((Integer) data.get("cableCountFrom"));
        Integer cableCountTo = ((Integer) data.get("cableCountTo"));

        List<String> route = new Dijkstra().findRoute(getMetricsTable(true), source, target);

        if (route == null) {
            new JustAlert(Message.ROUTE_NOT_FOUND).showAndWait();
            return;
        }

        showComparingGraphView(ReliabilityGraphData.comparingCableCount(
                routerRel, cableCountFrom, cableCountTo, route.size()
        ));
    }

    @FXML
    void saveTopology() {
        if (routersMap.size() < 2) {
            new JustAlert(Message.AT_LEAST_TWO_ROUTERS).showAndWait();
            return;
        }
        Optional<Map<String, String>> stringStringMap = new SaveTopologyDialog(routersMap.keySet(), topologyDataCache.size()).showAndWait();
        stringStringMap.ifPresent(map -> {
            String name = map.get("name");
            String source = map.get("source");
            String target = map.get("target");
            List<String> route = new Dijkstra().findRoute(getMetricsTable(true), source, target);

            if (route == null) {
                new JustAlert(Message.ROUTE_NOT_FOUND).showAndWait();
                return;
            }

            if (topologyDataCache.containsKey(name)) {
                new JustAlert(Message.TOPOLOGY_NAME_EXISTS).showAndWait();
                return;
            }

            topologyDataCache.put(
                    name,
                    new TopologyData(
                            name,
                            source,
                            target,
                            getIsConnectedTable()
                    )
            );
            clearDesk();
        });
    }

    @FXML
    void clearCache() {
        new JustAlert(Message.DELETE_TOPOLOGY_CACHE_CONFIRMATION)
                .showAndWait()
                .ifPresent(buttonType -> {
                    if (buttonType == ButtonType.OK) {
                        topologyDataCache.clear();
                    }
                });
    }

    @FXML
    void close() {
        Platform.exit();
    }

    //helper methods

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

    private Map<String, Map<String, Boolean>> getIsConnectedTable() {
        Map<String, Map<String, Boolean>> map = new TreeMap<>();
        Set<String> routerNames = routersMap.keySet();

        connectionsTable.forEach((r, cableMap) -> map.put(r, new TreeMap<>()));
        connectionsTable.forEach((r1, row) -> {
            row.forEach((r2, con) -> {
                map.get(r1).put(r2, true);
                map.get(r2).put(r1, true);
            });
            routerNames.forEach(r2 -> map.get(r1).putIfAbsent(r2, false));
        });

        return map;
    }

    private void showComparingGraphView(Map<String, double[]> chartData) {
        new ComparingGraphView(
                chartData,
                new double[] {
                        0.99, 0.99099, 0.99198, 0.99297, 0.99396, 0.99495,
                        0.99594, 0.99693, 0.99792, 0.99891, 0.9999,
                }
        ).showAndWait();
    }
}