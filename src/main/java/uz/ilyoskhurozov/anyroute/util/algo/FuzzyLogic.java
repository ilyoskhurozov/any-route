package uz.ilyoskhurozov.anyroute.util.algo;

import uz.ilyoskhurozov.anyroute.component.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FuzzyLogic extends RouteAlgorithm {
    @Override
    public List<String> findRoute(TreeMap<String, TreeMap<String, Integer>> table, String source, String target, Double cableAvailability) {
        HashMap<String, Node> nodes = new HashMap<>();

        final Node[] node = {new Node(source, null, 0), null};
        nodes.put(source, node[0]);

        //relaxing all edges
        for (int i = 0; i < table.size() - 1; i++) {
            AtomicBoolean hasChange = new AtomicBoolean(false);
            table.forEach((r0, row) -> row.forEach((r1, w) -> {
                if (w != null && (node[0] = nodes.get(r0)) != null) {
                    if ((node[1] = nodes.get(r1)) == null) {
                        nodes.put(r1, new Node(r1, node[0].getName(), node[0].getDistance() + w));
                        hasChange.set(true);
                    } else if (node[0].getDistance() + w < node[1].getDistance()) {
                        nodes.put(r1, new Node(r1, node[0].getName(), node[0].getDistance() + w));
                        hasChange.set(true);
                    }
                }
            }));

            if (!hasChange.get()) break;
        }

        //checking if negative cycle exists
        table.forEach((r0, row) -> row.forEach((r1, w) -> {
            if (w != null && (node[0] = nodes.get(r0)) != null && (node[1] = nodes.get(r1)) != null) {
                if (node[0].getDistance() + w < node[1].getDistance()) {
                    throw new RuntimeException("Negative cycle exists in the give graph");
                }
            }
        }));

        LinkedList<String> route = new LinkedList<>();
        node[0] = nodes.get(target);
        node[1] = null;

        while (node[0] != null) {
            route.addFirst(node[0].getName());
            node[1] = node[0];
            node[0] = nodes.get(node[0].getPrevious());
        }

        if (node[1] != null && node[1].getName().equals(source)) {
            return route;
        } else {
            return null;
        }
    }
}
