package uz.khurozov.fuzzyroute.util.algo;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Floyd extends RouteAlgorithm {
    @Override
    public List<String> findRoute(TreeMap<String, TreeMap<String, Integer>> table, String source, String target) {
        Set<String> routers = table.keySet();
        //initialize prevTable
        TreeMap<String, TreeMap<String, String>> prevTable = new TreeMap<>();

        routers.forEach(r1 -> {
            TreeMap<String, String> row = new TreeMap<>();
            prevTable.put(r1, row);
            routers.forEach(r2 -> {
                if (!r1.equals(r2)) {
                    row.put(r2, r2);
                }
            });
        });

        //find routes
        System.out.println("Routes" + routers);
        routers.forEach(
                i -> routers.forEach(
                        col -> {
                            Integer dx = table.get(i).get(col);
                            if (dx == null) {
                                return;
                            }

                            routers.stream().takeWhile(row -> !row.equals(col)).forEach(
                                    row -> {
                                        Integer dy = table.get(row).get(i);

                                        if (dy != null) {
                                            Integer d = dy + dx;
                                            Integer cell = table.get(row).get(col);
                                            if (cell == null || cell > d) {
                                                table.get(row).put(col, d);
                                                table.get(col).put(row, d);

                                                String pCol = prevTable.get(i).get(col);
                                                if (pCol.equals(col)) pCol = i;

                                                String pRow = prevTable.get(row).get(i);

                                                prevTable.get(row).put(col, pCol);
                                                prevTable.get(col).put(row, pRow);
                                            }
                                        }
                                    }
                            );
                        }
                )
        );

        //assemble route
        LinkedList<String> route = new LinkedList<>();
        route.add(target);
        String prev = prevTable.get(source).get(target), prePrev;

        while (!(prePrev = prevTable.get(source).get(prev)).equals(prev)) {
            route.addFirst(prev);
            prev = prePrev;
        }
        if (table.get(source).get(prev) == null) return null;

        if (!route.contains(prev)) {
            route.addFirst(prev);
        }
        route.addFirst(source);

        return route;
    }
}
