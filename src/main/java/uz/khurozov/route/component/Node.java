package uz.khurozov.route.component;

public class Node {
    private final String name;
    private String previous;
    private int distance;

    public Node(String name, String previous, int distance) {
        this.name = name;
        this.previous = previous;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
