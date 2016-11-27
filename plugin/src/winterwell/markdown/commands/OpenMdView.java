package winterwell.markdown.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import winterwell.markdown.Log;
import winterwell.markdown.views.MarkdownPreview;

public class OpenMdView extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
			IViewPart mdView = activePage.showView(MarkdownPreview.ID);
			MarkdownPreview preview = (MarkdownPreview) mdView;
			activePage.activate(preview);
		} catch (PartInitException e) {
			showError(e);
		}
		return null;
	}

	private void showError(Exception e) {
		String title = "Exception while opening Markdown Preview";
		String message = title + " (" + MarkdownPreview.ID + ")";
		Log.error(message, e);

		Shell shell = Display.getDefault().getActiveShell();
		MessageDialog.openError(shell, title , message);
	}
}
