import java.io.*;
import java.util.*;

public class EdgeWeightedGraph
{
	public int numVertices;			// Number of vertices
	private int numEdges;			// Number of edges
	private Bag<Edge>[] adj;		// Adjacency lists

	public EdgeWeightedGraph() {
		numVertices = 0;
		numEdges = 0;
		adj = (Bag<Edge>[]) new Bag[numVertices];
		// Default constructor does nothing
	}

	public EdgeWeightedGraph(String file) {
		// Open file
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			// First line is number of vertices (V)
			numVertices = Integer.parseInt(br.readLine());

			// Create list of bags
			adj = (Bag<Edge>[]) new Bag[numVertices];
			for (int i = 0; i < numVertices; i++) {
				adj[i] = new Bag<Edge>();
			}

			String line;
			// Read in all lines
			while ((line = br.readLine()) != null) {
				// Split line into its parts
				String[] lineSplit = line.split(" ");
				int v = Integer.parseInt(lineSplit[0]);					// vertex 1
				int w = Integer.parseInt(lineSplit[1]);					// vertex 2
				String type = lineSplit[2];								// type
				double bandwidth = Double.parseDouble(lineSplit[3]);	// bandwidth
				double length = Double.parseDouble(lineSplit[4]);		// length

				// Create edge & add edge to graph
				addEdge(new Edge(v, w, bandwidth, length, type));
				addEdge(new Edge(w, v, bandwidth, length, type));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addEdge(Edge e) {
		int v = e.either();
		int w = e.other(v);

		// Add edge to list
		adj[v].add(e);

		// Increment number of edges
		numEdges++;
	}

	public int getNumVertices() {
		return numVertices;
	}

	public Bag<Edge> getAdj(int v) {
		return adj[v];
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(numVertices + " " + numEdges + "\n");
		for (int i = 0; i < numVertices; i++) {
			s.append(i + ": ");
			for (Edge e : adj[i]) {
				s.append(e + "  ");
			}
			s.append("\n");
		}

		return s.toString();
	}
}