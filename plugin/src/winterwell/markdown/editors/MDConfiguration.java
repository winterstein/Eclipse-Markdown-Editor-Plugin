package winterwell.markdown.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class MDConfiguration extends TextSourceViewerConfiguration {

	private ColorManager colorManager;

	public MDConfiguration(ColorManager colorManager, IPreferenceStore prefStore) {
		super(prefStore);
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
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		return super.getReconciler(sourceViewer);

		// if (fPreferenceStore == null ||
		// !fPreferenceStore.getBoolean(SpellingService.PREFERENCE_SPELLING_ENABLED)) {
		// return null;
		// }
		//
		// SpellingService spellingService = MarkdownUIPlugin.getSpellingService();
		// // EditorsUI.getSpellingService();
		// if (spellingService.getActiveSpellingEngineDescriptor(fPreferenceStore) == null) return
		// null;
		//
		// IReconcilingStrategy strategy = new SpellingReconcileStrategy(sourceViewer,
		// spellingService);
		// MonoReconciler reconciler = new MonoReconciler(strategy, false);
		// reconciler.setDelay(500);
		// return reconciler;
	}

	@SuppressWarnings("unused")
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		if (true) return super.getTextHover(sourceViewer, contentType);
		// Add hover support for images
		return new MDTextHover();
	}
}
