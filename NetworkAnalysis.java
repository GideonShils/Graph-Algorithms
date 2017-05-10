import java.util.*;
import java.io.*;

public class NetworkAnalysis {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Correct input is as follows: java Network <fileName>");
			System.out.println("Now quiting...");
			System.exit(0);
		}

		String fileName = args[0];

		// Create graph frome file
		EdgeWeightedGraph graph = new EdgeWeightedGraph(fileName);

		// Get input
		Scanner inScan = new Scanner(System.in);

		// Loop until user quits
		while (true) {
			// Display user options
			System.out.println("\nMenu:");
			System.out.println("-----------------------------------------------");
			System.out.println("1. Find lowest latency path");
			System.out.println("2. Determine if graph is copper-only connected");
			System.out.println("3. Find maximum amount of data on path");
			System.out.println("4. Find lowest average latency spanning tree");
			System.out.println("5. Determine if possible to become disconnected");
			System.out.println("6. Quit");
			System.out.println("-----------------------------------------------");

			int input = 0;
			while (input < 1 || input > 6) {
				System.out.print("\nWhat would you like to do? (Enter 1-6) > ");
				input = inScan.nextInt();

				if (input < 1 || input > 6) {
					System.out.println("Incorrect input. Please try again.");
				}
			}

			// Find lowest latency
			if (input == 1) {
				int numVertices = graph.getNumVertices() - 1;
				int firstVertex = -1;
				int secondVertex = -1;

				// Get first vertex
				while (firstVertex < 0 || firstVertex > numVertices) {
					System.out.print("Enter first vertex (0-" + numVertices + ") > ");
					firstVertex = inScan.nextInt();
					if (firstVertex < 0 || firstVertex > numVertices) {
						System.out.println("Try again with a valid number");
					}
				}

				// Get second vertex
				while (secondVertex < 0 || secondVertex > numVertices) {
					System.out.print("Enter second vertex (0-" + numVertices + ") > ");
					secondVertex = inScan.nextInt();
					if (secondVertex < 0 || secondVertex > numVertices) {
						System.out.println("Try again with a valid number");
					}
				}

				lowestLatency(graph, firstVertex, secondVertex);
			}

			// Determine if connected
			if (input == 2) {
				copperConnected(graph);
			}

			// Find maximum amount of data
			if (input == 3) {
				int numVertices = graph.getNumVertices() - 1;
				int firstVertex = -1;
				int secondVertex = -1;

				// Get first vertex
				while (firstVertex < 0 || firstVertex > numVertices) {
					System.out.print("Enter first vertex (0-" + numVertices + ") > ");
					firstVertex = inScan.nextInt();
					if (firstVertex < 0 || firstVertex > numVertices) {
						System.out.println("Try again with a valid number");
					}
				}

				// Get second vertex
				while (secondVertex < 0 || secondVertex > numVertices) {
					System.out.print("Enter second vertex (0-" + numVertices + ") > ");
					secondVertex = inScan.nextInt();
					if (secondVertex < 0 || secondVertex > numVertices) {
						System.out.println("Try again with a valid number");
					}
				}

				maximumData(graph, firstVertex, secondVertex);
			}

			// Find lowest average latency spanning tree
			if (input == 4) {
				averageLatency(graph);
			}

			// Determine if possible to become disconnected
			if (input == 5) {
				disconnected(graph);
			}

			if (input == 6) {
				System.out.println("\n-----------------------------------------------");
				System.out.println("Goodbye!");
				System.out.println("-----------------------------------------------\n");
				System.exit(0);
			}
		}
	}

	// Uses dijkstra's algorithm to compute lowest latency
	public static void lowestLatency(EdgeWeightedGraph G, int firstVertex, int secondVertex) {
		Dijkstra paths = new Dijkstra(G, firstVertex);
		Double minBandwidth;
		System.out.println("\n-----------------------------------------------");
		System.out.println("Here is the path from " + firstVertex + " to " + secondVertex + ": \n");

		Stack<Edge> path = paths.pathTo(secondVertex);
		minBandwidth = path.peek().bandwidth();
		while (!path.empty()) {
			if (path.peek().bandwidth() < minBandwidth) {
				minBandwidth = path.peek().bandwidth();
			}

			System.out.print(path.pop() + "  ");
		}

		System.out.println("\n\nMinimum bandwidth: " + minBandwidth + " Mb/s");
		System.out.println("-----------------------------------------------\n");
	}

	// Uses breadth first search to check for connected graph using only copper edges
	public static void copperConnected(EdgeWeightedGraph G) {
		Bag<Edge> s;
		// Mark all vertices not visited
		boolean visited[] = new boolean[G.getNumVertices()];

		// Create queue
		LinkedList<Bag<Edge>> queue = new LinkedList<Bag<Edge>>();

		// Get first adjacency list and add it to queue
		visited[0] = true;
		queue.add(G.getAdj(0));

		// Continue until queue is empty
		while (queue.size() != 0) {
			// Dequeue adjacency list
			s = queue.poll();

			// Loop through every edge
			Iterator<Edge> i = s.iterator();
			while (i.hasNext()) {
				Edge n = i.next();
				int v = n.either();	// Set v to vertex
				int w = n.other(v);	// Set w to other vertex

				// If the other vertex hasnt been visited and the edge is made of copper
				// mark the other vertex visited and add it to the queue
				if (!visited[w] && n.type().equals("copper")) {
					visited[w] = true;
					queue.add(G.getAdj(w));
				}
			}
		}

		boolean connected = true;
		// Loop through vertices and return false if not all have been visited
		for (int j = 0; j < G.getNumVertices(); j++) {
			if (!visited[j]) {
				connected = false;
				break;
			} 
		}

		// Print result
		System.out.println("\n-----------------------------------------------");
		if (connected) {
			System.out.println("Graph is connected along copper wires!");
		} else {
			System.out.println("Graph is not connected along copper wires.");
		}
		System.out.println("-----------------------------------------------\n");
	}

	// Use FordFulkerson algorithm to compute maximum data flow
	public static void maximumData(EdgeWeightedGraph G, int firstVertex, int secondVertex) {
		double maxData;

		FordFulkerson maxFlow = new FordFulkerson(G, firstVertex, secondVertex);
		maxData = maxFlow.value();

		// Print result
		System.out.println("\n-----------------------------------------------");
		System.out.print("Max amount of data that can be transmitted: ");
		System.out.println(maxData + "Mb/s");
		System.out.println("-----------------------------------------------\n");

		// Reset flow values in case maxFlow is run again
		for (int i = 0; i < G.getNumVertices(); i++) {
			Bag<Edge> n = G.getAdj(i);
			Iterator<Edge> j = n.iterator();
			while (j.hasNext()) {
				Edge e = j.next();
				e.resetFlow();
			}
		}
	}

	// Use prims algorithm to compute minimum spanning tree
	public static void averageLatency(EdgeWeightedGraph G) {

		// Generate path
		PrimMST mst = new PrimMST(G);
		double totalLatency = 0;
		double numEdges = G.getNumVertices() - 1;
		double average;

		// Print path
		System.out.println("\n-----------------------------------------------");
		System.out.println("Here is the spanning tree:\n");
		for (Edge e : mst.edges()) {
			System.out.print(e + "  ");
			totalLatency += e.getLatency();
		}
		System.out.println();
		// Calculate & print average
		average = totalLatency / numEdges;
		System.out.println("\nAverage latency: " + average);
		System.out.println("-----------------------------------------------\n");
	}

	// Use breadth first search to check if all other vertices found when each pair of two is removed
	public static void disconnected(EdgeWeightedGraph G) {
		boolean connected = true;
		int vertex1 = -1;
		int vertex2 = -1;

		// Go through list of pairs of vertices.
		for (int i = 0; i < G.getNumVertices(); i++) {
			for (int j = i+1; j < G.getNumVertices(); j++) {
				vertex1 = i;
				vertex2 = j;

				// Run bfs with pair
				int count = bfs(G, vertex1, vertex2);

				// If bfs doesn't touch all other vertices, unconnected
				if (count != G.getNumVertices()-2) {
					connected = false;
					break;
				}
			}

			if (!connected) {
				break;
			}
		}

		System.out.println("\n-----------------------------------------------");
		if (connected) {
			System.out.println("Graph would remain connected if any two vertices were removed!");
		} else {
			System.out.println("Graph would become disconnected if vertices " + vertex1 + " and " + vertex2 + " failed.");
		}
		System.out.println("-----------------------------------------------\n");
	}

	// BFS helper method for disconnected()
	public static int bfs(EdgeWeightedGraph G, int vertex1, int vertex2) {
		Bag<Edge> s;
		// Mark all vertices not visited
		boolean visited[] = new boolean[G.getNumVertices()];

		// Create queue
		LinkedList<Bag<Edge>> queue = new LinkedList<Bag<Edge>>();

		// Find an adjacency list other than the two removed vertices
		if (vertex1 != 0 && vertex2 != 0) {
			visited[0] = true;
			queue.add(G.getAdj(0));
		} else if (vertex1 != 1 && vertex2 != 1) {
			visited[1] = true;
			queue.add(G.getAdj(1));
		} else {
			visited[2] = true;
			queue.add(G.getAdj(2));
		}

		// Continue until queue is empty
		while (queue.size() != 0) {
			// Dequeue adjacency list
			s = queue.poll();

			// Loop through every edge
			Iterator<Edge> i = s.iterator();
			while (i.hasNext()) {
				Edge n = i.next();
				int v = n.either();	// Set v to vertex
				int w = n.other(v);	// Set w to other vertex

				// If edge is connected to one of the removed vertices, skip it
				if (v == vertex1 || v == vertex2 || w == vertex1 || w == vertex2) {
					continue;
				}

				// If the other vertex hasnt been visited and the edge is made of copper
				// mark the other vertex visited and add it to the queue
				if (!visited[w]) {
					visited[w] = true;
					queue.add(G.getAdj(w));
				}
			}
		}

		int numHit = 0;
		// Loop through vertices increment numHit for every touched vertex
		for (int j = 0; j < G.getNumVertices(); j++) {
			if (visited[j]) {
				numHit += 1;
			}
		}

		return numHit;
	}
}