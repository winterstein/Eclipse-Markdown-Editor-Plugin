package winterwell.markdown.spelling;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;

/**
 * A utility class to work with IStatus.
 *
 * @since 3.1
 */
public class StatusUtil {

	/**
	 * Compares two instances of {@link IStatus}. The more severe is returned: An error is more
	 * severe than a warning, and a warning is more severe than OK. If the two statuses have the
	 * same severity, the second is returned.
	 *
	 * @param s1 a status object
	 * @param s2 a status object
	 * @return the more severe status
	 */
	public static IStatus getMoreSevere(IStatus s1, IStatus s2) {
		if (s1.getSeverity() > s2.getSeverity()) return s1;

		return s2;
	}

	/**
	 * Finds the most severe status from a array of statuses. An error is more severe than a
	 * warning, and a warning is more severe than OK.
	 *
	 * @param status an array with status objects
	 * @return the most severe status object
	 */
	public static IStatus getMostSevere(IStatus[] status) {
		IStatus max = null;
		for (int i = 0; i < status.length; i++) {
			IStatus curr = status[i];
			if (curr.matches(IStatus.ERROR)) {
				return curr;
			}
			if (max == null || curr.getSeverity() > max.getSeverity()) {
				max = curr;
			}
		}
		return max;
	}

	/**
	 * Applies the status to the status line of a dialog page.
	 *
	 * @param page the dialog page
	 * @param status the status
	 */
	public static void applyToStatusLine(DialogPage page, IStatus status) {
		String message = status.getMessage();
		switch (status.getSeverity()) {
			case IStatus.OK:
				page.setMessage(message, IMessageProvider.NONE);
				page.setErrorMessage(null);
				break;
			case IStatus.WARNING:
				page.setMessage(message, IMessageProvider.WARNING);
				page.setErrorMessage(null);
				break;
			case IStatus.INFO:
				page.setMessage(message, IMessageProvider.INFORMATION);
				page.setErrorMessage(null);
				break;
			default:
				if (message.length() == 0) {
					message = null;
				}
				page.setMessage(null);
				page.setErrorMessage(message);
				break;
		}
	}
}
