package uz.khurozov.fuzzyroute.util.algo;

import java.util.*;

public class FuzzyLogic extends RouteAlgorithm {
    @Override
    public List<String> findRoute(TreeMap<String, TreeMap<String, Integer>> table, String source, String target) {

        List<List<String>> allRoutes = new ArrayList<>();
        List<String> currentRoute = new ArrayList<>();

        dfs(table, source, target, currentRoute, allRoutes);

        List<String> routesAsString = new ArrayList<>();
        for (List<String> route : allRoutes) {
            routesAsString.add(route.toString());
        }

        return routesAsString;
    }

    private static void dfs(TreeMap<String, TreeMap<String, Integer>> table, String current, String end,
                            List<String> currentRoute, List<List<String>> allRoutes) {
        currentRoute.add(current);

        if (current.equals(end)) {
            allRoutes.add(new ArrayList<>(currentRoute));
        } else {
            TreeMap<String, Integer> innerMap = table.get(current);
            if (innerMap != null) {
                for (Map.Entry<String, Integer> entry : innerMap.entrySet()) {
                    String next = entry.getKey();
                    if (!currentRoute.contains(next)) {
                        dfs(table, next, end, currentRoute, allRoutes);
                    }
                }
            }
        }

        currentRoute.remove(currentRoute.size() - 1);
    }
}
