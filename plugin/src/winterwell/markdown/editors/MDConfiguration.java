package winterwell.markdown.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import winterwell.markdown.pagemodel.MarkdownPage;
import winterwell.markdown.pagemodel.MarkdownPage.Header;

public class MDConfiguration extends TextSourceViewerConfiguration {
	private ColorManager colorManager;

	public MDConfiguration(ColorManager colorManager, IPreferenceStore prefStore) {
		super(prefStore);
		this.colorManager = colorManager;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		MDScanner scanner = new MDScanner(colorManager);
		PresentationReconciler pr = (PresentationReconciler) super.getPresentationReconciler(sourceViewer); // FIXME
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(scanner);
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		return pr;
	}

	
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		// This awful mess adds in update support
		// Get super strategy
		IReconciler rs = super.getReconciler(sourceViewer);
		if (true) return rs;	// Seems to work fine?!
		final IReconcilingStrategy fsuperStrategy = rs==null? null : rs.getReconcilingStrategy("text");
		// Add our own
		IReconcilingStrategy strategy = new IReconcilingStrategy() {
			private IDocument doc;
			public void reconcile(IRegion partition) {
				MarkdownEditor ed = MarkdownEditor.getEditor(doc);
				if (ed != null) ed.updatePage(partition);
				if (fsuperStrategy!=null) fsuperStrategy.reconcile(partition);
			}
			public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
				MarkdownEditor ed = MarkdownEditor.getEditor(doc);
				if (ed != null) ed.updatePage(subRegion);
				if (fsuperStrategy!=null) fsuperStrategy.reconcile(dirtyRegion, subRegion);
			}
			public void setDocument(IDocument document) {
				this.doc = document;
				if (fsuperStrategy!=null) fsuperStrategy.setDocument(document);
			}			
		};
		// Make a reconciler
		MonoReconciler m2 = new MonoReconciler(strategy, true);
		m2.setIsIncrementalReconciler(true);
		m2.setProgressMonitor(new NullProgressMonitor());
		m2.setDelay(500);
		// Done
		return m2;
	}
	
	@SuppressWarnings("unused")
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		if (true) return super.getTextHover(sourceViewer, contentType);
		// Add hover support for images
		return new MDTextHover();
	}
}


