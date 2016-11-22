package winterwell.markdown;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import winterwell.markdown.preferences.MarkdownPreferencePage;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "winterwell.markdown";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		MarkdownPreferencePage.setDefaultPreferences(getPreferenceStore());
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
