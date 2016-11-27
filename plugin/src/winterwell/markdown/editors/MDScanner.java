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
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;

import winterwell.markdown.MarkdownUI;
import winterwell.markdown.preferences.PrefPageGeneral;

/**
 * 
 *
 * @author Daniel Winterstein
 */
public class MDScanner extends RuleBasedScanner {
	ColorManager cm;
    public MDScanner(ColorManager cm) {
    	this.cm = cm;
    	IPreferenceStore pStore = MarkdownUI.getDefault().getPreferenceStore();
    	Token heading = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, PrefPageGeneral.PREF_HEADER)), null, SWT.BOLD));
    	Token comment = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, PrefPageGeneral.PREF_COMMENT))));
    	Token emphasis = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, PrefPageGeneral.PREF_DEFAULT)), null, SWT.ITALIC));
    	Token list = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, PrefPageGeneral.PREF_HEADER)), null, SWT.BOLD));
    	Token link = new Token(new TextAttribute(cm.getColor(PreferenceConverter.getColor(pStore, PrefPageGeneral.PREF_LINK)), null, TextAttribute.UNDERLINE));
    	Token code = new Token(new TextAttribute(
    			cm.getColor(PreferenceConverter.getColor(pStore, PrefPageGeneral.PREF_CODE)),
    			cm.getColor(PreferenceConverter.getColor(pStore, PrefPageGeneral.PREF_CODE_BG)),
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
