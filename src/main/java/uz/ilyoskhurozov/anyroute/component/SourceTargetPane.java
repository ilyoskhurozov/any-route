package uz.ilyoskhurozov.anyroute.component;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.Map;
import java.util.Set;

public class SourceTargetPane extends GridPane {
    private final ComboBox<String> targetBox;
    private final ComboBox<String> sourceBox;
    private final Set<String> routerNames;

    public SourceTargetPane(Set<String> routerNames, Font labelFont) {
        this.routerNames = routerNames;
        Label sourceLabel = new Label("Source:");
        sourceLabel.setFont(labelFont);
        Label targetLabel = new Label("Destination:");
        targetLabel.setFont(labelFont);

        targetBox = new ComboBox<>();
        sourceBox = new ComboBox<>();
        sourceBox.getItems().addAll(routerNames);

        sourceBox.setOnAction(actionEvent -> updateTargetBoxItems());
        sourceBox.getSelectionModel().selectFirst();
        updateTargetBoxItems();

        add(sourceLabel, 0, 0);
        add(sourceBox, 1, 0);
        add(targetLabel, 0, 1);
        add(targetBox, 1, 1);

        setHgap(10);
        setVgap(10);
    }

    private void updateTargetBoxItems() {
        targetBox.getItems().clear();
        targetBox.getItems().addAll(routerNames);
        targetBox.getItems().remove(sourceBox.getValue());
        targetBox.getSelectionModel().selectFirst();
    }

    public Map<String, String> getValue() {
        return Map.of("source", sourceBox.getValue(), "target", targetBox.getValue());
    }
}
