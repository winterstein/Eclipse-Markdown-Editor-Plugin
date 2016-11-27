package winterwell.markdown.util;

public class Pair<T> {

	public final T first;
	public final T second;

	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Return the first item in the pair
	 *
	 * @return the first item in the pair
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Return the second item in the pair
	 *
	 * @return the second item in the pair
	 */
	public T getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null) return false;
		} else if (!first.equals(other.first)) return false;
		if (second == null) {
			if (other.second != null) return false;
		} else if (!second.equals(other.second)) return false;
		return true;
	}
}
