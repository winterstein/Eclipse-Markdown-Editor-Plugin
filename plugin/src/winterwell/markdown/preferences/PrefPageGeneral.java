package winterwell.markdown.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import winterwell.markdown.Activator;

public class PrefPageGeneral extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, Prefs {

	public PrefPageGeneral() {
		super(GRID);
		setDescription("");
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
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
		GridLayoutFactory.fillDefaults().applyTo(internal);

		// Word wrap
		addField(new BooleanFieldEditor(PREF_WORD_WRAP, "Soft word wrapping", internal));

		// Code folding
		addField(new BooleanFieldEditor(PREF_FOLDING, "Document folding", internal));

		// Task tags
		addField(new BooleanFieldEditor(PREF_TASK_TAGS, "Manage tasks using task tags ", internal));
		new Label(internal, SWT.NONE).setText(" -- add and delete tags in sync with edits");
		new Label(internal, SWT.NONE);

		addField(new StringFieldEditor(PREF_TASK_TAGS_DEFINED, "Task tags", internal));

		// External cli
		addField(new StringFieldEditor(PREF_MARKDOWN_COMMAND, "External Markdown Run Command", internal));
		new Label(internal, SWT.NONE).setText(" -- UNSTABLE: blank to use builtin converter");
		new Label(internal, SWT.NONE);

	}
}
