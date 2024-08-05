package uz.khurozov.route.util.algo;

import java.util.List;
import java.util.TreeMap;

public abstract class RouteAlgorithm {
    public abstract List<String> findRoute(
            TreeMap<String, TreeMap<String, Integer>> table,
            String source,
            String target
    );
}
