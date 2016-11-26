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
	// private OverlayPreferenceStore spellStore;
	// private SpellingService spellService;

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
		// boolean global =
		// fPreferenceStore.getBoolean(SpellingService.PREFERENCE_SPELLING_ENABLED);
		// boolean local = fPreferenceStore.getBoolean(Prefs.PREF_SPELLING_ENABLED);
		// if (local && !global) {
		// SpellingService service = getSpellingService();
		// if (service.getActiveSpellingEngineDescriptor(getSpellStore()) == null) {
		// return super.getReconciler(viewer); // bail
		// };
		//
		// IReconcilingStrategy strategy = new SpellingReconcileStrategy(viewer, service);
		// MonoReconciler reconciler = new MonoReconciler(strategy, false);
		// reconciler.setDelay(500);
		// return reconciler;
		// }
		return super.getReconciler(viewer);
	}

	// private SpellingService getSpellingService() {
	// if (spellService == null) {
	// spellService = new SpellingService(getSpellStore());
	// }
	// return spellService;
	// }
	//
	// private IPreferenceStore getSpellStore() {
	// if (spellStore == null) {
	// OverlayKey[] keys = new OverlayKey[] {
	// new OverlayKey(OverlayPreferenceStore.BOOLEAN, SpellingService.PREFERENCE_SPELLING_ENABLED)
	// };
	// this.spellStore = new OverlayPreferenceStore(fPreferenceStore, keys);
	// this.spellStore.setValue(SpellingService.PREFERENCE_SPELLING_ENABLED, true);
	// }
	// return spellStore;
	// }

	@SuppressWarnings("unused")
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		if (true) return super.getTextHover(sourceViewer, contentType);
		// Add hover support for images
		return new MDTextHover();
	}
}
