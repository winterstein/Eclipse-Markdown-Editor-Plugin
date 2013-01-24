package winterwell.markdown.editors;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;

import winterwell.markdown.pagemodel.MarkdownFormatter;
import winterwell.markdown.pagemodel.MarkdownPage;
import winterwell.markdown.pagemodel.MarkdownPage.KLineType;
import winterwell.utils.containers.IntRange;

/**
 * TODO An action for formatting text (via hard wrapping, i.e. inserting returns).
 * 
 *
 * @author daniel
 */
public class FormatAction extends Action implements IHandler {

	public FormatAction() {
		super("&Format paragraph");
		setActionDefinitionId("winterwell.markdown.formatParagraphCommand");
		setToolTipText("Format the paragraph under the caret by inserting/removing line-breaks");
	}
	
	@Override
	public void run() {
		try {
			MarkdownEditor ed = (MarkdownEditor) ActionBarContributor.getActiveEditor();
			if (ed == null) return; // The active editor is not a markdown editor.
			int cols = ed.getPrintColumns();
			// Do we have a selection?
			ITextSelection s = (ITextSelection) ed.getSelectionProvider().getSelection();
			if (s != null && s.getLength() > 0) {
				formatSelectedRegion(ed, s, cols);
				return;
			}
			// Where is the caret?
			ISourceViewer viewer = ed.getViewer();
			int caretOffset = viewer.getTextWidget().getCaretOffset();
			int lineNum = ed.getDocument().getLineOfOffset(caretOffset);
			// Get a paragraph region
			MarkdownPage page = ed.getMarkdownPage();
			IRegion pRegion = getParagraph(page, lineNum, ed.getDocument());
			if (pRegion==null) {
				// Not in a paragraph - so give up
				 // TODO tell the user why we've given up
				return;
			}
			String paragraph = ed.getDocument().get(pRegion.getOffset(), pRegion.getLength());
			// Format
			String formatted = MarkdownFormatter.format(paragraph, cols);
			if (formatted.equals(paragraph)) return; // No change!
			// Replace the unformatted region with the new formatted one
			ed.getDocument().replace(pRegion.getOffset(), pRegion.getLength(), formatted);
			// Done
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private void formatSelectedRegion(MarkdownEditor ed, ITextSelection s, int cols) 
	throws BadLocationException {
		int start = s.getStartLine();
		int end = s.getEndLine();
		IDocument doc = ed.getDocument();
		int soff = doc.getLineOffset(start);
		int eoff = lineEndOffset(end, doc);		
		IntRange editedRegion = new IntRange(soff, eoff);		
		MarkdownPage page = ed.getMarkdownPage();
		StringBuilder sb = new StringBuilder(s.getLength());
		for(int i=start; i<=end; i++) {
			IRegion para = getParagraph(page, i, ed.getDocument());
			if (para==null) {
				sb.append(page.getText().get(i));
				continue;
			}
			String paragraph = ed.getDocument().get(para.getOffset(), para.getLength());
//			int lines = StrUtils.splitLines(paragraph).length;
			String formatted = MarkdownFormatter.format(paragraph, cols);
			// append formatted and move forward
			sb.append(formatted);
			CharSequence le = lineEnd(i, doc);
			sb.append(le);
			int pEnd = doc.getLineOfOffset(para.getOffset()+para.getLength());
			i = pEnd;
			// Adjust edited region?
			IntRange pr = new IntRange(para.getOffset(), 
					para.getOffset()+para.getLength()+le.length());			
			editedRegion = new IntRange(Math.min(pr.low, editedRegion.low), 
										Math.max(pr.high, editedRegion.high));			
		}		
		// Replace the unformatted region with the new formatted one
		String old = doc.get(editedRegion.low, editedRegion.size());
		String newText = sb.toString();
		if (old.equals(newText)) return;
		ed.getDocument().replace(editedRegion.low, editedRegion.size(), newText);		
	}

	private CharSequence lineEnd(int line, IDocument doc) throws BadLocationException {
		int eoff = doc.getLineOffset(line) + doc.getLineInformation(line).getLength();		
		char c = doc.getChar(eoff);
		if (c=='\r' && doc.getLength() > eoff+1 
				&& doc.getChar(eoff+1) =='\n') return "\r\n";
		return ""+c;
	}

	private int lineEndOffset(int end, IDocument doc) 
	throws BadLocationException {
		int eoff = doc.getLineOffset(end) + doc.getLineInformation(end).getLength();
		// Include line end
		char c = doc.getChar(eoff);
		if (c=='\r' && doc.getLength() > eoff+1 
				&& doc.getChar(eoff+1) =='\n') eoff += 2;
		else eoff += 1;
		return eoff;
	}

	/**
	 * 
	 * @param page
	 * @param lineNum
	 * @param doc
	 * @return region of paragraph containing this line, or null
	 * @throws BadLocationException
	 */
	private IRegion getParagraph(MarkdownPage page, int lineNum, IDocument doc) 
	throws BadLocationException {
		// Get doc info
		List<String> lines = page.getText();
		List<KLineType> lineInfo = page.getLineTypes();
		// Check we are in a paragraph or list
		KLineType pType = lineInfo.get(lineNum);
		switch(pType) {
		case NORMAL: break;
		default: // Not in a paragraph, so we cannot format.  
			return null;
		}
		// Work out the paragraph
		// Beginning
		int start;
		for(start=lineNum; start>-1; start--) {
			if (lineInfo.get(start) != pType) {
				start++;
				break;
			}
		}
		// End
		int end;
		for(end=lineNum; end<lines.size(); end++) {
			if (lineInfo.get(end) != pType) {
				end--;
				break;
			}
		}
		// Get offset info
		int sOff = doc.getLineOffset(start);
		IRegion endLine = doc.getLineInformation(end);  // exclude final line end
		int eOff = endLine.getOffset()+endLine.getLength();
		return new Region(sOff, eOff-sOff);
	}

	public void addHandlerListener(IHandlerListener handlerListener) {
		// Ignore
	}

	public void dispose() {
		// Ignore		
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		run();
		return null;
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
		// Ignore		
	}
	
}
