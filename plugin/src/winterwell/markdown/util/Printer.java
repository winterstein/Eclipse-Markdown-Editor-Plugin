package winterwell.markdown.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

public class Printer {

	private static boolean useListMarkers;
	// static final Map useMe = Collections.synchronizedMap(new HashMap());
	private static final Class<?> ArraysListType = Arrays.asList(new Object[0]).getClass();
	private static final DecimalFormat df = new DecimalFormat("#.##");
	public static final Key INDENT;
	// private static final int MAX_ITEMS = 12;
	static final Pattern n = Pattern.compile("[1-9]");
	// private static final IPrinter PLAIN_TO_STRING = new IPrinter() {
	//
	// public void append(Object obj, StringBuilder sb) {
	// sb.append(obj);
	// }
	//
	// public String toString() {
	// return "PLAIN_TO_STRING";
	// }
	//
	// };

	static {
		INDENT = new Key("Printer.indent");
		Environment.putDefault(INDENT, "");
	}

	public static interface IPrinter {

		public abstract void append(Object obj, StringBuilder stringbuilder);
	}

	public Printer() {}

	public static void addIndent(String indent) {
		Environment env = Environment.get();
		String oldindent = (String) env.get(INDENT);
		String newIndent = (new StringBuilder(String.valueOf(oldindent))).append(indent).toString();
		env.put(INDENT, newIndent);
	}

	private static void append(Object x, StringBuilder sb) {
		if (x == null) {
			return;
		}
		if (x instanceof CharSequence) {
			sb.append((CharSequence) x);
			return;
		}
		if (x.getClass().isArray()) {
			for (Object y : (Object[]) x) {
				sb.append(y);
			}
			return;
		}
		if (x instanceof Number) {
			sb.append(toStringNumber((Number) x));
			return;
		}
		if ((x instanceof ArrayList) || (x instanceof HashSet) || (x instanceof ArraySet)
				|| x.getClass() == ArraysListType) {
			append(sb, (Collection<?>) x, ", ");
			return;
		}
		if (x instanceof HashMap) {
			append(sb, (Map<?, ?>) x, (new StringBuilder(String.valueOf(Strings.EOL)))
					.append((String) Environment.get().get(INDENT)).toString(), ": ", "{}");
			return;
		}
		// if (ReflectionUtils.hasMethod(x.getClass(), "toString")) {
		// useMe.put(x.getClass(), PLAIN_TO_STRING);
		// sb.append(x.toString());
		// return;
		// }
		if (x instanceof Iterable) {
			List<Object> target = new ArrayList<>();
			for (Object y : (Iterable<?>) x) {
				target.add(y);
			}
			x = target;
		}
		if (x instanceof Enumeration) {
			x = Collections.list((Enumeration<?>) x);
		}
		if (x instanceof Collection) {
			append(sb, (Collection<?>) x, ", ");
			return;
		}
		if (x instanceof Map) {
			append(sb, (Map<?, ?>) x, (new StringBuilder(String.valueOf(Strings.EOL)))
					.append((String) Environment.get().get(INDENT)).toString(), ": ", "{}");
			return;
		}
		if (x instanceof Exception) {
			sb.append(toString((Exception) x, true));
			return;
		}
		if (x instanceof Node) {
			Node node = (Node) x;
			sb.append((new StringBuilder("<")).append(node.getNodeName()).append(">").append(node.getTextContent())
					.append("</").append(node.getNodeName()).append(">").toString());
			return;
		} else {
			sb.append(x);
			return;
		}
	}

	public static StringBuilder append(StringBuilder sb, Collection<?> list, String separator) {
		boolean added = false;
		if (useListMarkers) {
			sb.append('[');
		}
		for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
			Object y = iterator.next();
			if (y != null) {
				added = true;
				if (y == list) {
					sb.append("(this Collection)");
				} else {
					append(y, sb);
					sb.append(separator);
				}
			}
		}

		if (added) {
			Strings.pop(sb, separator.length());
		}
		if (useListMarkers) {
			sb.append(']');
		}
		return sb;
	}

	public static void append(StringBuilder sb, Map<?, ?> x, String entrySeparator, String keyValueSeparator,
			String startEnd) {
		if (startEnd != null && startEnd.length() != 0 && startEnd.length() != 2) {
			throw new AssertionError();
		}
		List<?> keys = new ArrayList<>(x.keySet());
		if (keys.size() > 12) {
			keys = keys.subList(0, 12);
		}
		if (startEnd != null && startEnd.length() > 0) {
			sb.append(startEnd.charAt(0));
		}
		for (Iterator<?> iterator = keys.iterator(); iterator.hasNext(); sb.append(entrySeparator)) {
			Object k = iterator.next();
			sb.append(toString(k));
			sb.append(keyValueSeparator);
			sb.append(toString(x.get(k)));
		}

		if (keys.size() > 1) {
			Strings.pop(sb, entrySeparator.length());
		}
		if (startEnd != null && startEnd.length() > 1) {
			sb.append(startEnd.charAt(1));
		}
	}

	public static void appendFormat(StringBuilder result, String message, Object args[]) {
		for (int i = 0; i < args.length; i++) {
			message = message.replace((new StringBuilder("{")).append(i).append("}").toString(), toString(args[i]));
		}

		result.append(message);
	}

	public static String format(String message, Object args[]) {
		for (int i = 0; i < args.length; i++) {
			message = message.replace((new StringBuilder("{")).append(i).append("}").toString(), toString(args[i]));
		}

		return message;
	}

	public static void formatOut(String message, Object args[]) {
		String fm = format(message, args);
		System.out.println(fm);
	}

	public static void out(Object x[]) {
		System.out.println(toString(((Object) (x))));
	}

	public static String prettyNumber(double x) {
		return prettyNumber(x, 3);
	}

	public static String prettyNumber(double x, int sigFigs) {
		if (x >= 1000000D) {
			return (new StringBuilder(String.valueOf(Strings.toNSigFigs(x / 1000000D, sigFigs)))).append(" million")
					.toString();
		}
		if (x >= 1000D) {
			String s = Strings.toNSigFigs(x, sigFigs);
			x = Double.valueOf(s).doubleValue();
			DecimalFormat f = new DecimalFormat("###,###");
			return f.format(x);
		} else {
			return Strings.toNSigFigs(x, sigFigs);
		}
	}

	public static void removeIndent(String indent) {
		Environment env = Environment.get();
		String oldindent = (String) env.get(INDENT);
		if (!oldindent.endsWith(indent)) {
			throw new AssertionError();
		} else {
			String newIndent = oldindent.substring(0, oldindent.length() - indent.length());
			env.put(INDENT, newIndent);
			return;
		}
	}

	// public static void setUseListMarkers(boolean useListMarkers) {
	// useListMarkers = useListMarkers;
	// }

	public static String toString(Collection<?> list, String separator) {
		StringBuilder sb = new StringBuilder();
		append(sb, list, separator);
		return sb.toString();
	}

	public static String toString(Map<?, ?> x, String entrySeparator, String keyValueSeparator) {
		StringBuilder sb = new StringBuilder();
		append(sb, x, entrySeparator, keyValueSeparator, "{}");
		return sb.toString();
	}

	public static String toString(Object x) {
		if (x == null) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder();
			append(x, sb);
			return sb.toString();
		}
	}

	public static String toString(Throwable x, boolean stacktrace) {
		if (!stacktrace) {
			return x.getMessage() != null ? (new StringBuilder(String.valueOf(x.getClass().getSimpleName())))
					.append(": ").append(x.getMessage()).toString() : x.getClass().getSimpleName();
		} else {
			StringWriter w = new StringWriter();
			w.append((new StringBuilder()).append(x.getClass()).append(": ").append(x.getMessage()).append(Strings.EOL)
					.append((String) Environment.get().get(INDENT)).append("\t").toString());
			try (PrintWriter pw = new PrintWriter(w);) {
				x.printStackTrace(pw);
				pw.flush();
			}
			return w.toString();
		}
	}

	public static String toStringNumber(Number x) {
		float f = x.floatValue();
		if (f == (float) Math.round(f)) {
			return Integer.toString((int) f);
		}
		if (Math.abs(f) >= 1.0F) {
			return df.format(f);
		}
		String fs = Float.toString(f);
		if (fs.contains("E")) {
			return fs;
		}
		String fss = Strings.substring(fs, 0, 5);
		if (n.matcher(fss).find()) {
			return fss;
		} else {
			return fs;
		}
	}
}
