package org.verapdf.gui.tools;

/**
 * @author Evgeniy Muravitskiy
 */
public enum ProcessingType {

	VALIDATING_AND_FEATURES(GUIConstants.VALIDATING_AND_FEATURES),

	VALIDATING(GUIConstants.VALIDATING),

	FEATURES(GUIConstants.FEATURES);

	private final String value;

	ProcessingType(String value) {
		this.value = value;
	}

	public boolean isValidating() {
		return this == VALIDATING || this == VALIDATING_AND_FEATURES;
	}

	public boolean isFeatures() {
		return this == FEATURES || this == VALIDATING_AND_FEATURES;
	}

	public static ProcessingType fromString(String string) throws IllegalArgumentException{
		if(string == null)
			throw new IllegalArgumentException("String to be parsed into ProcessingType cannot be null");

		switch(string) {
			case GUIConstants.VALIDATING:
				return VALIDATING;
			case GUIConstants.FEATURES:
				return FEATURES;
			case GUIConstants.VALIDATING_AND_FEATURES:
				return VALIDATING_AND_FEATURES;
			default:
				throw new IllegalArgumentException("String can't be parsed into ProcessingType");
		}
	}

	public String toText() {
		switch(this) {
			case VALIDATING:
				return GUIConstants.TEXT_VALIDATING;
			case FEATURES:
				return GUIConstants.TEXT_FEATURES;
			case VALIDATING_AND_FEATURES:
				return GUIConstants.TEXT_VALIDATING_AND_FEATURES;
			default:
				throw new IllegalArgumentException("ProcessingType can't be parsed into String");
		}
	}

	@Override
	public String toString() {
		return this.value;
	}

}
