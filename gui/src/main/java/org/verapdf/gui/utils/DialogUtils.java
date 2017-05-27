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
		throw new AssertionError("Shoul'd never happen."); //$NON-NLS-1$
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
