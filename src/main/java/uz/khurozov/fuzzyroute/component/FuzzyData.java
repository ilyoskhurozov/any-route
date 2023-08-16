package uz.khurozov.fuzzyroute.component;

import java.util.List;

public class FuzzyData {

    private final List<String> route;
    private final Double metrics;
    private final Double availability;
    private final Double fuzziedValue;

    public FuzzyData(List<String> route, Double metrics, Double availability, Double fuzziedValue) {
        this.route = route;
        this.metrics = metrics;
        this.availability = availability;
        this.fuzziedValue = fuzziedValue;
    }

    public List<String> getRoute() {
        return route;
    }

    public Double getMetrics() {
        return metrics;
    }

    public Double getAvailability() {
        return availability;
    }

    public Double getFuzziedValue() {
        return fuzziedValue;
    }

    @Override
    public String toString() {
        return "FuzzyData{" +
                "route=" + route +
                ", metrics=" + metrics +
                ", availability=" + availability +
                ", fuzziedValue=" + fuzziedValue +
                '}';
    }
}
