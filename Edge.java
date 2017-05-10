public class Edge {
	private final int v;			// One vertex
	private final int w;			// The other vertex
	private final double bandwidth;	// Cable bandwidth (capacity)
	private final double length;	// Cable length
	private final String type;		// Cable type (copper or optical)
	private double latency;			// The latency
	private double flow;			// Flow
	private double capacity;		// Capacity is same as bandwidth, just simplifies things

	public Edge(int v, int w, double bandwidth, double length, String type) {
		double copperSpeed = 230000000;
		double opticalSpeed = 200000000;
		this.v = v;
		this.w = w;
		this.bandwidth = bandwidth;
		this.length = length;
		this.type = type;
		this.flow = 0.0;
		this.capacity = bandwidth;

		// Calculate latency as length / speed where speed differs based on type
		if (type.equals("copper")) {
			latency = length / copperSpeed;
		} else {
			latency = length / opticalSpeed;
		}
	}

	public double bandwidth() {
		return bandwidth;
	}

	public double length() {
		return length;
	}

	public String type() {
		return type;
	}

	public double capacity() {
		return capacity;
	}

	public double flow() {
		return flow;
	}

	public int either() {
		return v;
	}

	public int other(int vertex) {
		if (vertex == v) {
			return w;
		}
		else {
			return v;
		}
	}

	public double getLatency() {
		return latency;
	}

	public double residualCapacityTo(int vertex) {
		if (vertex == v) {
			return flow;			// backward edge
		} else {
			return capacity - flow;	// forward edge
		}
	}

	public void addResidualFlowTo(int vertex, double delta) {
		if (vertex == v) {
			flow -= delta;		// bakward edge
		} else {
			flow += delta;		// forward edge
		}

		if (Math.abs(flow) <= 1E-10)
            flow = 0;
        if (Math.abs(flow - capacity) <= 1E-10)
            flow = capacity;
	}

	public void resetFlow() {
		flow = 0.0;
	}

	public int compareLengthTo(Edge that) {
		return Double.compare(this.length, that.length);
	}

	public int compareBandwidthTo(Edge that) {
		return Double.compare(this.bandwidth, that.bandwidth);
	}

	public int compareLatency(Edge that) {
		return Double.compare(this.latency, that.latency);
	}

	public String toString() {
		return String.format("%d-%d", v, w);
	}
}