package sample;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public int x, y, id;
    public List<Integer> connections;

    public Node(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        connections = new ArrayList<>();
    }
}
