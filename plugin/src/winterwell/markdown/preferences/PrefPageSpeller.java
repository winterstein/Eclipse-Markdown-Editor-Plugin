package winterwell.markdown.preferences;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.texteditor.spelling.IPreferenceStatusMonitor;

import winterwell.markdown.MarkdownUI;
import winterwell.markdown.spelling.AbstractConfigurationBlockPreferencePage;
import winterwell.markdown.spelling.IPreferenceConfigurationBlock;
import winterwell.markdown.spelling.OverlayPreferenceStore;
import winterwell.markdown.spelling.SpellingConfigurationBlock;
import winterwell.markdown.spelling.StatusUtil;

/**
 * Spelling preference page for options specific to Markdown.
 */
public class PrefPageSpeller extends AbstractConfigurationBlockPreferencePage {

	/** Status monitor */
	private class StatusMonitor implements IPreferenceStatusMonitor {

		@Override
		public void statusChanged(IStatus status) {
			handleStatusChanged(status);
		}
	}

	public PrefPageSpeller() {
		super();
	}

	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new SpellingConfigurationBlock(overlayPreferenceStore, new StatusMonitor());
	}

	/**
	 * Handles status changes.
	 *
	 * @param status the new status
	 */
	protected void handleStatusChanged(IStatus status) {
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}

	@Override
	protected void setDescription() {}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(MarkdownUI.getDefault().getCombinedPreferenceStore());
	}

	@Override
	protected String getHelpId() {
		return ITextEditorHelpContextIds.SPELLING_PREFERENCE_PAGE;
	}
}
