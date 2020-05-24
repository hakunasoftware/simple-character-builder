package simplecharacterbuilder.characterbuilder.util.ui;

import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

@SuppressWarnings("serial")
public class SearchableComboBox extends JComboBox<String> {
	private final SearchableDocument searchableDocument;
	private final List<Runnable> selectionListeners = new ArrayList<>();

	private String[] unfilteredOptions;
	private String regex;
	
	private boolean upDownKeyPressed;
	private boolean optionsRecentlyFiltered;
	private String lastSelection;

	public SearchableComboBox(String[] options) {
		super(options);
		this.unfilteredOptions = options;
		this.setEditable(true);
		JTextComponent editor = (JTextComponent) this.getEditor().getEditorComponent();
		editor.setDocument(this.searchableDocument = new SearchableDocument());
		editor.addKeyListener(new NavigationKeyListener());
		editor.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent evt) {
				SwingUtilities.invokeLater(() -> editor.selectAll());
				setPopupVisible(true);
			}
		});
	}
	
	public void setOptions(String[] options) {
		this.unfilteredOptions = options;
		resetOptions();
	}
	
	public void addSelectionListener(Runnable listener) {
		this.selectionListeners.add(listener);
	}

	private class SearchableDocument extends PlainDocument {
		private boolean textManipulationDisabled = false;

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (textManipulationDisabled) {
				return;
			}
			if (regex != null && !str.matches(regex)) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			super.insertString(offs, str, a);
			updateOptions();
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			if (textManipulationDisabled) {
				return;
			}
			super.remove(offs, len);
			updateOptions();
		}

		private void updateOptions() throws BadLocationException {
			String content = getText(0, getLength());
			String[] filteredOptions = (String[]) Arrays.asList(unfilteredOptions).stream()
					.filter(o -> o.toLowerCase().startsWith(content.toLowerCase())).toArray(String[]::new);
			textManipulationDisabled = true;
			setModel(new DefaultComboBoxModel<>(filteredOptions));
			setPopupVisible(filteredOptions.length > 0);
			textManipulationDisabled = false;
			optionsRecentlyFiltered = true;
		}
	}

	@Override
	public void setSelectedItem(Object anObject) {
		lastSelection = (String) anObject;
		if (upDownKeyPressed) {
			upDownKeyPressed = false;
			return;
		}
		super.setSelectedItem(anObject);
		SwingUtilities.invokeLater(() -> {
			editor.selectAll();
			resetOptions();
			selectionListeners.stream().forEach(l -> l.run());
		});
	}

	private class NavigationKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
				upDownKeyPressed = true;
				optionsRecentlyFiltered = false;
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String selection;
				if (optionsRecentlyFiltered) {
					selection = getModel().getElementAt(0);
					optionsRecentlyFiltered = false;
				} else {
					selection = lastSelection;
				}
				getEditor().setItem(selection);
				SwingUtilities.invokeLater(() -> editor.selectAll());
				resetOptions();
				selectionListeners.stream().forEach(l -> l.run());
			}
		}
	}

	private void resetOptions() {
		searchableDocument.textManipulationDisabled = true;
		setModel(new DefaultComboBoxModel<>(unfilteredOptions));
		searchableDocument.textManipulationDisabled = false;
	}

	/**
	 * Sets a regex that any input is matched against.
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}
}
