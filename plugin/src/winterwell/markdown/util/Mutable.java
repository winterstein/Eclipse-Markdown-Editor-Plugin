package winterwell.markdown.util;

public final class Mutable {

	public static final class Dble {

		public String toString() {
			return (new StringBuilder()).append(value).toString();
		}

		public double value;

		public Dble() {
			this(0.0D);
		}

		public Dble(double v) {
			value = v;
		}
	}

	public static final class Int {

		public String toString() {
			return (new StringBuilder()).append(value).toString();
		}

		public int value;

		public Int() {
			this(0);
		}

		public Int(int v) {
			value = v;
		}
	}

	public static final class Ref {

		public String toString() {
			return Printer.toString(value);
		}

		public Object value;

		public Ref() {}

		public Ref(Object value) {
			this.value = value;
		}
	}

	public static final class Strng {

		public String toString() {
			return value;
		}

		public String value;

		public Strng() {
			this("");
		}

		public Strng(String v) {
			value = v;
		}
	}

	public Mutable() {}
}
