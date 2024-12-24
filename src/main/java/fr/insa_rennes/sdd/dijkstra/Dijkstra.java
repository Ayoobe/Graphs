package fr.insa_rennes.sdd.dijkstra;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import fr.insa_rennes.sdd.graph.Graph;
import fr.insa_rennes.sdd.graph.VertexAndWeight;
import fr.insa_rennes.sdd.priority_queue.PriorityQueue;

public class Dijkstra<T> {
	private final PriorityQueue<DijkstraNode<T>> pq;	
	private final Map<T, Double> cost = new HashMap<>();
	private final Map<T, T> prev = new HashMap<>();

	public Dijkstra(Graph<T> graph, T source) {
		this(graph, source, FactoryPQ.newInstance("HeapPQ"));
	}	

	public Dijkstra(Graph<T> graph, T source, PriorityQueue<DijkstraNode<T>> pq) {
		this.pq = pq; 
		solve(graph, source);
	}

	private void solve(Graph<T> graph, T source) {
		cost.put(source, 0.0);
		pq.add(new DijkstraNode<>(0.0, source));
		while (!pq.isEmpty()) {
			T u = pq.poll().vertex;
			for (VertexAndWeight<T> vw : graph.neighbors(u)) {
				T v = vw.vertex;
				double w = vw.weight;
				double newCost = getCost(u) + w;
				if (newCost < getCost(v)) {
					cost.put(v, newCost);
					prev.put(v, u);
					pq.add(new DijkstraNode<>(newCost, v));
				}


			}
		}
	}




	public Deque<T> getPathTo(T v) {
		Deque<T> path = new ArrayDeque<>();
		while(v!=null) {
			path.addFirst(v);
			v = prev.get(v);
		}
		return path;
	}

	public double getCost(T v) {
		return cost.getOrDefault(v, Double.POSITIVE_INFINITY);
	}
	
	public boolean hasPathTo(T v) {
		return getCost(v) != Double.POSITIVE_INFINITY;
	}


}
