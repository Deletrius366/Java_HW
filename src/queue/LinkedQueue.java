package queue;

public class LinkedQueue extends AbstractQueue implements Copiable {
	private Node head, tail;

	public LinkedQueue initImpl() {
		return makeCopy();
	}

	public void enqueueImpl(Object elem) {

		Node element = new Node(elem, null);
		if (size == 0) {
			head = tail = element;
		} else {
			tail.next = element;
			tail = tail.next;
		}
	}

	public Object dequeueImpl() {
		if(size == -1){
			return null;
		}
		Object rc = head.value;
		head = head.next;
		if (size == 0) {
			head = tail = null;
		}
		return rc;
	}

	public Object elementImpl() {
	//	assert size > 0;
		
		return head.value;
	}

	public void clearImpl() {
		head = null;
		tail = null;
	}

	public LinkedQueue makeCopy() {
		final LinkedQueue copy = new LinkedQueue();
		copy.size = size;
		copy.head = head;
		copy.tail = tail;
		return copy;
	}

	private class Node {
		private Object value;
		private Node next;

		public Node(Object value, Node next) {
		//	assert value != null;
					
			this.value = value;
			this.next = next;
		}
	}

}
