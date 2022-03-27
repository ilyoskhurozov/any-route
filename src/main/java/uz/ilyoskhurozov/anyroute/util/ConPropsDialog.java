package uz.ilyoskhurozov.anyroute.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class ConPropsDialog extends Dialog<ConPropsDialog.ConProps> {
    public static class ConProps {
        public final int metrics;
        public final float reliability;
        public final int count;

        public ConProps(int metrics, float reliability, int count){
            this.metrics = metrics;
            this.reliability = reliability;
            this.count = count;
        }
    }

    private final Spinner<Integer> metricsSpinner;
    private final Spinner<String> reliabilitySpinner;
    private final Spinner<Integer> countSpinner;

    public ConPropsDialog() {

        Font font = new Font("JetBrainsMono Nerd Font", 16);

        Label metricsLabel = new Label("Metrics:");
        metricsLabel.setFont(font);

        Label reliabilityLabel = new Label("Reliability:");
        reliabilityLabel.setFont(font);

        Label countLabel = new Label("Count:");
        reliabilityLabel.setFont(font);

        metricsSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE)
        );
        metricsSpinner.setEditable(true);
        metricsSpinner.getEditor().setFont(font);

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

        countSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6)
        );
        countSpinner.setEditable(true);
        countSpinner.getEditor().setFont(font);

        GridPane gridPane = new GridPane();
        gridPane.add(metricsLabel, 0, 0);
        gridPane.add(metricsSpinner, 1, 0);
        gridPane.add(reliabilityLabel, 0, 1);
        gridPane.add(reliabilitySpinner, 1, 1);
        gridPane.add(countLabel, 0, 2);
        gridPane.add(countSpinner, 1, 2);

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
                return new ConProps(
                        metricsSpinner.getValue(),
                        Float.parseFloat(reliabilitySpinner.getValue()),
                        countSpinner.getValue()
                );
            }
            return null;
        });

        setOnShowing(dialogEvent -> Platform.runLater(metricsSpinner::requestFocus));
    }

    public void setProps(int metrics, float reliability){
        metricsSpinner.getValueFactory().setValue(metrics);

        int rel = (int) (reliability * 10000), n = 4;
        while (rel % 10 == 0) {
            rel /= 10;
            n--;
        }
        reliabilitySpinner.getValueFactory().setValue(String.format("0.%0"+n+"d", rel));
    }
}
