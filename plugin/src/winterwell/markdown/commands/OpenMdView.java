package winterwell.markdown.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import winterwell.markdown.Log;
import winterwell.markdown.MarkdownUIPlugin;
import winterwell.markdown.views.MarkdownPreview;

public class OpenMdView extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
			IViewPart mdView = activePage.showView(MarkdownPreview.ID);
			activePage.activate(mdView);
		} catch (Exception e) {
			showError(event, e);
		}
		return null;
	}

	private void showError(ExecutionEvent event, Exception e) {
		String title = "Exception while opening Markdown Preview";
		String message = title + " (" + MarkdownPreview.ID + ")" + "\nCheck Error Log";
		Log.error(message, e);

		Shell shell = HandlerUtil.getActiveShell(event);
		IStatus status = new Status(IStatus.ERROR, MarkdownUIPlugin.PLUGIN_ID, message, e);
		ErrorDialog.openError(shell, title, message, status, 0);
	}
}
