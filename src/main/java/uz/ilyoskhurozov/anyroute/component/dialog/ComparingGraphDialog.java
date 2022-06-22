package uz.ilyoskhurozov.anyroute.component.dialog;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import uz.ilyoskhurozov.anyroute.component.SourceTargetPane;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ComparingGraphDialog extends Dialog<Map<String, Object>> {


    public ComparingGraphDialog(Set<String> names, boolean isByTopologies) {

        //SAME
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(200,200,200),
                new ColumnConstraints(100,100,100)
        );

        Font font = new Font("JetBrainsMono Nerd Font", 16);

        Label routerReliabilityLabel = new Label("Cable's reliability (0.xxxx):");

        Spinner<Integer> routerReliability = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(9900, 9999, 9990)
        );
        routerReliability.setEditable(true);

        routerReliabilityLabel.setFont(font);
        routerReliability.getEditor().setFont(font);

        gridPane.add(routerReliabilityLabel, 0, 0);
        gridPane.add(routerReliability, 1, 0);

        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //DIVERSE
        if (isByTopologies) {
            FlowPane topologiesPane = new FlowPane();
            List<CheckBox> checks = names.stream().map(CheckBox::new).collect(Collectors.toList());
            topologiesPane.getChildren().addAll(checks);

            Label topologiesLabel = new Label("Topologies:");

            checks.forEach(checkBox -> checkBox.setFont(font));
            topologiesLabel.setFont(font);

            gridPane.add(topologiesLabel, 0, 1);
            gridPane.add(topologiesPane, 1, 1);

            getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
                {
                    super.bind(
                            checks.stream()
                                    .map(CheckBox::selectedProperty)
                                    .toArray(BooleanProperty[]::new)
                    );
                }
                @Override
                protected boolean computeValue() {
                    return checks.parallelStream().filter(CheckBox::isSelected).count() < 2;
                }
            });

            setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return Map.of(
                            "routerRel", routerReliability.getValue() / 10000.0,
                            "topologies", checks.stream()
                                    .filter(CheckBox::isSelected)
                                    .map(CheckBox::getText)
                                    .collect(Collectors.toList())
                    );
                }
                return null;
            });
        } else {
            Label ccFromLabel = new Label("Cable count (from):");
            Label ccToLabel = new Label("Cable count (to):");

            SourceTargetPane stPane = new SourceTargetPane(names, font);
            stPane.getColumnConstraints().addAll(gridPane.getColumnConstraints());

            Spinner<Integer> ccFromSpinner = new Spinner<>(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1)
            );
            ccFromSpinner.setEditable(true);

            Spinner<Integer> ccToSpinner = new Spinner<>(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3)
            );
            ccToSpinner.setEditable(true);


            ccFromLabel.setFont(font);
            ccToLabel.setFont(font);
            ccFromSpinner.getEditor().setFont(font);
            ccToSpinner.getEditor().setFont(font);

            gridPane.add(stPane, 0, 1, 2, 1);
            gridPane.add(ccFromLabel, 0, 2);
            gridPane.add(ccFromSpinner, 1, 2);
            gridPane.add(ccToLabel, 0, 3);
            gridPane.add(ccToSpinner, 1, 3);

            getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
                {
                    super.bind(ccFromSpinner.valueProperty(), ccFromSpinner.valueProperty());
                }
                @Override
                protected boolean computeValue() {
                    return ccToSpinner.getValue() - ccFromSpinner.getValue() < 2;
                }
            });

            setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    Map<String, String> st = stPane.getValue();
                    return Map.of(
                            "routerRel", routerReliability.getValue() / 10000.0,
                            "source", st.get("source"),
                            "target", st.get("target"),
                            "cableCountFrom", ccFromSpinner.getValue(),
                            "cableCountTo", ccToSpinner.getValue()
                    );
                }
                return null;
            });
        }
    }
}
