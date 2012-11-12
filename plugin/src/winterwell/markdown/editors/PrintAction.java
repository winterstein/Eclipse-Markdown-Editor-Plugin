//package winterwell.markdown.editors;
//
//import java.util.List;
//
//import net.sf.paperclips.PaperClips;
//import net.sf.paperclips.Print;
//import net.sf.paperclips.PrintJob;
//import net.sf.paperclips.TextPrint;
//
//import org.eclipse.core.commands.ExecutionEvent;
//import org.eclipse.core.commands.ExecutionException;
//import org.eclipse.core.commands.IHandler;
//import org.eclipse.core.commands.IHandlerListener;
//import org.eclipse.jface.action.Action;
//import org.eclipse.jface.text.BadLocationException;
//import org.eclipse.jface.text.DocumentEvent;
//import org.eclipse.jface.text.IDocument;
//import org.eclipse.jface.text.IDocumentListener;
//import org.eclipse.jface.text.IRegion;
//import org.eclipse.jface.text.ITextSelection;
//import org.eclipse.jface.text.Region;
//import org.eclipse.jface.text.source.ISourceViewer;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.printing.PrintDialog;
//import org.eclipse.swt.printing.PrinterData;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.ui.IEditorPart;
//import org.eclipse.ui.IPropertyListener;
//import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
//
//import winterwell.markdown.pagemodel.MarkdownFormatter;
//import winterwell.markdown.pagemodel.MarkdownPage;
//import winterwell.markdown.pagemodel.MarkdownPage.KLineType;
//import winterwell.utils.containers.Pair;
//import winterwell.utils.containers.Range;
//
///**
// * Print the file
// * 
// *
// * @author daniel
// */
//public class PrintAction extends Action {
//
//	public PrintAction() {
//		super("Print...");
//	}
//	
//	@Override
//	public void run() {
//		try {
//			MarkdownEditor ed = (MarkdownEditor) ActionBarContributor.getActiveEditor();
//			if (ed == null) return; // The active editor is not a markdown editor.
//			PrintDialog dialog = new PrintDialog(Display.getDefault().getActiveShell(), SWT.NONE);
//			PrinterData printerData = dialog.open ();
//			if (printerData == null) return;
//			Print doc = new TextPrint(ed.getText());
//			PrintJob job = new PrintJob(ed.getTitle(), doc );
//			PaperClips.print(job, printerData);
//			// Done
//		} catch (Exception ex) {
//			System.out.println(ex);
//		}
//	}
//
//
//
//	public void dispose() {
//		// Ignore		
//	}
//
//	
//}
// 