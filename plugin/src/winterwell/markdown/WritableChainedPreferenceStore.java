package winterwell.markdown;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

/**
 * Provides a chained preference store where the first of the chained stores is available for writes.
 */
public class WritableChainedPreferenceStore extends ChainedPreferenceStore {

	private IPreferenceStore writeStore;

	public WritableChainedPreferenceStore(IPreferenceStore[] preferenceStores) {
		super(preferenceStores);
		this.writeStore = preferenceStores[0];
	}

	@Override
	public boolean needsSaving() {
		return writeStore.needsSaving();
	}

	@Override
	public void setValue(String name, double value) {
		writeStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, float value) {
		writeStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, int value) {
		writeStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, long value) {
		writeStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, String value) {
		writeStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, boolean value) {
		writeStore.setValue(name, value);
	}

	@Override
	public void setToDefault(String name) {
		writeStore.setToDefault(name);
	}

	@Override
	public void setDefault(String name, boolean value) {

		writeStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, double value) {
		writeStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, float value) {
		writeStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, int value) {
		writeStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, long value) {
		writeStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, String defaultObject) {
		writeStore.setDefault(name, defaultObject);
	}
}
