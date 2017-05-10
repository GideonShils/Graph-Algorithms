import java.util.*;

public class FordFulkerson {

    private final int V;          // number of vertices
    private boolean[] marked;     // marked[v] = true iff s->v path in residual graph
    private Edge[] edgeTo;    // edgeTo[v] = last edge on shortest residual s->v path
    private double value;         // current value of max flow
  
  	// s = source, t = sink
    public FordFulkerson(EdgeWeightedGraph G, int s, int t) {
        V = G.getNumVertices();

        // while there exists an augmenting path, use it
        value = excess(G, t);
        while (hasAugmentingPath(G, s, t)) {

            // compute bottleneck capacity
            double bottle = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }

            // augment flow
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle); 
            }

            value += bottle;
        }
    }

    public double value()  {
        return value;
    }

    public boolean inCut(int v)  {
        return marked[v];
    }

    // is there an augmenting path? 
    // if so, upon termination edgeTo[] will contain a parent-link representation of such a path
    // this implementation finds a shortest augmenting path (fewest number of edges),
    // which performs well both in theory and in practice
    private boolean hasAugmentingPath(EdgeWeightedGraph G, int s, int t) {
        edgeTo = new Edge[G.getNumVertices()];
        marked = new boolean[G.getNumVertices()];

        // breadth-first search
        LinkedList<Bag<Edge>> queue = new LinkedList<Bag<Edge>>();

        queue.add(G.getAdj(s));
        marked[s] = true;
        while (!queue.isEmpty() && !marked[t]) {
            Bag<Edge> n = queue.poll();

            Iterator<Edge> i = n.iterator();
            while (i.hasNext()) {
            	Edge e = i.next();
            	int v = e.either();
            	int w = e.other(v);

            	if (e.residualCapacityTo(w) > 0) {
            		if (!marked[w]) {
            			edgeTo[w] = e;
            			marked[w] = true;
            			queue.add(G.getAdj(w));
            		}
            	}
            }
        }

        // is there an augmenting path?
        return marked[t];
    }

    // return excess flow at vertex v
    private double excess(EdgeWeightedGraph G, int v) {
        double excess = 0.0;
        Bag<Edge> n = G.getAdj(v);

        Iterator<Edge> i = n.iterator();
        while (i.hasNext()) {
        	Edge e = i.next();
        	if (v == e.either()) {
        		excess -= e.flow();
        	} else {
        		excess += e.flow();
        	}
        }

        return excess;
    }
}