/**
 * Copyright winterwell Mathematics Ltd.
 * @author Daniel Winterstein
 * 11 Jan 2007
 */
package winterwell.markdown.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

/**
 * 
 *
 * @author Daniel Winterstein
 */
public class EmphasisRule extends MultiLineRule {


	public EmphasisRule(String marker, IToken token) {
		super(marker, marker, token);
		assert marker.equals("*") || marker.equals("_") || marker.equals("**")
				|| marker.equals("***");
	}
	
	@Override
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		boolean detect = super.sequenceDetected(scanner, sequence, eofAllowed);
		if (!detect) return false;
		// But is it an emphasis* or a * list item?
		for(int i=0; i <= sequence.length; i++) scanner.unread();
		char before = (char) scanner.read();
		String star = "";
		for(int i=0; i <sequence.length; i++) star += scanner.read();
		char after = (char) scanner.read();
		// Set the scanner back to where it was
		scanner.unread();
		// Is it a list item marker, or just a floating *?
		if (Character.isWhitespace(after) && Character.isWhitespace(before)) return false;
		return true;
	}
	
}
