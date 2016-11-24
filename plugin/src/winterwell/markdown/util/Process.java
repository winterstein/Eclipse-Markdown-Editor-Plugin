package winterwell.markdown.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Process {

	boolean closed;
	private String command;
	private boolean echo;
	private StreamGobbler err;
	private StreamGobbler out;
	private final ProcessBuilder pb;
	private java.lang.Process process;

	public Process(List<String> command) {
		pb = new ProcessBuilder(command);
	}

	public Process(String command) {
		this.command = command;
		pb = new ProcessBuilder(parse(command));
	}

	private void closeStreams() {
		if (process == null) return;
		if (err != null) err.pleaseStop();
		if (out != null) out.pleaseStop();

		FileUtils.close(process.getInputStream());
		FileUtils.close(process.getOutputStream());
		FileUtils.close(process.getErrorStream());
	}

	public void destroy() {
		if (closed) {
			return;
		} else {
			closed = true;
			process.destroy();
			closeStreams();
			return;
		}
	}

	protected void finalize() throws Throwable {
		super.finalize();
		closeStreams();
	}

	public String getCommand() {
		return command != null ? command : Printer.toString(pb.command(), " ");
	}

	public Map<String, String> getEnvironment() {
		return pb.environment();
	}

	public String getError() {
		try {
			return err != null ? err.getString() : "";
		} catch (IOException e) {
			return "";
		}
	}

	public String getOutput() {
		try {
			return out.getString();
		} catch (IOException e) {
			return "";
		}
	}

	List<String> parse(String command) {
		List<String> bits = new ArrayList<String>();
		StringBuilder bit = new StringBuilder();
		boolean inQuotes = false;
		char ac[];
		int j = (ac = command.toCharArray()).length;
		for (int i = 0; i < j; i++) {
			char c = ac[i];
			if (inQuotes) {
				bit.append(c);
				if (c == '"') {
					inQuotes = false;
				}
			} else if (Character.isWhitespace(c)) {
				if (bit.length() != 0) {
					bits.add(bit.toString());
					bit = new StringBuilder();
				}
			} else {
				bit.append(c);
				if (c == '"') {
					inQuotes = true;
				}
			}
		}

		if (bit.length() != 0) {
			bits.add(bit.toString());
		}
		String osName = System.getProperty("os.name");
		if (osName.equals("Windows 95")) {
			ArrayList<String> wbits = new ArrayList<String>(bits.size() + 2);
			wbits.add("command.com");
			wbits.add("/C");
			wbits.addAll(bits);
			bits = wbits;
		} else if (osName.startsWith("Windows")) {
			ArrayList<String> wbits = new ArrayList<String>(bits.size() + 2);
			wbits.add("cmd.exe");
			wbits.add("/C");
			wbits.addAll(bits);
			bits = wbits;
		}
		return bits;
	}

	public void run() {
		try {
			process = pb.start();
			out = new StreamGobbler(process.getInputStream());
			if (echo) out.setEcho(true);
			out.start();
			if (!pb.redirectErrorStream()) {
				err = new StreamGobbler(process.getErrorStream());
				if (echo) err.setEcho(true);
				err.start();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setDirectory(File dir) {
		pb.directory(dir);
	}

	public void setEcho(boolean echo) {
		if (process != null) {
			throw new AssertionError("do earlier!");
		} else {
			this.echo = echo;
			return;
		}
	}

	public void setRedirectErrorStream(boolean redirect) {
		pb.redirectErrorStream(redirect);
	}

	public String toString() {
		String data = "";
		try {
			data = out.getString();
		} catch (IOException e) {}
		return (new StringBuilder(String.valueOf(pb.command().toString()))).append("\n").append(data).toString();
	}

	public int waitFor() {
		sleep(5L);
		int v = 0;
		try {
			v = process.waitFor();
		} catch (InterruptedException e) {}
		closeStreams();
		return v;
	}

	public int waitFor(long timeout) {
		if (timeout < 1L) return waitFor();

		sleep(5L);

		TimeOut interrupter = new TimeOut(timeout);
		int v = 0;
		try {
			v = process.waitFor();
		} catch (InterruptedException e) {}
		interrupter.cancel();
		closeStreams();
		return v;

	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {}
	}
}
