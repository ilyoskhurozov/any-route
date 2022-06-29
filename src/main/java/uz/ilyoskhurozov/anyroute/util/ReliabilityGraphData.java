package uz.ilyoskhurozov.anyroute.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.pow;

public class ReliabilityGraphData {

    //from 0.99 to 0.9999 with 0.00099 step
    private static final double[] routerReliabilities = new double[] {
            0.99, 0.99099, 0.99198, 0.99297, 0.99396, 0.99495,
            0.99594, 0.99693, 0.99792, 0.99891, 0.9999,
    };

    public static Map<String, double[]> comparingCableCount(
            double connectionReliability,
            int cableCountFrom,
            int cableCountTo,
            int routersCountInRoute
    ) {
        Map<String, double[]> chartData = new LinkedHashMap<>();

        for (int i = cableCountFrom; i <= cableCountTo; i++) {
            double[] row = new double[routerReliabilities.length];
            for (int j = 0; j < routerReliabilities.length; j++) {
                row[j] = round(calculateFullReliability(
                        routerReliabilities[j], connectionReliability,
                        i, routersCountInRoute
                ));
            }

            chartData.put(
                    i+" cable" + (i == 1 ? "" : "s"),
                    row
            );
        }

        return chartData;
    }

    public static Map<String, double[]> comparingTopologies(
            double connectionReliability,
            List<TopologyData> topologyData
    ) {
        Map<String, double[]> chartData = new LinkedHashMap<>();

        topologyData.forEach(data -> {
            List<String> routes = new ArrayList<>();
            findAllRoutes(
                    data.isConnectedTable(), data.name()+"-",
                    data.source(), data.target(), routes
            );

            List<Integer> routerCountList = routes.parallelStream()
                    .map(route -> route.split("-").length).toList();

            double[] row = new double[routerReliabilities.length];

            for (int i = 0; i < routerReliabilities.length; i++) {
                double mid = 1;

                for (Integer routerCount : routerCountList) {
                    mid *= calculateMidReliability(
                            routerReliabilities[i],
                            connectionReliability,
                            routerCount
                    );
                }
                row[i] = round(
                        routerReliabilities[i]
                            * (1-mid)
                            * routerReliabilities[i]
                );
            }

            chartData.put(data.name(), row);
        });

        return chartData;
    }

    private static double calculateFullReliability(
            double routerReliability,
            double connectionReliability,
            int cableCountInConnection,
            int routersCountInRoute
    ) {
        return 1-pow(
                1-pow(routerReliability, routersCountInRoute)
                    * pow(connectionReliability,routersCountInRoute-1),
                cableCountInConnection
        );
    }

    private static double calculateMidReliability(
            double routerReliability,
            double connectionReliability,
            int routersCountInRoute
    ) {
        return 1-pow(routerReliability, routersCountInRoute-2)
                * pow(connectionReliability,routersCountInRoute-1);
    }

    private static double round(double rel) {
        double p = 1000_000_000.0;          //precision
        return Math.round(rel * p) / p;
    }

    private static void findAllRoutes(
            Map<String, Map<String, Boolean>> isConnectedTable,
            String route,
            String cur,
            String target,
            List<String> routes
    ){
        isConnectedTable.get(cur).forEach((r, isConnected) -> {
            if (isConnected && !route.contains(r + "-")) {
                if (r.equals(target)) {
                    routes.add(route+target);
                } else {
                    findAllRoutes(
                            isConnectedTable, route+r+"-",
                            r, target, routes
                    );
                }
            }
        });
    }
}