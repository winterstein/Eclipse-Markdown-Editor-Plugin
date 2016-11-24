package winterwell.markdown.util;

import java.util.Collection;

public interface IProperties {

	public abstract boolean containsKey(Key key);

	public abstract Object get(Key key);

	public abstract Collection<? extends Key> getKeys();

	public abstract boolean isTrue(Key key);

	public abstract Object put(Key key, Object obj);
}
