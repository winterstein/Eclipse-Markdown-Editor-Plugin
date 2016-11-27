package winterwell.markdown.pagemodel;

import java.io.File;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.markdownj.MarkdownProcessor;
import org.pegdown.PegDownProcessor;

import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Configuration.Builder;
import com.github.rjeschke.txtmark.Processor;

import winterwell.markdown.MarkdownUI;
import winterwell.markdown.preferences.PrefPageGeneral;
import winterwell.markdown.preferences.Prefs;
import winterwell.markdown.util.FailureException;
import winterwell.markdown.util.FileUtils;
import winterwell.markdown.util.Process;
import winterwell.markdown.util.Strings;

public class MdConverter {

	private IPreferenceStore store;

	public MdConverter() {
		super();
		store = MarkdownUI.getDefault().getPreferenceStore();
	}

	public String convert(String text) {
		switch (store.getString(Prefs.PREF_MD_CONVERTER)) {
			case Prefs.KEY_MARDOWNJ:
				return useMarkDownJ(text);
			case Prefs.KEY_PEGDOWN:
				return usePegDown(text);
			case Prefs.KEY_COMMONMARK:
				return useCommonMark(text);
			case Prefs.KEY_TXTMARK:
				return useTxtMark(text);
			case Prefs.PREF_EXTERNAL_COMMAND:
				return useExternalCli(text);
		}
		return "";
	}

	// Use MarkdownJ
	private String useMarkDownJ(String text) {
		MarkdownProcessor markdown = new MarkdownProcessor();
		return markdown.markdown(text);
	}

	// Use PegDown
	private String usePegDown(String text) {
		PegDownProcessor pegdown = new PegDownProcessor();
		return pegdown.markdownToHtml(text);
	}

	// Use CommonMark
	private String useCommonMark(String text) {
		Parser parser = Parser.builder().build();
		Node document = parser.parse(text);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}

	// Use TxtMark
	private String useTxtMark(String text) {
		boolean safeMode = store.getBoolean(Prefs.PREF_TXTMARK_SAFEMODE);
		boolean extended = store.getBoolean(Prefs.PREF_TXTMARK_EXTENDED);

		Builder builder = Configuration.builder();
		if (safeMode) builder.enableSafeMode();
		if (extended) builder.forceExtentedProfile();
		Configuration config = builder.build();
		return Processor.process(text, config);
	}

	// Run external command
	private String useExternalCli(String text) {
		String cmd = store.getString(PrefPageGeneral.PREF_EXTERNAL_COMMAND);
		if (Strings.isBlank(cmd) || (cmd.startsWith("(") && cmd.contains("MarkdownJ"))) {
			return "No external markdown converter specified; update preferences.";
		}

		try {
			final File md = File.createTempFile("tmp", ".md");
			FileUtils.write(md, text);
			Process process = new Process(cmd + " " + md.getAbsolutePath());
			process.run();
			int ok = process.waitFor(10000);
			if (ok != 0) throw new FailureException(cmd + " failed:\n" + process.getError());
			String html = process.getOutput();
			FileUtils.delete(md);
			return html;
		} catch (Exception e) {}
		return "External markdown convertion <strong>failed</strong>; update preferences.";
	}
}
