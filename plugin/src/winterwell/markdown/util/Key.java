package winterwell.markdown.util;

import java.io.Serializable;
import java.util.Map;

public class Key implements Comparable<String>, Serializable {

	public static class RichKey<T> extends Key {

		public final String description;
		public final Class<T> valueClass;

		public RichKey(String name, Class<T> valueClass, String description) {
			super(name);
			if (valueClass == null) {
				throw new AssertionError();
			} else {
				this.valueClass = valueClass;
				this.description = description;
				return;
			}
		}
	}

	private final String name;

	public Key(String name) {
		if (name == null) {
			throw new AssertionError();
		} else {
			this.name = name;
			return;
		}
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Key)) return false;
		return name.equals(((Key) obj).name);
	}

	public Object getFromMap(Map<String, Object> map) {
		return map.get(name);
	}

	public final String getName() {
		return name;
	}

	@Override
	public final int hashCode() {
		return 31 + name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	public int compareTo(Key key) {
		return name.compareTo(key.name);
	}

	@Override
	public int compareTo(String o) {
		return name.compareTo(o);
	}
}
