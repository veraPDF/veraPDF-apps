package org.verapdf.gui;

import javax.swing.*;
import java.awt.*;

class ChooseFlavourRenderer extends JLabel implements ListCellRenderer<String> {

	/**
	 *
	 */
	private static final long serialVersionUID = 3740801661593829099L;

	public ChooseFlavourRenderer() {
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(final JList<? extends String> list,
	                                              final String value, final int index,
	                                              final boolean isSelected, final boolean cellHasFocus) {
		this.setText(value);
		return this;
	}
}
