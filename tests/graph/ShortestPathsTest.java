package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ShortestPathsTest {
    /** The graph example from Prof. Myers's notes. There are 7 vertices labeled a-g, as
     *  described by vertices1. 
     *  Edges are specified by edges1 as triples of the form {src, dest, weight}
     *  where src and dest are the indices of the source and destination
     *  vertices in vertices1. For example, there is an edge from a to d with
     *  weight 15.
     */
    static final String[] vertices1 = { "a", "b", "c", "d", "e", "f", "g" };

    static final int[][] edges1 = {
        {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
        {1, 4, 23},
        {2, 4, 17}, {2, 3, 5}, {2, 5, 30},
        {3, 5, 20}, {3, 6, 37},
        {4, 5, 3}, {4, 6, 20},
        {5, 6, 16}
    };

    static final int[][] edges2 = {
            {0, 1, 10}, {0, 2, 15}, {0, 3, 20},
            {1, 4, 12}, {1, 5, 22},
            {2, 4, 15}, {2, 6, 30},
            {3, 6, 20},
            {4, 6, 10}, {4, 5, 7},
            {5, 6, 6}
    };

    static final String[] vertices3 = {"a","b","c","d","e","f","g"};
    static final int[][] edges3 = {
            {0,1,1},{0,2,1},
            {1,0,1},{1,3,1},
            {2,0,1},{2,3,1},
            {3,1,1},{3,2,1}, {3,4,1},
            {3,5,1},
            {4,3,1},{4,6,1},
            {5,3,1},{5,6,1},
            {6,4,1}
            ,{6,5,1}

    };


    static final int[][] edgesUltimate = {
            {0, 1, 5},    // Simple edge with small weight
            {0, 2, 20},   // Simple edge with larger weight
            {1, 2, 10},   // Parallel edge (alternative path between a and c)
            {2, 3, 15},   // Simple edge
            {3, 0, 25},   // Edge forming a cycle with previous edges
            {3, 4, 7},    // Simple edge with small weight
            {4, 5, 12},   // Simple edge with moderate weight
            {4, 6, 30},   // Simple edge with larger weight
            {5, 6, 8},    // Parallel edge (alternative path between e and g)
            {6, 7, 18},   // Simple edge
            {7, 8, 4},    // Simple edge with very small weight
            {8, 9, 22},  // Simple edge with larger weight
            {9, 0, 28},  // Edge forming a cycle with previous edges
            {2, 5, 11},   // Simple edge forming a shortcut between existing paths
            {7, 4, 6},    // Simple edge forming a shortcut between existing paths
            {8, 3, 17},   // Simple edge forming a shortcut between existing paths
            {9, 6, 19},  // Simple edge forming a shortcut between existing paths
            {1, 1, 3},    // Loop on vertex b
            {3, 3, 10},   // Loop on vertex d
            {5, 5, 15}    // Loop on vertex f
    };

    static final String[] vertices2 = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l" };


    static class TestGraph implements WeightedDigraph<String, int[]> {
        int[][] edges;
        String[] vertices;
        Map<String, Set<int[]>> outgoing;

        TestGraph(String[] vertices, int[][] edges) {
            this.vertices = vertices;
            this.edges = edges;
            this.outgoing = new HashMap<>();
            for (String v : vertices) {
                outgoing.put(v, new HashSet<>());
            }
            for (int[] edge : edges) {
                outgoing.get(vertices[edge[0]]).add(edge);
            }
        }
        public Iterable<int[]> outgoingEdges(String vertex) { return outgoing.get(vertex); }
        public String source(int[] edge) { return vertices[edge[0]]; }
        public String dest(int[] edge) { return vertices[edge[1]]; }
        public double weight(int[] edge) { return edge[2]; }
    }
    static TestGraph testGraph1() {
        return new TestGraph(vertices1, edges1);
    }

    static TestGraph testGraph2() {return new TestGraph(vertices1, edges2); }

    static TestGraph testGraphUltimate() {return new TestGraph(vertices2, edgesUltimate); }

    static TestGraph testGraph3() {return new TestGraph(vertices3, edges3); }

    @Test
    void lectureNotesTest() {
        TestGraph graph = testGraph1();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(50, ssp.getDistance("g"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("g")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" g");
        assertEquals("best path: a c e f g", sb.toString());
    }

    // TODO: Add 2 more tests
    @Test
    void Test2() {
        TestGraph graph = testGraph2();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(32, ssp.getDistance("g"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("g")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" g");
        assertEquals("best path: a b e g", sb.toString());
    }

    @Test
    void testUltimate() {
        TestGraph graph = new TestGraph(vertices2, edgesUltimate);
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("b");
        assertEquals(73, ssp.getDistance("j"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("j")) {
            sb.append(" " + vertices2[e[0]]);
        }
        sb.append(" j");
        assertEquals("best path: b c f g h i j", sb.toString());
    }
}
