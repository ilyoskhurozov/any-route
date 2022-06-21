package uz.ilyoskhurozov.anyroute.component;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComparingGraphView extends Stage {

    public ComparingGraphView(Map<String, double[]> table, double[] x) {
        initModality(Modality.APPLICATION_MODAL);
        double[] min = {1};

        TableView<Double[]> tableView = new TableView<>();
        Map<String, Integer> indexMap = new HashMap<>();
        String[] topologies = table.keySet().toArray(new String[0]);
        double[][] tmp = new double[topologies.length][];
        for (int i = 0; i < topologies.length; i++) {
            indexMap.put(topologies[i], i);
            tmp[i] = table.get(topologies[i]);
        }
        Double[][] values = new Double[tmp[0].length][tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[i].length; j++) {
                values[j][i] = tmp[i][j];
            }
        }
        ObservableList<Double[]> items = FXCollections.observableArrayList(values);

        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();


        table.forEach((name, row) -> {
            TableColumn<Double[], Double> col = new TableColumn<>(name);
            col.setCellValueFactory(features -> new SimpleObjectProperty<>(features.getValue()[indexMap.get(name)]));
            tableView.getColumns().add(col);

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
        tableView.setItems(items);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(Math.ceil(min[0] * 98)/100);
        yAxis.setUpperBound(1.01);
        yAxis.setTickUnit(0.005);

        LineChart<String, Number> lineChart = new LineChart<>(
                new CategoryAxis(), yAxis,
                FXCollections.observableArrayList(seriesList)
        );

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.add(lineChart, 0, 0);
        gridPane.add(tableView, 1, 0);

        ColumnConstraints colGrowA = new ColumnConstraints();
        ColumnConstraints colGrowB = new ColumnConstraints();
        RowConstraints rowGrow = new RowConstraints();
        colGrowA.setHgrow(Priority.ALWAYS);
        colGrowB.setHgrow(Priority.ALWAYS);
        rowGrow.setVgrow(Priority.ALWAYS);
        colGrowA.setPercentWidth(70);
        colGrowB.setPercentWidth(30);
        gridPane.getColumnConstraints().addAll(colGrowA, colGrowB);
        gridPane.getRowConstraints().add(rowGrow);

        setScene(new Scene(gridPane, 1000, 600));
    }
}
