package queue;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
	protected int size;

	public AbstractQueue init() {
		return initImpl();
	}

	protected abstract AbstractQueue initImpl();

	public void enqueue(Object elem) {
		// assert elem != null;
		enqueueImpl(elem);
		size++;
	}

	protected abstract void enqueueImpl(Object elem);

	public Object dequeue() {
		// assert size > 0;
		size--;
		return dequeueImpl();
	}

	protected abstract Object dequeueImpl();

	public Object element() {
		return elementImpl();
	}

	protected abstract Object elementImpl();

	public void clear() {
		size = 0;
		clearImpl();
	}

	protected abstract void clearImpl();

	public AbstractQueue filter(Predicate T) {
		// return null;
		AbstractQueue ans = init();
		for (int i = 0; i < size; i++) {
			Object elem = ans.dequeue();
			if (T.test(elem)) {
				ans.enqueue(elem);
			}
		}
		return ans;
	}

	public AbstractQueue map(Function F) {
		// return null;
		AbstractQueue ans = init();
		for (int i = 0; i < size; i++) {
			Object elem = ans.dequeue();
			ans.enqueue(F.apply(elem));	
		}
		return ans;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}
}
