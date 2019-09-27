package queue;

public class ArrayQueueADT {
	public static int size= 0;
	public static int last = -1;
	public static int first = 1;
	// public int capacity = 10;
	public Object[] elem = new Object[10];

	public static void enqueue(ArrayQueueADT queue, Object elem) {
		assert elem != null;
		ensureCapacity(queue, (queue.size + 1));
		queue.last = (queue.last + 1) % queue.elem.length;
		queue.elem[queue.last] = elem;
		queue.size++;
		if(queue.size == 1){
			queue.first = queue.last;
		}
	}

	public static void push(ArrayQueueADT queue, Object element) {
		assert element != null;
		ensureCapacity(queue, queue.size + 1);
		queue.first = queue.first-1;
		if(queue.first < 0){
			queue.first += queue.elem.length;
		}
		queue.elem[queue.first] = element;
		queue.size++;
		if(queue.size == 1){
			queue.last = queue.first;
		}
	}

	public static Object peek(ArrayQueueADT queue) {
		return queue.elem[queue.last];
	}

	public static Object remove(ArrayQueueADT queue) {
		assert queue.size > 0;	
		Object element = queue.elem[queue.last];
		queue.last--;
		if(queue.last < 0){
			queue.last += queue.elem.length;
		}
		queue.size--;
		return element;
	}

	public static void ensureCapacity(ArrayQueueADT queue, int capacity) {
		if (capacity <= queue.elem.length) {
			return;
		}
		Object[] newElements = new Object[2 * capacity];
		int j = queue.first;
		for (int i = 0; i < queue.size; i++) {
			newElements[i] = queue.elem[j];
			j++;
			j %= queue.elem.length;
		}
		queue.elem = newElements;
		queue.first = 0;
		queue.last = queue.size - 1;
	}

	public static Object dequeue(ArrayQueueADT queue) {
		assert queue.size > 0;
		Object elem = queue.elem[queue.first];
		queue.first = (queue.first + 1) % queue.elem.length;
		queue.size--;
		return elem;
	}

	public static Object element(ArrayQueueADT queue) {
		return queue.elem[queue.first];
	}

	public static int size(ArrayQueueADT queue) {
		return queue.size;
	}

	public static void clear(ArrayQueueADT queue) {
		queue.size = 0;
		queue.last = -1;
		queue.first = 1;
	}

	public static boolean isEmpty(ArrayQueueADT queue) {
		return queue.size == 0;
	}

}