package uz.ilyoskhurozov.anyroute.component;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class ConTooltip extends Tooltip {

    public ConTooltip(Connection con) {
        Font font = new Font("JetBrainsMono Nerd Font", 12);

        Label metricsLabel = new Label("Metrics:");
        metricsLabel.setFont(font);

        Label reliabilityLabel = new Label("Reliability:");
        reliabilityLabel.setFont(font);

        Label countLabel = new Label("Count:");
        countLabel.setFont(font);

        Label metrics = new Label();
        metrics.setFont(font);

        Label reliability = new Label();
        reliability.setFont(font);

        Label count = new Label();
        count.setFont(font);

        Label name = new Label(
                con.getStart().compareTo(con.getEnd()) < 0
                ? con.getStart() + " - " + con.getEnd()
                : con.getEnd() + " - " + con.getStart()
        );
        name.setFont( new Font("JetBrainsMono Nerd Font", 14));
        GridPane.setHalignment(name, HPos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.add(name, 0, 0, 2, 1);
        gridPane.add(metricsLabel, 0, 1);
        gridPane.add(metrics, 1, 1);
        gridPane.add(reliabilityLabel, 0, 2);
        gridPane.add(reliability, 1, 2);
        gridPane.add(countLabel, 0, 3);
        gridPane.add(count, 1, 3);

        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(5));
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(60,60,60),
                new ColumnConstraints(40,40,40)
        );

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(gridPane);

        setOnShowing(windowEvent -> {
            metrics.setText(con.getMetrics()+"");
            reliability.setText(con.getReliability()+"");
            count.setText(con.getCount()+"");
        });
    }
}
