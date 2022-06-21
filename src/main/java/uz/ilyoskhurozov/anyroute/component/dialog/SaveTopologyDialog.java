package uz.ilyoskhurozov.anyroute.component.dialog;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.Collection;
import java.util.Map;

public class SaveTopologyDialog extends Dialog<Map<String, String>> {

    public SaveTopologyDialog(Collection<String> routerNamesList, int n) {

        Font font = new Font("JetBrainsMono Nerd Font", 16);

        Label nameLabel = new Label("Name:");
        nameLabel.setFont(font);

        Label sourceLabel = new Label("Source:");
        sourceLabel.setFont(font);

        Label targetLabel = new Label("Target:");
        targetLabel.setFont(font);

        TextField nameField = new TextField("Topology"+n);

        ComboBox<String> targetBox = new ComboBox<>();
        ComboBox<String> sourceBox = new ComboBox<>();
        sourceBox.getItems().addAll(routerNamesList);

        sourceBox.setOnAction(actionEvent -> updateTargetBoxItems(targetBox, routerNamesList, sourceBox.getValue()));
        sourceBox.getSelectionModel().selectFirst();
        updateTargetBoxItems(targetBox, routerNamesList, sourceBox.getValue());

        GridPane gridPane = new GridPane();
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(sourceLabel, 0, 1);
        gridPane.add(sourceBox, 1, 1);
        gridPane.add(targetLabel, 0, 2);
        gridPane.add(targetBox, 1, 2);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(100,100,100),
                new ColumnConstraints(100,100,100)
        );

        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                super.bind(nameField.textProperty());
            }
            @Override
            protected boolean computeValue() {
                return nameField.getText().isBlank();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return Map.of(
                        "name", nameField.getText().trim(),
                        "source", sourceBox.getValue(),
                        "target", targetBox.getValue()
                );
            }
            return null;
        });

        setOnShowing(dialogEvent -> Platform.runLater(nameField::requestFocus));
    }

    private void updateTargetBoxItems(ComboBox<String> targetBox, Collection<String> items, String exclude) {
        targetBox.getItems().clear();
        targetBox.getItems().addAll(items);
        targetBox.getItems().remove(exclude);
        targetBox.getSelectionModel().selectFirst();
    }
}
