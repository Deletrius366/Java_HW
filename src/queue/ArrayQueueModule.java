package queue;

public class ArrayQueueModule {
	public static int size, first = 0;
	public static int last = -1;
	// private int capacity = 10;
	public static Object[] elem = new Object[10];

	public  static void enqueue(Object element) {
		assert element != null;
		ensureCapacity(size + 1);
		last = (last + 1) % elem.length;
		elem[last] = element;
		size++;
	}

	public static void push(Object element) {
		assert element != null;
		ensureCapacity(size + 1);
		first = first-1;
		if(first < 0){
			first += elem.length;
		}
		elem[first] = element;
		size++;
	}

	public static Object peek() {
		return elem[last];
	}

	public static Object remove() {
		assert size > 0;
		Object element = elem[last];
		last--;
		if(last < 0){
			last += elem.length;
		}
		size--;
		return element;
	}

	public static Object dequeue() {
		assert size > 0;
		Object element = elem[first];
		first = (first + 1) % elem.length;
		size--;
		return element;
	}

	public static Object element() {
		return elem[first];
	}

	public static void ensureCapacity(int capacity) {
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

	public static int size() {
		return size;
	}

	public static void clear() {
		first = size = 0;
		last = -1;
	}

	public static boolean isEmpty() {
		return size == 0;
	}

}