package comp2402a3;

import comp2402a3.IndexedSSet;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class SkippityFast<T> implements IndexedSSet<T> {
	protected Comparator<T> c;

	protected static class Node<T> {
		T x;
		Node<T>[] next;

		public Node(T ix, int h) {
			x = ix;
			next = new Node[h + 1];
		}

		public int height() {
            return 0;
        }
	}

	protected Node<T> sentinel;
	int h;
	int n;
	Random rand;
	protected Node<T>[] stack;

	public SkippityFast(Comparator<T> c) {
		this.c = c;
		n = 0;
		sentinel = new Node<>(null, 32);
		stack = new Node[sentinel.next.length];
		h = 0;
		rand = new Random();
	}

	public SkippityFast() {
		this(new DefaultComparator<T>());
	}

	//Ensures that the height of the node is not greater than the height of the sentinel
	protected int pickHeight() {
		int z = rand.nextInt();
		int k = 0;
		int m = 1;
		while ((z & m) != 0 && k < h) {
			k++;
			m <<= 1;
		}
		return k;
	}


	protected Node<T> findPredNode(T x) {
		Node<T> u = sentinel;
		int r = h;
		while (r >= 0) {
			while (u.next[r] != null && c.compare(u.next[r].x, x) < 0)
				u = u.next[r];
			r--;
		}
		return u;
	}

	public T find(T x) {
		Node<T> u = findPredNode(x);
		return u.next[0] == null ? null : u.next[0].x;
	}

	public T findGE(T x) {
		if (x == null) {
			return sentinel.next[0] == null ? null : sentinel.next[0].x;
		}
		return find(x);
	}

	public T findLT(T x) {
		if (x == null) {
			Node<T> u = sentinel;
			int r = h;
			while (r >= 0) {
				while (u.next[r] != null)
					u = u.next[r];
				r--;
			}
			return u.x;
		}
		return findPredNode(x).x;
	}

	public boolean add(T x) {
		Node<T> u = sentinel;
		int r = h;
		int comp = 0;
		while (r >= 0) {
			while (u.next[r] != null && (comp = c.compare(u.next[r].x, x)) < 0)
				u = u.next[r];
			if (u.next[r] != null && comp == 0)
				return false;
			stack[r--] = u;
		}
		Node<T> w = new Node<>(x, pickHeight());
		while (h < w.height())
			stack[++h] = sentinel;
		for (int i = 0; i < w.next.length; i++) {
			w.next[i] = stack[i].next[i];
			stack[i].next[i] = w;
		}
		n++;
		return true;
	}

	public boolean remove(T x) {
		boolean removed = false;
		Node<T> u = sentinel;
		int r = h;
		int comp = 0;
		while (r >= 0) {
			while (u.next[r] != null && (comp = c.compare(u.next[r].x, x)) < 0) {
				u = u.next[r];
			}
			if (u.next[r] != null && comp == 0) {
				removed = true;
				u.next[r] = u.next[r].next[r];
				if (u == sentinel && u.next[r] == null)
					h--;
			}
			r--;
		}
		if (removed) n--;
		return removed;
	}

	public T get(int i) {
		if (i < 0 || i >= n) {
			throw new IndexOutOfBoundsException("Index out of bounds");
		}

		Node<T> u = sentinel;
		int r = h;
		int j = -1;
		while (r >= 0) {
			while (u.next[r] != null && j + u.next[r].height() <= i) {
				j += u.next[r].height();
				u = u.next[r];
			}
			r--;

			if (j == i) {
				return u.next[0].x;
			}
		}
		return null;
	}


	public int rangecount(T x, T y) {
		if (c.compare(x, y) > 0) {
			T temp = x;
			x = y;
			y = temp;
		}

		Node<T> u = sentinel;
		int r = h;
		int count = 0;

		while (r >= 0) {
			while (u.next[r] != null && c.compare(u.next[r].x, x) < 0) {
				u = u.next[r];
			}

			if (u.next[r] != null) {
				while (c.compare(u.next[r].x, y) <= 0) {
					count++;
					u = u.next[r];
				}
			}
			r--;
		}

		return count;
	}


	public void clear() {
		n = 0;
		h = 0;
		for (int i = 0; i < sentinel.next.length; i++) {
			sentinel.next[i] = null;
		}
	}

	public int size() {
		return n;
	}

	public Comparator<T> comparator() {
		return c;
	}

	protected Iterator<T> iterator(Node<T> u) {
		class SkiplistIterator implements Iterator<T> {
			Node<T> u, prev;

			public SkiplistIterator(Node<T> u) {
				this.u = u;
				prev = null;
			}

			public boolean hasNext() {
				return u.next[0] != null;
			}

			public T next() {
				prev = u;
				u = u.next[0];
				return u.x;
			}

			public void remove() {
				SkippityFast.this.remove(prev.x);
			}
		}
		return new SkiplistIterator(u);
	}

	public Iterator<T> iterator() {
		return iterator(sentinel);
	}

	public Iterator<T> iterator(T x) {
		return iterator(findPredNode(x));
	}
}
