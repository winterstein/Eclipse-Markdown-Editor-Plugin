package winterwell.markdown.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * An overlaying preference store.
 *
 * @since 2.1
 */
public class OverlayPreferenceStore implements IPreferenceStore {

	/**
	 * Descriptor used to denote data types.
	 */
	public static final class TypeDescriptor {

		private TypeDescriptor() {}
	}

	public static final TypeDescriptor BOOLEAN = new TypeDescriptor();
	public static final TypeDescriptor DOUBLE = new TypeDescriptor();
	public static final TypeDescriptor FLOAT = new TypeDescriptor();
	public static final TypeDescriptor INT = new TypeDescriptor();
	public static final TypeDescriptor LONG = new TypeDescriptor();
	public static final TypeDescriptor STRING = new TypeDescriptor();

	/**
	 * Data structure for the overlay key.
	 */
	public static class OverlayKey {

		TypeDescriptor fDescriptor;
		String fKey;

		public OverlayKey(TypeDescriptor descriptor, String key) {
			fDescriptor = descriptor;
			fKey = key;
		}
	}

	/*
	 * @see IPropertyChangeListener
	 */
	private class PropertyListener implements IPropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			OverlayKey key = findOverlayKey(event.getProperty());
			if (key != null) propagateProperty(fParent, key, fStore);
		}
	}

	/** The parent preference store. */
	private IPreferenceStore fParent;
	/** This store. */
	private IPreferenceStore fStore;
	/** The keys of this store. */
	private OverlayKey[] fOverlayKeys;
	/** The property listener. */
	private PropertyListener fPropertyListener;
	private boolean fLoaded;

	/**
	 * Creates and returns a new overlay preference store.
	 *
	 * @param parent the parent preference store
	 * @param overlayKeys the overlay keys
	 */
	public OverlayPreferenceStore(IPreferenceStore parent, OverlayKey[] overlayKeys) {
		fParent = parent;
		fOverlayKeys = overlayKeys;
		fStore = new PreferenceStore();
	}

	/**
	 * Tries to find and return the overlay key for the given preference key string.
	 *
	 * @param key the preference key string
	 * @return the overlay key or <code>null</code> if none can be found
	 */
	private OverlayKey findOverlayKey(String key) {
		for (int i = 0; i < fOverlayKeys.length; i++) {
			if (fOverlayKeys[i].fKey.equals(key)) return fOverlayKeys[i];
		}
		return null;
	}

	/**
	 * Tells whether the given preference key string is covered by this overlay store.
	 *
	 * @param key the preference key string
	 * @return <code>true</code> if this overlay store covers the given key
	 */
	private boolean covers(String key) {
		return (findOverlayKey(key) != null);
	}

	/**
	 * Propagates the given overlay key from the orgin to the target preference store.
	 *
	 * @param orgin the source preference store
	 * @param key the overlay key
	 * @param target the preference store to which the key is propagated
	 */
	private void propagateProperty(IPreferenceStore orgin, OverlayKey key, IPreferenceStore target) {

		if (orgin.isDefault(key.fKey)) {
			if (!target.isDefault(key.fKey)) target.setToDefault(key.fKey);
			return;
		}

		TypeDescriptor d = key.fDescriptor;
		if (BOOLEAN == d) {

			boolean originValue = orgin.getBoolean(key.fKey);
			boolean targetValue = target.getBoolean(key.fKey);
			if (targetValue != originValue) target.setValue(key.fKey, originValue);

		} else if (DOUBLE == d) {

			double originValue = orgin.getDouble(key.fKey);
			double targetValue = target.getDouble(key.fKey);
			if (targetValue != originValue) target.setValue(key.fKey, originValue);

		} else if (FLOAT == d) {

			float originValue = orgin.getFloat(key.fKey);
			float targetValue = target.getFloat(key.fKey);
			if (targetValue != originValue) target.setValue(key.fKey, originValue);

		} else if (INT == d) {

			int originValue = orgin.getInt(key.fKey);
			int targetValue = target.getInt(key.fKey);
			if (targetValue != originValue) target.setValue(key.fKey, originValue);

		} else if (LONG == d) {

			long originValue = orgin.getLong(key.fKey);
			long targetValue = target.getLong(key.fKey);
			if (targetValue != originValue) target.setValue(key.fKey, originValue);

		} else if (STRING == d) {

			String originValue = orgin.getString(key.fKey);
			String targetValue = target.getString(key.fKey);
			if (targetValue != null && originValue != null && !targetValue.equals(originValue))
				target.setValue(key.fKey, originValue);

		}
	}

	/**
	 * Propagates all overlay keys from this store to the parent store.
	 */
	public void propagate() {
		for (int i = 0; i < fOverlayKeys.length; i++)
			propagateProperty(fStore, fOverlayKeys[i], fParent);
	}

	/**
	 * Loads the given key from the orgin into the target.
	 *
	 * @param orgin the source preference store
	 * @param key the overlay key
	 * @param target the preference store to which the key is propagated
	 * @param forceInitialization if <code>true</code> the value in the target gets initialized
	 *            before loading
	 */
	private void loadProperty(IPreferenceStore orgin, OverlayKey key, IPreferenceStore target,
			boolean forceInitialization) {
		TypeDescriptor d = key.fDescriptor;
		if (BOOLEAN == d) {

			if (forceInitialization) target.setValue(key.fKey, true);
			target.setValue(key.fKey, orgin.getBoolean(key.fKey));
			target.setDefault(key.fKey, orgin.getDefaultBoolean(key.fKey));

		} else if (DOUBLE == d) {

			if (forceInitialization) target.setValue(key.fKey, 1.0D);
			target.setValue(key.fKey, orgin.getDouble(key.fKey));
			target.setDefault(key.fKey, orgin.getDefaultDouble(key.fKey));

		} else if (FLOAT == d) {

			if (forceInitialization) target.setValue(key.fKey, 1.0F);
			target.setValue(key.fKey, orgin.getFloat(key.fKey));
			target.setDefault(key.fKey, orgin.getDefaultFloat(key.fKey));

		} else if (INT == d) {

			if (forceInitialization) target.setValue(key.fKey, 1);
			target.setValue(key.fKey, orgin.getInt(key.fKey));
			target.setDefault(key.fKey, orgin.getDefaultInt(key.fKey));

		} else if (LONG == d) {

			if (forceInitialization) target.setValue(key.fKey, 1L);
			target.setValue(key.fKey, orgin.getLong(key.fKey));
			target.setDefault(key.fKey, orgin.getDefaultLong(key.fKey));

		} else if (STRING == d) {

			if (forceInitialization) target.setValue(key.fKey, "1"); //$NON-NLS-1$
			target.setValue(key.fKey, orgin.getString(key.fKey));
			target.setDefault(key.fKey, orgin.getDefaultString(key.fKey));

		}
	}

	/**
	 * Loads the values from the parent into this store.
	 */
	public void load() {
		for (int i = 0; i < fOverlayKeys.length; i++)
			loadProperty(fParent, fOverlayKeys[i], fStore, true);

		fLoaded = true;
	}

	/**
	 * Loads the default values.
	 */
	public void loadDefaults() {
		for (int i = 0; i < fOverlayKeys.length; i++)
			setToDefault(fOverlayKeys[i].fKey);
	}

	/**
	 * Starts to listen for changes.
	 */
	public void start() {
		if (fPropertyListener == null) {
			fPropertyListener = new PropertyListener();
			fParent.addPropertyChangeListener(fPropertyListener);
		}
	}

	/**
	 * Stops to listen for changes.
	 */
	public void stop() {
		if (fPropertyListener != null) {
			fParent.removePropertyChangeListener(fPropertyListener);
			fPropertyListener = null;
		}
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		fStore.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		fStore.removePropertyChangeListener(listener);
	}

	@Override
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
		fStore.firePropertyChangeEvent(name, oldValue, newValue);
	}

	@Override
	public boolean contains(String name) {
		return fStore.contains(name);
	}

	@Override
	public boolean getBoolean(String name) {
		return fStore.getBoolean(name);
	}

	@Override
	public boolean getDefaultBoolean(String name) {
		return fStore.getDefaultBoolean(name);
	}

	@Override
	public double getDefaultDouble(String name) {
		return fStore.getDefaultDouble(name);
	}

	@Override
	public float getDefaultFloat(String name) {
		return fStore.getDefaultFloat(name);
	}

	@Override
	public int getDefaultInt(String name) {
		return fStore.getDefaultInt(name);
	}

	@Override
	public long getDefaultLong(String name) {
		return fStore.getDefaultLong(name);
	}

	@Override
	public String getDefaultString(String name) {
		return fStore.getDefaultString(name);
	}

	@Override
	public double getDouble(String name) {
		return fStore.getDouble(name);
	}

	@Override
	public float getFloat(String name) {
		return fStore.getFloat(name);
	}

	@Override
	public int getInt(String name) {
		return fStore.getInt(name);
	}

	@Override
	public long getLong(String name) {
		return fStore.getLong(name);
	}

	@Override
	public String getString(String name) {
		return fStore.getString(name);
	}

	@Override
	public boolean isDefault(String name) {
		return fStore.isDefault(name);
	}

	@Override
	public boolean needsSaving() {
		return fStore.needsSaving();
	}

	@Override
	public void putValue(String name, String value) {
		if (covers(name)) fStore.putValue(name, value);
	}

	@Override
	public void setDefault(String name, double value) {
		if (covers(name)) fStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, float value) {
		if (covers(name)) fStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, int value) {
		if (covers(name)) fStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, long value) {
		if (covers(name)) fStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, String value) {
		if (covers(name)) fStore.setDefault(name, value);
	}

	@Override
	public void setDefault(String name, boolean value) {
		if (covers(name)) fStore.setDefault(name, value);
	}

	@Override
	public void setToDefault(String name) {
		fStore.setToDefault(name);
	}

	@Override
	public void setValue(String name, double value) {
		if (covers(name)) fStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, float value) {
		if (covers(name)) fStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, int value) {
		if (covers(name)) fStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, long value) {
		if (covers(name)) fStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, String value) {
		if (covers(name)) fStore.setValue(name, value);
	}

	@Override
	public void setValue(String name, boolean value) {
		if (covers(name)) fStore.setValue(name, value);
	}

	/**
	 * The keys to add to the list of overlay keys.
	 * <p>
	 * Note: This method must be called before {@link #load()} is called.
	 * </p>
	 *
	 * @param keys an array with overlay keys
	 * @since 3.0
	 */
	public void addKeys(OverlayKey[] keys) {
		Assert.isTrue(!fLoaded);
		Assert.isNotNull(keys);

		int overlayKeysLength = fOverlayKeys.length;
		OverlayKey[] result = new OverlayKey[keys.length + overlayKeysLength];

		for (int i = 0, length = overlayKeysLength; i < length; i++)
			result[i] = fOverlayKeys[i];

		for (int i = 0, length = keys.length; i < length; i++)
			result[overlayKeysLength + i] = keys[i];

		fOverlayKeys = result;

		if (fLoaded) load();
	}
}
