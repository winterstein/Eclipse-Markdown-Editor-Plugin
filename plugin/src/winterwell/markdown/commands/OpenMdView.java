package winterwell.markdown.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import winterwell.markdown.LogUtil;

public class OpenMdView extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
	        IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
	        String mdViewId = "winterwell.markdown.views.MarkdownPreview";
	        IViewPart mdView = activePage.showView(mdViewId);
	        activePage.activate(mdView);
		} catch (PartInitException e) {
			showError(e);
	    } catch (Exception e) {
	    	showError(e);
	    }		
		return null;
	}

	private void showError(Exception e) {
		String title = "Exception while opening Markdown View";
		String message = title+" (winterwell.markdown.views.MarkdownPreview)"
				+"\nCheck Error Log View";
		LogUtil.error(message, e);
		MessageDialog.openError(Display.getDefault().getActiveShell(), title , message);
	}
}
