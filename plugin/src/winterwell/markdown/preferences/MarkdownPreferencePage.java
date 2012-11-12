package winterwell.markdown.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.editors.text.TextEditorPreferencePage;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import winterwell.markdown.Activator;
import winterwell.markdown.editors.MarkdownEditor;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class MarkdownPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public static final String PREF_FOLDING = "Pref_Folding";
	public static final String PREF_WORD_WRAP = "Pref_WordWrap";
	public static final String PREF_TASK_TAGS = "Pref_TaskTagsOn";
	public static final String PREF_TASK_TAGS_DEFINED = "Pref_TaskTags";
	public static final String PREF_SECTION_NUMBERS = "Pref_SectionNumbers";

	public static final String PREF_MARKDOWN_COMMAND = "Pref_Markdown_Command";
	private static final String MARKDOWNJ = "(use built-in MarkdownJ converter)";
	
	public MarkdownPreferencePage() {
		super(GRID);
		IPreferenceStore pStore = Activator.getDefault().getPreferenceStore();
		pStore.setDefault(PREF_WORD_WRAP, false);
		pStore.setDefault(PREF_FOLDING, true);
		pStore.setDefault(PREF_TASK_TAGS, true);
		pStore.setDefault(PREF_TASK_TAGS_DEFINED, "TODO,FIXME,??");
		pStore.setDefault(PREF_MARKDOWN_COMMAND, MARKDOWNJ);
		pStore.setDefault(PREF_SECTION_NUMBERS, true);
		setPreferenceStore(pStore);
		setDescription("Settings for the Markdown text editor. See also the general text editor preferences.");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {
		// Word wrap
		BooleanFieldEditor fd = new BooleanFieldEditor(PREF_WORD_WRAP,
				"Soft word wrapping \r\n"
+"Note: may cause line numbers and related \r\n" +
		"functionality to act a bit strangely",
				getFieldEditorParent());
		addField(fd);
		// Task tags
		fd = new BooleanFieldEditor(PREF_TASK_TAGS,
				"Manage tasks using task tags \r\n" +
				"If true, this will add and delete tags in sync with edits.",
				getFieldEditorParent());
		addField(fd);		
		StringFieldEditor tags = new StringFieldEditor(PREF_TASK_TAGS_DEFINED,
				"Task tags\nComma separated list of recognised task tags.", getFieldEditorParent());
		addField(tags);
		// Code folding
		fd = new BooleanFieldEditor(PREF_FOLDING,
				"Document folding, a.k.a. outline support",
				getFieldEditorParent());
		addField(fd);
		// Command line		
//		addField(new DummyField() {
//			protected void makeComponent(Composite parent) {
//				Label label = new Label(parent, 0);
//				label.setText("Hello!");
//				GridData gd = new GridData(100, 20);
//				label.setLayoutData(gd);
//			}			
//		});
		StringFieldEditor cmd = new StringFieldEditor(PREF_MARKDOWN_COMMAND,
				"UNSTABLE: Command-line to run Markdown.\r\n" +
				"This should take in a file and output to std-out.\n" +
				"Leave blank to use the built-in Java converter.", getFieldEditorParent());		
		addField(cmd);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		
	}

	public static boolean wordWrap() {
		IPreferenceStore pStore = Activator.getDefault().getPreferenceStore();
		if (! pStore.contains(MarkdownPreferencePage.PREF_WORD_WRAP)) {
			return false;
		}
		return pStore.getBoolean(MarkdownPreferencePage.PREF_WORD_WRAP);		
	}
	
}

abstract class DummyField extends FieldEditor {
	@Override
	protected void adjustForNumColumns(int numColumns) {
		// do nothing
	}
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		makeComponent(parent);
	}
	abstract protected void makeComponent(Composite parent);
	
	@Override
	protected void doLoad() {
		// 
	}
	@Override
	protected void doLoadDefault() {
		// 
	}

	@Override
	protected void doStore() {
		// 
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}
	
}