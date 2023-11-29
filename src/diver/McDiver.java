package diver;

import game.*;
import graph.ShortestPaths;
import graph.WeightedDigraph;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {

    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void seek(SeekState state) {
        // TODO : Look for the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method (it may be recursive) elsewhere, with a
        // good specification, and call it from this one.
        //
        // Working this way provides you with flexibility. For example, write
        // one basic method, which always works. Then, make a method that is a
        // copy of the first one and try to optimize in that second one.
        // If you don't succeed, you can always use the first one.
        //
        // Use this same process on the second method, scram.
        protoSeek(state);
    }

    /**
     * helper method for seek method. THis helper computes all the necessary resources
     * required for seek to run.
     * @param state
     */
    public void protoSeek(SeekState state) {
        Set<Long> visited = new HashSet<>();
        dfsTraversalProtoOptimized(state, visited);

    }

    /**
    proto optimized version accounts for the fact that ALL neighbors must be considered, but
    neighbors closer to the ring have higher priority.
     */
    private void dfsTraversalProtoOptimized(SeekState state, Set<Long> visited) {
        visited.add(state.currentLocation());

        if (state.distanceToRing() == 0) {
            return;
        }

        // Sort neighbors in descending order based on their distance to the ring
        List<NodeStatus> sortedNeighbors = new ArrayList<>(state.neighbors());
        sortedNeighbors.sort(Comparator.comparingInt(NodeStatus::getDistanceToRing));

        for (NodeStatus neighbor : sortedNeighbors) {
            if (!visited.contains(neighbor.getId())) {
                Long prevLoc = state.currentLocation();
                state.moveTo(neighbor.getId());
                dfsTraversalProtoOptimized(state, visited);

                if (state.distanceToRing() != 0) {
                    state.moveTo(prevLoc);
                } else {
                    break;
                }
            }
        }
    }


    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void scram(ScramState state) {
        // TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        // with a good specification, and call it from this one.
        //protoScram(state);
        scramHelper(state);
    }

    /**
     * helper for scram method. this helper computes all the necessary computations needed for
     * scram to work.
     * @param state
     */
    private void scramHelper(ScramState state) {
        Maze map = new Maze((Set<Node>) state.allNodes());
        scramAlgorithm(state, map);
    }


    /*
    Move McDiver along a path, given the gameState and the path. Returns nothing.
     */
    private void moveAlongPath(ScramState state, List<Edge> path) {
        //TODO: CREATE FUNCTION THAT MOVES McDIVER ALONG PATH GIVEN (NODES)
        for (Edge n: path) {
            state.moveTo(n.destination());
        }
    }
    /**
    calculates the coin per step of each node and maps that metric to the corresponding node
    in a hashmap. returns a map.
     */
    public Map<Node,Double> getCoinPerStepMap(ScramState state, WeightedDigraph<Node,Edge> map) {
        Map<Node,Double> coinMap = new HashMap<>();
        ShortestPaths<Node,Edge> pathCompute = new ShortestPaths<>(map);
        pathCompute.singleSourceDistances(state.currentNode());
        for (Node n : state.allNodes()) {
            if (n.getTile().coins() > 0) {
                double dist = pathCompute.getDistance(n);
                coinMap.put(n, n.getTile().coins() / dist);
            }
        }
        return coinMap;
    }

    /**
    regular pathfinding algorithm to exit. returns the path need to take in a
    list of edges.
     */
    public List<Edge> miniScram(ScramState state, WeightedDigraph<Node,Edge> map) {
        ShortestPaths<Node, Edge> algoCompute = new ShortestPaths<>(map);
        algoCompute.singleSourceDistances(state.currentNode());
        return algoCompute.bestPath(state.exit());
    }

    /**
    this function computes the "greedy" path and moves McDiver along it. This path prioritizes coins
    that have a higher coin per step value in order to maximize coin collection whiles also taking
    the shortest amount of steps possible (theoretically). Returns nothing.
     */
    private void scramAlgorithm(ScramState state, WeightedDigraph<Node,Edge> map) {
        ShortestPaths<Node, Edge> paths = new ShortestPaths(map);
        paths.singleSourceDistances(state.currentNode());

        while (true)
        {
            Map<Node, Double> coinPerStep = getCoinPerStepMap(state, map);
            paths.singleSourceDistances(state.currentNode());
            Map.Entry<Node,Double> maxVal = null;
            for (Map.Entry<Node, Double> entry : coinPerStep.entrySet()) {
                    if (maxVal == null || entry.getValue() > maxVal.getValue()) {
                        maxVal = entry;
                    }
            }
            if (maxVal == null) {
                for (Edge edge: miniScram(state,map)) {
                    state.moveTo(edge.destination());
                }
                return;
            }

            for (Edge e : paths.bestPath(maxVal.getKey())) {
                //check if dist from currentNode to e.destination()
                //PLUS dist from e.destination() to state.exit() < steps left.
                //if yes, then move to next vertex in the path to maxVal
                double toDestination = paths.getDistance(e.destination());
                paths.singleSourceDistances(e.destination());
                double toExit = paths.getDistance(state.exit());
                if ((toDestination + toExit) < state.stepsToGo()) {
                    state.moveTo(e.destination());
                } else {
                    //leave
                    for (Edge edge: miniScram(state,map)) {
                        state.moveTo(edge.destination());
                    }
                    return;
                }
            }
        }


    }

}
