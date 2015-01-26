package winterwell.markdown.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import winterwell.markdown.LogUtil;

public class OpenGfmView extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
	        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	        String gfmViewId = "code.satyagraha.gfm.viewer.views.GfmView";
	        IViewPart gfmView = activePage.showView(gfmViewId);
	        activePage.activate(gfmView);
		} catch (PartInitException e) {
			showError(e);
	    } catch (Exception e) {
	    	showError(e);
	    }		
		return null;
	}

	private void showError(Exception e) {
		String title = "Exception while opening GitHub Flavored Markdown View";
		String message = title+" (code.satyagraha.gfm.viewer.views.GfmView)"
				+"\nCheck Error Log View and continue at https://github.com/winterstein/Eclipse-Markdown-Editor-Plugin/issues/42"
				+"\n\nYou can also right-click file in Project Explorer"
				+"\n and select \"Show in GFM view\".";
		LogUtil.error(message, e);
		MessageDialog.openError(Display.getDefault().getActiveShell(), title , message);
	}
}
