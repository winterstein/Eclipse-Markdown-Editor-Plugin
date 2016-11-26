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
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import winterwell.markdown.Log;
import winterwell.markdown.MarkdownUIPlugin;
import winterwell.markdown.editors.ActionBarContributor;
import winterwell.markdown.editors.MarkdownEditor;
import winterwell.markdown.pagemodel.MarkdownPage;
import winterwell.markdown.preferences.Prefs;
import winterwell.markdown.util.PartListener;
import winterwell.markdown.util.Strings;

public class MarkdownPreview extends ViewPart implements Prefs {

	public static final String ID = "winterwell.markdown.views.MarkdownPreview";

	// script to return the current top scroll position of the browser widget
	private static final String GETSCROLLTOP = "function getScrollTop() { " //$NON-NLS-1$
			+ "  if(typeof pageYOffset!='undefined') return pageYOffset;" //$NON-NLS-1$
			+ "  else{" //$NON-NLS-1$
			+ "var B=document.body;" //$NON-NLS-1$
			+ "var D=document.documentElement;" //$NON-NLS-1$
			+ "D=(D.clientHeight)?D:B;return D.scrollTop;}" //$NON-NLS-1$
			+ "}; return getScrollTop();"; //$NON-NLS-1$

	private static MarkdownPreview view;
	private Browser browser;

	private Limiter limiter;

	private IPartListener partListener = new PartListener() {

		@Override
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof MarkdownEditor) {
				((MarkdownEditor) part).getViewer().addTextListener(textListener);
				limiter.trigger();
			} else if (part instanceof MarkdownPreview) {
				limiter.trigger();
			}
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			if (part instanceof MarkdownEditor) {
				getActivePage().hideView(view);
			}
		}
	};

	private final IPropertyChangeListener styleListener = new IPropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			switch (event.getProperty()) {
				case PREF_CSS_CUSTOM:
				case PREF_CSS_DEFAULT:
					limiter.trigger();
			}
		}
	};

	private ITextListener textListener = new ITextListener() {

		@Override
		public void textChanged(TextEvent event) {
			limiter.trigger();
		}
	};

	/**
	 * The constructor.
	 */
	public MarkdownPreview() {
		view = this;
	}

	/**
	 * Callback to create and initialize the browser.
	 */
	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.MULTI);
		limiter = new Limiter(view);

		getPreferenceStore().addPropertyChangeListener(styleListener);
		getActivePage().addPartListener(partListener);
	}

	@Override
	public void dispose() {
		getPreferenceStore().removePropertyChangeListener(styleListener);
		getActivePage().removePartListener(partListener);
		ITextViewer srcViewer = getSourceViewer();
		srcViewer.removeTextListener(textListener);

		if (limiter != null) {
			limiter.dispose();
			limiter = null;
		}
		browser = null;
		super.dispose();
	}

	public synchronized void update() {
		if (browser == null) return;

		try {
			IEditorPart editor = ActionBarContributor.getActiveEditor();
			if (!(editor instanceof MarkdownEditor)) {
				browser.setText("");
				return;
			}

			// // object used for wait/notify communications
			// Boolean load = true;

			Object result = browser.evaluate(GETSCROLLTOP);
			final int scrollTop = result != null ? ((Number) result).intValue() : 0;

			String html = "";
			MarkdownEditor ed = (MarkdownEditor) editor;
			MarkdownPage page = ed.getMarkdownPage();
			if (page != null) {
				html = page.html();
				IPath path = getInputPath(editor);
				html = addHeader(html, getBaseUrl(path), getMdStyles(path));

				browser.addProgressListener(new ProgressAdapter() {

					@Override
					public void completed(ProgressEvent event) {
						browser.removeProgressListener(this);
						browser.execute(String.format("window.scrollTo(0,%d);", scrollTop)); //$NON-NLS-1$
						browser.setRedraw(true);
						// load.notify();
					}
				});
			}

			browser.setRedraw(false);
			browser.setText(html);

			// wait for the browser load operation to complete
			// load.wait(getPreferenceStore().getInt(PREF_UPDATE_DELAY) * 1000);

		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			Log.error(e.getLocalizedMessage() + Strings.EOL + errors.toString());

			List<Status> lines = new ArrayList<>();
			for (StackTraceElement line : e.getStackTrace()) {
				lines.add(new Status(IStatus.ERROR, MarkdownUIPlugin.PLUGIN_ID, line.toString()));
			}
			MultiStatus status = new MultiStatus(MarkdownUIPlugin.PLUGIN_ID, IStatus.ERROR,
					lines.toArray(new Status[lines.size()]), e.getLocalizedMessage(), e);
			ErrorDialog.openError(null, "Viewer error", e.getMessage(), status);
		}
	}

	/**
	 * Passing the focus request to the browser's control.
	 */
	@Override
	public void setFocus() {
		if (browser != null) browser.setFocus();
	}

	protected IWorkbenchPage getActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	protected ITextViewer getSourceViewer() {
		MarkdownEditor editor = (MarkdownEditor) ActionBarContributor.getActiveEditor();
		return editor.getViewer();
	}

	protected IPreferenceStore getPreferenceStore() {
		return MarkdownUIPlugin.getDefault().getPreferenceStore();
	}

	private IPath getInputPath(IEditorPart editor) {
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
		IPreferenceStore store = MarkdownUIPlugin.getDefault().getPreferenceStore();
		String customCss = store.getString(PREF_CSS_CUSTOM);
		if (!customCss.isEmpty()) {
			File file = new File(customCss);
			if (file.isFile() && file.getName().endsWith("." + CSS)) {
				return customCss;
			}
		}

		// 4) read the file identified by the pref key 'PREF_CSS_DEFAULT' from the bundle
		String defaultCss = store.getString(PREF_CSS_DEFAULT);
		if (!defaultCss.isEmpty()) {
			try {
				URI uri = new URI(defaultCss);
				File file = new File(uri);
				if (file.isFile()) return file.getPath();
			} catch (URISyntaxException e) {
				MessageDialog.openInformation(null, "Default CSS from bundle", defaultCss);
			}
		}

		// 5) read 'markdown.css' from the bundle
		Bundle bundle = Platform.getBundle(MarkdownUIPlugin.PLUGIN_ID);
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

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IContainer dir = root.getContainerForLocation(base);

		while (dir.getType() != IResource.ROOT) {
			IResource member = dir.findMember(name);
			if (member != null) {
				return root.getLocation().append(member.getFullPath()).toFile().toURI().toString();
			}
			dir = dir.getParent();
		}
		return null;
	}

	private String addHeader(String html, String base, String style) {
		StringBuilder sb = new StringBuilder("<html><head>" + Strings.EOL);
		if (base != null) sb.append("<base href='" + base + "' />" + Strings.EOL);
		if (style != null) {
			sb.append("<link rel='stylesheet' type='text/css' href='" + style + "' media='screen' />" + Strings.EOL);
		}
		sb.append("</head><body>" + Strings.EOL + html + Strings.EOL + "</body></html>");
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
