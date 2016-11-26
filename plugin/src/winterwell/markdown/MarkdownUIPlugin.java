package winterwell.markdown;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MarkdownUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "winterwell.markdown";

	// The shared instance
	private static MarkdownUIPlugin plugin;

	// preference store specific to this plugin
	// private IPreferenceStore store;

	public MarkdownUIPlugin() {
		super();
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 */
	public static MarkdownUIPlugin getDefault() {
		return plugin;
	}

	// /**
	// * Workaround to allow editor specific enabling of the speller. Overrides the global
	// preference
	// * value for spelling enabled.
	// */
	// public IPreferenceStore getPreferenceStore() {
	// if (store == null) {
	// OverlayKey[] keys = new OverlayKey[] {
	// new OverlayKey(OverlayPreferenceStore.BOOLEAN, SpellingService.PREFERENCE_SPELLING_ENABLED)
	// };
	// this.store = new OverlayPreferenceStore(super.getPreferenceStore(), keys);
	// this.store.setValue(SpellingService.PREFERENCE_SPELLING_ENABLED, true);
	// }
	// return store;
	// }
}
