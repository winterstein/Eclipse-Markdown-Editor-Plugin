package winterwell.markdown;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MarkdownUI extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "winterwell.markdown";

	// The shared instance
	private static MarkdownUI plugin;

	private IPreferenceStore combinedStore;

	public MarkdownUI() {
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
	public static MarkdownUI getDefault() {
		return plugin;
	}

	/**
	 * Returns a chained preference store representing the combined values of the MarkdownUI,
	 * EditorsUI, and PlatformUI stores.
	 */
	public IPreferenceStore getCombinedPreferenceStore() {
		if (combinedStore == null) {
			List<IPreferenceStore> stores = new ArrayList<>();
			stores.add(getPreferenceStore()); // MarkdownUI store
			stores.add(EditorsUI.getPreferenceStore());
			stores.add(PlatformUI.getPreferenceStore());
			combinedStore = new WritableChainedPreferenceStore(stores.toArray(new IPreferenceStore[stores.size()]));
		}
		return combinedStore;
	}
}
