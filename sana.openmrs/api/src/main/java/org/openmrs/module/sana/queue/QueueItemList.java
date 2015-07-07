package org.openmrs.module.sana.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class QueueItemList implements List<QueueItem>{
	private List<QueueItem> items;
	
	public QueueItemList(){
		items = new ArrayList<QueueItem>();
	}
	
	public QueueItemList(int size){
		items = new ArrayList<QueueItem>(size);
	}
	
	public int size() {
		return items.size();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public boolean contains(Object o) {
		return items.contains(o);
	}

	public Iterator<QueueItem> iterator() {
		return items.iterator();
	}

	public Object[] toArray() {
		return items.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return null;
	}

	public boolean add(QueueItem e) {
		return items.add(e);
	}

	public boolean remove(Object o) {
		return items.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return items.containsAll(c);
	}

	public boolean addAll(Collection<? extends QueueItem> c) {
		return items.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends QueueItem> c) {
		return items.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return items.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return items.retainAll(c);
	}

	public void clear() {
		items.clear();
	}

	public QueueItem get(int index) {
		return items.get(index);
	}

	public QueueItem set(int index, QueueItem element) {
		return items.set(index, element);
	}

	public void add(int index, QueueItem element) {
		items.add(index, element);
	}

	public QueueItem remove(int index) {
		return items.remove(index);
	}

	public int indexOf(Object o) {
		return items.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return items.lastIndexOf(o);
	}

	public ListIterator<QueueItem> listIterator() {
		return items.listIterator();
	}

	public ListIterator<QueueItem> listIterator(int index) {
		return items.listIterator(index);
	}

	public List<QueueItem> subList(int fromIndex, int toIndex) {
		return items.subList(fromIndex, toIndex);
	}

}
