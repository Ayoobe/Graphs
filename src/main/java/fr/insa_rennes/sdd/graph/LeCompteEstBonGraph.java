package fr.insa_rennes.sdd.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import fr.insa_rennes.sdd.util.SmallSet;

public class LeCompteEstBonGraph implements Graph<LeCompteEstBonGraph.State> {
	private int[] plaques;
	private double[] operatorsCost = {1, 1, 2, 6};
	static final int ADD = 6;
	static final int MINUS = 7;
	static final int TIMES = 8;
	static final int DIV = 9;
	private Map<State, HashSet<VertexAndWeight<State>>> adjacency = new HashMap<>();
	private Map<Integer, ArrayList<State>> compteToState = new HashMap<>();
	private int numberOfEdges;

	public LeCompteEstBonGraph(int...plaques) {
		if (plaques.length != 6) {
			throw new IllegalArgumentException();
		}
		this.plaques = plaques;
	}

	public void setOperatorCost(int op, double cost) {
		operatorsCost[op - ADD] = cost;
	}

	public double getOperatorCost(int op) {
		return operatorsCost[op - ADD];
	}

	public Map<Integer, ArrayList<State>> compteToState() {
		return compteToState;
	}

	@Override
	public int numberOfVertices() {
		return adjacency.size();
	}

	@Override
	public int numberOfEdges() {
		return numberOfEdges;
	}

	@Override
	public void addVertex(State v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addEdge(State u, State v, double weight) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<VertexAndWeight<State>> neighbors(State s) {
		if (adjacency.containsKey(s)) {
			return adjacency.get(s);
		}
		HashSet<VertexAndWeight<State>> neighbors = s.neighbors();
		adjacency.put(s, neighbors);
		for (VertexAndWeight<State> vw : neighbors) {
			int compte = vw.vertex.getCompte();
			if (compte == State.UNFINISHED) continue;
			if (!compteToState.containsKey(compte)) {
				compteToState.put(compte, new ArrayList<>());
			}
			compteToState.get(compte).add(vw.vertex);
		}
		numberOfEdges += neighbors.size();
		return neighbors;
	}

	public State initialState() {
		return new State(0, State.UNFINISHED);
	}

	public class State {
		static final int UNFINISHED = -1;
		static final int IMPOSSIBLE = -2;
		private long stack;
		private int compte;

		State(long stack, int compte) {
			this.stack = stack;
			this.compte = compte;
		}
		public int getCompte() {
			return compte;
		}
		HashSet<VertexAndWeight<State>> neighbors() {
			HashSet<VertexAndWeight<State>> res = new HashSet<>();
			// Create a set of plaques we already have
			HashSet<Integer> haveplaques = new HashSet<>();
			// Compute our own cost and fill in the set
			int cost = 0;
			for (int idx = 1; idx <= this.size(this.stack); idx++) {
				int data = this.get(this.stack, idx);
				try {
					cost += getOperatorCost(data);
				} catch (ArrayIndexOutOfBoundsException e) {
					haveplaques.add(data);
					cost += 1;
				}
			}
			int[] ops = {ADD, MINUS, DIV, TIMES};
			for (int op : ops) {
				long opstack = this.push(stack, op);
				State opstate = new State(opstack, this.compte(opstack));
				if (opstate.compte == IMPOSSIBLE) continue;
				res.add(new VertexAndWeight<>(opstate, cost+getOperatorCost(op)));
			}
			for (int pl = 0; pl < plaques.length; pl++) {
				// Check that the stack does not contain PL
				if (haveplaques.contains(pl)) continue;
				// Else create stuff
				long nstack = this.push(stack, pl);
				State nstate = new State(nstack, this.compte(nstack));
				if (nstate.compte == IMPOSSIBLE) continue;
				res.add(new VertexAndWeight<>(nstate, cost+1));
			}
			// Push the operations with their additional cost
//			res.add(new VertexAndWeight<>)
			return res;
		}
		private SmallSet plaques(long stack) {
			SmallSet res = new SmallSet();
			int n = size(stack);
			for (int i = 1; i <= n; i++) {
				int v = get(stack, i);
				if (v < ADD) {
					res.insert(v);
				}
			}
			return res;
		}
		int compte(long stack) {
			Deque<Integer> calc = new ArrayDeque<>();
			for (int idx = 1; idx <= this.size(stack); idx++) {
				// Get element
				int data = this.get(stack, idx);
				switch (data) {
					case TIMES: {
						if (calc.size() < 2) {
							return IMPOSSIBLE;
						}
						calc.push(calc.pop() * calc.pop());
						break;
					}
					case MINUS: {
						if (calc.size() < 2) {
							return IMPOSSIBLE;
						}
						int potres = -calc.pop()+calc.pop();
						if (potres < 0) { return IMPOSSIBLE; }
						calc.push(potres);
						break;
					}
					case ADD: {
						if (calc.size() < 2) {
							return IMPOSSIBLE;
						}
						calc.push(calc.pop()+calc.pop());
						break;
					}
					case DIV: {
						if (calc.size() < 2) {
							return IMPOSSIBLE;
						}
						int opa = calc.pop();
						int opb = calc.pop();
						int potres = opb/opa;
						if (potres * opa != opb) { return IMPOSSIBLE; } // It wasn't an integer division
						calc.push(potres);
						break;
					}
					default: {
						// It's a number
						calc.push(plaques[data]);
					}
				}
			}
			if (calc.size() > 1) {
				return UNFINISHED;
			}
			if (calc.size() == 1) {
				return calc.pop();
			}
			return UNFINISHED; // The queue was empty from the start
		}
		private int get(long stack, int index) {
			return (int) ((stack & (0xFL << index * 4)) >>> index * 4);
		}
		private long set(long stack, int index, long v) {
			stack = stack & ~(0xFL << index * 4);
			stack = stack | (v << index * 4);
			return stack;
		}
		private int size(long stack) {
			return get(stack, 0);
		}
		long push(long stack, int v) {
			int n = get(stack, 0);
			stack = set(stack, n + 1, v);
			stack = set(stack, 0, n + 1);
			return stack;
		}
		public String toString() {
			String res = "";
			int n = size(stack);
			for (int i = 1; i <= n; i++) {
				switch (get(stack, i)) {
					case ADD:
						res += "+" + (i != n ? " " : "");
						break;
					case MINUS:
						res += "-" + (i != n ? " " : "");
						break;
					case TIMES:
						res += "*" + (i != n ? " " : "");
						break;
					case DIV:
						res += "/" + (i != n ? " " : "");
						break;
					default:
						res += plaques[get(stack, i)] + (i != n ? " " : "");
				}
			}
			return res;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + compte;
			result = prime * result + (int) (stack ^ (stack >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (compte != other.compte)
				return false;
			if (stack != other.stack)
				return false;
			return true;
		}
	}
}