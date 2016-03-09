package winterwell.markdown.views;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.ViewPart;

import winterwell.markdown.Activator;
import winterwell.markdown.editors.ActionBarContributor;
import winterwell.markdown.editors.MarkdownEditor;
import winterwell.markdown.pagemodel.MarkdownPage;
import winterwell.utils.io.FileUtils;




public class MarkdownPreview extends ViewPart {
	
	private final Runnable refresh = new Runnable() {
		volatile long lastExecution = System.currentTimeMillis();
		final static long WAIT_TIME = 2000L;		

		@Override
		public synchronized void run() {
			try {
				if (System.currentTimeMillis() - lastExecution < WAIT_TIME) {
					return;
				} else {
					lastExecution = System.currentTimeMillis();
				}
				URL fileUrl = previewFile.toURI().toURL();
				URL oldUrl = null;
				try {
					oldUrl = new URL(viewer.getUrl());
				} catch (MalformedURLException e) {
					// Can happen, e.g. browser url is 'about:config'
				}
				if (fileUrl.equals(oldUrl)) {
					viewer.refresh();
				} else {
					viewer.setUrl(fileUrl.toString());
				}	
			} catch (MalformedURLException e) {
				// Should not happen since the URL is generated from a file
				throw new RuntimeException(e); 
			}
		}
	};
	
	public static MarkdownPreview preview = null;
	
	private static File previewFile = null;
	
	private static Browser viewer = null;

	/**
	 * The constructor.
	 */
	public MarkdownPreview() {
		preview = this;
		previewFile = Activator.getDefault().getStateLocation().append("markdown.html").toFile();
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
			if (page != null) {
				String html = page.html();
				html = addBaseURL(editor, html);
				FileUtils.write(previewFile, html);
				Display.getCurrent().asyncExec(refresh);
			} else {
				viewer.setText("");
			}
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