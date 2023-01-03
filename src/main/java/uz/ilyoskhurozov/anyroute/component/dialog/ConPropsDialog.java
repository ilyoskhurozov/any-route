package uz.ilyoskhurozov.anyroute.component.dialog;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class ConPropsDialog extends Dialog<ConPropsDialog.ConProps> {
    public record ConProps(int metrics, int count, int availability) {
    }

    private final Spinner<Integer> metricsSpinner;
    private final Spinner<Integer> countSpinner;
    private final Spinner<Integer> availabilitySpinner;

    public ConPropsDialog() {

        Font font = new Font("JetBrainsMono Nerd Font", 16);

        Label metricsLabel = new Label("Metrics:");
        metricsLabel.setFont(font);

        Label countLabel = new Label("Count:");
        countLabel.setFont(font);

        Label availabilityLabel = new Label("Availability:");
        availabilityLabel.setFont(font);

        metricsSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 1)
        );
        metricsSpinner.setEditable(true);
        metricsSpinner.getEditor().setFont(font);

        countSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6)
        );
        countSpinner.setEditable(true);
        countSpinner.getEditor().setFont(font);

        availabilitySpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6)
        );
        availabilitySpinner.setEditable(true);
        availabilitySpinner.getEditor().setFont(font);

        GridPane gridPane = new GridPane();
        gridPane.add(metricsLabel, 0, 0);
        gridPane.add(metricsSpinner, 1, 0);
        gridPane.add(countLabel, 0, 1);
        gridPane.add(countSpinner, 1, 1);
        gridPane.add(availabilityLabel, 0, 2);
        gridPane.add(availabilitySpinner, 1, 2);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(100, 100, 100),
                new ColumnConstraints(100, 100, 100)
        );

        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                super.bind(metricsSpinner.valueProperty());
            }

            @Override
            protected boolean computeValue() {
                return metricsSpinner.getValue() == 0;
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new ConProps(
                        metricsSpinner.getValue(),
                        countSpinner.getValue(),
                        availabilitySpinner.getValue()
                );
            }
            return null;
        });

        setOnShowing(dialogEvent -> Platform.runLater(metricsSpinner::requestFocus));
    }

    public void setProps(int metrics, int count, int availability) {
        metricsSpinner.getValueFactory().setValue(metrics);
        countSpinner.getValueFactory().setValue(count);
        availabilitySpinner.getValueFactory().setValue(availability);
    }
}
