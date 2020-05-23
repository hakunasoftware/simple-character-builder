package simplecharacterbuilder.characterbuilder.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentChangeListener implements DocumentListener {
	private Runnable action;

	public DocumentChangeListener(Runnable action) {
		this.action = action;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.action.run();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		this.action.run();

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.action.run();

	}

}
