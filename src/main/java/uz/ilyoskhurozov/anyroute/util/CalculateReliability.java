package uz.ilyoskhurozov.anyroute.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CalculateReliability {

    public static float inModeVirtualChannel(List<String> route, Map<String, Float> routersRel, Map<String, Map<String, Float>> conRelTable){
        float k = 1;

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

}
