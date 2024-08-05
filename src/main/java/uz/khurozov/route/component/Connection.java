package uz.khurozov.route.component;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class Connection extends Group {
    private int metrics;
    private int cableAvailability;
    private final Label label;
    private final ArrayList<Line> cables;
    private String source;
    private String target;
    private boolean isSendingData = false;
    private Color defColor;
    private static final int DEFAULT_METRIC = 1;
    private static final int DEFAULT_AVAILABILITY = 1000;
    private DoubleBinding startX;
    private DoubleBinding startY;
    private DoubleBinding endX;
    private DoubleBinding endY;

    public Connection() {
        this(DEFAULT_METRIC, DEFAULT_AVAILABILITY);
    }

    public Connection(int metrics, int cableAvailability) {
        this.cableAvailability = cableAvailability;
        this.metrics = metrics;
        label = new Label(String.valueOf(metrics));
        label.setPadding(new Insets(0, 5, 0, 5));
        label.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        cables = new ArrayList<>();
        Line cable = new Line();
        cable.setStrokeWidth(1);
        defColor = Color.BLACK;

        getChildren().add(cable);
        cables.add(cable);
        makeHoverable();
    }

    public void setSource(Router router) {
        source = router.getName();

        setStartCoors(
                router.layoutXProperty().add(router.widthProperty().divide(2.0)),
                router.layoutYProperty().add(router.heightProp().divide(2.0))
        );
    }

    private void setStartCoors(DoubleBinding x, DoubleBinding y) {
        startX = x;
        startY = y;

        cables.get(0).startXProperty().bind(startX);
        cables.get(0).startYProperty().bind(startY);
    }

    public String getSource() {
        return source;
    }

    public void setTarget(Router router) {
        target = router.getName();

        setEndCoors(
                router.layoutXProperty().add(router.widthProperty().divide(2.0)),
                router.layoutYProperty().add(router.heightProp().divide(2.0))
        );

        DoubleBinding x = startX.add(endX).divide(2.0).subtract(label.widthProperty().divide(2));
        DoubleBinding y = startY.add(endY).divide(2.0).subtract(label.heightProperty().divide(2));

        label.translateXProperty().bind(x);
        label.translateYProperty().bind(y);
        getChildren().add(label);
    }

    public void setCableCount(int n) {
        while (cables.size() > n) {
            getChildren().remove(
                    cables.remove(n)
            );
        }
        while (cables.size() < n) {
            Line cable = new Line();
            cable.setStrokeWidth(1);

            getChildren().add(cable);
            cables.add(cable);
        }

        updateCables();
        label.toFront();
    }

    public int getCableCount() {
        return cables.size();
    }

    private void updateCables() {
        DoubleBinding xSign = new DoubleBinding() {
            {
                super.bind(startY, endY);
            }

            @Override
            protected double computeValue() {
                return Math.signum(startY.subtract(endY).get());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.observableArrayList(startY, endY);
            }

            @Override
            public void dispose() {
                super.unbind(startY, endY);
            }
        };
        DoubleBinding ySign = new DoubleBinding() {
            {
                super.bind(startX, endX);
            }

            @Override
            protected double computeValue() {
                return -Math.signum(startX.subtract(endX).get());
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.observableArrayList(startX, endX);
            }

            @Override
            public void dispose() {
                super.unbind(startX, endX);
            }
        };

        double k = 3.0;
        int n = cables.size();
        double cur = -k * (n - 1) / 2.0;

        for (int i = 0; i < n; i++, cur += k) {
            Line cable = cables.get(i);

            cable.startXProperty().bind(startX.subtract(xSign.multiply(cur)));
            cable.startYProperty().bind(startY.subtract(ySign.multiply(cur)));

            cable.endXProperty().bind(endX.subtract(xSign.multiply(cur)));
            cable.endYProperty().bind(endY.subtract(ySign.multiply(cur)));
        }
    }

    private void setEndCoors(DoubleBinding x, DoubleBinding y) {
        endX = x;
        endY = y;

        updateCables();
    }

    public String getTarget() {
        return target;
    }

    private void setColor(Color color) {
        label.setTextFill(color);
        cables.forEach(cable -> cable.setStroke(color));
    }

    public void startSendingData(boolean isForward) {
        isSendingData = true;
        setDefColor(Color.GREEN);
        double begin = (isForward) ? 5.0 : 25;
        double step = (isForward) ? 5 : -5;
        Thread animation = new Thread(() -> {
            while (isSendingData) {
                for (double i = begin; isSendingData && ((isForward) ? i < 26.0 : i > 4.0); i += step) {
                    double finalI = i;
                    Platform.runLater(() -> {
                        cables.forEach(cable -> {
                            cable.getStrokeDashArray().clear();
                            cable.getStrokeDashArray().addAll(finalI, 5.0);
                        });

                        double l = Math.sqrt(Math.pow(startX.get() - endX.get(), 2) + Math.pow(startY.get() - endY.get(), 2)) - finalI - 5;
                        while (l > -1) {
                            cables.forEach(cable -> cable.getStrokeDashArray().addAll(25.0, 5.0));
                            l -= 30;
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            cables.forEach(cable -> cable.getStrokeDashArray().clear());
            setDefColor(Color.BLACK);
        });
        animation.setDaemon(true);
        animation.start();
    }

    public void stopSendingData() {
        isSendingData = false;
        cables.forEach(cable -> cable.getStrokeDashArray().clear());
        setDefColor(Color.BLACK);
    }

    public boolean isSendingData() {
        return isSendingData;
    }

    private void setDefColor(Color color) {
        defColor = color;
        setColor(defColor);
    }

    private void makeHoverable() {
        label.onMouseEnteredProperty().bind(onMouseEnteredProperty());
        label.onMouseExitedProperty().bind(onMouseExitedProperty());

        setOnMouseEntered(mouseEvent -> setColor(Color.STEELBLUE));
        setOnMouseExited(mouseEvent -> setColor(defColor));
    }

    public double getStartX() {
        return startX.get();
    }

    public double getStartY() {
        return startY.get();
    }

    public void setEndXY(double x, double y) {
        cables.get(0).setEndX(x);
        cables.get(0).setEndY(y);

        endX = new DoubleBinding() {
            @Override
            protected double computeValue() {
                return x;
            }
        };
        endY = new DoubleBinding() {
            @Override
            protected double computeValue() {
                return y;
            }
        };
    }

    public double getEndX() {
        return endX.get();
    }

    public double getEndY() {
        return endY.get();
    }

    public int getMetrics() {
        return metrics;
    }

    public int getCableAvailability() {
        return cableAvailability;
    }

    public void setProps(int metrics, int cableAvailability) {
        this.metrics = metrics;
        this.cableAvailability = cableAvailability;
        label.setText(Integer.toString(metrics));
    }

    @Override
    public String toString() {
        return "Cable{ metrics: " + metrics + ",\n"
                + "availability: " + cableAvailability + "}";
    }
}
