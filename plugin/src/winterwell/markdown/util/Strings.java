package winterwell.markdown.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Strings {

	public static final String ISO_LATIN = "ISO-8859-1";
	public static final String UTF_8 = "UTF-8";
	public static final String EOL = System.getProperty("line.separator");
	public static final String EOL2 = EOL + EOL;

	public static final String APOSTROPHES = "'`\u2019\u2018\u2019\u02BC";
	public static final Pattern ASCII_PUNCTUATION = Pattern
			.compile("[.<>,@~\\{\\}\\[\\]-_+=()*%?^$!\\\\/|\254:;#`'\"]");
	public static final Pattern BLANK_LINE = Pattern.compile("^\\s+$", 8);
	public static final String COMMON_BULLETS = "-*\uE00Co";
	public static final String DASHES = "\u2010\u2011\u2012\u2013\u2014\u2015-";
	public static final Pattern LINEENDINGS = Pattern.compile("(\r\n|\r|\n)");
	public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
	public static final String QUOTES = "\"\u201C\u201D\u201E\u201F\u275B\u275C\u275D\u275E\253\273";

	public static final Pattern TAG_REGEX = Pattern.compile("<(/?[a-zA-Z][a-zA-Z0-9]*)[^>]*>", 32);
	public static final Pattern pComment = Pattern.compile("<!-*.*?-+>", 32);
	public static final Pattern pDocType = Pattern.compile("<!DOCTYPE.*?>", 34);
	public static final Pattern pScriptOrStyle = Pattern.compile("<(script|style)[^<>]*>.+?</(script|style)>", 34);

	public static final String ARRAY[] = new String[0];
	private static final double TENS[];
	static {
		TENS = new double[20];
		TENS[0] = Math.pow(10D, -6D);
		for (int i = 1; i < TENS.length; i++) {
			TENS[i] = 10D * TENS[i - 1];
		}
	}

	public static char charAt(CharSequence chars, int i) {
		return i >= chars.length() ? '\0' : chars.charAt(i);
	}

	public static String compactWhitespace(String txt) {
		if (txt == null) {
			return null;
		} else {
			txt = txt.trim();
			txt = txt.replaceAll("\\s+", " ");
			txt = txt.replaceAll("> <", "><");
			return txt;
		}
	}

	public static boolean containsIgnoreCase(CharSequence pageTitle, String snippet) {
		String pt = pageTitle.toString().toLowerCase();
		return pt.contains(snippet.toLowerCase());
	}

	private static String convertToJavaString(String txt) {
		String lines[] = splitLines(txt);
		String jtxt = "";
		String as[];
		int j = (as = lines).length;
		for (int i = 0; i < j; i++) {
			String line = as[i];
			line = line.replace("\\", "\\\\");
			line = line.replace("\"", "\\\"");
			jtxt = (new StringBuilder(String.valueOf(jtxt))).append("+\"").append(line).append("\\n\"\n").toString();
		}

		jtxt = jtxt.substring(1);
		return jtxt;
	}

	public static String ellipsize(String input, int maxLength) {
		if (input == null) {
			return null;
		}
		if (input.length() <= maxLength) {
			return input;
		}
		if (maxLength < 3) {
			return "";
		}
		if (maxLength == 3) {
			return "...";
		}
		int i = input.lastIndexOf(' ', maxLength - 3);
		if (i < 1 || i < maxLength - 10) {
			i = maxLength - 3;
		}
		return (new StringBuilder(String.valueOf(substring(input, 0, i)))).append("...").toString();
	}

	public static void endLine(StringBuilder text) {
		newLine(text);
	}

	public static Map<String, String> extractHeader(StringBuilder txt) {
		if (txt == null) {
			throw new AssertionError();
		}
		String lines[] = splitLines(txt.toString());
		String key = null;
		StringBuilder value = new StringBuilder();
		Map<String, String> headers = new LinkedHashMap<>();
		for (String line : lines) {
			if (line.trim().isEmpty()) break;
			int i = line.indexOf(":");
			if (i == -1) {
				value.append(EOL);
				value.append(line);
			} else {
				if (key != null) {
					headers.put(key, value.toString());
				}
				value = new StringBuilder();
				key = line.substring(0, i).toLowerCase();
				if (++i != line.length()) {
					if (line.charAt(i) == ' ') i++;
					if (i != line.length()) {
						value.append(line.substring(i));
					}
				}
			}
		}

		if (key != null) {
			headers.put(key, value.toString());
		}
		if (headers.size() == 0) {
			return headers;
		}
		Pattern blankLine = Pattern.compile("^\\s*$", 8);
		Matcher m = blankLine.matcher(txt);
		boolean ok = m.find();
		if (ok) {
			txt.delete(0, m.end());
			if (txt.length() != 0) {
				if (txt.charAt(0) == '\r' && txt.charAt(1) == '\n') {
					txt.delete(0, 2);
				} else {
					txt.delete(0, 1);
				}
			}
		}
		return headers;
	}

	public static String[] find(Pattern pattern, String input) {
		Matcher m = pattern.matcher(input);
		boolean fnd = m.find();
		if (!fnd) {
			return null;
		}
		int n = m.groupCount() + 1;
		String grps[] = new String[n];
		grps[0] = m.group();
		for (int i = 1; i < n; i++) {
			grps[i] = m.group(i);
		}

		return grps;
	}

	public static String[] find(String regex, String string) {
		return find(Pattern.compile(regex), string);
	}

	public static Pair<Integer> findLenient(String content, String text, int start) {
		content = normalise(content);
		text = normalise(text);
		content = content.toLowerCase();
		text = text.toLowerCase();
		String regex = content.replace("\\", "\\\\");
		String SPECIAL = "()[]{}$^.*+?";
		for (int i = 0; i < SPECIAL.length(); i++) {
			char c = SPECIAL.charAt(i);
			regex = regex.replace((new StringBuilder()).append(c).toString(),
					(new StringBuilder("\\")).append(c).toString());
		}

		regex = regex.replaceAll("\\s+", "\\\\s+");
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		if (m.find(start)) {
			return new Pair<>(Integer.valueOf(m.start()), Integer.valueOf(m.end()));
		} else {
			return null;
		}
	}

	public static String getFirstName(String name) {
		name = name.trim();
		if (name.contains("\n")) {
			throw new AssertionError(name);
		}
		String nameBits[] = name.split("[ \t\\.,]+");
		String firstName = nameBits[0];
		firstName = toTitleCase(firstName);
		List<String> titles = Arrays
				.asList(new String[] { "Mr", "Mrs", "Ms", "Dr", "Doctor", "Prof", "Professor", "Sir", "Director" });
		if (titles.contains(firstName)) {
			firstName = nameBits[1];
			firstName = toTitleCase(firstName);
		}
		return firstName;
	}

	public static String getHeaderString(Map<?, ?> header) {
		StringBuilder sb = new StringBuilder();
		String ks;
		String vs;
		for (Iterator<?> iterator = header.keySet().iterator(); iterator.hasNext(); sb
				.append((new StringBuilder(String.valueOf(ks))).append(": ").append(vs).append(EOL).toString())) {
			Object k = iterator.next();
			ks = k.toString().trim().toLowerCase();
			vs = header.get(k).toString();
		}

		return sb.toString();
	}

	public static int[] getLineStarts(String text) {
		List<Integer> starts = new ArrayList<>();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				starts.add(Integer.valueOf(i));
			}
			if (c == '\r') {
				int ni = i + 1;
				if (ni == text.length() || text.charAt(ni) != '\n') {
					starts.add(Integer.valueOf(i));
				}
			}
		}
		int[] results = new int[starts.size()];
		for (int i = 0; i < starts.size(); i++) {
			results[i] = starts.get(i);
		}
		return results;
	}

	private static String hash(String hashAlgorithm, String txt) {
		StringBuffer result;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(hashAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		result = new StringBuffer();
		try {
			byte abyte0[];
			int j = (abyte0 = md.digest(txt.getBytes("UTF8"))).length;
			for (int i = 0; i < j; i++) {
				byte b = abyte0[i];
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0xf));
			}

		} catch (UnsupportedEncodingException e) {
			byte abyte1[];
			int l = (abyte1 = md.digest(txt.getBytes())).length;
			for (int k = 0; k < l; k++) {
				byte b = abyte1[k];
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0xf));
			}

		}
		return result.toString();
	}

	public static boolean isJustDigits(String possNumber) {
		for (int i = 0; i < possNumber.length(); i++) {
			if (!Character.isDigit(possNumber.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public static boolean isNumber(String x) {
		if (x == null) {
			return false;
		}
		try {
			Double.valueOf(x);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isOnly(String txt, char c) {
		if (txt == null || txt.length() == 0) {
			return false;
		}
		for (int i = 0; i < txt.length(); i++) {
			if (txt.charAt(i) != c) {
				return false;
			}
		}

		return true;
	}

	public static boolean isWord(String txt) {
		return txt.matches("\\w+");
	}

	public static String join(Collection<?> list, String separator) {
		return Printer.toString(list, separator);
	}

	public static StringBuilder join(String start, Collection<?> list, String separator, String end) {
		StringBuilder sb = new StringBuilder(start);
		if (!list.isEmpty()) {
			for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
				Object t = (Object) iterator.next();
				if (t != null) {
					sb.append(Printer.toString(t));
					sb.append(separator);
				}
			}

			if (sb.length() != 0) {
				pop(sb, separator.length());
			}
		}
		sb.append(end);
		return sb;
	}

	public static String join(String array[], String separator) {
		if (array.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		String as[];
		int j = (as = array).length;
		for (int i = 0; i < j; i++) {
			String string = as[i];
			if (string != null) {
				sb.append(string);
				sb.append(separator);
			}
		}

		if (sb.length() != 0) {
			pop(sb, separator.length());
		}
		return sb.toString();
	}

	public static void join(StringBuilder sb, Collection<?> list, String separator) {
		Printer.append(sb, list, separator);
	}

	public static void main(String args[]) throws IOException {
		String txt = "";
		BufferedReader in = FileUtils.getReader(System.in);
		do {
			String line = in.readLine();
			if (!line.equals("EXIT") && !line.equals("QUIT")) {
				txt = (new StringBuilder(String.valueOf(txt))).append(line).append("\n").toString();
			} else {
				String jtxt = convertToJavaString(txt);
				System.out.println(jtxt);
				return;
			}
		} while (true);
	}

	public static String md5(String txt) {
		return hash("MD5", txt);
	}

	public static void newLine(StringBuilder text) {
		if (text.length() == 0) {
			return;
		}
		char last = text.charAt(text.length() - 1);
		if (last == '\r' || last == '\n') {
			return;
		} else {
			text.append(EOL);
			return;
		}
	}

	public static String normalise(String unicode) throws IllegalArgumentException {
		boolean ascii = true;
		int i = 0;
		for (int n = unicode.length(); i < n; i++) {
			char c = unicode.charAt(i);
			if (c <= '\177' && c != 0) continue;
			ascii = false;
			break;
		}

		if (ascii) return unicode;

		String normed = Normalizer.normalize(unicode, java.text.Normalizer.Form.NFD);
		StringBuilder clean = new StringBuilder(normed.length());
		i = 0;
		for (int n = normed.length(); i < n; i++) {
			char c = normed.charAt(i);
			if ("'`\u2019\u2018\u2019\u02BC".indexOf(c) != -1) {
				clean.append('\'');
			} else if ("\"\u201C\u201D\u201E\u201F\u275B\u275C\u275D\u275E\253\273".indexOf(c) != -1) {
				clean.append('"');
			} else if ("\u2010\u2011\u2012\u2013\u2014\u2015-".indexOf(c) != -1) {
				clean.append('-');
			} else if (c < '\200' && c != 0) {
				clean.append(c);
			} else if (Character.isLetter(c)) {
				// Log.report((new StringBuilder("Could not normalise to ascii:
				// ")).append(unicode).toString());
			}
		}

		return clean.toString();
	}

	public static void pop(StringBuilder sb, int chars) {
		sb.delete(sb.length() - chars, sb.length());
	}

	public static String remove(String string, String regex, final Collection<String> removed) {
		String s2 = replace(string, Pattern.compile(regex), new IReplace() {

			public void appendReplacementTo(StringBuilder sb, Matcher match) {
				removed.add(match.group());
			}
		});
		return s2;
	}

	public static String repeat(char c, int n) {
		char chars[] = new char[n];
		Arrays.fill(chars, c);
		return new String(chars);
	}

	public static String repeat(String string, int n) {
		StringBuilder sb = new StringBuilder(string.length() * n);
		for (int i = 0; i < n; i++) {
			sb.append(string);
		}

		return sb.toString();
	}

	public static String replace(String string, Pattern regex, IReplace replace) {
		Matcher m = regex.matcher(string);
		StringBuilder sb = new StringBuilder(string.length() + 16);
		int pos;
		for (pos = 0; m.find(); pos = m.end()) {
			sb.append(string.substring(pos, m.start()));
			replace.appendReplacementTo(sb, m);
		}

		sb.append(string.substring(pos, string.length()));
		return sb.toString();
	}

	public static StringBuilder sb(CharSequence charSeq) {
		return (charSeq instanceof StringBuilder) ? (StringBuilder) charSeq : new StringBuilder(charSeq);
	}

	public static List<String> split(String line) {
		if (line == null || line.length() == 0) return Collections.emptyList();

		ArrayList<String> row = new ArrayList<>();
		StringBuilder field = new StringBuilder();
		char quote = '"';
		boolean inQuotes = false;
		int i = 0;
		for (int n = line.length(); i < n; i++) {
			char c = line.charAt(i);
			if (c == quote) {
				inQuotes = !inQuotes;
			} else if (inQuotes) {
				field.append(c);
			} else if (Character.isWhitespace(c) || c == ',') {
				if (field.length() != 0) {
					row.add(field.toString());
					field = new StringBuilder();
				}
			} else {
				field.append(c);
			}
		}

		if (field.length() == 0) return row;

		String f = field.toString();
		row.add(f);
		return row;
	}

	public static String[] splitBlocks(String message) {
		return message.split("\\s*\r?\n\\s*\r?\n");
	}

	public static Pair<String> splitFirst(String line, char c) {
		int i = line.indexOf(c);
		if (i == -1) return null;
		String end = i != line.length() ? line.substring(i + 1) : "";
		return new Pair<>(line.substring(0, i), end);
	}

	public static String[] splitLines(String txt) {
		return LINEENDINGS.split(txt);
	}

	public static String substring(String string, int start, int end) {
		if (string == null) {
			return null;
		}
		int len = string.length();
		if (start < 0) {
			start = len + start;
			if (start < 0) {
				start = 0;
			}
		}
		if (end <= 0) {
			end = len + end;
			if (end < start) {
				return "";
			}
		}
		if (end > len) {
			end = len;
		}
		if (start == 0 && end == len) {
			return string;
		} else {
			return string.substring(start, end);
		}
	}

	public static String toCanonical(String string) {
		if (string == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean spaced = false;
		int i = 0;
		for (int n = string.length(); i < n; i++) {
			char c = string.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				spaced = false;
				c = Character.toLowerCase(c);
				sb.append(c);
			} else if (!spaced && sb.length() != 0) {
				sb.append(' ');
				spaced = true;
			}
		}

		if (spaced) {
			pop(sb, 1);
		}
		string = sb.toString();
		return normalise(string);
	}

	public static String toCleanLinux(String text) {
		text = text.replace("\r\n", "\n");
		text = text.replace('\r', '\n');
		text = BLANK_LINE.matcher(text).replaceAll("");
		return text;
	}

	public static String toInitials(String name) {
		StringBuilder sb = new StringBuilder();
		boolean yes = true;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isWhitespace(c)) {
				yes = true;
			} else {
				if (yes) {
					c = Character.toUpperCase(c);
					sb.append(c);
				}
				yes = false;
			}
		}

		return sb.toString();
	}

	public static String toNSigFigs(double x, int n) {
		if (n <= 0) {
			throw new AssertionError();
		}
		String sign = x >= 0.0D ? "" : "-";
		double v = Math.abs(x);
		double lv = Math.floor(Math.log10(v));
		double keeper = Math.pow(10D, n - 1);
		double tens = Math.pow(10D, lv);
		int keepMe = (int) Math.round((v * keeper) / tens);
		if (lv < 0.0D) {
			String s = toNSigFigs2_small(n, sign, lv, keepMe);
			if (s != null) {
				return s;
			}
		}
		double vt = ((double) keepMe * tens) / keeper;
		String num = Printer.toStringNumber(Double.valueOf(vt));
		return (new StringBuilder(String.valueOf(sign))).append(num).toString();
	}

	private static String toNSigFigs2_small(int n, String sign, double lv, int keepMe) {
		if (lv < -8D) {
			return null;
		}
		StringBuilder sb = new StringBuilder(sign);
		int zs = (int) (-lv);
		String sKeepMe = Integer.toString(keepMe);
		if (sKeepMe.length() > n) {
			if (sKeepMe.charAt(sKeepMe.length() - 1) != '0') {
				throw new AssertionError();
			}
			zs--;
			sKeepMe = sKeepMe.substring(0, sKeepMe.length() - 1);
			if (zs == 0) {
				return null;
			}
		}
		sb.append("0.");
		for (int i = 1; i < zs; i++) {
			sb.append('0');
		}

		sb.append(sKeepMe);
		return sb.toString();
	}

	public static String toTitleCase(String title) {
		if (title.length() < 2) {
			return title.toUpperCase();
		}
		StringBuilder sb = new StringBuilder(title.length());
		boolean goUp = true;
		int i = 0;
		for (int n = title.length(); i < n; i++) {
			char c = title.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '\'') {
				if (goUp) {
					sb.append(Character.toUpperCase(c));
					goUp = false;
				} else {
					sb.append(Character.toLowerCase(c));
				}
			} else {
				sb.append(c);
				goUp = true;
			}
		}

		return sb.toString();
	}

	public static String toTitleCasePlus(String wouldBeTitle) {
		String words[] = wouldBeTitle.split("(_|\\s+)");
		StringBuilder sb = new StringBuilder();
		String as[];
		int j = (as = words).length;
		for (int i = 0; i < j; i++) {
			String word = as[i];
			if (word.length() != 0) {
				if (Character.isUpperCase(word.charAt(0))) {
					sb.append(word);
					sb.append(' ');
				} else {
					word = replace(word, Pattern.compile("[A-Z]?[^A-Z]+"), new IReplace() {

						public void appendReplacementTo(StringBuilder sb2, Matcher match) {
							String w = match.group();
							w = Strings.toTitleCase(w);
							sb2.append(w);
							sb2.append(' ');
						}

					});
					sb.append(word);
				}
			}
		}

		if (sb.length() != 0) {
			pop(sb, 1);
		}
		return sb.toString();
	}

	public static String trimPunctuation(String string) {
		return string;
	}

	public static String trimQuotes(String string) {
		if (string.charAt(0) != '\'' && string.charAt(0) != '"') {
			return string;
		}
		char c = string.charAt(string.length() - 1);
		if (c != '\'' && c != '"') {
			return string;
		} else {
			return string.substring(1, string.length() - 1);
		}
	}

	public static int wordCount(String text) {
		return text.split("\\s+").length;
	}

	public static boolean isBlank(String line) {
		return line.trim().isEmpty();
	}

	public static String stripTags(String xml) {
		if (xml == null) {
			return null;
		}
		if (xml.indexOf('<') == -1) {
			return xml;
		} else {
			Matcher m4 = pScriptOrStyle.matcher(xml);
			xml = m4.replaceAll("");
			Matcher m2 = pComment.matcher(xml);
			String txt = m2.replaceAll("");
			Matcher m = TAG_REGEX.matcher(txt);
			String txt2 = m.replaceAll("");
			Matcher m3 = pDocType.matcher(txt2);
			String txt3 = m3.replaceAll("");
			return txt3;
		}
	}

}
