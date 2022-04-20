package uz.ilyoskhurozov.anyroute.component;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

public class Router extends VBox {
    // router position
    private double x = 0;
    private double y = 0;
    // mouse position
    private double mouseX = 0;
    private double mouseY = 0;

    private final Label label;
    private final FontIcon icon;

    public Router(int number, double x, double y) {
        icon = new FontIcon("mdi2r-router");
        icon.setIconSize(30);
        label = new Label("R" + number);
        setColor(Color.BLACK);

        getChildren().addAll(icon, label);

        setLayoutX(x);
        setLayoutY(y);
        setAlignment(Pos.CENTER);

        makeHoverable();
        makeDraggable();
    }

    public String getName() {
        return label.getText();
    }

    public DoubleBinding heightProp() {
        return heightProperty().subtract(label.heightProperty());
    }

    private void setColor(Color color) {
        icon.setIconColor(color);
        label.setTextFill(color);
    }

    private void makeHoverable() {
        setOnMouseEntered(mouseEvent -> setColor(Color.STEELBLUE));
        setOnMouseExited(mouseEvent -> setColor(Color.BLACK));
    }

    private void makeDraggable() {
        setOnMousePressed(event -> {
            setColor(Color.BLACK);

            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            x = getLayoutX();
            y = getLayoutY();
        });

        setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - mouseX;
            double offsetY = event.getSceneY() - mouseY;

            x += offsetX;
            y += offsetY;

            double maxX = getParent().getLayoutBounds().getMaxX();
            double maxY = getParent().getLayoutBounds().getMaxY();

            if (x >= 0 && x + getWidth() <= maxX) setLayoutX(x);
            if (y >= 0 && y + getHeight() <= maxY) setLayoutY(y);

            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            event.consume();
        });
    }

    @Override
    public String toString() {
        return "Router{" + label.getText() + "}";
    }
}
