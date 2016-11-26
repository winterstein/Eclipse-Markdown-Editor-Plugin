package winterwell.markdown.preferences;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.Bundle;

import winterwell.markdown.MarkdownUIPlugin;

/**
 * Initialize default preference values
 */
public class PrefsInit extends AbstractPreferenceInitializer implements Prefs {

	private static final RGB DEF_DEFAULT = new RGB(0, 0, 0);
	private static final RGB DEF_COMMENT = new RGB(128, 0, 0);
	private static final RGB DEF_HEADER = new RGB(0, 128, 0);
	private static final RGB DEF_LINK = new RGB(106, 131, 199);
	private static final RGB DEF_CODE = new RGB(0, 0, 0);
	private static final RGB DEF_CODE_BG = new RGB(244, 244, 244);

	public void initializeDefaultPreferences() {
		IPreferenceStore store = MarkdownUIPlugin.getDefault().getPreferenceStore();

		store.setDefault(PREF_WORD_WRAP, false);
		store.setDefault(PREF_FOLDING, true);
		store.setDefault(PREF_TASK_TAGS, true);
		store.setDefault(PREF_TASK_TAGS_DEFINED, "TODO,FIXME,??");

		store.setDefault(PREF_MD_CONVERTER, KEY_MARDOWNJ);
		store.setDefault(PREF_TXTMARK_SAFEMODE, false);
		store.setDefault(PREF_TXTMARK_EXTENDED, true);

		store.setDefault(PREF_EXTERNAL_COMMAND, "");
		store.setDefault(PREF_SECTION_NUMBERS, true);

		store.setDefault(PREF_CSS_DEFAULT, cssDefault());
		store.setDefault(PREF_CSS_CUSTOM, "");
		store.setDefault(PREF_GITHUB_SYNTAX, true);
		store.setDefault(PREF_MULTIMARKDOWN_METADATA, false);

		store.setDefault(PREF_SPELLING_ENABLED, true);

		PreferenceConverter.setDefault(store, PREF_DEFAULT, DEF_DEFAULT);
		PreferenceConverter.setDefault(store, PREF_COMMENT, DEF_COMMENT);
		PreferenceConverter.setDefault(store, PREF_HEADER, DEF_HEADER);
		PreferenceConverter.setDefault(store, PREF_LINK, DEF_LINK);
		PreferenceConverter.setDefault(store, PREF_CODE, DEF_CODE);
		PreferenceConverter.setDefault(store, PREF_CODE_BG, DEF_CODE_BG);
	}

	// create bundle cache URL for the default stylesheet
	private String cssDefault() {
		Bundle bundle = Platform.getBundle(MarkdownUIPlugin.PLUGIN_ID);
		URL url = FileLocator.find(bundle, new Path("resources/" + DEF_MDCSS), null);
		try {
			url = FileLocator.toFileURL(url);
			return url.toURI().toString();
		} catch (IOException | URISyntaxException e) {}
		return DEF_MDCSS; // really an error
	}
}
