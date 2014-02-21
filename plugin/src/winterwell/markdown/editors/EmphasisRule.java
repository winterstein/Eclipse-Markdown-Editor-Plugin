/**
 * Copyright winterwell Mathematics Ltd.
 * @author Daniel Winterstein
 * 11 Jan 2007
 */
package winterwell.markdown.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 *
 * @author Daniel Winterstein
 */
public class EmphasisRule implements IRule {
	private static char[][] fDelimiters = null;
	private char[] fSequence;
	protected IToken fToken;


	public EmphasisRule(String marker, IToken token) {
		assert marker.equals("*") || marker.equals("_") || marker.equals("**")
				|| marker.equals("***") || marker.equals("`") || marker.equals("``");
		Assert.isNotNull(token);
		fSequence = marker.toCharArray();
		fToken = token;
	}
	
	// Copied from org.eclipse.jface.text.rules.PatternRule
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		for (int i = 1; i < sequence.length; i++) {
			int c = scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if (c != sequence[i]) {
				// Non-matching character detected, rewind the scanner back to
				// the start.
				// Do not unread the first character.
				for (int j = i; j > 0; j--)
					scanner.unread();
				return false;
			}
		}
		return true;
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 * 
	 * @since 2.0
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		// Should be connected only on the right side
		scanner.unread();
		boolean sawSpaceBefore = Character.isWhitespace(scanner.read());
		if (!sawSpaceBefore && scanner.getColumn() != 0) {
			return Token.UNDEFINED;
		}

		int c = scanner.read();
		// Should be connected only on right side
		if (c != fSequence[0] || !sequenceDetected(scanner, fSequence, false)) {
			scanner.unread();
			return Token.UNDEFINED;
		}
		int readCount = fSequence.length;
		if (fDelimiters == null) {
			fDelimiters = scanner.getLegalLineDelimiters();
		}
		// Start sequence detected
		int delimiterFound = 0;
		// Is it a list item marker, or just a floating *?
		if (sawSpaceBefore) {
			boolean after = Character.isWhitespace(scanner.read());
			scanner.unread();
			if (after)
				delimiterFound = 2;
		}

		while (delimiterFound < 2
				&& (c = scanner.read()) != ICharacterScanner.EOF) {
			readCount++;

			if (!sawSpaceBefore && c == fSequence[0]
					&& sequenceDetected(scanner, fSequence, false)) {
				return fToken;
			}

			int i;
			for (i = 0; i < fDelimiters.length; i++) {
				if (c == fDelimiters[i][0]
						&& sequenceDetected(scanner, fDelimiters[i], true)) {
					delimiterFound++;
					break;
				}
			}
			if (i == fDelimiters.length)
				delimiterFound = 0;
			sawSpaceBefore = Character.isWhitespace(c);
		}
		// Reached ICharacterScanner.EOF
		for (; readCount > 0; readCount--)
			scanner.unread();
		return Token.UNDEFINED;
	}
	
}
