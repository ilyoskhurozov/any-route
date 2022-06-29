package uz.ilyoskhurozov.anyroute.util.algo;

public class RouteUtil {

    public static RouteAlgorithm getRouteAlgorithm(String algoName){
        return switch (algoName) {
            case "Dijkstra" -> new Dijkstra();
            case "Floyd" -> new Floyd();
            case "Bellman-Ford" -> new BellmanFord();
            default -> throw new RuntimeException("Not implemented Algorithm");
        };
    }

}
