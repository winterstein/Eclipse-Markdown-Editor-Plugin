package winterwell.markdown.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.spelling.SpellingReconcileStrategy;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import winterwell.markdown.preferences.Prefs;

public class MDConfiguration extends TextSourceViewerConfiguration {

	private ColorManager colorManager;

	public MDConfiguration(ColorManager colorManager, IPreferenceStore store) {
		super(store);
		this.colorManager = colorManager;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		MDScanner scanner = new MDScanner(colorManager);
		PresentationReconciler pr = (PresentationReconciler) super.getPresentationReconciler(sourceViewer);
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer(scanner);
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		return pr;
	}

	@Override
	public IReconciler getReconciler(ISourceViewer viewer) {
		boolean local = fPreferenceStore.getBoolean(Prefs.PREF_SPELLING_ENABLED);
		if (local) {

			// use the combined preference store
			SpellingService service = new SpellingService(fPreferenceStore);
			if (service.getActiveSpellingEngineDescriptor(fPreferenceStore) == null) {
				return super.getReconciler(viewer); // bail
			}

			IReconcilingStrategy strategy = new SpellingReconcileStrategy(viewer, service);
			MonoReconciler reconciler = new MonoReconciler(strategy, false);
			reconciler.setDelay(500);
			return reconciler;
		}
		
		// default; uses just the PlatformUI store
		return super.getReconciler(viewer);
	}

	@SuppressWarnings("unused")
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		if (true) return super.getTextHover(sourceViewer, contentType);
		// Add hover support for images
		return new MDTextHover();
	}
}
