package uz.ilyoskhurozov.anyroute.component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Connection extends Group {
    private int metrics;
    private final Label label;
    private final Line cable;
    private String begin;
    private String end;
    private boolean isSendingData = false;
    private Color defColor;
    private static final int DEFAULT_METRIC = 1;

    public Connection(){
        this(DEFAULT_METRIC);
    }

    public Connection(int metrics) {
        this.metrics = metrics;
        label = new Label(metrics + "");
        label.setPadding(new Insets(0, 5, 0 , 5));
        label.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        cable = new Line();
        cable.setStrokeWidth(2);
        defColor = Color.BLACK;

        getChildren().add(cable);
        makeHoverable();
    }

    public void setBegin(Router router) {
        begin = router.getName();

        cable.startXProperty().bind(router.layoutXProperty().add(router.widthProperty().divide(2.0)));
        cable.startYProperty().bind(router.layoutYProperty().add(router.heightProp().divide(2.0)));
    }

    public String getBegin() {
        return begin;
    }

    public void setEnd(Router router) {
        end = router.getName();

        cable.endXProperty().bind(router.layoutXProperty().add(router.widthProperty().divide(2.0)));
        cable.endYProperty().bind(router.layoutYProperty().add(router.heightProp().divide(2.0)));

        label.translateXProperty().bind(cable.startXProperty().add(cable.endXProperty()).divide(2.0).subtract(label.widthProperty().divide(2)));
        label.translateYProperty().bind(cable.startYProperty().add(cable.endYProperty()).divide(2.0).subtract(label.heightProperty().divide(2)));
        getChildren().add(label);
    }

    public String getEnd() {
        return end;
    }

    private void setColor(Color color) {
        label.setTextFill(color);
        cable.setStroke(color);
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
                        cable.getStrokeDashArray().clear();
                        cable.getStrokeDashArray().addAll(finalI, 5.0);
                        double l = Math.sqrt(Math.pow(cable.getStartX() - cable.getEndX(), 2) + Math.pow(cable.getStartY() - cable.getEndY(), 2)) - finalI - 5;
                        while (l > -1) {
                            cable.getStrokeDashArray().addAll(25.0, 5.0);
                            l -= 30;
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            cable.getStrokeDashArray().clear();
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
        return cable.getStartX();
    }

    public double getStartY() {
        return cable.getStartY();
    }

    public void setEndX(double v) {
        cable.setEndX(v);
    }

    public double getEndX() {
        return cable.getEndX();
    }


    public void setEndY(double v) {
        cable.setEndY(v);
    }

    public double getEndY() {
        return cable.getEndY();
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
