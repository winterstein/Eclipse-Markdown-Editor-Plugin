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
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;

import winterwell.markdown.Activator;

public class MarkdownPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, Prefs {

	public MarkdownPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
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
		addField(new ComboFieldEditor(PREF_CSS_DEFAULT, "Default Stylesheet", builtins(), parent));
		addField(new FileFieldEditor(PREF_CSS_CUSTOM, "Custom Stylesheet", parent));

		// Github Syntax support
		addField(new BooleanFieldEditor(PREF_GITHUB_SYNTAX, "Support Github Syntax", parent));

		// Multi-Markdown support
		addField(new BooleanFieldEditor(PREF_MULTIMARKDOWN_METADATA, "Support Multi-Markdown Metadata", parent));
	}

	// build list of builtin stylesheets
	// key=name, value=bundle cache URL as string
	private String[][] builtins() {
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

	public void init(IWorkbench workbench) {}

	public static boolean wordWrap() {
		IPreferenceStore pStore = Activator.getDefault().getPreferenceStore();
		if (!pStore.contains(PREF_WORD_WRAP)) {
			return false;
		}
		return pStore.getBoolean(PREF_WORD_WRAP);
	}
}
