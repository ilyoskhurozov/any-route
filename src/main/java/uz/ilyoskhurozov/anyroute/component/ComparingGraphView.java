package uz.ilyoskhurozov.anyroute.component;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComparingGraphView extends Stage {

    public ComparingGraphView(Map<String, double[]> table, double[] x) {
        initModality(Modality.APPLICATION_MODAL);
        double[] min = {1};

        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        table.forEach((name, row) -> {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(name);

            for (int i = 0; i < x.length; i++) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(x[i] + "", row[i]);

                StackPane node = new StackPane();
                node.setPrefSize(7, 7);
                node.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
                node.setUserData(i);
                node.setOnMouseEntered(e -> {
                    System.out.println(node.getUserData());
                    //TODO visible chart data
                });
                data.setNode(node);
                series.getData().add(data);
            }

            seriesList.add(series);
            if (row[0] < min[0]) min[0] = row[0];
        });

        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(Math.ceil(min[0] * 98)/100);
        yAxis.setUpperBound(1.01);
        yAxis.setTickUnit(0.005);

        LineChart<String, Number> lineChart = new LineChart<>(
                new CategoryAxis(), yAxis,
                FXCollections.observableArrayList(seriesList)
        );
        setScene(new Scene(lineChart, 800, 600));
    }
}
