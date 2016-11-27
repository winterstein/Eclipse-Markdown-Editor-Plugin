package winterwell.markdown.spelling;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.ui.editors.text.EditorsUI;

/**
 * A settable IStatus.
 * Can be an error, warning, info or ok. For error, info and warning states,
 * a message describes the problem.
 *
 * @since 2.1
 */
class StatusInfo implements IStatus {

	/** The message of this status.  */
	private String fStatusMessage;
	/** The severity of this status. */
	private int fSeverity;

	/**
	 * Creates a status set to OK (no message).
	 */
	public StatusInfo() {
		this(OK, null);
	}

	/**
	 * Creates a status with the given severity and message.
	 *
	 * @param severity the severity of this status: ERROR, WARNING, INFO and OK.
	 * @param message the message of this status. Applies only for ERROR,
	 * WARNING and INFO.
	 */
	public StatusInfo(int severity, String message) {
		fStatusMessage= message;
		fSeverity= severity;
	}

	@Override
	public boolean isOK() {
		return fSeverity == IStatus.OK;
	}

	/**
	 * Returns whether this status indicates a warning.
	 *
	 * @return <code>true</code> if this status has severity
	 *    {@link IStatus#WARNING} and <code>false</code> otherwise
	 */
	public boolean isWarning() {
		return fSeverity == IStatus.WARNING;
	}

	/**
	 * Returns whether this status indicates an info.
	 *
	 * @return <code>true</code> if this status has severity
	 *    {@link IStatus#INFO} and <code>false</code> otherwise
	 */
	public boolean isInfo() {
		return fSeverity == IStatus.INFO;
	}

	/**
	 * Returns whether this status indicates an error.
	 *
	 * @return <code>true</code> if this status has severity
	 *    {@link IStatus#ERROR} and <code>false</code> otherwise
	 */
	public boolean isError() {
		return fSeverity == IStatus.ERROR;
	}

	@Override
	public String getMessage() {
		return fStatusMessage;
	}

	/**
	 * Sets the status to ERROR.
	 *
	 * @param errorMessage the error message which can be an empty string, but not <code>null</code>
	 */
	public void setError(String errorMessage) {
		Assert.isNotNull(errorMessage);
		fStatusMessage= errorMessage;
		fSeverity= IStatus.ERROR;
	}

	/**
	 * Sets the status to WARNING.
	 *
	 * @param warningMessage the warning message which can be an empty string, but not <code>null</code>
	 */
	public void setWarning(String warningMessage) {
		Assert.isNotNull(warningMessage);
		fStatusMessage= warningMessage;
		fSeverity= IStatus.WARNING;
	}

	/**
	 * Sets the status to INFO.
	 *
	 * @param infoMessage the info message which can be an empty string, but not <code>null</code>
	 */
	public void setInfo(String infoMessage) {
		Assert.isNotNull(infoMessage);
		fStatusMessage= infoMessage;
		fSeverity= IStatus.INFO;
	}

	/**
	 * Sets the status to OK.
	 */
	public void setOK() {
		fStatusMessage= null;
		fSeverity= IStatus.OK;
	}

	@Override
	public boolean matches(int severityMask) {
		return (fSeverity & severityMask) != 0;
	}

	/**
	 * Returns always <code>false</code>.
	 *
	 * @see IStatus#isMultiStatus()
	 */
	@Override
	public boolean isMultiStatus() {
		return false;
	}

	@Override
	public int getSeverity() {
		return fSeverity;
	}

	@Override
	public String getPlugin() {
		return EditorsUI.PLUGIN_ID;
	}

	/**
	 * Returns always <code>null</code>.
	 *
	 * @see IStatus#getException()
	 */
	@Override
	public Throwable getException() {
		return null;
	}

	/**
	 * Returns always the error severity.
	 *
	 * @see IStatus#getCode()
	 */
	@Override
	public int getCode() {
		return fSeverity;
	}

	/**
	 * Returns always <code>null</code>.
	 *
	 * @see IStatus#getChildren()
	 */
	@Override
	public IStatus[] getChildren() {
		return new IStatus[0];
	}

}
