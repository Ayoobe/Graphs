package fr.insa_rennes.sdd.priority_queue;

import fr.insa_rennes.sdd.util.ArraySupport;

import java.util.Arrays;
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

	// Coordinate calculators
	private int parent(int i) { return (i-1)/2; }
	private int leftChild(int i) { return (2*i+1); }
	private int rightChild(int i) { return (2*i+2); }
 	private void swap(int i, int j) {
		T tmp = heap[i];
		heap[i] = heap[j];
		heap[j] = tmp;
	}
	private int compare_at(int i, int j) {
		return comparator.compare(heap[i], heap[j]);
	}
	private void shiftUp(int i) {
		while (i > 0 && this.compare_at(parent(i), i) > 0) {
			this.swap(parent(i), i);
			i = parent(i);
		}
	}
	private void shiftDown(int i) {
		boolean finish = false;
		int k = i;
		while (k < size / 2 && !finish) {
			int index = leftChild(k);
			if (index < size && this.compare_at(index, rightChild(k)) > 0) {
				index = rightChild(k);
			}
			if (this.compare_at(index, k) > 0) {
				finish = true;
			} else {
				swap(k, index);
				k = index;
			}
		}
	}

	@Override
	public boolean isEmpty() {
		for (int idx = 0; idx < this.size; idx++) {
			if (heap[idx] != null)
				return false;
		}
		return true;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public void add(T e) {
		if (e == null) {
			throw new NullPointerException();
		}
		if (size >= heap.length) {
			grow();
		}
		heap[size] = e;
		shiftUp(size);
		size++;
	}

	private void grow() {
		int oldLength = heap.length;
		heap = Arrays.copyOf(heap,
				ArraySupport.newLength(oldLength, oldLength+1,
						oldLength *2));
	}

	@Override
	public T peek() {
		return (size == 0) ? null : heap[0];
	}

	@Override
	public T poll() {
		if (size == 0) {
			return null;
		}
		T result = heap[0];
		heap[0] = heap[size-1];
		size--;
		shiftDown(0);
		return result;
	}

}