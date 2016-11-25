package winterwell.markdown.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import winterwell.markdown.MarkdownUIPlugin;

public class PrefPageColoring extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, Prefs {

	public PrefPageColoring() {
		super(GRID);
		setDescription("");
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(MarkdownUIPlugin.getDefault().getPreferenceStore());
	}

	/** Creates the field editors. */
	@Override
	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		Group frame = new Group(parent, SWT.NONE);
		GridDataFactory.fillDefaults().indent(0, 6).grab(true, false).span(2, 1).applyTo(frame);
		GridLayoutFactory.fillDefaults().margins(6, 6).applyTo(frame);
		frame.setText("Highlight Elements");

		Composite internal = new Composite(frame, SWT.NONE);
		GridDataFactory.fillDefaults().indent(0, 4).grab(true, false).applyTo(internal);
		GridLayoutFactory.fillDefaults().applyTo(internal);

		addField(new ColorFieldEditor(PREF_DEFAULT, "Default text", internal));
		addField(new ColorFieldEditor(PREF_COMMENT, "Comment", internal));
		addField(new ColorFieldEditor(PREF_LINK, "Link", internal));
		addField(new ColorFieldEditor(PREF_HEADER, "Header and List indicator", internal));
		addField(new ColorFieldEditor(PREF_CODE, "Code", internal));
		addField(new ColorFieldEditor(PREF_CODE_BG, "Code Background", internal));
	}
}
