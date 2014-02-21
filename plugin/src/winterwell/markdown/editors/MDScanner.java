/**
 * Copyright winterwell Mathematics Ltd.
 * @author Daniel Winterstein
 * 13 Jan 2007
 */
package winterwell.markdown.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.SWT;

import winterwell.markdown.Activator;
import winterwell.markdown.preferences.MarkdownPreferencePage;

/**
 * 
 *
 * @author Daniel Winterstein
 */
public class MDScanner extends RuleBasedScanner {
	ColorManager cm;
    public MDScanner(ColorManager cm) {
    	this.cm = cm;
    	IPreferenceStore pStore = Activator.getDefault().getPreferenceStore();
    	Token heading = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, MarkdownPreferencePage.PREF_HEADER)), null, SWT.BOLD));
    	Token comment = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, MarkdownPreferencePage.PREF_COMMENT))));
    	Token emphasis = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, MarkdownPreferencePage.PREF_DEFUALT)), null, SWT.ITALIC));
    	Token list = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, MarkdownPreferencePage.PREF_HEADER)), null, SWT.BOLD));
    	Token link = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, MarkdownPreferencePage.PREF_LINK)), null, TextAttribute.UNDERLINE));
    	Token code = new Token(new TextAttribute(
    			cm.getColor(PreferenceConverter.getColor(pStore, MarkdownPreferencePage.PREF_CODE)),
    			cm.getColor(PreferenceConverter.getColor(pStore, MarkdownPreferencePage.PREF_CODE_BG)),
    			SWT.NORMAL));
        setRules(new IRule[] {
           new LinkRule(link),
           new HeaderRule(heading),
           new HeaderWithUnderlineRule(heading),
           new ListRule(list),
           new EmphasisRule("_", emphasis),
           new EmphasisRule("***", emphasis),
           new EmphasisRule("**", emphasis),
           new EmphasisRule("*", emphasis),
           new EmphasisRule("``", code),
           new EmphasisRule("`", code),
           new MultiLineRule("<!--", "-->", comment),
           // WhitespaceRule messes up with the rest of rules
//           new WhitespaceRule(new IWhitespaceDetector() {
//              public boolean isWhitespace(char c) {
//                 return Character.isWhitespace(c);
//              }
//           }),
        });
     }
}
