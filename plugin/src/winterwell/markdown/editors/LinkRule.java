/**
 * Copyright winterwell Mathematics Ltd.
 * @author Daniel Winterstein
 * 11 Jan 2007
 */
package winterwell.markdown.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.core.runtime.Assert;

/**
 * 
 *
 * @author Amir Pakdel
 */
public class LinkRule implements IRule {
	private static char[][] fDelimiters = null;
	protected IToken fToken;

	public LinkRule(IToken token) {
		Assert.isNotNull(token);
		fToken= token;
	}
	
	/*
	 * @see IPredicateRule#getSuccessToken()
	 * @since 2.0
	 */
	public IToken getSuccessToken() {
		return fToken;
	}


	// Copied from org.eclipse.jface.text.rules.PatternRule
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		for (int i= 1; i < sequence.length; i++) {
			int c= scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if (c != sequence[i]) {
				// Non-matching character detected, rewind the scanner back to the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j= i-1; j > 0; j--)
					scanner.unread();
				return false;
			}
		}
		return true;
	}
	
	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 * @since 2.0
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		int c;
		if ((c = scanner.read()) != '[') {
			if ((c != 'h' || ( !sequenceDetected(scanner, "http://".toCharArray(), false) && !sequenceDetected(scanner, "https://".toCharArray(), false) ))
					&& (c != 'f' || !sequenceDetected(scanner, "ftp://".toCharArray(), false)) ) {
				// Not even a non-standard link
				scanner.unread();
				return Token.UNDEFINED;
			}
			
			//+ preventing NPE (Non-standard link should not be below as comment above suggests) by Paul Verest
			if (fDelimiters == null) {
				scanner.unread();
				return Token.UNDEFINED;
			}
			
			// Non-standard link
			while ((c = scanner.read()) != ICharacterScanner.EOF && !Character.isWhitespace(c)) {
				for (int i = 0; i < fDelimiters.length; i++) {
					if (c == fDelimiters[i][0] && sequenceDetected(scanner, fDelimiters[i], true)) {
						return fToken;
					}
				}
			}
			return fToken;
		}
		if (fDelimiters == null) {
			fDelimiters = scanner.getLegalLineDelimiters();
		}
		int readCount = 1;
		
		// Find '](' and then find ')'
		boolean sequenceFound = false;
		int delimiterFound = 0;
		while ((c = scanner.read()) != ICharacterScanner.EOF && delimiterFound < 2) {
			readCount++;
			if ( !sequenceFound && c == ']') {
				c = scanner.read();
				if (c == '(') {
					readCount++;
					sequenceFound = true;
				} else {
					scanner.unread();
				}
			} else if (c == ')') { // '](' is already found
				return fToken;
			}
			
			int i;
			for (i = 0; i < fDelimiters.length; i++) {
				if (c == fDelimiters[i][0] && sequenceDetected(scanner, fDelimiters[i], true)) {
					delimiterFound ++;
					break;
				}	
			}
			if (i == fDelimiters.length)
				delimiterFound = 0;
		}
		
		for (; readCount > 0; readCount--)
			scanner.unread();
		return Token.UNDEFINED;
	}
	
}
