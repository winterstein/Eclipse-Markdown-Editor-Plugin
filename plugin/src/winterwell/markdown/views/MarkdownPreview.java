package winterwell.markdown.views;


import java.io.File;
import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.ViewPart;

import winterwell.markdown.editors.ActionBarContributor;
import winterwell.markdown.editors.MarkdownEditor;
import winterwell.markdown.pagemodel.MarkdownPage;




public class MarkdownPreview extends ViewPart {
	
	public static MarkdownPreview preview = null;
	
	private Browser viewer = null;

	/**
	 * The constructor.
	 */
	public MarkdownPreview() {
		preview = this;
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new Browser(parent, SWT.MULTI); // | SWT.H_SCROLL | SWT.V_SCROLL
	}




	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		if (viewer==null) return;
		viewer.setFocus();
		update();
	}

	public void update() {
		if (viewer==null) return;
		try {
			IEditorPart editor = ActionBarContributor.getActiveEditor();
			if (!(editor instanceof MarkdownEditor)) {
				viewer.setText("");
				return;
			}
			MarkdownEditor ed = (MarkdownEditor) editor;
			MarkdownPage page = ed.getMarkdownPage();
			String html = page.html();
			html = addBaseURL(editor, html);
			if (page != null) viewer.setText(html);
			else viewer.setText("");
		} catch (Exception ex) {
			// Smother
			System.out.println(ex);
			
			if (viewer != null && !viewer.isDisposed())
				viewer.setText(ex.getMessage());
		}
	}

	/**
	 * Adjust the URL base to be the file's directory.
	 * @param editor
	 * @param html
	 * @return
	 */
	private String addBaseURL(IEditorPart editor, String html) {
	try {
		IPathEditorInput input = (IPathEditorInput) editor.getEditorInput();
		IPath path = input.getPath();
		path = path.removeLastSegments(1);
		File f = path.toFile();
		URI fileURI = f.toURI();
		String html2 = "<html><head><base href='"+fileURI+"' /></head><body>\r\n"+html
		+"\r\n</body></html>";
		return html2;
	} catch (Exception ex) {
		return html;
	}
	}
}