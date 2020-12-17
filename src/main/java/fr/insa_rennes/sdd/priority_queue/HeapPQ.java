package fr.insa_rennes.sdd.priority_queue;

import java.util.Comparator;

public class HeapPQ<T> implements PriorityQueue<T> {
	private static final int DEFAULT_INITIAL_CAPACITY = 8;
	private Comparator<? super T> comparator;
	private int size;
	T[] heap;

	public HeapPQ() {
		this(DEFAULT_INITIAL_CAPACITY, null);
	}	

	public HeapPQ(int initialCapacity) {
		this(initialCapacity, null);
	}

	public HeapPQ(Comparator<? super T> comparator) {
		this(DEFAULT_INITIAL_CAPACITY, comparator);
	}

	@SuppressWarnings("unchecked")
	public HeapPQ(int initialCapacity, Comparator<? super T> comparator) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException();
		}
		heap = (T[])new Object[initialCapacity];
		this.comparator = comparator == null ? (t1, t2) -> ((Comparable<? super T>)t1).compareTo(t2) : comparator;
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(T e) {
		throw new UnsupportedOperationException();
	}

	private void grow() {		        
		throw new UnsupportedOperationException();
	}	

	@Override
	public T peek() {
		throw new UnsupportedOperationException();
	}

	@Override
	public T poll() {
		throw new UnsupportedOperationException();
	}
	
}
