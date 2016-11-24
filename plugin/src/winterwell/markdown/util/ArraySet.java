package winterwell.markdown.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;

public final class ArraySet<E> extends AbstractList<E> implements Set<E>, List<E>, Serializable {

	private final ArrayList<E> backing;

	public ArraySet() {
		backing = new ArrayList<E>();
	}

	public ArraySet(Collection<E> elements) {
		backing = new ArrayList<>(elements.size());
		for (E element : elements) {
			add(element);
		}
	}

	public ArraySet(int initialSize) {
		backing = new ArrayList<>(initialSize);
	}

	public ArraySet(E[] elements) {
		backing = new ArrayList<>(elements.length);
		for (E element : elements) {
			add(element);
		}
	}

	public boolean add(E e) {
		if (backing.contains(e)) {
			return false;
		} else {
			return backing.add(e);
		}
	}

	public void clear() {
		backing.clear();
	}

	public boolean contains(Object o) {
		if (o == null) throw new AssertionError();
		return backing.contains(o);
	}

	public E get(int i) {
		return backing.get(i);
	}

	public Iterator<E> iterator() {
		return backing.iterator();
	}

	public boolean remove(Object o) {
		return backing.remove(o);
	}

	@Deprecated
	public E set(int index, E element) {
		E old = backing.set(index, element);
		return old;
	}

	public int size() {
		return backing.size();
	}

	@Override
	public Spliterator<E> spliterator() {
		return super.spliterator();
	}
}
