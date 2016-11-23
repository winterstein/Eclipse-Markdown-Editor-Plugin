package winterwell.markdown.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;

import winterwell.markdown.Activator;

public class PrefPageStyles extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, Prefs {

	public PrefPageStyles() {
		super(GRID);
		setDescription("");
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/** Creates the field editors. */
	@Override
	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		Group frame = new Group(parent, SWT.NONE);
		GridDataFactory.fillDefaults().indent(0, 6).grab(true, false).span(2, 1).applyTo(frame);
		GridLayoutFactory.fillDefaults().numColumns(3).margins(6, 6).applyTo(frame);
		frame.setText("Stylesheets");

		Composite internal = new Composite(frame, SWT.NONE);
		GridDataFactory.fillDefaults().indent(0, 4).grab(true, false).applyTo(internal);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(internal);

		// Github Syntax support
		addField(new BooleanFieldEditor(PREF_GITHUB_SYNTAX, "Support Github Syntax", internal));

		// Multi-Markdown support
		addField(new BooleanFieldEditor(PREF_MULTIMARKDOWN_METADATA, "Support Multi-Markdown Metadata", internal));

		// Browser CSS
		addField(new ComboFieldEditor(PREF_CSS_DEFAULT, "Default Stylesheet", builtins(), internal));
		addField(new FileFieldEditor(PREF_CSS_CUSTOM, "Custom Stylesheet", internal));
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
}
