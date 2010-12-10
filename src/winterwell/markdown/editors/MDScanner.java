/**
 * Copyright winterwell Mathematics Ltd.
 * @author Daniel Winterstein
 * 13 Jan 2007
 */
package winterwell.markdown.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

/**
 * 
 *
 * @author Daniel Winterstein
 */
public class MDScanner extends RuleBasedScanner {
	ColorManager cm;
    public MDScanner(ColorManager cm) {
    	this.cm = cm;      
    	Token heading = new Token(new TextAttribute(cm.getColor(MDColorConstants.HEADER), null, SWT.BOLD));
        Token comment = new Token(new TextAttribute(cm.getColor(MDColorConstants.COMMENT)));
        Token emphasis = new Token(new TextAttribute(cm.getColor(MDColorConstants.DEFAULT), null, SWT.ITALIC));
        setRules(new IRule[] {           
           new HeaderRule(heading),
           new EmphasisRule("_", emphasis),
           new EmphasisRule("***", emphasis),
           new EmphasisRule("**", emphasis),
           new EmphasisRule("*", emphasis),
           new MultiLineRule("<!--", "-->", comment),
           new WhitespaceRule(new IWhitespaceDetector() {
              public boolean isWhitespace(char c) {
                 return Character.isWhitespace(c);
              }
           }),
        });
     }
}
