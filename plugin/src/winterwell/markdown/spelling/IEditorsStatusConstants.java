package winterwell.markdown.spelling;

/**
 * Defines plug-in-specific status codes.
 *
 * @see org.eclipse.core.runtime.IStatus#getCode()
 * @see org.eclipse.core.runtime.Status#Status(int, java.lang.String, int, java.lang.String,
 *      java.lang.Throwable)
 * @since 2.1
 */
public interface IEditorsStatusConstants {

	/**
	 * Status constant indicating that an internal error occurred. Value: <code>1001</code>
	 */
	public static final int INTERNAL_ERROR = 10001;

}
