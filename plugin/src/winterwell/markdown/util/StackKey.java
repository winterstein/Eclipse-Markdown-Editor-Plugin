package winterwell.markdown.util;

final class StackKey extends Key {

	public StackKey(Key key) {
		super((new StringBuilder(String.valueOf(key.getName()))).append(".envstack").toString());
	}
}
