package uz.ilyoskhurozov.anyroute.component.dialog;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import uz.ilyoskhurozov.anyroute.component.SourceTargetPane;

import java.util.Map;
import java.util.Set;

public class SaveTopologyDialog extends Dialog<Map<String, String>> {

    public SaveTopologyDialog(Set<String> routerNamesList, int n) {

        Font font = new Font("JetBrainsMono Nerd Font", 16);

        Label nameLabel = new Label("Name:");
        nameLabel.setFont(font);
        TextField nameField = new TextField("Topology"+n);
        SourceTargetPane stPane = new SourceTargetPane(routerNamesList, font);


        GridPane gridPane = new GridPane();
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);
        gridPane.add(stPane, 0, 1, 2, 1);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(100,100,100),
                new ColumnConstraints(100,100,100)
        );
        stPane.getColumnConstraints().addAll(gridPane.getColumnConstraints());

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
                Map<String, String> st = stPane.getValue();
                return Map.of(
                        "name", nameField.getText().trim(),
                        "source", st.get("source"),
                        "target", st.get("target")
                );
            }
            return null;
        });

        setOnShowing(dialogEvent -> Platform.runLater(nameField::requestFocus));
    }
}
