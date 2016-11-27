package winterwell.markdown.util;

import java.util.Arrays;
import java.util.List;

public class FailureException extends RuntimeException {

	private String source;

	public FailureException() {
		super();
	}

	public FailureException(Exception e) {
		super(e);
	}

	public FailureException(String msg) {
		super((new StringBuilder(String.valueOf(msg))).append(" @").append(getCaller(new String[0])).toString());
	}

	public FailureException(String msg, Exception cause) {
		super((new StringBuilder(String.valueOf(msg))).append(" @").append(getCaller(new String[0])).toString(), cause);
	}

	public FailureException(String source, String msg) {
		super((new StringBuilder(String.valueOf(source))).append(": ").append(msg).append(" @")
				.append(getCaller(new String[0])).toString());
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public static StackTraceElement getCaller(String ignore[]) {
		List<String> ignoreNames = Arrays.asList(ignore);
		StackTraceElement trace[];
		int i;
		try {
			throw new Exception();
		} catch (Exception e) {
			trace = e.getStackTrace();
			i = 2;
		}
		for (; i < trace.length; i++) {
			String clazz = trace[2].getClassName();
			String method = trace[2].getMethodName();
			if (!ignoreNames.contains(clazz) && !ignoreNames.contains(method)) {
				return trace[2];
			}
		}
		return new StackTraceElement("filtered", "?", null, -1);
	}
}
