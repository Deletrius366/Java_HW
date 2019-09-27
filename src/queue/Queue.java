package queue;

import java.util.function.Function;
import java.util.function.Predicate;

// enqueue - insert element in the end
// dequeue - delete element from the begin
// size - quantity of elements in queue
// clear - delete all elements
// isEmpty - true if there are any elements in queue
// makeCopy - copy of the queue
// filter - queue with elements after filter apllying
// map - queue with elements after function apllying

public interface Queue extends Copiable {
	// pre: size == 0 && first = -1 && last = -1 || size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	void enqueue(Object element);
	// post: size' = size+1, last.next = new elem, last' = last.next, first' =
	// first;

	// pre: size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	Object dequeue();
	// post: size' = size-1, first' = first.next
	
	// pre: size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	Object element();
	//post: nothing happened to queue, return first
	
	// pre: size == 0 && first = -1 && last = -1 || size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	int size();
	//post: nothing happened to queue, return size
	
	// pre: size == 0 && first = -1 && last = -1 || size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	void clear();
	//post: clear queue, size = 0, first = last = -1;
	
	// pre: size == 0 && first = -1 && last = -1 || size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	boolean isEmpty();
	//post: nothing happened to queue, size == 0 - true, else false
	
	// pre: size == 0 && first = -1 && last = -1 || size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	Queue makeCopy();
	//post: nothing happened to queue, return copy of queue
	
	// pre: size == 0 && first = -1 && last = -1 || size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	Queue filter(Predicate T);
	//post: elements, invalid Predicate, deleted from queue
	
	// pre: size == 0 && first = -1 && last = -1 || size > 0 && first - pointer on
	// first elem, last - pointer on last elem
	Queue map(Function F);
	// post: each element' = f(element);
}
