/**
 * Copyright winterwell Mathematics Ltd.
 * @author Daniel Winterstein
 * 11 Jan 2007
 */
package winterwell.markdown.pagemodel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;

import winterwell.markdown.Activator;
import winterwell.markdown.StringMethods;
import winterwell.markdown.preferences.MarkdownPreferencePage;
import winterwell.utils.FailureException;
import winterwell.utils.Process;
import winterwell.utils.StrUtils;
import winterwell.utils.Utils;
import winterwell.utils.io.FileUtils;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * Understands Markdown syntax.
 * 
 * @author Daniel Winterstein
 */
public class MarkdownPage {

	/**
	 * Strip leading and trailing #s and whitespace
	 * 
	 * @param line
	 * @return cleaned up line
	 */
	private String cleanHeader(String line) {
		for (int j = 0; j < line.length(); j++) {
			char c = line.charAt(j);
			if (c != '#' && !Character.isWhitespace(c)) {
				line = line.substring(j);
				break;
			}
		}
		for (int j = line.length() - 1; j > 0; j--) {
			char c = line.charAt(j);
			if (c != '#' && !Character.isWhitespace(c)) {
				line = line.substring(0, j + 1);
				break;
			}
		}
		return line;
	}

	/**
	 * Represents information about a section header. E.g. ## Misc Warblings
	 * 
	 * @author daniel
	 */
	public class Header {
		/**
		 * 1 = top-level (i.e. #), 2= 2nd-level (i.e. ##), etc.
		 */
		final int level;
		/**
		 * The text of the Header
		 */
		final String heading;
		/**
		 * Sub-sections, if any
		 */
		final List<Header> subHeaders = new ArrayList<Header>();
		/**
		 * The line on which this header occurs.
		 */
		final int lineNumber;

		public int getLineNumber() {
			return lineNumber;
		}

		/**
		 * 
		 * @return the next section (at this depth if possible), null if none
		 */
		public Header getNext() {
			if (parent == null) {
				int ti = level1Headers.indexOf(this);
				if (ti == -1 || ti == level1Headers.size() - 1)
					return null;
				return level1Headers.get(ti + 1);
			}
			int i = parent.subHeaders.indexOf(this);
			assert i != -1 : this;
			if (i == parent.subHeaders.size() - 1)
				return parent.getNext();
			return parent.subHeaders.get(i + 1);
		}
		/**
		 * 
		 * @return the next section (at this depth if possible), null if none
		 */
		public Header getPrevious() {
			if (parent == null) {
				int ti = level1Headers.indexOf(this);
				if (ti == -1 || ti == 0)
					return null;
				return level1Headers.get(ti - 1);
			}
			int i = parent.subHeaders.indexOf(this);
			assert i != -1 : this;
			if (i == 0)
				return parent.getPrevious();
			return parent.subHeaders.get(i - 1);
		}
		

		/**
		 * The parent section. Can be null.
		 */
		private Header parent;

		/**
		 * Create a marker for a section Header
		 * 
		 * @param level
		 *            1 = top-level (i.e. #), 2= 2nd-level (i.e. ##), etc.
		 * @param lineNumber
		 *            The line on which this header occurs
		 * @param heading
		 *            The text of the Header, trimmed of #s
		 * @param currentHeader
		 *            The previous Header. This is used to find the parent
		 *            section if there is one. Can be null.
		 */
		Header(int level, int lineNumber, String heading, Header currentHeader) {
			this.lineNumber = lineNumber;
			this.level = level;
			this.heading = cleanHeader(heading);
			// Heading Tree
			setParent(currentHeader);
		}

		private void setParent(Header currentHeader) {
			if (currentHeader == null) {
				parent = null;
				return;
			}
			if (currentHeader.level < level) {
				parent = currentHeader;
				parent.subHeaders.add(this);
				return;
			}
			setParent(currentHeader.parent);
		}

		public Header getParent() {
			return parent;
		}

		/**
		 * Sub-sections. May be zero-length, never null.
		 */
		public List<Header> getSubHeaders() {
			return subHeaders;
		}

		@Override
		public String toString() {
			return heading;
		}

		public int getLevel() {
			return level;
		}
	}

	/**
	 * The raw text, broken up into individual lines.
	 */
	private List<String> lines;

	/**
	 * The raw text, broken up into individual lines.
	 */
	public List<String> getText() {
		return Collections.unmodifiableList(lines);
	}

	public enum KLineType {
		NORMAL, H1, H2, H3, H4, H5, H6, BLANK,
		// TODO LIST, BLOCKQUOTE,
		/** A line marking Markdown info about the preceding line, e.g. ====== */
		MARKER,
		/** A line containing meta-data, e.g. title: My Page */
		META
	}

	/**
	 * Information about each line.
	 */
	private List<KLineType> lineTypes;
	private Map<Integer,Object> pageObjects = new HashMap<Integer, Object>();

	// TODO meta-data, footnotes, tables, link & image attributes
	private static Pattern multiMarkdownTag = Pattern.compile("^([\\w].*):(.*)");
	private Map<String, String> multiMarkdownTags = new HashMap<String, String>();
	
	// Regular expression for Github support
	private static Pattern githubURLDetection = Pattern.compile("((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");

	/**
	 * The top-level headers. FIXME handle documents which have a 2nd level
	 * header before any 1st level ones
	 */
	private final List<Header> level1Headers = new ArrayList<Header>();
	private final IPreferenceStore pStore;

	/**
	 * Create a page.
	 * 
	 * @param text
	 */
	public MarkdownPage(String text) {
		pStore = Activator.getDefault().getPreferenceStore();
		setText(text);
	}

	/**
	 * Reset the text for this page.
	 * 
	 * @param text
	 */
	private void setText(String text) {
		// Get lines
		lines = StringMethods.splitLines(text);
		// Clean out old
		level1Headers.clear();
		lineTypes = new ArrayList<KLineType>(lines.size());
		pageObjects.clear();
		// Dummy level-1 header in case there are none		
		Header dummyTopHeader = new Header(1, 0, "", null);
		level1Headers.add(dummyTopHeader);
		Header currentHeader = dummyTopHeader;		
		// Identify line types		
		int lineNum = 0;

		// Check if we should support the Multi-Markdown Metadata
		boolean multiMarkdownMetadataSupport =
				pStore.getBoolean(MarkdownPreferencePage.PREF_MULTIMARKDOWN_METADATA);
		
		// Multi-markdown header
		if (multiMarkdownMetadataSupport) {
			// The key is the text before the colon, and the data is the text
			// after the
			// colon. In the above example, notice that there are two lines of
			// information
			// for the Author key. If you end a line with “space-space-newline”,
			// the newline
			// will be included when converted to other formats.
			//
			// There must not be any whitespace above the metadata, and the
			// metadata block
			// ends with the first whitespace only line. The metadata is
			// stripped from the
			// document before it is passed on to the syntax parser.
			
			//
			// Check if the Metdatas are valid
			//
			boolean validMetadata = true;
			for (lineNum = 0; lineNum < lines.size(); lineNum++) {
				String line = lines.get(lineNum);
				if (Utils.isBlank(line)) {
					break;
				}
				Matcher m = multiMarkdownTag.matcher(line);
				if (!m.find()) {
					if (lineNum == 0) {
						// No MultiMarkdown metadata
						validMetadata = false;
						break;
					} else if (!line.matches("^\\s.*\n")) {
						// The next line was not intended (ie. it does not start
						// with a whitespace)
						validMetadata = false;
						break;
					}
				}
			}
			
			// Valid Metadatas have been found. We need to retrieve these keys/values.
			if (validMetadata) {
				String data = "";
				String tag = "";
				for (lineNum = 0; lineNum < lines.size(); lineNum++) {
					String line = lines.get(lineNum);
					if (Utils.isBlank(line)) {
						break;
					}
					Matcher m = multiMarkdownTag.matcher(line);
					if (!m.find()) {
						if (lineNum == 0) {
							break;
						}
						// Multi-line tag
						lineTypes.add(KLineType.META);
						data += StrUtils.LINEEND + line.trim();
						multiMarkdownTags.put(tag, data);
					} else {
						lineTypes.add(KLineType.META);
						tag = m.group(0);
						data = m.group(1).trim();
						if (m.group(1).endsWith(line))
							multiMarkdownTags.put(tag, data);
					}
				}
			} else {
				lineNum = 0;
			}
		}
		
		boolean githubSyntaxSupport =
				pStore.getBoolean(MarkdownPreferencePage.PREF_GITHUB_SYNTAX);
		
		boolean inCodeBlock = false;
		
		for (; lineNum < lines.size(); lineNum++) {
			String line = lines.get(lineNum);
			// Code blocks
			if (githubSyntaxSupport && line.startsWith("```")) {
				inCodeBlock = !inCodeBlock;
			}
			if (!inCodeBlock) {
				// Headings
				int h = numHash(line);
				String hLine = line;
				int hLineNum = lineNum;
				int underline = -1;
				if (lineNum != 0) {
					underline = just(line, '=') ? 1 : just(line, '-') ? 2 : -1;
				}
				if (underline != -1) {
					h = underline;
					hLineNum = lineNum - 1;
					hLine = lines.get(lineNum - 1);
					lineTypes.set(hLineNum, KLineType.values()[h]);
					lineTypes.add(KLineType.MARKER);
				}
				// Create a Header object
				if (h > 0) {
					if (underline == -1)
						lineTypes.add(KLineType.values()[h]);
					Header header = new Header(h, hLineNum, hLine, currentHeader);
					if (h == 1) {
						level1Headers.add(header);
					}
					pageObjects.put(hLineNum, header);
					currentHeader = header;
					continue;
				}
			}
			// TODO List
			// TODO Block quote
			// Blank line
			if (Utils.isBlank(line)) {
				lineTypes.add(KLineType.BLANK);
				continue;
			}
			// Normal
			lineTypes.add(KLineType.NORMAL);
		} // end line-loop
		// Remove dummy header?
		if (dummyTopHeader.getSubHeaders().size() == 0) {
			level1Headers.remove(dummyTopHeader);
		}
		if (githubSyntaxSupport) {
			/*
			 * Support Code block
			 */
			inCodeBlock = false;
			for (lineNum = 0; lineNum < lines.size(); lineNum++) {
				String line = lines.get(lineNum);
				// Found the start or end of a code block
				if (line.matches("^```.*\n")) {
					// We reverse the boolean value
					inCodeBlock = !inCodeBlock;

					// We force the line to be blank. But we mark it as normal
					// to prevent to be stripped
					lines.set(lineNum, "\n");
					lineTypes.set(lineNum, KLineType.NORMAL);
					continue;
				}
				if (inCodeBlock) {
					lines.set(lineNum, "    " + line);
				}
			}
			
			/*
			 * Support for URL Detection
			 * We search for links that are not captured by Markdown syntax
			 */
			for (lineNum = 0; lineNum < lines.size(); lineNum++) {
				String line = lines.get(lineNum);
				// When a link has been replaced we need to scan again the string
				// as the offsets have changed (we add '<' and '>' to the link to
				// be interpreted by the markdown library)
				boolean urlReplaced;

				do {
					urlReplaced = false;
					Matcher m = githubURLDetection.matcher(line);
					while (m.find()) {
						// Ignore the URL following the format <link>
						if ((m.start() - 1 >= 0) && (m.end() < line.length()) &&
							(line.charAt(m.start() - 1) == '<') &&
							(line.charAt(m.end()) == '>'))
						{
							continue;
						}
	
						// Ignore the URL following the format [description](link)
						if ((m.start() - 2 >= 0) && (m.end() < line.length()) &&
							(line.charAt(m.start() - 2) == ']') &&
							(line.charAt(m.start() - 1) == '(') &&
							(line.charAt(m.end()) == ')'))
						{
							continue;
						}
	
						// Ignore the URL following the format [description](link "title")
						if ((m.start() - 2 >= 0) && (m.end() + 1 < line.length()) &&
							(line.charAt(m.start() - 2) == ']') &&
							(line.charAt(m.start() - 1) == '(') &&
							(line.charAt(m.end()) == ' ') &&
							(line.charAt(m.end() + 1) == '"'))
						{
							continue;
						}
						
						if (m.start() - 1 >= 0) {
							// Case when the link is at the beginning of the string
							line = line.substring(0, m.start()) + "<" + m.group(0) + ">" + line.substring(m.end());
						} else {
							line = "<" + m.group(0) + ">" + line.substring(m.end());
						}
						
						// We replaced the string in the array
						lines.set(lineNum, line);
						urlReplaced = true;
						break;
					}
				} while (urlReplaced);
			}
		}
	}

	/**
	 * @param line
	 * @param c
	 * @return true if line is just cs (and whitespace at the start/end)
	 */
	boolean just(String line, char c) {
		return line.matches("\\s*"+c+"+\\s*");
	}

	/**
	 * @param line
	 * @return The number of # symbols prepending the line.
	 */
	private int numHash(String line) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) != '#')
				return i;
		}
		return line.length();
	}

	/**
	 * 
	 * @param parent
	 *            Can be null for top-level
	 * @return List of sub-headers. Never null. FIXME handle documents which
	 *         have a 2nd level header before any 1st level ones
	 */
	public List<Header> getHeadings(Header parent) {
		if (parent == null) {
			return Collections.unmodifiableList(level1Headers);
		}
		return Collections.unmodifiableList(parent.subHeaders);
	}

	// public WebPage getWebPage() {
	// WebPage page = new WebPage();
	// // Add the lines, one by one
	// boolean inParagraph = false;
	// for (int i=0; i<lines.size(); i++) {
	// String line = lines.get(i);
	// KLineType type = lineTypes.get(i);
	// switch(type) {
	// // Heading?
	// case H1: case H2: case H3:
	// case H4: case H5: case H6:
	// if (inParagraph) page.addText("</p>");
	// line = cleanHeader(line);
	// page.addText("<"+type+">"+line+"</"+type+">");
	// continue;
	// case MARKER: // Ignore
	// continue;
	// // TODO List?
	// // TODO Block quote?
	// }
	// // Paragraph end?
	// if (Utils.isBlank(line)) {
	// if (inParagraph) page.addText("</p>");
	// continue;
	// }
	// // Paragraph start?
	// if (!inParagraph) {
	// page.addText("<p>");
	// inParagraph = true;
	// }
	// // Plain text
	// page.addText(line);
	// }
	// return page;
	// }

	/**
	 * Get the HTML for this page. Uses the MarkdownJ project.
	 */
	public String html() {
		// Section numbers??
		boolean sectionNumbers = pStore
				.getBoolean(MarkdownPreferencePage.PREF_SECTION_NUMBERS);
		// Chop out multi-markdown header
		StringBuilder sb = new StringBuilder();
		assert lines.size() == lineTypes.size();
		for (int i = 0, n = lines.size(); i < n; i++) {
			KLineType type = lineTypes.get(i);
			if (type == KLineType.META)
				continue;
			String line = lines.get(i);
			if (sectionNumbers && isHeader(type) && line.contains("$section")) {
				// TODO Header section = headers.get(i);
				// String secNum = section.getSectionNumber();
				// line.replace("$section", secNum);
			}
			sb.append(line);
		}
		String text = sb.toString();
		// Use external converter?
		final String cmd = pStore
				.getString(MarkdownPreferencePage.PREF_MARKDOWN_COMMAND);
		if (Utils.isBlank(cmd)
				|| (cmd.startsWith("(") && cmd.contains("MarkdownJ"))) {
			// Use MarkdownJ
			MarkdownProcessor markdown = new MarkdownProcessor();
			// MarkdownJ doesn't convert £s for some reason
			text = text.replace("£", "&pound;");
			String html = markdown.markdown(text);
			return html;
		}
		// Attempt to run external command
		try {
			final File md = File.createTempFile("tmp", ".md");
			FileUtils.write(md, text);
			Process process = new Process(cmd+" "+md.getAbsolutePath());
			process.run();
			int ok = process.waitFor(10000);
			if (ok != 0) throw new FailureException(cmd+" failed:\n"+process.getError());
			String html = process.getOutput();
			FileUtils.delete(md);
			return html;
		} catch (Exception e) {
			throw Utils.runtime(e);
		}
	}

	/**
	 * @param type
	 * @return
	 */
	private boolean isHeader(KLineType type) {
		return type == KLineType.H1 || type == KLineType.H2
				|| type == KLineType.H3 || type == KLineType.H4
				|| type == KLineType.H5 || type == KLineType.H6;
	}

	/**
	 * Return the raw text of this page.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
		}
		return sb.toString();
	}

	/**
	 * Line type information for the raw text.
	 * 
	 * @return
	 */
	public List<KLineType> getLineTypes() {
		return Collections.unmodifiableList(lineTypes);
	}

	/**
	 * @param line
	 * @return
	 */
	public Object getPageObject(int line) {		
		return pageObjects.get(line);
	}

}
