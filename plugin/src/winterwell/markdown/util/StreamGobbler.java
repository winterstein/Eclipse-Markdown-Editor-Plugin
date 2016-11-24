package winterwell.markdown.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class StreamGobbler extends Thread {

	private boolean echo;
	private IOException ex;
	private final InputStream is;
	private boolean stopFlag;
	private StringBuffer stringBuffer;

	public StreamGobbler(InputStream is) {
		super((new StringBuilder("gobbler:")).append(is.toString()).toString());
		setDaemon(true);
		this.is = is;
		stringBuffer = new StringBuffer();
	}

	public void clearString() {
		stringBuffer = new StringBuffer();
	}

	public String getString() throws IOException {
		if (ex != null) throw new IOException(ex);
		return stringBuffer.toString();
	}

	public boolean hasText() {
		return stringBuffer.length() != 0;
	}

	public void pleaseStop() {
		try {
			is.close();
		} catch (IOException e) {}
		stopFlag = true;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while (!stopFlag) {
				int ich = br.read();
				if (ich == -1) {
					break;
				}
				char ch = (char) ich;
				stringBuffer.append(ch);
				if (echo) {
					System.out.print(ch);
				}
			}
		} catch (IOException ioe) {
			if (stopFlag) {
				return;
			}
			ioe.printStackTrace();
			ex = ioe;
		}
	}

	public void setEcho(boolean echo) {
		this.echo = echo;
	}

	public String toString() {
		return (new StringBuilder("StreamGobbler:")).append(stringBuffer.toString()).toString();
	}
}
