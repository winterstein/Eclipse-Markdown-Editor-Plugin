/**
 * Copyright winterwell Mathematics Ltd.
 * @author Daniel Winterstein
 * 11 Jan 2007
 */
package winterwell.markdown.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import winterwell.markdown.Log;
import winterwell.markdown.pagemodel.MarkdownPage;
import winterwell.markdown.pagemodel.MarkdownPage.Header;
import winterwell.markdown.pagemodel.MarkdownPage.KLineType;
import winterwell.markdown.util.Strings;

/**
 * @author Daniel Winterstein
 */
public final class MarkdownOutlinePage extends ContentOutlinePage {

	public final class ContentProvider implements ITreeContentProvider, IDocumentListener {

		private MarkdownPage page;

		private void parse() {
			this.page = editor.getMarkdownPage();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			if (oldInput != null) {
				IDocument document = fDocumentProvider.getDocument(oldInput);
				if (document != null) {
					document.removeDocumentListener(this);
				}
			}

			page = null;
			if (newInput != null) {
				IDocument document = fDocumentProvider.getDocument(newInput);
				if (document != null) {
					document.addDocumentListener(this);
					parse();
				}
			}
		}

		public void dispose() {
			page = null;
		}

		public boolean isDeleted(Object element) {
			return false;
		}

		public Object[] getElements(Object element) {
			return page.getHeadings(null).toArray();
		}

		public boolean hasChildren(Object element) {
			if (element == fInput) {
				return true;
			}
			if (element instanceof MarkdownPage.Header) {
				MarkdownPage.Header header = (MarkdownPage.Header) element;
				return header.getSubHeaders().size() > 0;
			} ;
			return false;
		}

		public Object getParent(Object element) {
			if (!(element instanceof MarkdownPage.Header)) return null;
			return ((MarkdownPage.Header) element).getParent();
		}

		public Object[] getChildren(Object element) {
			if (element == fInput) {
				return page.getHeadings(null).toArray();
			}
			if (!(element instanceof MarkdownPage.Header)) return null;
			return ((MarkdownPage.Header) element).getSubHeaders().toArray();
		}

		public void documentAboutToBeChanged(DocumentEvent event) {}

		public void documentChanged(DocumentEvent event) {
			parse();
			update();
		}
	}

	private Object fInput = null;
	private final IDocumentProvider fDocumentProvider;
	private final MarkdownEditor editor;
	protected boolean showWordCounts;
	private List<Header> selectedHeaders;

	/**
	 * @param documentProvider
	 * @param editor
	 */
	public MarkdownOutlinePage(IDocumentProvider documentProvider, MarkdownEditor editor) {
		this.fDocumentProvider = documentProvider;
		this.editor = editor;

		editor.getViewer().addTextListener(new ITextListener() {

			@Override
			public void textChanged(TextEvent event) {}
		});
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new ContentProvider());
		// Add word count annotations
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (!(element instanceof MarkdownPage.Header)) return super.getText(element);
				Header header = ((MarkdownPage.Header) element);
				String hText = header.toString();
				if (!showWordCounts) return hText;
				IRegion region = getRegion(header);
				String text;
				try {
					text = editor.getDocument().get(region.getOffset(), region.getLength());
					text = Strings.stripTags(text);
					text = text.replaceAll("#", "").trim();
					assert text.startsWith(hText);
					text = text.substring(hText.length());
					int wc = Strings.wordCount(text);
					return hText + " (" + wc + ":" + text.length() + ")";
				} catch (BadLocationException e) {
					return hText;
				}
			}
		});
		viewer.addSelectionChangedListener(this);

		if (fInput != null) viewer.setInput(fInput);

		// Buttons
		IPageSite site = getSite();
		IActionBars bars = site.getActionBars();
		IToolBarManager toolbar = bars.getToolBarManager();
		// Word count action
		Action action = new Action("123", IAction.AS_CHECK_BOX) {

			@Override
			public void run() {
				showWordCounts = isChecked();
				update();
			}
		};
		action.setToolTipText("Show/hide section word:character counts");
		toolbar.add(action);
		// +/- actions
		action = new Action("<") {

			@Override
			public void run() {
				doPromoteDemote(-1);
			}
		};
		action.setToolTipText("Promote the selected section\n -- move it up a level.");
		toolbar.add(action);
		//
		action = new Action(">") {

			@Override
			public void run() {
				doPromoteDemote(1);
			}
		};
		action.setToolTipText("Demote the selected section\n -- move it down a level.");
		toolbar.add(action);
		// up/down actions
		action = new Action("/\\") {

			@Override
			public void run() {
				try {
					doMove(-1);
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
			}
		};
		action.setToolTipText("Move the selected section earlier");
		toolbar.add(action);
		//
		action = new Action("\\/") {

			@Override
			public void run() {
				try {
					doMove(1);
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
			}
		};
		action.setToolTipText("Move the selected section later");
		toolbar.add(action);
		// Collapse
		ImageDescriptor id = ImageDescriptor.createFromFile(getClass(), "collapseall.gif");
		action = new Action("collapse", id) {

			@Override
			public void run() {
				doCollapseAll();
			}
		};
		action.setImageDescriptor(id);
		action.setToolTipText("Collapse outline tree");
		toolbar.add(action);
		// Sync
		id = ImageDescriptor.createFromFile(getClass(), "synced.gif");
		action = new Action("sync") {

			@Override
			public void run() {
				try {
					doSyncToEditor();
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
			}
		};
		action.setImageDescriptor(id);
		action.setToolTipText("Link with editor");
		toolbar.add(action);
		// Add edit ability
		viewer.getControl().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F2) {
					doEditHeader();
				}
			}

			public void keyReleased(KeyEvent e) {
				//
			}
		});
	}

	/**
	 * @throws BadLocationException
	 */
	protected void doSyncToEditor() throws BadLocationException {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null) return;
		// Get header
		MarkdownPage page = editor.getMarkdownPage();
		int caretOffset = editor.getViewer().getTextWidget().getCaretOffset();
		IDocument doc = editor.getDocument();
		int line = doc.getLineOfOffset(caretOffset);
		List<KLineType> lineTypes = page.getLineTypes();
		for (; line > -1; line--) {
			KLineType lt = lineTypes.get(line);
			if (lt.toString().startsWith("H")) break;
		}
		if (line < 0) return;
		Header header = (Header) page.getPageObject(line);
		// Set
		IStructuredSelection selection = new StructuredSelection(header);
		viewer.setSelection(selection, true);
	}

	void doEditHeader() {
		TreeViewer viewer = getTreeViewer();
		viewer.editElement(selectedHeaders.get(0), 0);
	}

	protected void doCollapseAll() {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null) return;
		// Control control = viewer.getControl();
		// if (control != null && !control.isDisposed()) {
		// control.setRedraw(false);
		viewer.collapseAll();
		// control.setRedraw(true);
		// }
	}

	/**
	 * Move the selected sections up/down
	 * 
	 * @param i 1 or -1. 1==move later, -1=earlier
	 * @throws BadLocationException
	 */
	protected void doMove(int i) throws BadLocationException {
		assert i == 1 || i == -1;
		if (selectedHeaders == null || selectedHeaders.size() == 0) return;
		// Get text region to move
		MarkdownPage.Header first = selectedHeaders.get(0);
		MarkdownPage.Header last = selectedHeaders.get(selectedHeaders.size() - 1);
		int start = editor.getDocument().getLineOffset(first.getLineNumber());
		IRegion r = getRegion(last);
		int end = r.getOffset() + r.getLength();
		// int length = end - start;
		// Get new insertion point
		int insert;
		if (i == 1) {
			Header nextSection = last.getNext();
			if (nextSection == null) return;
			IRegion nr = getRegion(nextSection);
			insert = nr.getOffset() + nr.getLength();
		} else {
			Header prevSection = first.getPrevious();
			if (prevSection == null) return;
			IRegion nr = getRegion(prevSection);
			insert = nr.getOffset();
		}
		// Get text
		String text = editor.getDocument().get();
		// Move text
		String section = text.substring(start, end);
		String pre, post;
		if (i == 1) {
			pre = text.substring(0, start) + text.substring(end, insert);
			post = text.substring(insert);
		} else {
			pre = text.substring(0, insert);
			post = text.substring(insert, start) + text.substring(end);
		}
		text = pre + section + post;
		assert text.length() == editor.getDocument().get().length() : text.length()
				- editor.getDocument().get().length() + " chars gained/lost";
		// Update doc
		editor.getDocument().set(text);
	}

	/**
	 * Does not support -------- / ========= underlining, only # headers
	 * 
	 * @param upDown 1 for demote (e.g. h2 -> h3), -1 for promote (e.g. h2 -> h1)
	 */
	protected void doPromoteDemote(int upDown) {
		assert upDown == 1 || upDown == -1;
		if (selectedHeaders == null || selectedHeaders.size() == 0) return;
		HashSet<Header> toAdjust = new HashSet<Header>(selectedHeaders);
		HashSet<Header> adjusted = new HashSet<Header>();
		// Adjust
		MarkdownPage mdPage = editor.getMarkdownPage();
		List<String> lines = new ArrayList<String>(mdPage.getText());
		while (toAdjust.size() != 0) {
			Header h = toAdjust.iterator().next();
			toAdjust.remove(h);
			adjusted.add(h);
			String line = lines.get(h.getLineNumber());
			if (upDown == -1) {
				if (h.getLevel() == 1) return; // Level 1; can't promote
				if (line.startsWith("##"))
					line = line.substring(1);
				else {
					return; // TODO support for ------ / ========
				}
			} else
				line = "#" + line;
			int ln = h.getLineNumber();
			lines.set(ln, line);
			// kids
			ArrayList<Header> kids = new ArrayList<Header>(h.getSubHeaders());
			for (Header header : kids) {
				if (!adjusted.contains(header)) toAdjust.add(header);
			}
		}
		// Set
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
		}
		editor.getDocument().set(sb.toString());
	}

	/**
	 * The region of text for this header. This includes the header itself.
	 * 
	 * @param header
	 * @return
	 * @throws BadLocationException
	 */
	protected IRegion getRegion(Header header) {
		try {
			IDocument doc = editor.getDocument();
			// Line numbers
			int start = header.getLineNumber();
			Header next = header.getNext();
			int end;
			if (next != null) {
				end = next.getLineNumber() - 1;
			} else {
				end = doc.getNumberOfLines() - 1;
			}
			int offset = doc.getLineOffset(start);
			IRegion ei = doc.getLineInformation(end);
			int length = ei.getOffset() + ei.getLength() - offset;
			return new Region(offset, length);
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc) Method declared on ContentOutlinePage
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		selectedHeaders = null;
		ISelection selection = event.getSelection();
		if (selection.isEmpty()) return;
		if (!(selection instanceof IStructuredSelection)) return;
		try {
			IStructuredSelection strucSel = (IStructuredSelection) selection;
			Object[] sections = strucSel.toArray();
			selectedHeaders = (List) Arrays.asList(sections);
			MarkdownPage.Header first = (Header) sections[0];
			MarkdownPage.Header last = (Header) sections[sections.length - 1];
			int start = editor.getDocument().getLineOffset(first.getLineNumber());
			int length;
			if (first == last) {
				length = editor.getDocument().getLineLength(first.getLineNumber());
			} else {
				IRegion r = getRegion(last);
				int end = r.getOffset() + r.getLength();
				length = end - start;
			}
			editor.setHighlightRange(start, length, true);
		} catch (Exception e) {
			Log.error(e);
			editor.resetHighlightRange();
		}
	}

	/**
	 * Sets the input of the outline page
	 *
	 * @param input the input of this outline page
	 */
	public void setInput(Object input) {
		fInput = input;
		update();
	}

	/**
	 * Updates the outline page.
	 */
	public void update() {
		TreeViewer viewer = getTreeViewer();

		if (viewer != null) {
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);
				viewer.setInput(fInput);
				viewer.expandAll();
				control.setRedraw(true);
			}
		}
	}

}
