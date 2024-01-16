package bearmaps.utils.graph;

import java.util.List;

/**
 * Represents a graph of vertices.
 * Created by hug.
 */
public interface AStarGraph<Vertex> {
    /* Provides a list of all edges that go out from V to its neighbors. */
    List<WeightedEdge<Vertex>> neighbors(Vertex v);

    /* Provides an estimate of the "distance" to reach the goal from
       the start position. For results to be correct, this estimate must
       be less than or equal to the correct "distance". */
    double estimatedDistanceToGoal(Vertex s, Vertex goal);
}
