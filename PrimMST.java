import java.util.*;

public class PrimMST {
	private Edge[] edgeTo;
	private double[] distTo;
	private boolean[] marked;
	private IndexMinPQ<Double> pq;

	public PrimMST(EdgeWeightedGraph G) {
		edgeTo = new Edge[G.getNumVertices()];
		distTo = new double[G.getNumVertices()];
		marked = new boolean[G.getNumVertices()];
		pq = new IndexMinPQ<Double>(G.getNumVertices());

		for (int i = 0; i < G.getNumVertices(); i++) {
			distTo[i] = Double.POSITIVE_INFINITY;
		}
		for (int i = 0; i < G.getNumVertices(); i++) {
			if (!marked[i]) {
				prim(G, i);
			}
		}
	}

	private void prim(EdgeWeightedGraph G, int s) {
		distTo[s] = 0.0;
		pq.insert(s, distTo[s]);
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			scan(G, v);
		}
	}

	private void scan(EdgeWeightedGraph G, int v) {
		marked[v] = true;
		for (Edge e : G.getAdj(v)) {
			int w = e.other(v);
			if (marked[w]) {
				continue;
			}
			if (e.getLatency() < distTo[w]) {
				distTo[w] = e.getLatency();
				edgeTo[w] = e;
				if (pq.contains(w)) {
					pq.decreaseKey(w, distTo[w]);
				}
				else {
					pq.insert(w, distTo[w]);
				}
			}
		}
	}

	public Iterable<Edge> edges() {
		LinkedList<Edge> mst = new LinkedList<Edge>();
        for (int v = 0; v < edgeTo.length; v++) {
            Edge e = edgeTo[v];
            if (e != null) {
                mst.add(e);
            }
        }
        return mst;
	}

	public double weight() {
		double weight = 0.0;
		for (Edge e : edges()) {
			weight += e.getLatency();
		}
		return weight;
	}
}