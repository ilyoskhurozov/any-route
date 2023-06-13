package uz.ilyoskhurozov.anyroute.component.dialog;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import uz.ilyoskhurozov.anyroute.util.GlobalVariables;

public class AvailabilityDialog extends Dialog<AvailabilityDialog.ConProps> {
    public record ConProps(int routerAvailability, int connectionAvailability) {
    }

    private final Spinner<Integer> routerSpinner;
    private final Spinner<Integer> connectionSpinner;

    public AvailabilityDialog() {

        Font font = new Font("JetBrainsMono Nerd Font", 16);

        Label routerLabel = new Label("Router (0.xxx):");
        routerLabel.setFont(font);

        Label connectionLabel = new Label("Connection (0.xxx):");
        connectionLabel.setFont(font);

        routerSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 999)
        );
        routerSpinner.setEditable(true);
        routerSpinner.getEditor().setFont(font);

        connectionSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1000)
        );
        connectionSpinner.setEditable(true);
        connectionSpinner.getEditor().setFont(font);

        GridPane gridPane = new GridPane();
        gridPane.add(routerLabel, 0, 0);
        gridPane.add(routerSpinner, 1, 0);
        gridPane.add(connectionLabel, 0, 1);
        gridPane.add(connectionSpinner, 1, 1);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(200, 200, 200),
                new ColumnConstraints(200, 200, 200)
        );

        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                super.bind(routerSpinner.valueProperty());
            }

            @Override
            protected boolean computeValue() {
                return routerSpinner.getValue() == 0;
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                GlobalVariables.routerAvailability = routerSpinner.getValue() / 1000f;
                GlobalVariables.connectionAvailability = connectionSpinner.getValue() / 1000f;
            }
            return null;
        });

        setOnShowing(dialogEvent -> Platform.runLater(routerSpinner::requestFocus));
        setProps();
    }

    public void setProps() {
        routerSpinner.getValueFactory().setValue((int) (GlobalVariables.routerAvailability * 1000));
        connectionSpinner.getValueFactory().setValue((int) (GlobalVariables.connectionAvailability * 1000));
    }
}
