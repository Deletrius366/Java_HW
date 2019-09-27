package queue;

import java.util.Arrays;

public class ArrayQueue extends AbstractQueue implements Copiable {
	private int first = 1;
	private int last = -1;
	private Object[] elem = new Object[10];

	public ArrayQueue initImpl() {
		return makeCopy();
	}

	public void enqueueImpl(Object element) {
		ensureCapacity(size + 1);
		last = (last + 1) % elem.length;
		elem[last] = element;
		if (size == 0) {
			first = last;
		}
	}

	public void push(Object element) {
		assert element != null;
		ensureCapacity(size + 1);
		first = first - 1;
		if (first < 0) {
			first += elem.length;
		}
		elem[first] = element;
		size++;
		if (size == 1) {
			last = first;
		}
	}

	public Object peek() {
		return elem[last];
	}

	public Object remove() {
		assert size > 0;
		Object element = elem[last];
		last--;
		if (last < 0) {
			last += elem.length;
		}
		size--;
		return element;
	}

	public Object dequeueImpl() {
		Object element = elem[first];
		first = (first + 1) % elem.length;
		return element;
	}

	public Object elementImpl() {
		return elem[first];
	}

	public void ensureCapacity(int capacity) {
		if (capacity <= elem.length) {
			return;
		}
		Object[] newElements = new Object[2 * capacity];
		int j = first;
		for (int i = 0; i < size; i++) {
			newElements[i] = elem[j];
			j++;
			j %= elem.length;
		}
		elem = newElements;
		first = 0;
		last = size - 1;
	}

	public void clearImpl() {
		first = 0;
		last = -1;
	}

	public ArrayQueue makeCopy() {
		final ArrayQueue copy = new ArrayQueue();
		copy.first = first;
		copy.last = last;
		copy.size = size;
		copy.elem = Arrays.copyOf(elem, elem.length);
		return copy;
	}

}