/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * VeraPDF Library GUI is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * VeraPDF Library GUI as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.gui.utils;

import java.awt.*;

/**
 * @author Maksim Bezrukov
 */
public final class GUIConstants {
	// Paths
	private static final String imgRoot = "org/verapdf/gui/images/"; //$NON-NLS-1$
	public static final String LOGO_NAME = imgRoot + "veraPDF-logo.jpg"; //$NON-NLS-1$
	public static final String PARTNERS_NAME = imgRoot + "partners.png"; //$NON-NLS-1$
	public static final String XML_LOGO_NAME = imgRoot + "xml-logo.png"; //$NON-NLS-1$
	public static final String HTML_LOGO_NAME = imgRoot + "html-logo.png"; //$NON-NLS-1$

	// GUI Colors
	public static final Color LOGO_BACKGROUND = Color.WHITE;
	public static final Color PARTNERS_BACKGROUND = Color.WHITE;
	
	// URLs
	public static final String LOGO_LINK_TEXT = "Visit veraPDF.org"; //$NON-NLS-1$
	public static final String LOGO_LINK_URL = "http://www.verapdf.org"; //$NON-NLS-1$
	public static final String DOCS_LINK_URL = "http://docs.verapdf.org";
	public static final String DOCS_GUI_LINK_URL = DOCS_LINK_URL + "/gui";
	public static final String DOCS_VALIDATION_LINK_URL = DOCS_LINK_URL + "/validation";
	public static final String DOCS_POLICY_LINK_URL = DOCS_LINK_URL + "/policy";

	// File extensions
	public static final String PDF = "pdf"; //$NON-NLS-1$
	public static final String XML = "xml"; //$NON-NLS-1$
	public static final String HTML = "html"; //$NON-NLS-1$
	public static final String SCH = "sch"; //$NON-NLS-1$
	public static final String XSL = "xsl"; //$NON-NLS-1$
	public static final String XSLT = "xslt"; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$

	// Symbols
	public static final String PLUS = "+";
	public static final String CROSS = "x";

	// TODO: Messages and Dialog / Button text, need to be separated
	public static final String ERROR = "Error"; //$NON-NLS-1$
	public static final String WARNING = "Warning"; //$NON-NLS-1$
	public static final String CHOOSE_PDF_BUTTON_TEXT = "Choose PDF";
	public static final String PDF_NOT_CHOSEN_TEXT = "PDF file not chosen";
	public static final String FIX_METADATA_FOLDER_CHOOSE_BUTTON = "Choose";
	public static final String CHOOSE_PROFILE_BUTTON_TEXT = "Choose Profile";
	public static final String CHOOSE_POLICY_BUTTON_TEXT = "Choose Policy";
	public static final String POLICY_TITLE = "Policy Creator";
	public static final String CHOOSEN_PROFILE_TEXTFIELD_DEFAULT_TEXT = "Please, specify validation profile";
	public static final String CUSTOM_PROFILE_COMBOBOX_TEXT = "Custom profile";
	public static final String AUTO_FLAVOUR_COMBOBOX_TEXT = "Auto-detect";
	public static final String VALIDATION_PROFILE_NOT_CHOSEN = "Validation profile not chosen";
	public static final String OK = "OK"; //$NON-NLS-1$
	public static final String CANCEL = "Cancel"; //$NON-NLS-1$
	public static final String NUM_VALIDATION_ERROR = "Please enter a valid number";
	public static final String ARG_REQUIRED ="Argument required";
	public static final String POLICY_PROFILE_NOT_CHOSEN = "Policy file not chosen";
	public static final String VALIDATE_BUTTON_TEXT = "Execute";
	public static final String VALIDATION_OK = "PDF file is compliant with Validation Profile requirements";
	public static final String VALIDATION_FALSE = "PDF file is not compliant with Validation Profile requirements";
	public static final String SAVE_REPORT_BUTTON_TEXT = "Save XML";
	public static final String VIEW_REPORT_BUTTON_TEXT = "View XML";
	public static final String SAVE_HTML_REPORT_BUTTON_TEXT = "Save HTML";
	public static final String VIEW_HTML_REPORT_BUTTON_TEXT = "View HTML";
	public static final String REPORT = "Report";
	public static final String ERROR_SAVING_REPORT = "Unresolvable problem occured when saving the %S report.";
	public static final String IOEXCEP_OPENING_REPORT = "IOException when opening the %S report.";
	public static final String IOEXCEP_SAVING_REPORT = "IOException when saving the %S report.";
	public static final String LABEL_TEXT = "     Please choose a PDF and a Validation Profile, then press \"" + VALIDATE_BUTTON_TEXT + "\"";
	public static final String CONSORTIUM_TEXT = "© 2015 veraPDF Consortium"; //$NON-NLS-1$
	public static final String PREFORMA_FUNDED_TEXT = "Funded by the European Commission’s PREFORMA Project";
	public static final String TITLE = "PDF/A Conformance Checker";
	public static final String ENCRYPTED_PDF = "Document is password encrypted and can not be processed.";
	public static final String ERROR_IN_PARSING = "Failed to parse pdf document.";
	public static final String ERROR_IN_VALIDATING = "Could not complete validation due to an error.";
	public static final String ERROR_IN_FEATURES = "Could not complet feature extraction due to an error.";
	public static final String ERROR_IN_INCREMETAL_SAVE = "An error occurred when saving changes";
	public static final String ERROR_INTERRUPTED = "Process has been interrupted: ";
	public static final String ERROR_EXECUTION = "Execution exception in processing: ";
	public static final String ERROR_FILE_NOT_FOUND = "Error! One of the selected files can't be found.";
	public static final String ERROR_INVALID_EXT = "Chosen file extension can only be %s.";
	public static final String ERROR_SINGLE_FILE = "Error! You can only choose a single %s.";
	public static final String ERROR_NO_VALIDATION = "Error! Validation doesn't appear to have run and has returned a null report.";
	public static final String WARN_FILE_EXISTS = "Overwrite existing file %s?.";
	public static final String TITLE_OVERWRITE = "Confirm Overwrite";
	public static final String PROCESSING_TYPE = " Report type:   ";
	public static final String CHOOSE_FLAVOUR = "PDF/A flavour:   ";
	public static final String DISPLAY_PASSED_RULES = "Include passed rules:";
	public static final String PLUGINS_ENABLED_TEXT = "Use plugins when extracting features:";
	public static final String MAX_NUMBER_FAILED_DISPLAYED_CHECKS = "Display failed checks for rule: ";
	public static final String MAX_NUMBER_FAILED_CHECKS = "Halt validation after failed checks:";
	public static final String FEATURES_GENERATED_CORRECT = "Features report generated.";
	public static final String MAX_FAILED_CHECKS_SETTING_TIP = "1 to 999999 or empty for unlimited";
	public static final String MAX_FAILED_CHECKS_DISP_SETTING_TIP = "0 to 999999 or empty for unlimited";
	public static final String FIX_METADATA_LABEL_TEXT = "       Fix metadata:";
	public static final String SELECTED_PATH_FOR_FIXER_LABEL_TEXT = "Save repaired files into the folder:";
	public static final String FIX_METADATA_PREFIX_LABEL_TEXT = "Save repaired files with prefix:";
	public static final String SELECTED_PATH_FOR_FIXER_TOOLTIP = "Select a folder for saving repaired files to. If not set then they'll be saved next to the original.";
	public static final String SELECTED_PROFILES_WIKI_PATH = "Validation Profiles wiki root:";
	public static final String CHECK_FOR_UPDATES_TEXT = "Check for Updates...";

	public static final int EMPTY_BORDER_INSETS = 5;
	public static final int FRAME_COORD_X = 100;
	public static final int FRAME_COORD_Y = 100;
	public static final int FRAME_WIDTH = 710;
	public static final int FRAME_HEIGHT = 370;

	public static final int BORDER_WIDTH = 4;
	public static final int LOGO_PANEL_BORDER_WIDTH = 10;
	public static final int XML_LOGO_BORDER_WIDTH = 4;
	public static final int HTML_LOGO_BORDER_WIDTH = 4;
	public static final int ABOUT_DIALOG_COORD_X = 150;
	public static final int ABOUT_DIALOG_COORD_Y = 150;
	public static final int SETTINGS_DIALOG_COORD_X = 150;
	public static final int SETTINGS_DIALOG_COORD_Y = 150;
	public static final int SETTINGS_DIALOG_MAX_CHARS_TEXTFIELD = 19;
	public static final int PREFERRED_WIDTH = 450;
	public static final int PREFERRED_SIZE_WIDTH = 450;
	public static final int PREFERRED_SIZE_HEIGHT = 200;
	public static final int LOGO_PANEL_PREFERRED_SIZE_WIDTH = 450;
	public static final int LOGO_PANEL_PREFERRED_SIZE_HEIGHT = 200;
	public static final int VALIDATION_SUCCESS_COLOR_RGB_GREEN = 180;
	public static final int VALIDATION_FAILED_COLOR_RGB_RED = 180;

	public static final int CHOSEN_PDF_LABEL_CONSTRAINT_GRID_X = 0;
	public static final int CHOSEN_PDF_LABEL_CONSTRAINT_GRID_Y = 0;
	public static final int CHOSEN_PDF_LABEL_CONSTRAINT_WEIGHT_X = 4;
	public static final int CHOSEN_PDF_LABEL_CONSTRAINT_WEIGHT_Y = 1;
	public static final int CHOSEN_PDF_LABEL_CONSTRAINT_GRID_WIDTH = 4;
	public static final int CHOSEN_PDF_LABEL_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_X = 4;
	public static final int CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_Y = 0;
	public static final int CHOOSE_PDF_BUTTON_CONSTRAINT_WEIGHT_X = 0;
	public static final int CHOOSE_PDF_BUTTON_CONSTRAINT_WEIGHT_Y = 1;
	public static final int CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_WIDTH = 1;
	public static final int CHOOSE_PDF_BUTTON_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int PROCESS_TYPE_LABEL_CONSTRAINT_GRID_X = 0;
	public static final int PROCESS_TYPE_LABEL_CONSTRAINT_GRID_Y = 3;
	public static final int PROCESS_TYPE_LABEL_CONSTRAINT_WEIGHT_X = 0;
	public static final int PROCESS_TYPE_LABEL_CONSTRAINT_WEIGHT_Y = 1;
	public static final int PROCESS_TYPE_LABEL_CONSTRAINT_GRID_WIDTH = 1;
	public static final int PROCESS_TYPE_LABEL_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_X = 1;
	public static final int PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_Y = 3;
	public static final int PROCESSING_TYPE_COMBOBOX_CONSTRAINT_WEIGHT_X = 0;
	public static final int PROCESSING_TYPE_COMBOBOX_CONSTRAINT_WEIGHT_Y = 1;
	public static final int PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_WIDTH = 1;
	public static final int PROCESSING_TYPE_COMBOBOX_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_X = 2;
	public static final int FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_Y = 3;
	public static final int FIX_METADATA_CHECKBOX_CONSTRAINT_WEIGHT_X = 1;
	public static final int FIX_METADATA_CHECKBOX_CONSTRAINT_WEIGHT_Y = 1;
	public static final int FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_WIDTH = 1;
	public static final int FIX_METADATA_CHECKBOX_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int CHOOSE_FLAVOUR_LABEL_CONSTRAINT_GRID_X = 3;
	public static final int CHOOSE_FLAVOUR_LABEL_CONSTRAINT_GRID_Y = 3;
	public static final int CHOOSE_FLAVOUR_LABEL_CONSTRAINT_WEIGHT_X = 0;
	public static final int CHOOSE_FLAVOUR_LABEL_CONSTRAINT_WEIGHT_Y = 1;
	public static final int CHOOSE_FLAVOUR_LABEL_CONSTRAINT_GRID_WIDTH = 1;
	public static final int CHOOSE_FLAVOUR_LABEL_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_X = 4;
	public static final int CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_Y = 3;
	public static final int CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_WEIGHT_X = 0;
	public static final int CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_WEIGHT_Y = 1;
	public static final int CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_WIDTH = 1;
	public static final int CHOOSE_FLAVOUR_COMBOBOX_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_X = 0;
	public static final int CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_Y = 1;
	public static final int CHOSEN_PROFILE_LABEL_CONSTRAINT_WEIGHT_X = 4;
	public static final int CHOSEN_PROFILE_LABEL_CONSTRAINT_WEIGHT_Y = 1;
	public static final int CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_WIDTH = 4;
	public static final int CHOSEN_PROFILE_LABEL_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_X = 4;
	public static final int CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_Y = 1;
	public static final int CHOOSE_PROFILE_BUTTON_CONSTRAINT_WEIGHT_X = 0;
	public static final int CHOOSE_PROFILE_BUTTON_CONSTRAINT_WEIGHT_Y = 1;
	public static final int CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_WIDTH = 1;
	public static final int CHOOSE_PROFILE_BUTTON_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int CHOSEN_POLICY_LABEL_CONSTRAINT_GRID_X = 0;
	public static final int CHOSEN_POLICY_LABEL_CONSTRAINT_GRID_Y = 2;
	public static final int CHOSEN_POLICY_LABEL_CONSTRAINT_WEIGHT_X = 4;
	public static final int CHOSEN_POLICY_LABEL_CONSTRAINT_WEIGHT_Y = 1;
	public static final int CHOSEN_POLICY_LABEL_CONSTRAINT_GRID_WIDTH = 4;
	public static final int CHOSEN_POLICY_LABEL_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int CHOOSE_POLICY_BUTTON_CONSTRAINT_GRID_X = 4;
	public static final int CHOOSE_POLICY_BUTTON_CONSTRAINT_GRID_Y = 2;
	public static final int CHOOSE_POLICY_BUTTON_CONSTRAINT_WEIGHT_X = 0;
	public static final int CHOOSE_POLICY_BUTTON_CONSTRAINT_WEIGHT_Y = 1;
	public static final int CHOOSE_POLICY_BUTTON_CONSTRAINT_GRID_WIDTH = 1;
	public static final int CHOOSE_POLICY_BUTTON_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int RESULT_LABEL_CONSTRAINT_GRID_X = 0;
	public static final int RESULT_LABEL_CONSTRAINT_GRID_Y = 4;
	public static final int RESULT_LABEL_CONSTRAINT_WEIGHT_X = 4;
	public static final int RESULT_LABEL_CONSTRAINT_WEIGHT_Y = 1;
	public static final int RESULT_LABEL_CONSTRAINT_GRID_WIDTH = 4;
	public static final int RESULT_LABEL_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int PROGRESSBAR_CONSTRAINT_GRID_X = 0;
	public static final int PROGRESSBAR_CONSTRAINT_GRID_Y = 4;
	public static final int PROGRESSBAR_CONSTRAINT_WEIGHT_X = 4;
	public static final int PROGRESSBAR_CONSTRAINT_WEIGHT_Y = 1;
	public static final int PROGRESSBAR_CONSTRAINT_GRID_WIDTH = 4;
	public static final int PROGRESSBAR_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int VALIDATE_BUTTON_CONSTRAINT_GRID_X = 4;
	public static final int VALIDATE_BUTTON_CONSTRAINT_GRID_Y = 4;
	public static final int VALIDATE_BUTTON_CONSTRAINT_WEIGHT_X = 0;
	public static final int VALIDATE_BUTTON_CONSTRAINT_WEIGHT_Y = 1;
	public static final int VALIDATE_BUTTON_CONSTRAINT_GRID_WIDTH = 1;
	public static final int VALIDATE_BUTTON_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int REPORT_PANEL_CONSTRAINT_GRID_X = 0;
	public static final int REPORT_PANEL_CONSTRAINT_GRID_Y = 5;
	public static final int REPORT_PANEL_CONSTRAINT_WEIGHT_X = 5;
	public static final int REPORT_PANEL_CONSTRAINT_WEIGHT_Y = 3;
	public static final int REPORT_PANEL_CONSTRAINT_GRID_WIDTH = 5;
	public static final int REPORT_PANEL_CONSTRAINT_GRID_HEIGHT = 1;

	public static final int REPORT_PANEL_LINES_NUMBER = 2;
	public static final int REPORT_PANEL_COLUMNS_NUMBER = 3;

	public static final double SCALE = 0.6;
	public static final double CONSORTIUM_FONT_SCALE = 1.3;
	public static final double PREFORMA_FUNDED_FONT_SCALE = 1;

	public static final Color BEFORE_VALIDATION_COLOR = Color.BLACK;
	public static final Color VALIDATION_SUCCESS_COLOR = new Color(0,
			VALIDATION_SUCCESS_COLOR_RGB_GREEN, 0);
	public static final Color VALIDATION_FAILED_COLOR = new Color(
			VALIDATION_FAILED_COLOR_RGB_RED, 0, 0);

	public static final int PREFERRED_POLICY_SIZE_WIDTH = 850;
	public static final int PREFERRED_POLICY_SIZE_HEIGHT = 250;
	public static final int POLICY_DIALOG_COORD_X = 150;
	public static final int POLICY_DIALOG_COORD_Y = 150;
	public static final int PREFERRED_POLICY_WINDOW_ELEMENT_HEIGHT = 25;
	public static final int POLICY_PANEL_PREFERRED_COMBO_BOX_WIDTH = PREFERRED_POLICY_SIZE_WIDTH / 4;

	private GUIConstants() {
		// Disable default constructor
	}
}
