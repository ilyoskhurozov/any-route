package uz.ilyoskhurozov.anyroute.component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Cable extends Group {
    private int length;
    private final Label label;
    private final Line line;
    private String begin;
    private String end;
    private boolean isSendingPackages = false;
    private Color defColor;

    public Cable(int length) {
        this.length = length;
        label = new Label(length + "");
        label.setPadding(new Insets(0, 5, 0 , 5));
        label.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        line = new Line();
        line.setStrokeWidth(2);
        defColor = Color.BLACK;

        getChildren().add(line);
        makeHoverable();
    }

    public void setBegin(Router router) {
        begin = router.getName();

        line.startXProperty().bind(router.layoutXProperty().add(router.widthProperty().divide(2.0)));
        line.startYProperty().bind(router.layoutYProperty().add(router.heightProp().divide(2.0)));
    }

    public String getBegin() {
        return begin;
    }

    public void setEnd(Router router) {
        end = router.getName();

        line.endXProperty().bind(router.layoutXProperty().add(router.widthProperty().divide(2.0)));
        line.endYProperty().bind(router.layoutYProperty().add(router.heightProp().divide(2.0)));

        label.translateXProperty().bind(line.startXProperty().add(line.endXProperty()).divide(2.0).subtract(label.widthProperty().divide(2)));
        label.translateYProperty().bind(line.startYProperty().add(line.endYProperty()).divide(2.0).subtract(label.heightProperty().divide(2)));
        getChildren().add(label);
    }

    public String getEnd() {
        return end;
    }

    private void setColor(Color color) {
        label.setTextFill(color);
        line.setStroke(color);
    }

    public void startSendingPackages(boolean isBeginToEnd) {
        isSendingPackages = true;
        setDefColor(Color.GREEN);
        double begin = (isBeginToEnd) ? 5.0 : 25;
        double step = (isBeginToEnd) ? 5 : -5;
        Thread animation = new Thread(() -> {
            while (isSendingPackages) {
                for (double i = begin; isSendingPackages && ((isBeginToEnd) ? i < 26.0 : i > 4.0); i += step) {
                    double finalI = i;
                    Platform.runLater(() -> {
                        line.getStrokeDashArray().clear();
                        line.getStrokeDashArray().addAll(finalI, 5.0);
                        double l = Math.sqrt(Math.pow(line.getStartX() - line.getEndX(), 2) + Math.pow(line.getStartY() - line.getEndY(), 2)) - finalI - 5;
                        while (l > -1) {
                            line.getStrokeDashArray().addAll(25.0, 5.0);
                            l -= 30;
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            line.getStrokeDashArray().clear();
            setDefColor(Color.BLACK);
        });
        animation.setDaemon(true);
        animation.start();
    }

    public void stopSendingPackages() {
        isSendingPackages = false;
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
        return line.getStartX();
    }

    public double getStartY() {
        return line.getStartY();
    }

    public void setEndX(double v) {
        line.setEndX(v);
    }

    public double getEndX() {
        return line.getEndX();
    }


    public void setEndY(double v) {
        line.setEndY(v);
    }

    public double getEndY() {
        return line.getEndY();
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
        label.setText(Integer.toString(length));
    }

    @Override
    public String toString() {
        return "Cable{"+length+"}";
    }
}
