package winterwell.markdown.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import winterwell.markdown.MarkdownUI;

public class PrefPageGeneral extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, Prefs {

	private String[][] converters;
	private Group txtGroup;
	private Group extGroup;

	private BooleanFieldEditor safeMode;
	private BooleanFieldEditor extended;
	private StringFieldEditor extField;

	private class ComboFieldEditor2 extends ComboFieldEditor {

		public ComboFieldEditor2(String name, String labelText, String[][] entryNamesAndValues, Composite parent) {
			super(name, labelText, entryNamesAndValues, parent);
		}

		@Override
		protected void fireValueChanged(String property, Object oldValue, Object newValue) {
			super.fireValueChanged(property, oldValue, newValue);
			update((String) newValue);
		}
	}

	public PrefPageGeneral() {
		super(GRID);
		setDescription("");
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(MarkdownUI.getDefault().getPreferenceStore());
	}

	/** Create fields controlling general editor behavior */
	@Override
	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		Group frame = new Group(parent, SWT.NONE);
		GridDataFactory.fillDefaults().indent(0, 6).grab(true, false).applyTo(frame);
		GridLayoutFactory.fillDefaults().margins(6, 6).applyTo(frame);
		frame.setText("General");

		Composite internal = new Composite(frame, SWT.NONE);
		GridDataFactory.fillDefaults().indent(0, 4).grab(true, false).applyTo(internal);

		// Word wrap
		addField(new BooleanFieldEditor(PREF_WORD_WRAP, "Soft word wrapping", internal));

		// Code folding
		addField(new BooleanFieldEditor(PREF_FOLDING, "Document folding", internal));

		// Task tags
		addField(new BooleanFieldEditor(PREF_TASK_TAGS, "Use task tags ", internal));
		addField(new StringFieldEditor(PREF_TASK_TAGS_DEFINED, "Task tags defined:", internal));

		// Converter selection
		addField(new ComboFieldEditor2(PREF_MD_CONVERTER, "Markdown Converter:", converters(), internal));

		// Converter related options
		txtGroup = new Group(internal, SWT.NONE);
		txtGroup.setText("TxtMark Options");

		GridDataFactory.fillDefaults().indent(2, 6).grab(true, false).span(2, 1).applyTo(txtGroup);
		GridLayoutFactory.fillDefaults().margins(6, 6).applyTo(txtGroup);

		safeMode = new BooleanFieldEditor(PREF_TXTMARK_SAFEMODE, "Use safe mode", txtGroup);
		addField(safeMode);
		extended = new BooleanFieldEditor(PREF_TXTMARK_EXTENDED, "Use extended profile", txtGroup);
		addField(extended);

		// External cli
		extGroup = new Group(internal, SWT.NONE);
		extGroup.setText("External Run Command");

		GridDataFactory.fillDefaults().indent(2, 6).grab(true, false).span(2, 1).applyTo(extGroup);
		GridLayoutFactory.fillDefaults().margins(6, 6).applyTo(extGroup);

		extField = new StringFieldEditor(PREF_EXTERNAL_COMMAND, "", extGroup);

		((GridLayout) internal.getLayout()).numColumns = 2;
		update(getPreferenceStore().getString(PREF_MD_CONVERTER)); // init visibility
	}

	private void update(String value) {
		switch (value) {
			case KEY_TXTMARK:
				safeMode.setEnabled(true, txtGroup);
				extended.setEnabled(true, txtGroup);
				extField.setEnabled(false, extGroup);
				break;
			case KEY_USE_EXTERNAL:
				safeMode.setEnabled(false, txtGroup);
				extended.setEnabled(false, txtGroup);
				extField.setEnabled(true, extGroup);
				break;
			default:
				safeMode.setEnabled(false, txtGroup);
				extended.setEnabled(false, txtGroup);
				extField.setEnabled(false, extGroup);
		}
	}

	private String[][] converters() {
		if (converters == null) {
			converters = new String[5][2];
			converters[0][0] = "MarkdownJ";
			converters[0][1] = KEY_MARDOWNJ;
			converters[1][0] = "Commonmark";
			converters[1][1] = KEY_COMMONMARK;
			converters[2][0] = "PegDown";
			converters[2][1] = KEY_PEGDOWN;
			converters[3][0] = "TxtMark";
			converters[3][1] = KEY_TXTMARK;
			converters[4][0] = "External converter";
			converters[4][1] = KEY_USE_EXTERNAL;
		}
		return converters;
	}
}
