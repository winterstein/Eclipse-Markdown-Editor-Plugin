package winterwell.markdown.views;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import winterwell.markdown.MarkdownUI;
import winterwell.markdown.preferences.Prefs;

/**
 * Rate limits view updates by ignoring all but the last update trigger received within a limit
 * window.
 * <p>
 * A trigger received outside of an active limit window causes an immediate update and the opening
 * of a an active limit window.
 * <p>
 * All triggers received within an active limit window causes, on termination of that active limit
 * window, a single update and the start of a new active limit window.
 * <p>
 * If no trigger is received within the an active limit window, no terminal update is performed and
 * no new active limit window is opened.
 */
public class Limiter extends Thread {

	private MarkdownPreview view;
	private final Display display;
	private final int delay;

	private boolean running;
	private long start;
	private long mark;

	public Limiter(MarkdownPreview view) {
		super("Limiter");
		this.view = view;
		this.display = PlatformUI.getWorkbench().getDisplay();
		this.delay = MarkdownUI.getDefault().getPreferenceStore().getInt(Prefs.PREF_UPDATE_DELAY) * 1000;
	}

	public void dispose() {
		this.view = null;
	}

	public void trigger() {
		if (view != null) {
			if (!running) {
				startTimer();
			} else {
				mark(System.currentTimeMillis());
			}
		}
	}

	private synchronized void startTimer() {
		running = true;
		doUpdate();

		try {
			display.asyncExec(new Runnable() {

				public void run() {
					while (running) {
						start = System.currentTimeMillis();
						mark(start);
						while (System.currentTimeMillis() < start + delay) {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {}
						}
						if (mark > start) {	// triggered during window
							doUpdate();
						} else {			// no trigger, so close window
							running = false;
						}
					}
				}
			});
		} catch (SWTException e) {}
	}

	private synchronized void mark(long mark) {
		this.mark = System.currentTimeMillis();
	}

	// asynchronous callback to perform the actual update
	private void doUpdate() {
		display.asyncExec(new Runnable() {

			public void run() {
				if (view != null) view.update();
			}
		});
	}
}
