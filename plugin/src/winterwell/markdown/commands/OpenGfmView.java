package winterwell.markdown.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import winterwell.markdown.LogUtil;

public class OpenGfmView extends AbstractHandler {

	private static final String GFM_VIEW_ID = "code.satyagraha.gfm.viewer.views.GfmView";
	private static final String GFM_SYMBOLIC_NAME = "code.satyagraha.gfm.viewer.plugin";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			Bundle gfmBundle = Platform.getBundle(GFM_SYMBOLIC_NAME);
			if (gfmBundle == null) {
				showInstallWarning();
			} else {
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart gfmView = activePage.showView(GFM_VIEW_ID);
				activePage.activate(gfmView);
			}
		} catch (PartInitException e) {
			showError(e);
		} catch (Exception e) {
			showError(e);
		}
		return null;
	}

	private void showInstallWarning() {
		String title = "GFM viewer was not found";
		String message = title + " (" + GFM_SYMBOLIC_NAME + ")"
				+ "\n\nPlease ensure that you have correctly installed the plugin, see https://github.com/satyagraha/gfm_viewer";
		MessageDialog.openWarning(Display.getDefault().getActiveShell(), title, message);
	}

	private void showError(Exception e) {
		String title = "Exception while opening GitHub Flavored Markdown View";
		String message = title + " (" + GFM_VIEW_ID + ")"
				+ "\nCheck Error Log View and continue at https://github.com/winterstein/Eclipse-Markdown-Editor-Plugin/issues/42"
				+ "\n\nYou can also right-click file in Project Explorer"
				+ "\n and select \"Show in GFM view\".";
		LogUtil.error(message, e);
		MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);
	}
}
