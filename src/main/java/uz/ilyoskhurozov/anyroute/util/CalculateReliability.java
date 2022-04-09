package uz.ilyoskhurozov.anyroute.util;

import java.util.*;

public class CalculateReliability {

    public static double inModeVirtualChannel(Map<String, Double> routersRel, Map<String, Map<String, Double>> conRelTable, List<String> route){
        double k = 1;

        Iterator<String> routeIter = route.iterator();

        String prev, next = routeIter.next();
        k *= routersRel.get(next);

        while (routeIter.hasNext()){
            prev = next;
            next = routeIter.next();

            k *= conRelTable.get(prev).get(next);
            k *= routersRel.get(next);
        }

        return k;
    }

    public static double inModeDatagram(Map<String, Double> routersRel, Map<String, Map<String, Double>> conRelTable, String source, String target){
        List<String> routes = new ArrayList<>();
        findAllPaths(conRelTable, source+";-;", source, source, target, routes);

        double mid = 1;
        for (String route : routes) {
            String[] arr = route.split(";");

            double sub = 1;

            for(int i = 1; i < arr.length-1; i++){
                if (arr[i].equals("-")) {
                    sub *= conRelTable.get(arr[i-1]).get(arr[i+1]);
                } else {
                    sub *= routersRel.get(arr[i]);
                }
            }
            mid *= 1-sub;
        }

        return routersRel.get(source) * (1 - mid) * routersRel.get(target);
    }

    private static void findAllPaths(Map<String, Map<String, Double>> conRelTable, String route, String source, String cur, String target, List<String> routes){
        conRelTable.get(cur).forEach((r, rel) -> {
            if (rel > 0 && !route.contains(r + ";") && !r.equals(source)) {
                if (r.equals(target)) {
                    routes.add(route+target);
                } else {
                    findAllPaths(conRelTable, route+r+";-;", source, r, target, routes);
                }
            }
        });
    }
}
