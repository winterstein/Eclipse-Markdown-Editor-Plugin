package winterwell.markdown.util;

import java.util.AbstractList;

public final class IntRange extends AbstractList<Integer> {

	public final int high;
	public final int low;

	public IntRange(int a, int b) {
		if (a < b) {
			low = a;
			high = b;
		} else {
			low = b;
			high = a;
		}
	}

	public boolean contains(int x) {
		return x >= low && x <= high;
	}

	public Integer get(int index) {
		if (!contains(index + low)) throw new AssertionError();
		return Integer.valueOf(index + low);
	}

	public int size() {
		return (high - low) + 1;
	}

	public String toString() {
		return (new StringBuilder("[")).append(low).append(", ").append(high).append("]").toString();
	}
}
