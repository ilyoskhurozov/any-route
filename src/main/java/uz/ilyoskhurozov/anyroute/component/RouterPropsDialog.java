package uz.ilyoskhurozov.anyroute.component;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class RouterPropsDialog extends Dialog<RouterPropsDialog.RouterProps> {
    public static class RouterProps {
        public final double reliability;

        public RouterProps(double reliability){
            this.reliability = reliability;
        }
    }

    private final Spinner<String> reliabilitySpinner;

    public RouterPropsDialog() {
        Font font = new Font("JetBrainsMono Nerd Font", 16);

        Label reliabilityLabel = new Label("Reliability:");
        reliabilityLabel.setFont(font);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i < 10000; i++) {
            int tmp = i, n = 4;
            while (tmp % 10 == 0) {
                tmp /= 10;
                n--;
            }
            list.add(String.format("0.%0"+n+"d", tmp));
        }
        reliabilitySpinner = new Spinner<>(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList(list))
        );
        reliabilitySpinner.setEditable(true);
        reliabilitySpinner.getEditor().setFont(font);

        GridPane gridPane = new GridPane();
        gridPane.add(reliabilityLabel, 0, 1);
        gridPane.add(reliabilitySpinner, 1, 1);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(100,100,100),
                new ColumnConstraints(100,100,100)
        );

        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new RouterProps(
                        Double.parseDouble(reliabilitySpinner.getValue())
                );
            }
            return null;
        });

        setOnShowing(dialogEvent -> Platform.runLater(reliabilitySpinner::requestFocus));
    }

    public void setProps(double reliability){
        int rel = (int) (reliability * 10000), n = 4;
        while (rel % 10 == 0) {
            rel /= 10;
            n--;
        }
        reliabilitySpinner.getValueFactory().setValue(String.format("0.%0"+n+"d", rel));
    }
}
