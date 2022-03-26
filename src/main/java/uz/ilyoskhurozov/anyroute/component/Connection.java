package uz.ilyoskhurozov.anyroute.component;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
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
    private final Label label;
    private final ArrayList<Line> cables;
    private String start;
    private String end;
    private boolean isSendingData = false;
    private Color defColor;
    private static final int DEFAULT_METRIC = 1;
    private DoubleBinding startX;
    private DoubleBinding startY;
    private DoubleBinding endX;
    private DoubleBinding endY;

    public Connection(){
        this(DEFAULT_METRIC);
    }

    public Connection(int metrics) {
        this.metrics = metrics;
        label = new Label(metrics + "");
        label.setPadding(new Insets(0, 5, 0 , 5));
        label.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        cables = new ArrayList<>();
        Line cable = new Line();
        cable.setStrokeWidth(2);
        defColor = Color.BLACK;

        getChildren().add(cable);
        cables.add(cable);
        makeHoverable();
    }

    public void setStart(Router router) {
        start = router.getName();

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

    public String getStart() {
        return start;
    }

    public void setEnd(Router router) {
        end = router.getName();

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

    private void setEndCoors(DoubleBinding x, DoubleBinding y) {
        endX = x;
        endY = y;

        cables.get(0).endXProperty().bind(endX);
        cables.get(0).endYProperty().bind(endY);
    }

    public String getEnd() {
        return end;
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
        setEndCoors(
                new DoubleBinding() {
                    @Override
                    protected double computeValue() {
                        return x;
                    }
                },
                new DoubleBinding() {
                    @Override
                    protected double computeValue() {
                        return y;
                    }
                }
        );
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

    public void setMetrics(int metrics) {
        this.metrics = metrics;
        label.setText(Integer.toString(metrics));
    }

    @Override
    public String toString() {
        return "Cable{"+ metrics +"}";
    }
}
