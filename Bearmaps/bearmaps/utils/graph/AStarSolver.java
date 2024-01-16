package bearmaps.utils.graph;
import edu.princeton.cs.algs4.Stopwatch;
import java.util.List;
import bearmaps.utils.pq.DoubleMapPQ;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex>  {
    private SolverOutcome outcome;
    private double solutionWeight;
    private LinkedList<Vertex> solution;
    private double timeSpent;

    /* Constructor which finds the solution, computing everything necessary for all other
    methods to return their results in constant time. Note that timeout passed in is in seconds. */
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch sw = new Stopwatch();

        DoubleMapPQ<Vertex> fringe = new DoubleMapPQ<>();
        HashMap<Vertex, Double> distTo = new HashMap<>();
        HashMap<Vertex, WeightedEdge<Vertex>> edgeTo = new HashMap<>();
        HashSet<Vertex> visited = new HashSet<>();

        distTo.put(start, 0.0);
        fringe.insert(start, distTo.get(start) + input.estimatedDistanceToGoal(start, end));


        while (!fringe.peek().equals(end)) {
            if (sw.elapsedTime() >= timeout) {
                outcome = SolverOutcome.TIMEOUT;
                break;
            }
            if (fringe.size() == 0) {
                outcome = SolverOutcome.UNSOLVABLE;
                break;
            }
            Vertex v = fringe.poll();
            visited.add(v);
            if (v.equals(end)) {
                break;
            }

            for (WeightedEdge<Vertex> e : input.neighbors(v)) {
                Vertex q = e.to();
                if (visited.contains(q)) {
                    continue;
                }
                Vertex p = e.from();
                double w = e.weight();
                if (!fringe.contains(q)) {
                    distTo.put(q, distTo.get(p) + w);
                    edgeTo.put(q, e);
                    fringe.insert(q, distTo.get(q) + input.estimatedDistanceToGoal(q, end));
                    solutionWeight += w;
                } else if (distTo.get(p) + w < distTo.get(q)) {
                    distTo.put(q, distTo.get(p) + w);
                    fringe.changePriority(q, distTo.get(q) + input.estimatedDistanceToGoal(q, end));
                    solutionWeight = solutionWeight - edgeTo.get(q).weight() + w;
                    edgeTo.put(q, e);
                }
            }
        }
        outcome = SolverOutcome.SOLVED;
        solution = new LinkedList<>();
        solution.addFirst(end);
        while (!end.equals(start)) {
            Vertex prev = edgeTo.get(end).from();
            solution.addFirst(prev);
            solutionWeight += edgeTo.get(end).weight();
            end = prev;
        }
        timeSpent = sw.elapsedTime();
    }



    /* Returns one of SolverOutcome.SOLVED, SolverOutcome.TIMEOUT, or SolverOutcome.UNSOLVABLE.
    Should be SOLVED if the AStarSolver was able to complete all work in the time given.
    UNSOLVABLE if the priority queue became empty before finding the solution.
    TIMEOUT if the solver ran out of time. You should check to see if you have run out of time
    every time you dequeue. */
    public SolverOutcome outcome() {
        return outcome;
    }

    /* A list of vertices corresponding to a solution.
    Should be empty if result was TIMEOUT or UNSOLVABLE. */
    public List<Vertex> solution() {
        if (outcome().equals(SolverOutcome.UNSOLVABLE) || outcome().equals(SolverOutcome.TIMEOUT)) {
            return new LinkedList<>();
        }
        return solution;
    }

    /* The total weight of the given solution, taking into account edge weights.
    Should be 0 if result was TIMEOUT or UNSOLVABLE. */
    public double solutionWeight() {
        if (outcome().equals(SolverOutcome.UNSOLVABLE) || outcome().equals(SolverOutcome.TIMEOUT)) {
            return 0;
        }
        return solutionWeight;
    }

    /* The total number of priority queue poll() operations.
    Should be the number of states explored so far if result was TIMEOUT or UNSOLVABLE. */
    public int numStatesExplored() {
        return solution.size();
    }

    /* The total time spent in seconds by the constructor. */
    public double explorationTime() {
        return timeSpent;
    }
}
