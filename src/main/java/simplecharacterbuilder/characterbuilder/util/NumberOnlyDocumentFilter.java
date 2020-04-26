package simplecharacterbuilder.characterbuilder.util;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumberOnlyDocumentFilter extends DocumentFilter {
	private final String regex;

	public NumberOnlyDocumentFilter(Mode mode) {
		this.regex = mode.regex;
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a)
			throws BadLocationException {
		modifyText(fb, offset, length, str, null);
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String str, AttributeSet a) throws BadLocationException {
		modifyText(fb, offset, 0, str, null);
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		modifyText(fb, offset, length, "", null);
	}

	private void modifyText(FilterBypass fb, int offset, int length, String str, AttributeSet a)
			throws BadLocationException {
		String text = fb.getDocument().getText(0, fb.getDocument().getLength());
		int currentTextLength = text.length();

		text = new StringBuilder(text.substring(0, offset)).append(str)
				.append(text.substring(offset + length, text.length())).toString();

		if (text.isEmpty() || text.equals("0")) {
			fb.replace(0, currentTextLength, "0", a);
			return;
		}

		if (text.startsWith("0") && str != null && str.length() != 0 && str.matches(this.regex)) {
			fb.replace(0, 1, str, a);
			return;
		}

		if (!text.matches(this.regex)) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}

		fb.replace(offset, length, str, a);
	}
	
	public enum Mode{
		UNRESTRICTED("^[0-9]+"), PERCENTAGE("^((100)|([0-9]{1,2}))$");
		
		String regex;
		private Mode(String regex)
		{
			this.regex = regex;
		}
	}

}