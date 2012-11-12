package winterwell.markdown.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.internal.editors.text.PreviousPulldownActionDelegate;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

import winterwell.markdown.views.MarkdownPreview;

public class ActionBarContributor extends TextEditorActionContributor {
	
	private static IEditorPart activeEditor = null;

//	IAction print = new PrintAction();

	public void setActiveEditor(IEditorPart targetEditor) {
		super.setActiveEditor(targetEditor);
		activeEditor  = targetEditor;
		// add print action
		IActionBars bars= getActionBars();
	    if (bars != null) {
//	    	todo bars.setGlobalActionHandler(ActionFactory.PRINT.getId(), print);
//	    	bars.updateActionBars();
	    }
	    // Update preview?
		if (MarkdownPreview.preview != null) {
			MarkdownPreview.preview.update();
		}
	}
	public static IEditorPart getActiveEditor() {
		return activeEditor;
	}
	
	@Override
	public void contributeToMenu(IMenuManager menu) {	
		super.contributeToMenu(menu);
		// Add format action
		IMenuManager edit = menu.findMenuUsingPath("edit");
		if (edit != null) {
			edit.add(new FormatAction());
		}
		// Add Export action
		IMenuManager file = menu.findMenuUsingPath("file");
		if (file != null) {
			file.appendToGroup("import.ext", new ExportHTMLAction());
		}		
	}

}
