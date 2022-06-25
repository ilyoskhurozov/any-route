package uz.ilyoskhurozov.anyroute.util.algo;

public class RouteUtil {

    public static RouteAlgorithm getRouteAlgorithm(String algoName){
        switch (algoName){
            case "Dijkstra":        return new Dijkstra();
            case "Floyd":           return new Floyd();
            case "Bellman-Ford":    return new BellmanFord();
            default:                throw new RuntimeException("Not implemented Algorithm");
        }
    }

}
