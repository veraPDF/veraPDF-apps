/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015-2024, veraPDF Consortium <info@verapdf.org>
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
/**
 * 
 */
package org.verapdf.gui.utils;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 27 May 2017:14:45:35
 */

public final class DialogUtils {

	private DialogUtils() {
		throw new AssertionError("Should never happen."); //$NON-NLS-1$
	}

	public static void errorDialog(final Component parent, final String message) {
		JOptionPane.showMessageDialog(parent, message, GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
	}

	public static void errorDialog(final Component parent, final String message, final Logger logger, final Throwable cause) {
		JOptionPane.showMessageDialog(parent, message, GUIConstants.ERROR, JOptionPane.ERROR_MESSAGE);
		logger.log(Level.SEVERE, message, cause);
	}

	public static void warningDialog(final Component parent, final String message) {
		JOptionPane.showMessageDialog(parent, message, GUIConstants.WARNING, JOptionPane.WARNING_MESSAGE);
	}

	public static void warningDialog(final Component parent, final String message, final Logger logger, final Throwable cause) {
		JOptionPane.showMessageDialog(parent, message, GUIConstants.WARNING, JOptionPane.WARNING_MESSAGE);
		logger.log(Level.WARNING, message, cause);
	}
}
