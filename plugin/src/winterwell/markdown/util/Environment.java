package winterwell.markdown.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public final class Environment implements IProperties {

	private static final Map<Key, Object> defaultProperties = new HashMap<Key, Object>();
	private static final Environment dflt = new Environment();
	private final ThreadLocal<Map<Key, Stack<Object>>> localVars = new ThreadLocal<Map<Key, Stack<Object>>>() {

		protected Map<Key, Stack<Object>> initialValue() {
			return new HashMap<>();
		}
	};

	public Environment() {}

	public static Environment get() {
		return dflt;
	}

	public static void putDefault(Key key, Object value) {
		if (value == null) {
			defaultProperties.remove(key);
		} else {
			defaultProperties.put(key, value);
		}
	}

	public boolean containsKey(Key key) {
		Object result = get(key);
		return result != null;
	}

	public Object get(Key key) {
		if (key == null) {
			throw new AssertionError();
		}
		Map<Key, Stack<Object>> properties = localVars.get();
		Object v = properties.get(key);
		if (v == null) v = defaultProperties.get(key);
		return v;
	}

	public Collection<Key> getKeys() {
		Map<Key, Stack<Object>> properties = localVars.get();
		HashSet<Key> keys = new HashSet<Key>();
		keys.addAll(properties.keySet());
		keys.addAll(defaultProperties.keySet());
		return keys;
	}

	public boolean isTrue(Key key) {
		Boolean v = (Boolean) get(key);
		return v != null && v.booleanValue();
	}

	public Object pop(Key key) {
		if (key == null) {
			throw new AssertionError();
		}
		Map<Key, Stack<Object>> properties = localVars.get();
		Key stackKey = new StackKey(key);
		Stack<Object> stack = (Stack<Object>) properties.get(stackKey);
		if (stack == null) {
			throw new AssertionError();
		} else {
			Object oldValue = stack.pop();
			Object newValue = stack.peek();
			put(key, newValue);
			return oldValue;
		}
	}

	public void push(Key key, Object value) {
		if ((key == null || value == null)) throw new AssertionError();
		Map<Key, Stack<Object>> properties = localVars.get();
		put(key, value);
		Key stackKey = new StackKey(key);
		Stack<Object> stack = properties.get(stackKey);
		if (stack == null) {
			stack = new Stack<Object>();
			properties.put(stackKey, stack);
		}
		stack.push(value);
	}

	@SuppressWarnings("unchecked")
	public Object put(Key key, Object value) {
		if (key == null) throw new AssertionError();
		Map<Key, Stack<Object>> properties = localVars.get();
		if (value == null) return properties.remove(key);
		return properties.put(key, (Stack<Object>) value);
	}

	public void reset() {
		localVars.remove();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Key k;
		for (Iterator<Key> iterator = getKeys().iterator(); iterator.hasNext(); sb
				.append((new StringBuilder()).append(k).append(": ").append(get(k)).toString())) {
			k = iterator.next();
		}
		return sb.toString();
	}
}
