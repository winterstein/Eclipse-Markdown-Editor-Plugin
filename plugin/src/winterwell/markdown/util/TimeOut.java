package winterwell.markdown.util;

import java.util.Timer;
import java.util.TimerTask;

public final class TimeOut {

	private static final Timer timer = new Timer("TimeOuter", true);
	private TimerTask task;
	private final long timeout;

	public TimeOut(long timeout) {
		this.timeout = timeout;
		start();
	}

	public void cancel() {
		task.cancel();
	}

	private void start() {
		if (task != null) {
			return;
		} else {
			final Thread target = Thread.currentThread();
			task = new TimerTask() {

				public void run() {
					target.interrupt();
				}
			};
			timer.schedule(task, timeout);
			return;
		}
	}
}
