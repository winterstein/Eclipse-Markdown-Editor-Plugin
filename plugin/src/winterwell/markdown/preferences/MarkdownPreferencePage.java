package winterwell.markdown.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;

import winterwell.markdown.Activator;

public class MarkdownPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, Prefs {

	private static final String MARKDOWNJ = "(use built-in MarkdownJ converter)";

	private static final RGB DEF_DEFAULT = new RGB(0, 0, 0);
	private static final RGB DEF_COMMENT = new RGB(128, 0, 0);
	private static final RGB DEF_HEADER = new RGB(0, 128, 0);
	private static final RGB DEF_LINK = new RGB(106, 131, 199);
	private static final RGB DEF_CODE = new RGB(0, 0, 0);
	private static final RGB DEF_CODE_BG = new RGB(244, 244, 244);

	private static final String[][] builtins = builtins();

	public MarkdownPreferencePage() {
		super(GRID);
		IPreferenceStore pStore = Activator.getDefault().getPreferenceStore();
		setDefaultPreferences(pStore);
		setPreferenceStore(pStore);
		setDescription("Settings for the Markdown text editor. See also the general text editor preferences.");
	}

	/** Creates the field editors. */
	@Override
	public void createFieldEditors() {

		Composite parent = getFieldEditorParent();

		/* Fields for editor window */

		// Word wrap
		addField(new BooleanFieldEditor(PREF_WORD_WRAP, "Soft word wrapping \r\n"
				+ "Note: may cause line numbers and related \r\n" + "functionality to act a bit strangely", parent));

		// Task tags
		addField(new BooleanFieldEditor(PREF_TASK_TAGS,
				"Manage tasks using task tags \r\n" + "If true, this will add and delete tags in sync with edits.",
				parent));
		addField(new StringFieldEditor(PREF_TASK_TAGS_DEFINED,
				"Task tags\nComma separated list of recognised task tags.", parent));

		// Code folding
		addField(new BooleanFieldEditor(PREF_FOLDING, "Document folding, a.k.a. outline support", parent));

		addField(new StringFieldEditor(PREF_MARKDOWN_COMMAND,
				"UNSTABLE: Command-line to run Markdown.\r\n" + "This should take in a file and output to std-out.\n"
						+ "Leave blank to use the built-in Java converter.",
				parent));

		addField(new ColorFieldEditor(PREF_DEFAULT, "Default text", parent));
		addField(new ColorFieldEditor(PREF_COMMENT, "Comment", parent));
		addField(new ColorFieldEditor(PREF_LINK, "Link", parent));
		addField(new ColorFieldEditor(PREF_HEADER, "Header and List indicator", parent));
		addField(new ColorFieldEditor(PREF_CODE, "Code", parent));
		addField(new ColorFieldEditor(PREF_CODE_BG, "Code Background", parent));

		/* Fields for preview window */

		// Browser CSS
		addField(new ComboFieldEditor(PREF_CSS_DEFAULT, "Default Stylesheet", builtins, parent));
		addField(new FileFieldEditor(PREF_CSS_CUSTOM, "Custom Stylesheet", parent));

		// Github Syntax support
		addField(new BooleanFieldEditor(PREF_GITHUB_SYNTAX, "Support Github Syntax", parent));

		// Multi-Markdown support
		addField(new BooleanFieldEditor(PREF_MULTIMARKDOWN_METADATA, "Support Multi-Markdown Metadata", parent));
	}

	public void init(IWorkbench workbench) {}

	public static void setDefaultPreferences(IPreferenceStore pStore) {
		pStore.setDefault(PREF_WORD_WRAP, false);
		pStore.setDefault(PREF_FOLDING, true);
		pStore.setDefault(PREF_TASK_TAGS, true);
		pStore.setDefault(PREF_TASK_TAGS_DEFINED, "TODO,FIXME,??");

		pStore.setDefault(PREF_MARKDOWN_COMMAND, MARKDOWNJ);
		pStore.setDefault(PREF_SECTION_NUMBERS, true);

		pStore.setDefault(PREF_CSS_DEFAULT, DEF_MDCSS);
		pStore.setDefault(PREF_CSS_CUSTOM, "");
		pStore.setDefault(PREF_GITHUB_SYNTAX, true);
		pStore.setDefault(PREF_MULTIMARKDOWN_METADATA, false);

		PreferenceConverter.setDefault(pStore, PREF_DEFAULT, DEF_DEFAULT);
		PreferenceConverter.setDefault(pStore, PREF_COMMENT, DEF_COMMENT);
		PreferenceConverter.setDefault(pStore, PREF_HEADER, DEF_HEADER);
		PreferenceConverter.setDefault(pStore, PREF_LINK, DEF_LINK);
		PreferenceConverter.setDefault(pStore, PREF_CODE, DEF_CODE);
		PreferenceConverter.setDefault(pStore, PREF_CODE_BG, DEF_CODE_BG);
	}

	public static boolean wordWrap() {
		IPreferenceStore pStore = Activator.getDefault().getPreferenceStore();
		if (!pStore.contains(MarkdownPreferencePage.PREF_WORD_WRAP)) {
			return false;
		}
		return pStore.getBoolean(MarkdownPreferencePage.PREF_WORD_WRAP);
	}

	// build list of builtin stylesheets
	// key=name, value=bundle cache URL as string
	private static String[][] builtins() {
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL url = bundle.getEntry("resources/");
		File dir = null;
		try {
			url = FileLocator.toFileURL(url); // extracts to bundle cache
			dir = new File(url.toURI());
		} catch (IOException | URISyntaxException e) {
			String[][] values = new String[1][2];
			values[0][0] = "<invalid resources/ >";
			values[0][1] = "";
			return values;
		}
		List<String> cssNames = new ArrayList<>();
		if (dir.isDirectory()) {
			for (String name : dir.list()) {
				if (name.endsWith("." + CSS)) {
					cssNames.add(name);
				}
			}
		}

		String[][] values = new String[cssNames.size()][2];
		for (int idx = 0; idx < cssNames.size(); idx++) {
			String cssName = cssNames.get(idx);
			values[idx][0] = cssName;
			try {
				values[idx][1] = url.toURI().resolve(cssName).toString();
			} catch (URISyntaxException e) {
				values[idx][0] = cssName + " <invalid>";
			}
		}
		return values;
	}

}
