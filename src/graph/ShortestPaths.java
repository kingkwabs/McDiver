package graph;

import datastructures.PQueue;
import datastructures.SlowPQueue;
import datastructures.FastPQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This object computes and remembers shortest paths through a weighted, directed graph with
 * nonnegative weights. Once shortest paths are computed from a specified source vertex, it allows
 * querying the distance to arbitrary vertices and the best paths to arbitrary destination
 * vertices.
 * <p>
 * Types Vertex and Edge are parameters, so their operations are supplied by a model object supplied
 * to the constructor.
 */
public class ShortestPaths<Vertex, Edge> {

    /**
     * The model for treating types Vertex and Edge as forming a weighted directed graph.
     */
    private final WeightedDigraph<Vertex, Edge> graph;

    /**
     * The distance to each vertex from the source.
     */
    private Map<Vertex, Double> distances;

    /**
     * The incoming edge for the best path to each vertex from the source vertex.
     */
    private Map<Vertex, Edge> bestEdges;

    /**
     * Creates: a single-source shortest-path finder for a weighted graph.
     *
     * @param graph The model that supplies all graph operations.
     */
    public ShortestPaths(WeightedDigraph<Vertex, Edge> graph) {
        this.graph = graph;
    }

    /**
     * Effect: Computes the best paths from a given source vertex, which can then be queried using
     * bestPath().
     */
    public void singleSourceDistances(Vertex source) {
        // Implementation constraint: use Dijkstra's single-source shortest paths algorithm.
        PQueue<Vertex> frontier = new FastPQueue<>();
        distances = new HashMap<>();
        bestEdges = new HashMap<>();
           // TODO: Complete computation of distances and best-path edges
        Set<Vertex> settledSet = new HashSet<>();
        frontier.add(source, 0);
        distances.put(source,0.0);
        while (!frontier.isEmpty()) {
            Vertex v = frontier.extractMin();
            double currentDist = distances.get(v);
            settledSet.add(v);
            for (Edge e: graph.outgoingEdges(v)) {
                Vertex neighbor = graph.dest(e);
                double newDist = currentDist + graph.weight(e);
                if (!settledSet.contains(neighbor)
                && (!distances.containsKey(neighbor))) {
                    distances.put(neighbor,newDist);
                    bestEdges.put(neighbor, e);
                    frontier.add(neighbor,newDist);

                } else if (newDist < distances.get(neighbor)) {
                    distances.replace(neighbor,newDist);
                    bestEdges.replace(neighbor, e);
                    frontier.changePriority(neighbor,newDist);
                }
            }

        }

    }

    /**
     * Returns: the distance from the source vertex to the given vertex. Requires: distances have
     * been computed from a source vertex, and vertex v is reachable from that vertex.
     */
    public double getDistance(Vertex v) {
        assert !distances.isEmpty() : "Must run singleSourceDistances() first";
        Double d = distances.get(v);
        assert d != null : "v not reachable from source";
        return d;
    }

    /**
     * Returns: the best path from the source vertex to a given target vertex. The path is
     * represented as a list of edges. Requires: singleSourceDistances() has already been used to
     * compute the best paths, and vertex target is reachable from that source.
     */
    public List<Edge> bestPath(Vertex target) {
        assert !bestEdges.isEmpty() : "Must run singleSourceDistances() first";
        LinkedList<Edge> path = new LinkedList<>();
        Vertex v = target;
        while (true) {
            Edge e = bestEdges.get(v);
            if (e == null) {
                break; // must be the source vertex (assuming target is reachable)
            }
            path.addFirst(e);
            v = graph.source(e);
        }
        return path;
    }
}
