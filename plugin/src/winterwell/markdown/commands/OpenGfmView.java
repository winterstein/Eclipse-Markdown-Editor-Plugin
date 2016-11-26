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
import org.eclipse.ui.PlatformUI;

import winterwell.markdown.Log;
import winterwell.markdown.util.Strings;

public class OpenGfmView extends AbstractHandler {

	public static final String GFMV_ID = "code.satyagraha.gfm.viewer.views.GfmView";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart gfmView = activePage.showView(GFMV_ID);
			activePage.activate(gfmView);
		} catch (PartInitException e) {
			showError(e);
		}
		return null;
	}

	private void showError(Exception e) {
		String title = "Exception while opening GitHub Flavored Markdown View";
		String message = title + Strings.EOL2 //
				+ "The 'GitHub Flavored Markdown viewer' was not found." + Strings.EOL2 //
				+ "Install using the Eclipse Marketplace client " + Strings.EOL //
				+ "or from https://github.com/satyagraha/gfm_viewer";
		Log.error(message);

		Shell shell = Display.getDefault().getActiveShell();
		MessageDialog.openError(shell, title, message);
	}
}
