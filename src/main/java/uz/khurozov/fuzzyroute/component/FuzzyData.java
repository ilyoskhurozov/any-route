package uz.khurozov.fuzzyroute.component;

import java.util.List;

public record FuzzyData(List<String> route, Double metrics, Double availability, Double fuzziedValue) {

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
