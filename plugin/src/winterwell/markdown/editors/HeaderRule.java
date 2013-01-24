/**
 * Copyright winterwell Mathematics Ltd.
 * @author Daniel Winterstein
 * 11 Jan 2007
 */
package winterwell.markdown.editors;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;

/**
 * 
 *
 * @author Daniel Winterstein
 */
public class HeaderRule extends PatternRule {
		
	public HeaderRule(IToken token) {		
		super("#", null, token, (char) 0, true, true);
		setColumnConstraint(0);
	}
}
