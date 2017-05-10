import java.util.*;

public class Dijkstra {
	private double[] distTo;
	private Edge[] edgeTo;
	private IndexMinPQ<Double> pq;

	public Dijkstra(EdgeWeightedGraph G, int s) {
		distTo = new double[G.getNumVertices()];
		edgeTo = new Edge[G.getNumVertices()];

		for (int i = 0; i < G.getNumVertices(); i++) {
			distTo[i] = Double.POSITIVE_INFINITY;
		}
		distTo[s] = 0.0;

		pq = new IndexMinPQ<Double>(G.getNumVertices());
		pq.insert(s, distTo[s]);
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			for (Edge e : G.getAdj(v)) {
				relax(e);
			}
		}
	}

	public void relax(Edge e) {
		int v = e.either();
		int w = e.other(v);

		if (distTo[w] > distTo[v] + e.getLatency()) {
			distTo[w] = distTo[v] + e.getLatency();
			edgeTo[w] = e;
			if (pq.contains(w)) {
				pq.decreaseKey(w, distTo[w]);
			} else {
				pq.insert(w, distTo[w]);
			}
		}
	}

	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	public double distTo(int v) {
		return distTo[v];
	}

	public Stack<Edge> pathTo(int v) {
		if (!hasPathTo(v)) {
			return null;
		}
		Stack<Edge> path = new Stack<Edge>();
		for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.either()]) {
			path.push(e);
		}
		return path;
	}
}