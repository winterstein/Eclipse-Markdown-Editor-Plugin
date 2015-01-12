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
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		doInstall();
		MarkdownPreferencePage.setDefaultPreferences(getPreferenceStore());
	}

	// ?? Have this method called by start(), saving a reminder so it doesn't repeat itself?? 
	private void doInstall() {
		// ??Try to make this the default for file types -- but is this possible??
		// c.f. http://stackoverflow.com/questions/15877123/eclipse-rcp-programmatically-associate-file-type-with-editor
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
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
