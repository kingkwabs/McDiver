package datastructures;

import datastructures.PQueue;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class FastPQueue<E> implements PQueue<E>{
        record PrioElem<E> (E elem, double priority) {};

    private final ArrayList<PrioElem<E>> queue;

    public FastPQueue() {
        queue = new ArrayList<>();
    }

    private int getLeftChildIndex(int parentIndex) {
        return 2 * parentIndex + 1;
    }

    private boolean hasLeftChild(int i) {
        return getLeftChildIndex(i) < queue.size();
    }

    private PrioElem<E> leftChild(int i) {
        return queue.get(getLeftChildIndex(i));
    }

    private int getRightChildIndex(int parentIndex) {
        return 2 * parentIndex + 2;
    }

    private boolean hasRightChild(int i) {
        return getRightChildIndex(i) < queue.size();
    }

    private PrioElem<E> rightChild(int i) {
        return queue.get(getRightChildIndex(i));
    }

    private int getParentIndex(int childIndex) {
        return (childIndex - 1) / 2;
    }

    private boolean hasParent(int i) {
        return i != 0 && getParentIndex(i) >= 0;
    }

    private PrioElem<E> parent(int i) {
        return queue.get(getParentIndex(i));
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void add(E e, double priority) throws IllegalArgumentException {
        for (PrioElem<E> pe : queue) {
            if (pe.elem.equals(e)) throw new IllegalArgumentException("object "
                    + "already in the queue.");
        }
        queue.add(new PrioElem<>(e, priority));
        resortUp(queue.size() - 1);
    }

    private void resortUp(int i) {
        while (hasParent(i) && parent(i).priority > queue.get(i).priority) {
            swap(i, getParentIndex(i));
            i = getParentIndex(i);
        }
    }

    @Override
    public E peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty.");
        }
        return queue.get(0).elem;
    }

    @Override
    public E extractMin() {
        if (!isEmpty()) {
            E minItem = queue.get(0).elem;
            PrioElem<E> lastItem = queue.remove(queue.size() - 1); // Save the last element before removing
            if (!isEmpty()) { // Check if there's more than one element in the queue
                queue.set(0, lastItem);
                resortDown(0);
            }
            return minItem;
        }
        throw new NoSuchElementException("Queue is empty.");
    }

    private void resortDown(int i) {
        while (hasLeftChild(i)) {
            int smallerChild = getLeftChildIndex(i);
            if (hasRightChild(i) && rightChild(i).priority < leftChild(i).priority) {
                smallerChild = getRightChildIndex(i);
            }
            if (queue.get(i).priority > queue.get(smallerChild).priority) {
                swap(i, smallerChild);
                i = smallerChild;
            } else {
                break;
            }
        }
    }
    @Override
    public void changePriority(E e, double p) {
        for (int i = 0; i < queue.size(); i++) {
            PrioElem<E> pe = queue.get(i);
            if (pe.elem.equals(e)) {
                double oldPriority = pe.priority;
                queue.set(i, new PrioElem<>(e, p));
                if (p < oldPriority) {
                    resortUp(i);
                } else {
                    resortDown(i);
                }
                return;
            }
        }
        assert false;
    }

    private void swap(int i, int j) {
        PrioElem<E> temp = queue.get(i);
        queue.set(i, queue.get(j));
        queue.set(j, temp);
    }
}
