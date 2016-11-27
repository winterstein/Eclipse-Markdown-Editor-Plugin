package winterwell.markdown.spelling;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.preference.PreferencePage;

/**
 * Interface for preference configuration blocks which can either be wrapped by a
 * {@link org.eclipse.ui.internal.editors.text.AbstractConfigurationBlockPreferencePage} or be
 * included some preference page.
 * <p>
 * Clients may implement this interface.
 * </p>
 *
 * @since 3.0
 */
public interface IPreferenceConfigurationBlock {

	/**
	 * Creates the preference control.
	 *
	 * @param parent the parent composite to which to add the preferences control
	 * @return the control that was added to <code>parent</code>
	 */
	Control createControl(Composite parent);

	/**
	 * Called after creating the control. Implementations should load the preferences values and
	 * update the controls accordingly.
	 */
	void initialize();

	/**
	 * Called when the <code>OK</code> button is pressed on the preference page. Implementations
	 * should commit the configured preference settings into their form of preference storage.
	 */
	void performOk();

	/**
	 * Called when the <code>OK</code> button is pressed on the preference page. Implementations can
	 * abort the '<code>OK</code>' operation by returning <code>false</code>.
	 *
	 * @return <code>true</code> iff the '<code>OK</code>' operation can be performed
	 * @since 3.1
	 */
	boolean canPerformOk();

	/**
	 * Called when the <code>Defaults</code> button is pressed on the preference page.
	 * Implementation should reset any preference settings to their default values and adjust the
	 * controls accordingly.
	 */
	void performDefaults();

	/**
	 * Called when the preference page is being disposed. Implementations should free any resources
	 * they are holding on to.
	 */
	void dispose();

	/**
	 * Applies the given data.
	 * <p>
	 * It is up to the implementor to define whether it supports this and which kind of data it
	 * accepts.
	 * </p>
	 *
	 * @param data the data which is specified by each configuration block
	 * @see PreferencePage#applyData(Object)
	 * @since 3.4
	 */
	void applyData(Object data);
}
