package uz.ilyoskhurozov.anyroute.util.algo;

import uz.ilyoskhurozov.anyroute.component.Node;

import java.util.*;

public class Dijkstra extends RouteAlgorithm {

    @Override
    public List<String> findRoute(TreeMap<String, TreeMap<String, Integer>> table, String source, String target, Double cableAvailability) {
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
}
