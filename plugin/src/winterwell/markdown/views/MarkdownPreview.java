package winterwell.markdown.views;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import winterwell.markdown.Activator;
import winterwell.markdown.editors.ActionBarContributor;
import winterwell.markdown.editors.MarkdownEditor;
import winterwell.markdown.pagemodel.MarkdownPage;

public class MarkdownPreview extends ViewPart {

	private static final String MDCSS = "markdown.css";
	private static final String CSS = "css";
	private static final String EOL = System.getProperty("line.separator");

	private static final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	private static final IPath location = root.getLocation();

	public static MarkdownPreview preview = null;
	private Browser viewer = null;

	/**
	 * The constructor.
	 */
	public MarkdownPreview() {
		preview = this;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
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
		if (viewer == null) {
			return;
		}
		viewer.setFocus();
		update();
	}

	public void update() {
		if (viewer == null) {
			return;
		}
		try {
			IEditorPart editor = ActionBarContributor.getActiveEditor();
			if (!(editor instanceof MarkdownEditor)) {
				viewer.setText("");
				return;
			}

			String html = "";
			MarkdownEditor ed = (MarkdownEditor) editor;
			MarkdownPage page = ed.getMarkdownPage();
			if (page != null) {
				html = page.html();
				IPath path = getEditorInput(editor);
				html = addHeader(html, getBaseUrl(path), getMdStyles(path));
			}
			viewer.setText(html);
		} catch (Exception ex) {
			System.out.println(ex);

			if (viewer != null && !viewer.isDisposed()) {
				viewer.setText(ex.getMessage());
			}
		}
	}

	private IPath getEditorInput(IEditorPart editor) {
		IPathEditorInput input = (IPathEditorInput) editor.getEditorInput();
		return input.getPath();
	}

	private String getBaseUrl(IPath path) {
		return path.removeLastSegments(1).toFile().toURI().toString();
	}

	/*
	 * Look for a stylesheet file having the same name as the input file, beginning in the current
	 * directory, its parent directories, upto and including the current project directory. If not
	 * found, look for a file with the name 'markdown.css' in the same set of directories. If still
	 * not found, read 'markdown.css' from the bundle.
	 */
	private String getMdStyles(IPath path) {
		IPath styles = path.removeFileExtension().addFileExtension(CSS);
		String pathname = find(styles);
		if (pathname != null) return pathname;

		styles = path.removeLastSegments(1).append(MDCSS);
		pathname = find(styles);
		if (pathname != null) return pathname;

		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL url = FileLocator.find(bundle, new Path("resources/" + MDCSS), null);
		try {
			url = FileLocator.toFileURL(url);
			return url.toURI().toString();
		} catch (IOException | URISyntaxException e) {
			System.out.println(e);
			return null;
		}
	}

	private String find(IPath styles) {
		String name = styles.lastSegment();
		IPath base = styles.removeLastSegments(1);
		IContainer dir = root.getContainerForLocation(base);

		while (dir.getType() != IResource.ROOT) {
			IResource member = dir.findMember(name);
			if (member != null) {
				return location.append(member.getFullPath()).toFile().toURI().toString();
			}
			dir = dir.getParent();
		}
		return null;
	}

	private String addHeader(String html, String base, String style) {
		StringBuilder sb = new StringBuilder("<html><head>" + EOL);
		if (base != null) sb.append("<base href='" + base + "' />" + EOL);
		if (style != null) {
			sb.append("<link rel='stylesheet' type='text/css' href='" + style + "' media='screen' />" + EOL);
		}
		sb.append("</head><body>" + EOL + html + EOL + "</body></html>");
		return sb.toString();
	}
}
