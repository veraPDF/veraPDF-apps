package org.verapdf.cli;

import com.beust.jcommander.*;
import org.verapdf.features.FeatureObjectType;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.Profiles;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class FormatterHelper extends DefaultUsageFormatter {

	private static final String FLAVOUR = "flavour";

	public FormatterHelper(JCommander commander) {
		super(commander);
	}

	@Override
	public void appendAllParametersDetails(StringBuilder out, int indentCount, String indent, List<ParameterDescription> sortedParameters) {
		if (sortedParameters.size() > 0) {
			out.append(indent).append("  Options:\n");
		}

		for (ParameterDescription pd : sortedParameters) {
			WrappedParameter parameter = pd.getParameter();
			String description = pd.getDescription();
			boolean hasDescription = !description.isEmpty();

			// First line, command name
			out.append(indent)
			   .append("  ")
			   .append(parameter.required() ? "* " : "  ")
			   .append(pd.getNames())
			   .append("\n");

			if (hasDescription) {
				wrapDescription(out, indentCount, s(indentCount) + description);
			}
			Object def = pd.getDefault();

			if (pd.isDynamicParameter()) {
				String syntax = "Syntax: " + parameter.names()[0] + "key" + parameter.getAssignment() + "value";

				if (hasDescription) {
					out.append(newLineAndIndent(indentCount));
				} else {
					out.append(s(indentCount));
				}
				out.append(syntax);
			}

			if (def != null && !pd.isHelp()) {
				String displayedDef = Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
				String defaultText = "Default: " + (parameter.password() ? "********" : displayedDef);

				if (hasDescription) {
					out.append(newLineAndIndent(indentCount));
				} else {
					out.append(s(indentCount));
				}
				out.append(defaultText);
			}
			Class<?> type = pd.getParameterized().getType();

			if (type.isEnum()) {
				String valueList;
				if (PDFAFlavour.class.getCanonicalName().equals(type.getName())) {
					EnumSet<PDFAFlavour> flavours = EnumSet.copyOf(Profiles.getVeraProfileDirectory().getPDFAFlavours());
					if (FLAVOUR.equals(pd.getParameterized().getName())) {
						flavours.add(PDFAFlavour.NO_FLAVOUR);
					}
					valueList = flavours.toString();
				} else {
					valueList = EnumSet.allOf((Class<? extends Enum>) type).toString();
				}
				String possibleValues = "Possible Values: " + valueList;

				// Prevent duplicate values list, since it is set as 'Options: [values]' if the description
				// of an enum field is empty in ParameterDescription#init(..)
				if (!description.contains("Options: " + valueList)) {
					if (hasDescription) {
						out.append(newLineAndIndent(indentCount));
					} else {
						out.append(s(indentCount));
					}
					out.append(possibleValues);
				}
			}

			if (List.class.getCanonicalName().equals(type.getCanonicalName())) {
				Type fieldGenericType = pd.getParameterized().findFieldGenericType();
				String valueList = null;
				if (FeatureObjectType.class.equals(fieldGenericType)) {
					valueList = Arrays.asList(FeatureObjectType.values()).toString();
				}
				if (valueList != null) {
					String possibleValues = "Possible Values: " + valueList;
					out.append(newLineAndIndent(indentCount));
					out.append(possibleValues);
				}
			}
			out.append("\n");
		}
	}

	private static String newLineAndIndent(int indent) {
		return "\n" + s(indent);
	}
}
