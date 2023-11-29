package game;

import game.Tile.TileType;
import graph.WeightedDigraph;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import graph.ShortestPaths;

public class CoinWeightedMaze implements WeightedDigraph<Node,Edge> {
    private Maze maze;

    public CoinWeightedMaze(Maze maze) {
        this.maze = maze;
    }

    public Iterable<Edge> outgoingEdges(Node vertex) {
        return maze.outgoingEdges(vertex);
    }

    public Node source(Edge edge) {
        return maze.source(edge);
    }

    public Node dest(Edge edge) {
        return maze.dest(edge);
    }

    public double weight(Edge edge) {
        return maze.weight(edge);
    }
}
