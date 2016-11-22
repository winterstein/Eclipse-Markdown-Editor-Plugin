package winterwell.markdown.views;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import winterwell.markdown.Activator;
import winterwell.markdown.Log;
import winterwell.markdown.editors.ActionBarContributor;
import winterwell.markdown.editors.MarkdownEditor;
import winterwell.markdown.pagemodel.MarkdownPage;
import winterwell.markdown.preferences.Prefs;

public class MarkdownPreview extends ViewPart implements Prefs {

	// script to return the current top scroll position of the browser widget
	private static final String GETSCROLLTOP = "function getScrollTop() { " //$NON-NLS-1$
			+ "  if(typeof pageYOffset!='undefined') return pageYOffset;" //$NON-NLS-1$
			+ "  else{" //$NON-NLS-1$
			+ "var B=document.body;" //$NON-NLS-1$
			+ "var D=document.documentElement;" //$NON-NLS-1$
			+ "D=(D.clientHeight)?D:B;return D.scrollTop;}" //$NON-NLS-1$
			+ "}; return getScrollTop();"; //$NON-NLS-1$

	private static final String EOL = System.getProperty("line.separator");

	private static final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	private static final IPath location = root.getLocation();

	public static MarkdownPreview preview = null;
	private Browser viewer = null;
	private StyleListener styleListener;

	public class StyleListener implements IPropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			switch (event.getProperty()) {
				case PREF_CSS_CUSTOM:
				case PREF_CSS_DEFAULT:
					String value = (String) event.getNewValue();
					if (value != null && !value.isEmpty()) {
						update();
					}
			}
		}
	}

	/**
	 * The constructor.
	 */
	public MarkdownPreview() {
		preview = this;
		styleListener = new StyleListener();
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(styleListener);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new Browser(parent, SWT.MULTI);
	}

	public void update() {
		if (viewer == null) return;

		try {
			IEditorPart editor = ActionBarContributor.getActiveEditor();
			if (!(editor instanceof MarkdownEditor)) {
				viewer.setText("");
				return;
			}

			Object result = viewer.evaluate(GETSCROLLTOP);
			final int scrollTop = result != null ? ((Number) result).intValue() : 0;

			String html = "";
			MarkdownEditor ed = (MarkdownEditor) editor;
			MarkdownPage page = ed.getMarkdownPage();
			if (page != null) {
				html = page.html();
				IPath path = getEditorInput(editor);
				html = addHeader(html, getBaseUrl(path), getMdStyles(path));

				viewer.addProgressListener(new ProgressAdapter() {

					@Override
					public void completed(ProgressEvent event) {
						viewer.removeProgressListener(this);
						viewer.execute(String.format("window.scrollTo(0,%d);", scrollTop)); //$NON-NLS-1$
					}
				});
			}

			viewer.setText(html);

		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			Log.error(e.getLocalizedMessage() + EOL + errors.toString());

			List<Status> lines = new ArrayList<>();
			for (StackTraceElement line : e.getStackTrace()) {
				lines.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, line.toString()));
			}
			MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
					lines.toArray(new Status[lines.size()]), e.getLocalizedMessage(), e);
			ErrorDialog.openError(null, "Viewer error", e.getMessage(), status);
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		if (viewer == null) return;
		viewer.setFocus();
		update();
	}

	@Override
	public void dispose() {
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(styleListener);
		viewer = null;
		super.dispose();
	}

	private IPath getEditorInput(IEditorPart editor) {
		IPathEditorInput input = (IPathEditorInput) editor.getEditorInput();
		return input.getPath();
	}

	private String getBaseUrl(IPath path) {
		return path.removeLastSegments(1).toFile().toURI().toString();
	}

	private String getMdStyles(IPath path) {
		// 1) look for a file having the same name as the input file, beginning in the
		// current directory, parent directories, and the current project directory.
		IPath styles = path.removeFileExtension().addFileExtension(CSS);
		String pathname = find(styles);
		if (pathname != null) return pathname;

		// 2) look for a file with the name 'markdown.css' in the same set of directories
		styles = path.removeLastSegments(1).append(DEF_MDCSS);
		pathname = find(styles);
		if (pathname != null) return pathname;

		// 3) read the file identified by the pref key 'PREF_CSS_CUSTOM' from the filesystem
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String customCss = store.getString(PREF_CSS_CUSTOM);
		if (!customCss.isEmpty()) {
			File file = new File(customCss);
			if (file.isFile() && file.getName().endsWith("." + CSS)) {
				return customCss;
			}
		}

		// 4) read the file identified by the pref key 'PREF_CSS_DEFAULT' from the bundle
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		String defaultCss = store.getString(PREF_CSS_DEFAULT);
		if (!defaultCss.isEmpty()) {
			try {
				if (!defaultCss.startsWith("file:")) {
					// apparently not absolute - at preference default
					URL url = FileLocator.find(bundle, new Path("resources/" + defaultCss), null);
					try {
						url = FileLocator.toFileURL(url);
						defaultCss = url.toURI().toString();
					} catch (IOException | URISyntaxException e) {}
				}
				URI uri = new URI(defaultCss);
				File file = new File(uri);
				if (file.isFile()) return file.getPath();
			} catch (URISyntaxException e) {
				MessageDialog.openInformation(null, "Default CSS from bundle", defaultCss);
			}
		}

		// 5) read 'markdown.css' from the bundle
		URL url = FileLocator.find(bundle, new Path("resources/" + DEF_MDCSS), null);
		try {
			url = FileLocator.toFileURL(url);
			return url.toURI().toString();
		} catch (IOException | URISyntaxException e) {
			Log.error(e);
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

	// Notes for future enhancement:
	//
	// StyledText text = ed.getViewer().getTextWidget();
	// float offset = text.getCaretOffset();
	// float size = text.getCharCount();
	// float center = offset/size;
	//
	// var scrollTop = window.scrollTop();
	// var docHeight = document.height();
	// var winHeight = window.height();
	// var scrollPercent = (scrollTop) / (docHeight - winHeight);
	// var scrollPercentRounded = Math.round(scrollPercent * 100) / 100;
}
