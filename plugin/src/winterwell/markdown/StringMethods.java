/**
 * Basic String manipulation utilities.
 * (c) Winterwell 2010 and ThinkTank Mathematics 2007
 */
package winterwell.markdown;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import winterwell.utils.Mutable;
import winterwell.utils.containers.Pair;

/**
 * A collection of general-purpose String handling methods.
 * 
 * @author daniel.winterstein
 */
public final class StringMethods {

	/**
	 * Removes xml tags, comment blocks and script blocks.
	 * 
	 * @param page
	 * @return the page with all xml tags removed.
	 */
	public static String stripTags(String page) {
		// This code is rather ugly, but it does the job
		StringBuilder stripped = new StringBuilder(page.length());
		boolean inTag = false;
		// Comment blocks and script blocks are given special treatment
		boolean inComment = false;
		boolean inScript = false;
		// Go through the text
		for (int i = 0; i < page.length(); i++) {
			char c = page.charAt(i);
			// First check whether we are ignoring text
			if (inTag) {
				if (c == '>')
					inTag = false;
			} else if (inComment) {
				if (c == '>' && page.charAt(i - 1) == '-'
						&& page.charAt(i - 1) == '-') {
					inComment = false;
				}
			} else if (inScript) {
				if (c == '>' && page.substring(i - 7, i).equals("/script")) {
					inScript = false;
				}
			} else {
				// Check for the start of a tag - looks for '<' followed by any
				// non-whitespace character
				if (c == '<' && !Character.isWhitespace(page.charAt(i + 1))) {
					// Comment, script-block or tag?
					if (page.charAt(i + 1) == '!' && page.charAt(i + 2) == '-'
							&& page.charAt(i + 3) == '-') {
						inComment = true;
					} else if (i + 8 < page.length()
							&& page.substring(i + 1, i + 7).equals("script")) {
						inScript = true;
						i += 7;
					} else
						inTag = true; // Normal tag by default
				} else {
					// Append all non-tag chars
					stripped.append(c);
				}
			} // end if...
		}
		return stripped.toString();
	}
	
	/**
	 * The local line-end string. \n on unix, \r\n on windows, \r on mac.
	 */
	public static final String LINEEND = System.getProperty("line.separator");

	/**
	 * @param s
	 * @return A version of s where the first letter is uppercase and all others
	 *         are lowercase
	 */
	public static final String capitalise(final String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	/**
	 * Convert all line breaks into the system line break.
	 */
	public static final String convertLineBreaks(String text) {
		return convertLineBreaks(text, LINEEND);
	}

	/**
	 * Convert all line breaks into the specified line break.
	 */
	public static final String convertLineBreaks(String text, String br) {
		text = text.replaceAll("\r\n", br);
		text = text.replaceAll("\r", br);
		text = text.replaceAll("\n", br);
		return text;
	}

	/**
	 * @param string
	 * @param character
	 * @return the number of times character appears in the string
	 * @author Sam Halliday
	 */
	static public int countCharsInString(String string, char character) {
		int count = 0;
		for (char c : string.toCharArray()) {
			if (c == character) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * E.g.
	 * <code>findEnclosingRegion("text with a [region] inside", 15, '[', ']')</code>
	 * is (??,??)
	 * 
	 * @param text
	 * @param offset
	 * @param start
	 * @param end
	 * @return the smallest enclosed region (including start and end chars, the
	 *         1st number is inclusive, the 2nd exclusive), or null if none. So
	 *         text.subString(start,end) is the specified region
	 */
	public static Pair<Integer> findEnclosingRegion(String text, int offset,
			char startMarker, char endMarker) {
		// Forward
		int end = findEnclosingRegion2(text, offset, endMarker, 1);
		if (end == -1)
			return null;
		end++; // end is exclusive
		// Backward
		int start = findEnclosingRegion2(text, offset, startMarker, -1);
		if (start == -1)
			return null;
		// Sanity
		assert text.substring(start, end).charAt(0) == startMarker;
		assert text.substring(start, end).endsWith("" + endMarker);
		// Done
		return new Pair<Integer>(start, end);
	}

	private static int findEnclosingRegion2(String text, int offset,
			char endMarker, int direction) {
		while (offset > -1 && offset < text.length()) {
			char c = text.charAt(offset);
			if (c == endMarker)
				return offset;
			offset += direction;
		}
		return -1;
	}

	/**
	 * A convenience wrapper for
	 * {@link #findEnclosingRegion(String, int, char, char)} E.g. <code>
	 findEnclosingRegion("text with a [region] inside", 15, '[', ']') .equals("[region]");
	 </code>
	 * 
	 * @param text
	 * @param offset
	 * @param start
	 * @param end
	 * @return the smallest enclosed region (including start and end chars), or
	 *         null if none.
	 */
	public static String findEnclosingText(String text, int offset,
			char startMarker, char endMarker) {
		Pair<Integer> region = findEnclosingRegion(text, offset, startMarker,
				endMarker);
		if (region == null)
			return null;
		String s = text.substring(region.first, region.second);
		return s;
	}

	/**
	 * Format a block of text to use the given line-width. I.e. adjust the line
	 * breaks. Also known as <i>hard</i> line-wrapping. Paragraphs are
	 * recognised by a line of blank space between them (e.g. two returns).
	 * <p>
	 * Note: a side-effect of this method is that it converts all line-breaks
	 * into the local system's line-breaks. E.g. on Windows, \n will become \r\n
	 * 
	 * @param text
	 *            The text to format
	 * @param lineWidth
	 *            The number of columns in a line. Typically 78 or 80.
	 * @param respectLeadingCharacters
	 *            Can be null. If set, the specified leading characters will be
	 *            copied if the line is split. Use with " \t" to keep indented
	 *            paragraphs properly indented. Use with "> \t" to also handle
	 *            email-style quoting. Note that respected leading characters
	 *            receive no special treatment when they are used inside a
	 *            paragraph.
	 * @return A copy of text, formatted to the given line-width.
	 *         <p>
	 *         TODO: recognise paragraphs by changes in the respected leading
	 *         characters
	 */
	public static String format(String text, int lineWidth, int tabWidth,
			String respectLeadingCharacters) {
		// Switch to Linux line breaks for easier internal workings
		text = convertLineBreaks(text, "\n");
		// Find paragraphs
		List<String> paras = format2_splitParagraphs(text,
				respectLeadingCharacters);
		// Rebuild text
		StringBuilder sb = new StringBuilder(text.length() + 10);
		for (String p : paras) {
			String fp = format3_oneParagraph(p, lineWidth, tabWidth,
					respectLeadingCharacters);
			sb.append(fp);
			// Paragraphs end with a double line break
			sb.append("\n\n");
		}
		// Pop the last line breaks
		sb.delete(sb.length() - 2, sb.length());
		// Convert line breaks to system ones
		text = convertLineBreaks(sb.toString());
		// Done
		return text;
	}

	private static List<String> format2_splitParagraphs(String text,
			String respectLeadingCharacters) {
		List<String> paras = new ArrayList<String>();
		Mutable.Int index = new Mutable.Int(0);
		// TODO The characters prefacing this paragraph
		String leadingChars = "";
		while (index.value < text.length()) {
			// One paragraph
			boolean inSpace = false;
			int start = index.value;
			while (index.value < text.length()) {
				char c = text.charAt(index.value);
				index.value++;
				if (!Character.isWhitespace(c)) {
					inSpace = false;
					continue;
				}
				// Line end?
				if (c == '\r' || c == '\n') {
					// // Handle MS Windows 2 character \r\n line breaks
					// if (index.value < text.length()) {
					// char c2 = text.charAt(index.value);
					// if (c=='\r' && c2=='\n') index.value++; // Push on past
					// the 2nd line break char
					// }
					// Double line end - indicating a paragraph break
					if (inSpace)
						break;
					inSpace = true;
				}
				// TODO Other paragraph markers, spotted by a change in
				// leadingChars
			}
			String p = text.substring(start, index.value);
			paras.add(p);
		}
		// Done
		return paras;
	}

	/**
	 * Format a block of text to fit the given line width
	 * 
	 * @param p
	 * @param lineWidth
	 * @param tabWidth
	 * @param respectLeadingCharacters
	 * @return
	 */
	private static String format3_oneParagraph(String p, int lineWidth,
			int tabWidth, String respectLeadingCharacters) {
		// Collect the reformatted paragraph
		StringBuilder sb = new StringBuilder(p.length() + 10); // Allow for
																// some extra
																// line-breaks
		// Get respected leading chars
		String leadingChars = format4_getLeadingChars(p,
				respectLeadingCharacters);
		// First Line
		sb.append(leadingChars);
		int lineLength = leadingChars.length();
		int index = leadingChars.length();
		// Loop
		while (index < p.length()) {
			// Get the next word
			StringBuilder word = new StringBuilder();
			char c = p.charAt(index);
			index++;
			while (!Character.isWhitespace(c)) {
				word.append(c);
				if (index == p.length())
					break;
				c = p.charAt(index);
				index++;
			}
			// Break the line if the word will not fit
			if (lineLength + word.length() > lineWidth && lineLength != 0) {
				trimEnd(sb);
				sb.append('\n'); // lineEnd(sb);
				// New line
				sb.append(leadingChars);
				lineLength = leadingChars.length();
			}
			// Add word
			sb.append(word);
			lineLength += word.length();
			// Add the whitespace
			if (index != p.length() && lineLength < lineWidth) {
				if (c == '\n') {
					c = ' ';
				}
				sb.append(c);
				lineLength += (c == '\t') ? tabWidth : 1;
			}
		}
		// A final trim
		trimEnd(sb);
		// Done
		return sb.toString();
	}

	/**
	 * 
	 * @param text
	 * @param respectLeadingCharacters
	 *            Can be null
	 * @return The characters at the beginning of text which are respected. E.g.
	 *         ("> Hello", " \t>") --> "> "
	 */
	private static String format4_getLeadingChars(String text,
			String respectLeadingCharacters) {
		if (respectLeadingCharacters == null)
			return "";
		// Line-breaks cannot be respected
		assert respectLeadingCharacters.indexOf('\n') == -1;
		// Look for the first non-respected char
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (respectLeadingCharacters.indexOf(c) == -1) {
				// Return the previous chars
				return text.substring(0, i);
			}
		}
		// All chars are respected
		return text;
	}

	/**
	 * Ensure that line ends with the right line-end character(s)
	 */
	public static final String lineEnd(String line) {
		// strip possibly inappropriate line-endings
		if (line.endsWith("\n")) {
			line = line.substring(0, line.length() - 1);
		}
		if (line.endsWith("\r\n")) {
			line = line.substring(0, line.length() - 2);
		}
		if (line.endsWith("\r")) {
			line = line.substring(0, line.length() - 1);
		}
		// add in proper line end
		if (!line.endsWith(LINEEND)) {
			line += LINEEND;
		}
		return line;
	}

	/**
	 * Ensure that line ends with the right line-end character(s). This is more
	 * efficient than the version for Strings.
	 * 
	 * @param line
	 */
	public static final void lineEnd(final StringBuilder line) {
		if (line.length() == 0) {
			line.append(LINEEND);
			return;
		}
		// strip possibly inappropriate line-endings
		final char last = line.charAt(line.length() - 1);
		if (last == '\n') {
			if ((line.length() > 1) && (line.charAt(line.length() - 2) == '\r')) {
				// \r\n
				line.replace(line.length() - 2, line.length(), LINEEND);
				return;
			}
			line.replace(line.length() - 1, line.length(), LINEEND);
			return;
		}
		if (last == '\r') {
			line.replace(line.length() - 1, line.length(), LINEEND);
			return;
		}
		line.append(LINEEND);
		return;
	}


	
	/**
	 * @param string
	 * @return the MD5 sum of the string using the default charset. Null if
	 *         there was an error in calculating the hash.
	 * @author Sam Halliday
	 */
	public static String md5Hash(String string) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// ignore this exception, we know MD5 exists
		}
		md5.update(string.getBytes());
		BigInteger hash = new BigInteger(1, md5.digest());
		return hash.toString(16);
	}

	/**
	 * Removes HTML-style tags from a string.
	 * 
	 * @param s
	 *            a String from which to remove tags
	 * @return a string with all instances of <.*> removed.
	 */
	public static String removeTags(String s) {
		StringBuffer sb = new StringBuffer();
		boolean inTag = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '<')
				inTag = true;
			if (!inTag)
				sb.append(c);
			if (c == '>')
				inTag = false;
		}
		return sb.toString();
	}

	/**
	 * Repeat a character.
	 * 
	 * @param c
	 * @param i
	 * @return A String consisting of i x c.
	 * @example assert repeat('-', 5).equals("-----");
	 */
	public static String repeat(Character c, int i) {
		StringBuilder dashes = new StringBuilder(i);
		for (int j = 0; j < i; j++)
			dashes.append(c);
		return dashes.toString();
	}

	/**
	 * Split a piece of text into separate lines. The line breaks are left at
	 * the end of each line.
	 * 
	 * @param text
	 * @return The individual lines in the text.
	 */
	public static List<String> splitLines(String text) {
		List<String> lines = new ArrayList<String>();
		// Search for lines
		int start = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\r' || c == '\n') {
				// Handle MS Windows 2 character \r\n line breaks
				if (i + 1 < text.length()) {
					char c2 = text.charAt(i + 1);
					if (c == '\r' && c2 == '\n')
						i++;
				}
				// Get the line, with the line break
				String line = text.substring(start, i + 1);
				lines.add(line);
				start = i + 1;
			}
		}
		// Last one
		if (start != text.length()) {
			String line = text.substring(start);
			lines.add(line);
		}
		return lines;
	}

	/**
	 * Remove <i>trailing</i> whitespace. c.f. String#trim() which removes
	 * leading and trailing whitespace.
	 * 
	 * @param sb
	 */
	private static void trimEnd(StringBuilder sb) {
		while (true) {
			// Get the last character
			int i = sb.length() - 1;
			if (i == -1)
				return; // Quit if sb is empty
			char c = sb.charAt(i);
			if (!Character.isWhitespace(c))
				return; // Finish?
			sb.deleteCharAt(i); // Remove and continue
		}
	}

	/**
	 * Returns true if the string is just whitespace, or empty, or null.
	 * 
	 * @param s
	 */
	public static final boolean whitespace(final String s) {
		if (s == null) {
			return true;
		}
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (!Character.isWhitespace(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param text
	 * @return the number of words in text. Uses a crude whitespace
	 * measure.
	 */
	public static int wordCount(String text) {
		String[] bits = text.split("\\W+");
		int wc = 0;
		for (String string : bits) {
			if (!whitespace(string)) wc++;
		}
		return wc;
	}

}
