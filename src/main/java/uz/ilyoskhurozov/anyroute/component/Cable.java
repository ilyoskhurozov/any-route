package uz.ilyoskhurozov.anyroute.component;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Cable extends Group {
    private int length;
    private final Label label;
    private final Line line;
    private String begin;
    private String end;

    public Cable(int length) {
        this.length = length;
        label = new Label(length + "");
        line = new Line();
        line.setStrokeWidth(2);

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

        label.translateXProperty().bind(line.startXProperty().add(line.endXProperty()).divide(2.0));
        label.translateYProperty().bind(line.startYProperty().add(line.endYProperty()).divide(2.0));
        getChildren().add(label);
    }

    public String getEnd() {
        return end;
    }

    private void setColor(Color color) {
        label.setTextFill(color);
        line.setStroke(color);
    }

    private void makeHoverable() {
        label.onMouseEnteredProperty().bind(onMouseEnteredProperty());
        label.onMouseExitedProperty().bind(onMouseExitedProperty());

        setOnMouseEntered(mouseEvent -> setColor(Color.STEELBLUE));
        setOnMouseExited(mouseEvent -> setColor(Color.BLACK));
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
