package uz.ilyoskhurozov.anyroute.util;

import uz.ilyoskhurozov.anyroute.component.Node;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FindRoute {

    public static List<String> withDijkstra(TreeMap<String, TreeMap<String, Integer>> table, String source, String target) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparing(Node::getDistance));
        HashMap<String, Node> nodeMap = new HashMap<>();

        Node cur = new Node(source, null, 0);

        do {
            Node curNode = cur;
            table.get(cur.getName()).forEach((name, dis) -> {
                if (dis != null && !nodeMap.containsKey(name)) {
                    Node node = queue.stream().filter(n -> n.getName().equals(name)).findFirst().orElse(null);

                    int disToCheck = curNode.getDistance() + dis;
                    if (node == null) {
                        node = new Node(name, curNode.getName(), disToCheck);
                        queue.add(node);
                    } else {
                        if (node.getDistance() > disToCheck) {
                            node.setDistance(disToCheck);
                            node.setPrevious(curNode.getName());
                        }
                    }
                }
            });
            nodeMap.put(cur.getName(), cur);
            cur = queue.poll();
        } while (cur != null && !cur.getName().equals(target));

        if (cur == null) {
            return null;
        } else {
            LinkedList<String> route = new LinkedList<>();
            route.addFirst(target);

            while (cur.getPrevious() != null) {
                route.addFirst(cur.getPrevious());
                cur = nodeMap.get(cur.getPrevious());
            }

            return route;
        }
    }

    public static List<String> withFloyd(TreeMap<String, TreeMap<String, Integer>> table, String source, String target) {
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
        routers.forEach(
                i -> routers.forEach(
                        col -> routers.stream().takeWhile(row -> !row.equals(col)).forEach(
                                row -> {
                                    Integer d1 = table.get(row).get(i);
                                    Integer d2 = table.get(i).get(col);
                                    if (d1 != null && d2 != null) {
                                        Integer d = d1 + d2;
                                        Integer cell = table.get(row).get(col);
                                        if (cell == null || cell > d) {
                                            table.get(row).put(col, d);
                                            table.get(col).put(row, d);

                                            prevTable.get(row).put(col, i);
                                            prevTable.get(col).put(row, i);
                                        }
                                    }
                                }
                        )
                )
        );

        //check shortcuts
        AtomicBoolean hasChange = new AtomicBoolean();
        do {
            hasChange.set(false);
            routers.forEach(
                    row -> routers.stream().takeWhile(col -> !col.equals(row)).forEach(
                            col -> {
                                String prev = prevTable.get(row).get(col);
                                String prePrev = prevTable.get(row).get(prev);
                                String reverse = prevTable.get(col).get(row);

                                if (!prev.equals(prePrev) && !prev.equals(reverse)) {
                                    hasChange.set(true);
                                    prevTable.get(row).put(col, prePrev);
                                    prevTable.get(col).put(row, prePrev);
                                }
                            }
                    )
            );
        } while (hasChange.get());

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

    public static List<String> withBellmanFord(TreeMap<String, TreeMap<String, Integer>> table, String source, String target) {
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
