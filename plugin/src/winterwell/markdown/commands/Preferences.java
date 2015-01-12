package winterwell.markdown.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class Preferences extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(
				PlatformUI.getWorkbench().getDisplay().getActiveShell(),
				"winterwell.markdown.preferences.MarkdownPreferencePage", null, null);
		pref.open();
		return null;
	}
}
