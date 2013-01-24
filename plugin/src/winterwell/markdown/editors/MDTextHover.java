/**
 * (c) Winterwell 2010 and ThinkTank Mathematics 2007
 */
package winterwell.markdown.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import winterwell.markdown.StringMethods;
import winterwell.utils.containers.Pair;

/**
 * 
 *
 * @author daniel
 */
public class MDTextHover implements ITextHover //, ITextHoverExtension 
{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion region) {
		try {
			IDocument doc = textViewer.getDocument();
			String text = doc.get(region.getOffset(), region.getLength());
			return "<b>"+text+"</b>";
		} catch (Exception e) {
			return null;
		}		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		try {
			IDocument doc = textViewer.getDocument();
			int line = doc.getLineOfOffset(offset);
			int lineOffset = doc.getLineOffset(line);
			int lineLength = doc.getLineLength(line);
			String text = doc.get(lineOffset, lineLength);
			// Look for image tags
			Pair<Integer> altRegion;
			Pair<Integer> urlRegion = 
				StringMethods.findEnclosingRegion(text, offset-lineOffset, '(', ')');
			if (urlRegion==null) {
				altRegion = StringMethods.findEnclosingRegion(text, offset-lineOffset, '[', ']');
				if (altRegion == null) return null;
				urlRegion = StringMethods.findEnclosingRegion(text, altRegion.second, '(', ')');
			} else {
				altRegion = StringMethods.findEnclosingRegion(text, urlRegion.first-1, '[', ']');
			}
			if (urlRegion==null || altRegion==null) return null;
			// Is it an image link?
			if (text.charAt(altRegion.first-1) != '!') return null;
			Region r = new Region(urlRegion.first+1+lineOffset, urlRegion.second-urlRegion.first-2);
			return r;
		} catch (Exception ex) {
			return null;
		}
	}

//	public IInformationControlCreator getHoverControlCreator() {
//		return new IInformationControlCreator() {
//			public IInformationControl createInformationControl(Shell parent) {
//				int style= fIsFocusable ? SWT.V_SCROLL | SWT.H_SCROLL : SWT.NONE;
//				
//				if (BrowserInformationControl.isAvailable(parent)) {
//		            final int shellStyle= SWT.TOOL | (fIsFocusable ? SWT.RESIZE : SWT.NO_TRIM);
//		            return new BrowserInformationControl(parent, shellStyle, style, null);
//	            }
//				return new DefaultInformationControl(parent, style, new HTMLTextPresenter());
//			}						
//		};
//	}

}
