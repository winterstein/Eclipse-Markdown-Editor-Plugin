package winterwell.markdown.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public final class FileUtils {

	public static final String UTF8 = "UTF8";
	public static final char UTF8_BOM = 65279;

	public FileUtils() {}

	// public static void append(String string, File file) {
	// try {
	// BufferedWriter w = getWriter(new FileOutputStream(file, true));
	// w.write(string);
	// close(w);
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// public static File changeType(File file, String type) {
	// String fName = file.getName();
	// int i = fName.lastIndexOf('.');
	// if (type.length() == 0) {
	// if (i == -1) {
	// return file;
	// } else {
	// fName = fName.substring(0, i);
	// return new File(file.getParentFile(), fName);
	// }
	// }
	// if (type.charAt(0) == '.') {
	// type = type.substring(1);
	// }
	// if (type.length() <= 0) {
	// throw new AssertionError();
	// }
	// if (i == -1) {
	// fName = (new StringBuilder(String.valueOf(fName))).append(".").append(type).toString();
	// } else {
	// fName = (new StringBuilder(String.valueOf(fName.substring(0, i +
	// 1)))).append(type).toString();
	// }
	// return new File(file.getParentFile(), fName);
	// }

	public static void close(Closeable io) {
		if (io == null) {
			return;
		}
		try {
			io.close();
		} catch (IOException e) {
			if (e.getMessage() != null && e.getMessage().contains("Closed")) {
				return;
			}
			e.printStackTrace();
		}
	}

	// public static File copy(File in, File out) {
	// return copy(in, out, true);
	// }
	//
	// public static File copy(File in, File out, boolean overwrite)
	// throws RuntimeException
	// {
	// if( !in.exists())
	// {
	// throw new AssertionError((new StringBuilder("File does not exist:
	// ")).append(in.getAbsolutePath()).toString());
	// }
	// if( in.equals(out))
	// {
	// throw new AssertionError((new StringBuilder()).append(in).append(" = ").append(out).append("
	// can cause a delete!").toString());
	// }
	// if(in.isDirectory())
	// {
	// ArrayList failed = new ArrayList();
	// copyDir(in, out, overwrite, failed);
	// if(failed.size() != 0)
	// {
	// throw new RuntimeException((new StringBuilder("Could not copy files:
	// ")).append(Printer.toString(failed)).toString());
	// } else
	// {
	// return out;
	// }
	// }
	// if(out.isDirectory())
	// {
	// out = new File(out, in.getName());
	// }
	// if(out.exists() && !overwrite)
	// {
	// throw new RuntimeException((new StringBuilder("Copy failed: ")).append(out).append(" already
	// exists.").toString());
	// }
	// copy(((InputStream) (new FileInputStream(in))), out);
	// return out;
	// IOException e;
	// e;
	// throw new RuntimeException((new StringBuilder(String.valueOf(e.getMessage()))).append("
	// copying ").append(in.getAbsolutePath()).append(" to
	// ").append(out.getAbsolutePath()).toString());
	// }
	//
	// public static void copy(InputStream in, File out) {
	// if ((in == null || out == null)) {
	// throw new AssertionError();
	// }
	// if (!out.getParentFile().isDirectory()) {
	// throw new RuntimeException(
	// (new StringBuilder("Directory does not exist: ")).append(out.getParentFile()).toString());
	// }
	// try {
	// FileOutputStream outStream = new FileOutputStream(out);
	// copy(in, ((OutputStream) (outStream)));
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// public static void copy(InputStream in, OutputStream out)
	// {
	// try
	// {
	// byte bytes[] = new byte[20480];
	// do
	// {
	// int len = in.read(bytes);
	// if(len == -1)
	// {
	// break;
	// }
	// out.write(bytes, 0, len);
	// } while(true);
	// }
	// catch(IOException e)
	// {
	// throw new RuntimeException(e);
	// }
	// break MISSING_BLOCK_LABEL_53;
	// Exception exception;
	// exception;
	// close(in);
	// close(out);
	// throw exception;
	// close(in);
	// close(out);
	// return;
	// }
	//
	// private static void copyDir(File in, File out, boolean overwrite, List failed) {
	// if (!in.isDirectory()) {
	// throw new AssertionError(in);
	// }
	// if (!out.exists()) {
	// boolean ok = out.mkdir();
	// if (!ok) {
	// failed.add(in);
	// return;
	// }
	// }
	// if (!out.isDirectory()) {
	// throw new AssertionError(out);
	// }
	// File afile[];
	// int j = (afile = in.listFiles()).length;
	// for (int i = 0; i < j; i++) {
	// File f = afile[i];
	// if (f.isDirectory()) {
	// File subOut = new File(out, f.getName());
	// copyDir(f, subOut, overwrite, failed);
	// } else {
	// try {
	// copy(f, out, overwrite);
	// } catch (RuntimeException e) {
	// failed.add(f);
	// }
	// }
	// }
	//
	// }
	//
	// public static File createTempDir()
	// {
	// File f;
	// f = File.createTempFile("tmp", "dir");
	// if(f.exists())
	// {
	// delete(f);
	// }
	// f.mkdirs();
	// return f;
	// Exception e;
	// e;
	// throw Utils.runtime(e);
	// }
	//
	// public static File createTempFile(String prefix, String suffix)
	// {
	// return File.createTempFile(prefix, suffix);
	// IOException e;
	// e;
	// throw new RuntimeException(e);
	// }

	public static void delete(File file) {
		if (!file.exists()) return;
		if (file.delete()) return;
		System.gc();
		if (file.delete()) return;

		try {
			Thread.sleep(50L);
		} catch (InterruptedException interruptedexception) {}
		file.delete();
		if (!file.exists()) return;

		// if (file.isDirectory() && isSymLink(file)
		// && (Utils.getOperatingSystem().contains("linux") ||
		// Utils.getOperatingSystem().contains("unix"))) {
		// String path = file.getAbsolutePath();
		// Process p = new Process((new StringBuilder("rm -f ")).append(path).toString());
		// p.run();
		// p.waitFor(1000L);
		// if (!file.exists()) {
		// return;
		// } else {
		// throw new RuntimeException(new IOException((new StringBuilder("Could not delete
		// file")).append(file)
		// .append("; ").append(p.getError()).toString()));
		// }
		// } else {
		// throw new RuntimeException(
		// new IOException((new StringBuilder("Could not delete file ")).append(file).toString()));
		// }
		throw new RuntimeException(
				new IOException((new StringBuilder("Could not delete file ")).append(file).toString()));
	}

	// public static void deleteDir(File file) {
	// if (!file.isDirectory()) {
	// throw new RuntimeException((new StringBuilder()).append(file).append(" is not a
	// directory").toString());
	// }
	// if (isSymLink(file)) {
	// delete(file);
	// return;
	// }
	// File afile[];
	// int j = (afile = file.listFiles()).length;
	// for (int i = 0; i < j; i++) {
	// File f = afile[i];
	// if (f.isDirectory()) {
	// deleteDir(f);
	// } else {
	// delete(f);
	// }
	// }
	//
	// delete(file);
	// }
	//
	// private static void deleteNative(File out) {
	// if (!Utils.OSisUnix()) {
	// throw new TodoException((new StringBuilder()).append(out).toString());
	// }
	// Process p = new Process((new StringBuilder("rm -f
	// ")).append(out.getAbsolutePath()).toString());
	// p.run();
	// int ok = p.waitFor();
	// if (ok != 0) {
	// throw new RuntimeException(p.getError());
	// } else {
	// return;
	// }
	// }
	//
	// public static String filenameDecode(String name) {
	// name = name.replace("//", "");
	// name = name.replace("_", "%");
	// name = name.replace("%%", "_");
	// String original = WebUtils.urlDecode(name);
	// original = original.replace("%2E", ".");
	// original = original.replace("%3B", ";");
	// original = original.replace("%2F", "/");
	// return original;
	// }
	//
	// public static String filenameEncode(String name) {
	// String url = WebUtils.urlEncode(name);
	// url = url.replace("..", ".%2E");
	// url = url.replace(";", "%3B");
	// url = url.replace("%2F", "/");
	// url = url.replace("//", "/%2F");
	// url = url.replace("_", "__");
	// url = url.replace("%", "_");
	// String bits[] = url.split("/");
	// StringBuilder path = new StringBuilder(url.length());
	// boolean dbl = false;
	// String as[];
	// int k = (as = bits).length;
	// for (int j = 0; j < k; j++) {
	// String bit = as[j];
	// if (bit.length() == 0) {
	// System.out.println(path);
	// }
	// for (int i = 0; i < bit.length(); i += 240) {
	// int e = Math.min(bit.length(), i + 240);
	// path.append(bit.substring(i, e));
	// if (e == bit.length()) {
	// path.append("/");
	// dbl = false;
	// } else {
	// path.append("//");
	// dbl = true;
	// }
	// }
	//
	// }
	//
	// StrUtils.pop(path, dbl ? 2 : 1);
	// if (url.endsWith("/")) {
	// path.append('/');
	// }
	// return path.toString();
	// }
	//
	// public static List find(File baseDir, FileFilter filter) {
	// return find(baseDir, filter, true);
	// }
	//
	// public static List find(File baseDir, FileFilter filter, boolean includeHiddenFiles) {
	// if (!baseDir.isDirectory()) {
	// throw new IllegalArgumentException((new
	// StringBuilder(String.valueOf(baseDir.getAbsolutePath())))
	// .append(" is not a directory").toString());
	// } else {
	// List files = new ArrayList();
	// find2(baseDir, filter, files, includeHiddenFiles);
	// return files;
	// }
	// }
	//
	// public static List find(File baseDir, String regex) {
	// return find(baseDir, ((FileFilter) (new RegexFileFilter(regex))));
	// }
	//
	// private static void find2(File baseDir, FileFilter filter, List files, boolean
	// includeHiddenFiles) {
	// if ((baseDir == null || filter == null || files == null)) {
	// throw new AssertionError();
	// }
	// File afile[];
	// int j = (afile = baseDir.listFiles()).length;
	// for (int i = 0; i < j; i++) {
	// File f = afile[i];
	// if (!f.equals(baseDir) && (includeHiddenFiles || !f.isHidden())) {
	// if (!includeHiddenFiles && f.getName().startsWith(".")) {
	// throw new AssertionError(f);
	// }
	// if (filter.accept(f)) {
	// files.add(f);
	// }
	// if (f.isDirectory()) {
	// find2(f, filter, files, includeHiddenFiles);
	// }
	// }
	// }
	//
	// }
	//
	// private static List getAllClasses(File root) throws IOException {
	// if (root == null) {
	// throw new AssertionError("Root cannot be null");
	// } else {
	// List classNames = new ArrayList();
	// String path = root.getCanonicalPath();
	// getAllClasses(root, path.length() + 1, classNames);
	// return classNames;
	// }
	// }
	//
	// private static void getAllClasses(File root, int prefixLength, List result) throws
	// IOException {
	// if (root == null) {
	// throw new AssertionError("Root cannot be null");
	// }
	// if (prefixLength < 0) {
	// throw new AssertionError("Illegal index specifier");
	// }
	// if (result == null) {
	// throw new AssertionError("Missing return array");
	// }
	// File afile[];
	// int j = (afile = root.listFiles()).length;
	// for (int i = 0; i < j; i++) {
	// File entry = afile[i];
	// if (entry.isDirectory()) {
	// if (entry.canRead()) {
	// getAllClasses(entry, prefixLength, result);
	// }
	// } else {
	// String path = entry.getPath();
	// boolean isClass = path.endsWith(".class") && path.indexOf("$") < 0;
	// if (isClass) {
	// String name = entry.getCanonicalPath().substring(prefixLength);
	// String className = name.replace(File.separatorChar, '.').substring(0, name.length() - 6);
	// result.add(className);
	// }
	// }
	// }
	//
	// }
	//
	// public static String getBasename(File file) {
	// return getBasename(file.getName());
	// }
	//
	// public static String getBasename(String filename) {
	// int i = filename.lastIndexOf('.');
	// if (i == -1) {
	// return filename;
	// } else {
	// return filename.substring(0, i);
	// }
	// }
	//
	// public static String getBasenameCautious(String filename) {
	// int i = filename.lastIndexOf('.');
	// if (i == -1) {
	// return filename;
	// }
	// if (filename.length() - i > 5) {
	// return filename;
	// } else {
	// return filename.substring(0, i);
	// }
	// }
	//
	// /**
	// * @deprecated Method getDataFile is deprecated
	// */
	//
	// public static File getDataFile(String relativePath) {
	// File f = new File(dataDir, relativePath);
	// if (!f.getParentFile().exists()) {
	// f.getParentFile().mkdirs();
	// }
	// return f;
	// }
	//
	// public static String getExtension(File f) {
	// String filename = f.getName();
	// int i = filename.indexOf('.');
	// if (i == -1) {
	// return "";
	// } else {
	// return filename.substring(i).toLowerCase();
	// }
	// }
	//
	// public static String getExtension(String filename) {
	// return getExtension(new File(filename));
	// }
	//
	// public static File getNewFile(File file) {
	// if (!file.exists()) {
	// return file;
	// }
	// String path = file.getParent();
	// String name = file.getName();
	// int dotI = name.lastIndexOf('.');
	// String dotType = "";
	// String preType;
	// if (dotI == -1) {
	// preType = name;
	// } else {
	// preType = name.substring(0, dotI);
	// dotType = name.substring(dotI);
	// }
	// for (int i = 2; i < 10000; i++) {
	// File f = new File(path, (new
	// StringBuilder(String.valueOf(preType))).append(i).append(dotType).toString());
	// if (!f.exists()) {
	// return f;
	// }
	// }
	//
	// throw new RuntimeException(
	// (new StringBuilder("Could not find a non-existing file name for ")).append(file).toString());
	// }

	public static BufferedReader getReader(File file) {
		try {
			return getReader(((InputStream) (new FileInputStream(file))));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedReader getReader(InputStream in) {
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(in, "UTF8");
			return new BufferedReader(reader);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	// public static FileFilter getRegexFilter(String regex) {
	// return new RegexFileFilter(regex);
	// }
	//
	// public static String getRelativePath(File f, File base) throws IllegalArgumentException {
	// String fp = resolveDotDot(f.getAbsolutePath());
	// String bp = resolveDotDot(base.getAbsolutePath());
	// if (!fp.startsWith(bp)) {
	// if (f.equals(base)) {
	// return "";
	// } else {
	// throw new IllegalArgumentException((new StringBuilder()).append(f).append("=").append(fp)
	// .append(" is not a sub-file of ").append(base).append("=").append(bp).toString());
	// }
	// }
	// String rp = fp.substring(bp.length());
	// char ec = rp.charAt(0);
	// if (ec == '\\' || ec == '/') {
	// rp = rp.substring(1);
	// }
	// return rp;
	// }
	//
	// public static String getType(File f) {
	// String fs = f.toString();
	// return getType(fs);
	// }
	//
	// public static String getType(String filename) {
	// int i = filename.lastIndexOf(".");
	// if (i == -1 || i == filename.length() - 1) {
	// return "";
	// } else {
	// return filename.substring(i + 1).toLowerCase();
	// }
	// }
	//
	// public static File getWinterwellDir() {
	// File f;
	// String dd = System.getenv("WINTERWELL_HOME");
	// if (Utils.isBlank(dd)) {
	// break MISSING_BLOCK_LABEL_110;
	// }
	// if (dd.startsWith("~")) {
	// String home = System.getProperty("user.home");
	// if (home != null) {
	// dd = (new
	// StringBuilder(String.valueOf(home))).append("/").append(dd.substring(1)).toString();
	// }
	// }
	// f = (new File(dd)).getCanonicalFile();
	// if (!f.exists()) {
	// throw new FailureException(
	// (new StringBuilder("Path does not exist: WINTERWELL_HOME = ")).append(f).toString());
	// }
	// return f;
	// File ddf;
	// String home = System.getProperty("user.home");
	// if (Utils.isBlank(home)) {
	// home = "/home";
	// }
	// ddf = (new File(home, "winterwell")).getCanonicalFile();
	// if (ddf.exists() && ddf.isDirectory()) {
	// return ddf;
	// }
	// try {
	// throw new FailureException("Could not find directory - environment variable WINTERWELL_HOME
	// is not set.");
	// } catch (IOException e) {
	// throw Utils.runtime(e);
	// }
	// }
	//
	// public static File getWorkingDirectory()
	// {
	// return (new File(".")).getCanonicalFile();
	// IOException e;
	// e;
	// throw new RuntimeException(e);
	// }

	// public static BufferedWriter getWriter(OutputStream out)
	// {
	// OutputStreamWriter writer = new OutputStreamWriter(out, "UTF8");
	// return new BufferedWriter(writer);
	// UnsupportedEncodingException e;
	// e;
	// throw new RuntimeException(e);
	// }
	//
	// public static BufferedReader getZippedReader(File file)
	// {
	// GZIPInputStream zos;
	// FileInputStream fos = new FileInputStream(file);
	// zos = new GZIPInputStream(fos);
	// return getReader(zos);
	// IOException ex;
	// ex;
	// throw Utils.runtime(ex);
	// }
	//
	// public static BufferedWriter getZippedWriter(File file, boolean append)
	// {
	// GZIPOutputStream zos;
	// FileOutputStream fos = new FileOutputStream(file, append);
	// zos = new GZIPOutputStream(fos);
	// return getWriter(zos);
	// IOException ex;
	// ex;
	// throw Utils.runtime(ex);
	// }
	//
	// public static Iterable grep(File baseDir, String regex, String fileNameRegex) {
	// List files = find(baseDir, fileNameRegex);
	// Pattern p = Pattern.compile(regex);
	// List found = new ArrayList();
	// for (Iterator iterator = files.iterator(); iterator.hasNext();) {
	// File file = (File) iterator.next();
	// String lines[] = StrUtils.splitLines(read(file));
	// String as[];
	// int j = (as = lines).length;
	// for (int i = 0; i < j; i++) {
	// String line = as[i];
	// if (p.matcher(line).find()) {
	// found.add(new Pair2(line, file));
	// }
	// }
	//
	// }
	//
	// return found;
	// }
	//
	// public static boolean isSafe(String filename) {
	// if (Utils.isBlank(filename)) {
	// return false;
	// }
	// if (filename.contains("..")) {
	// return false;
	// }
	// if (filename.contains(";")) {
	// return false;
	// }
	// if (filename.contains("|")) {
	// return false;
	// }
	// if (filename.contains(">")) {
	// return false;
	// }
	// return !filename.contains("<");
	// }
	//
	// public static boolean isSymLink(File f)
	// {
	// File canon = f.getCanonicalFile();
	// if(!canon.getName().equals(f.getName()))
	// {
	// return true;
	// }
	// File parent;
	// parent = f.getParentFile();
	// if(parent == null)
	// {
	// parent = f.getAbsoluteFile().getParentFile();
	// }
	// if(parent == null)
	// {
	// return false;
	// }
	// File canonParent;
	// parent = parent.getCanonicalFile();
	// canonParent = canon.getParentFile();
	// return !parent.equals(canonParent);
	// IOException e;
	// e;
	// throw Utils.runtime(e);
	// }
	//
	// public static Object load(File file) {
	// BufferedReader reader = getReader(file);
	// return XStreamUtils.serialiseFromXml(reader);
	// }
	//
	// public static Properties loadProperties(File propsFile) {
	// Exception exception;
	// InputStream stream = null;
	// Properties properties;
	// try {
	// stream = new FileInputStream(propsFile);
	// Properties props = new Properties();
	// props.load(stream);
	// properties = props;
	// } catch (IOException e) {
	// throw Utils.runtime(e);
	// } finally {
	// close(stream);
	// }
	// close(stream);
	// return properties;
	// throw exception;
	// }
	//
	// public static File[] ls(File dir, String fileNameRegex) {
	// if (!dir.isDirectory()) {
	// throw new RuntimeException(
	// (new StringBuilder()).append(dir).append(" is not a valid directory").toString());
	// } else {
	// return dir.listFiles(getRegexFilter((new
	// StringBuilder(".*")).append(fileNameRegex).toString()));
	// }
	// }
	//
	// public static void makeSymLink(File original, File out) {
	// makeSymLink(original, out, true);
	// }
	//
	// public static void makeSymLink(File original, File out, boolean overwrite) {
	// if (!Utils.getOperatingSystem().contains("linux")) {
	// throw new TodoException();
	// }
	// if (original.getAbsolutePath().equals(out.getAbsolutePath())) {
	// throw new IllegalArgumentException((new StringBuilder("Cannot sym-link to self:
	// ")).append(original)
	// .append(" = ").append(out).toString());
	// }
	// if (!original.exists()) {
	// throw new winterwell.utils.RuntimeException.FileNotFoundException(original);
	// }
	// if (!original.isDirectory() && !original.isFile()) {
	// throw new RuntimeException((new StringBuilder("Weird: ")).append(original).toString());
	// }
	// if (out.exists()) {
	// if (overwrite) {
	// delete(out);
	// } else {
	// throw new RuntimeException((new StringBuilder("Creating symlink failed: ")).append(out)
	// .append(" already exists.").toString());
	// }
	// }
	// String err;
	// original = original.getCanonicalFile();
	// ShellScript ss = new ShellScript(
	// (new StringBuilder("ln -s ")).append(original).append(" ").append(out).toString());
	// ss.run();
	// ss.waitFor();
	// err = ss.getError();
	// if (Utils.isBlank(err)) {
	// break MISSING_BLOCK_LABEL_276;
	// }
	// if (overwrite && err.contains("File exists")) {
	// deleteNative(out);
	// makeSymLink(original, out, overwrite);
	// return;
	// }
	// try {
	// throw new RuntimeException(err);
	// } catch (Exception e) {
	// throw Utils.runtime(e);
	// }
	// }
	//
	// public static File move(File src, File dest) throws RuntimeException {
	// if (!src.exists()) {
	// throw new AssertionError();
	// }
	// File src2 = new File(src.getPath());
	// if (!src2.equals(src)) {
	// throw new AssertionError();
	// }
	// boolean ok = src2.renameTo(dest);
	// if (ok) {
	// return dest;
	// } else {
	// copy(src, dest);
	// delete(src);
	// return dest;
	// }
	// }
	//
	// public static int numLines(File file) {
	// int cnt = 0;
	// BufferedReader r = getReader(file);
	// try {
	// do {
	// String line = r.readLine();
	// if (line == null) break;
	// cnt++;
	// } while (true);
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// return cnt;
	// }
	//
	// public static void prepend(File file, String string) {
	// if ((file.isDirectory() || string == null)) {
	// throw new AssertionError();
	// }
	// if (!file.exists() || file.length() == 0L) {
	// write(file, string);
	// return;
	// }
	// try {
	// File temp = File.createTempFile("prepend", "");
	// write(temp, string);
	// FileInputStream in = new FileInputStream(file);
	// FileOutputStream out = new FileOutputStream(temp, true);
	// copy(in, out);
	// move(temp, file);
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }

	public static String read(File file) throws RuntimeException {
		try {
			return read(((InputStream) (new FileInputStream(file))));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String read(InputStream in) {
		return read(((Reader) (getReader(in))));
	}

	public static String read(Reader r) {
		try (BufferedReader reader = (r instanceof BufferedReader) ? (BufferedReader) r : new BufferedReader(r);) {
			int bufSize = 8192;
			StringBuilder sb = new StringBuilder(bufSize);
			char cbuf[] = new char[bufSize];
			do {
				int chars = reader.read(cbuf);
				if (chars == -1) break;
				if (sb.length() == 0 && cbuf[0] == '\uFEFF') {
					sb.append(cbuf, 1, chars - 1);
				} else {
					sb.append(cbuf, 0, chars);
				}
			} while (true);
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// public static byte[] readRaw(InputStream raw)
	// {
	// byte trimmed[];
	// byte all[] = new byte[10240];
	// int offset = 0;
	// do
	// {
	// int space = all.length - offset;
	// if(space < all.length / 8)
	// {
	// all = Arrays.copyOf(all, all.length * 2);
	// space = all.length - offset;
	// }
	// int r = raw.read(all, offset, space);
	// if(r == -1)
	// {
	// break;
	// }
	// offset += r;
	// } while(true);
	// trimmed = Arrays.copyOf(all, offset);
	// return trimmed;
	// Exception e;
	// e;
	// throw new RuntimeException(e);
	// }
	//
	// public static String resolveDotDot(String absolutePath)
	// {
	// return (new File(absolutePath)).getCanonicalPath();
	// IOException e;
	// e;
	// throw Utils.runtime(e);
	// }
	//
	// public static String safeFilename(String name) {
	// return safeFilename(name, true);
	// }
	//
	// public static String safeFilename(String name, boolean allowSubDirs) {
	// if (name == null) {
	// return "null";
	// }
	// name = name.trim();
	// if (name.equals("")) {
	// name = "empty";
	// }
	// if (name.length() > 5000) {
	// throw new IllegalArgumentException((new StringBuilder("Name is too long:
	// ")).append(name).toString());
	// }
	// name = name.replace("_", "__");
	// name = name.replace("..", "_.");
	// name = name.replaceAll("[^ a-zA-Z0-9-_.~/\\\\]", "");
	// name = name.trim();
	// name = name.replaceAll("\\s+", "_");
	// if (!allowSubDirs) {
	// name = name.replace("/", "_");
	// name = name.replace("\\", "_");
	// }
	// for (; "./-\\".indexOf(name.charAt(name.length() - 1)) != -1; name = name.substring(0,
	// name.length() - 1)) {}
	// if (name.length() > 50) {
	// name = (new StringBuilder(String.valueOf(name.substring(0, 10)))).append(name.hashCode())
	// .append(name.substring(name.length() - 10)).toString();
	// }
	// return name;
	// }
	//
	// public static void save(Object obj, File file) {
	// if (file.getParentFile() != null) {
	// file.getParentFile().mkdirs();
	// }
	// write(file, XStreamUtils.serialiseToXml(obj));
	// }
	//
	// private static File setDataDir()
	// {
	// String dd = System.getenv("WINTERWELL_DATA");
	// if(!Utils.isBlank(dd))
	// {
	// return (new File(dd)).getAbsoluteFile();
	// }
	// File ddf;
	// String home = System.getProperty("user.home");
	// ddf = new File(home, ".winterwell/data");
	// ddf.mkdirs();
	// if(ddf.exists() && ddf.isDirectory())
	// {
	// return ddf;
	// }
	// File f;
	// dd = "data";
	// f = (new File(dd)).getAbsoluteFile();
	// Log.report((new StringBuilder("Using fallback data directory ")).append(f).toString(),
	// Level.WARNING);
	// return f;
	// Exception e;
	// e;
	// return null;
	// }

	public static void write(File out, CharSequence page) {
		try (BufferedWriter writer = getWriter(new FileOutputStream(out))) {
			writer.append(page);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedWriter getWriter(File file) {
		try {
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedWriter getWriter(OutputStream out) {
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(out, "UTF8");
			return new BufferedWriter(writer);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	// public static BufferedReader getZippedReader(File file)
	// {
	// GZIPInputStream zos;
	// FileInputStream fos = new FileInputStream(file);
	// zos = new GZIPInputStream(fos);
	// return getReader(zos);
	// IOException ex;
	// ex;
	// throw Utils.runtime(ex);
	// }
	//
	// public static BufferedWriter getZippedWriter(File file, boolean append)
	// {
	// GZIPOutputStream zos;
	// FileOutputStream fos = new FileOutputStream(file, append);
	// zos = new GZIPOutputStream(fos);
	// return getWriter(zos);
	// IOException ex;
	// ex;
	// throw Utils.runtime(ex);
	// }
	//
	// public static Iterable grep(File baseDir, String regex, String fileNameRegex) {
	// List files = find(baseDir, fileNameRegex);
	// Pattern p = Pattern.compile(regex);
	// List found = new ArrayList();
	// for (Iterator iterator = files.iterator(); iterator.hasNext();) {
	// File file = (File) iterator.next();
	// String lines[] = StrUtils.splitLines(read(file));
	// String as[];
	// int j = (as = lines).length;
	// for (int i = 0; i < j; i++) {
	// String line = as[i];
	// if (p.matcher(line).find()) {
	// found.add(new Pair2(line, file));
	// }
	// }
	//
	// }
	//
	// return found;
	// }
	//
	// public static boolean isSafe(String filename) {
	// if (Utils.isBlank(filename)) {
	// return false;
	// }
	// if (filename.contains("..")) {
	// return false;
	// }
	// if (filename.contains(";")) {
	// return false;
	// }
	// if (filename.contains("|")) {
	// return false;
	// }
	// if (filename.contains(">")) {
	// return false;
	// }
	// return !filename.contains("<");
	// }
	//
	// public static boolean isSymLink(File f)
	// {
	// File canon = f.getCanonicalFile();
	// if(!canon.getName().equals(f.getName()))
	// {
	// return true;
	// }
	// File parent;
	// parent = f.getParentFile();
	// if(parent == null)
	// {
	// parent = f.getAbsoluteFile().getParentFile();
	// }
	// if(parent == null)
	// {
	// return false;
	// }
	// File canonParent;
	// parent = parent.getCanonicalFile();
	// canonParent = canon.getParentFile();
	// return !parent.equals(canonParent);
	// IOException e;
	// e;
	// throw Utils.runtime(e);
	// }
	//
	// public static Object load(File file) {
	// BufferedReader reader = getReader(file);
	// return XStreamUtils.serialiseFromXml(reader);
	// }
	//
	// public static Properties loadProperties(File propsFile) {
	// Exception exception;
	// InputStream stream = null;
	// Properties properties;
	// try {
	// stream = new FileInputStream(propsFile);
	// Properties props = new Properties();
	// props.load(stream);
	// properties = props;
	// } catch (IOException e) {
	// throw Utils.runtime(e);
	// } finally {
	// close(stream);
	// }
	// close(stream);
	// return properties;
	// throw exception;
	// }
	//
	// public static File[] ls(File dir, String fileNameRegex) {
	// if (!dir.isDirectory()) {
	// throw new RuntimeException(
	// (new StringBuilder()).append(dir).append(" is not a valid directory").toString());
	// } else {
	// return dir.listFiles(getRegexFilter((new
	// StringBuilder(".*")).append(fileNameRegex).toString()));
	// }
	// }
	//
	// public static void makeSymLink(File original, File out) {
	// makeSymLink(original, out, true);
	// }
	//
	// public static void makeSymLink(File original, File out, boolean overwrite) {
	// if (!Utils.getOperatingSystem().contains("linux")) {
	// throw new TodoException();
	// }
	// if (original.getAbsolutePath().equals(out.getAbsolutePath())) {
	// throw new IllegalArgumentException((new StringBuilder("Cannot sym-link to self:
	// ")).append(original)
	// .append(" = ").append(out).toString());
	// }
	// if (!original.exists()) {
	// throw new winterwell.utils.RuntimeException.FileNotFoundException(original);
	// }
	// if (!original.isDirectory() && !original.isFile()) {
	// throw new RuntimeException((new StringBuilder("Weird: ")).append(original).toString());
	// }
	// if (out.exists()) {
	// if (overwrite) {
	// delete(out);
	// } else {
	// throw new RuntimeException((new StringBuilder("Creating symlink failed: ")).append(out)
	// .append(" already exists.").toString());
	// }
	// }
	// String err;
	// original = original.getCanonicalFile();
	// ShellScript ss = new ShellScript(
	// (new StringBuilder("ln -s ")).append(original).append(" ").append(out).toString());
	// ss.run();
	// ss.waitFor();
	// err = ss.getError();
	// if (Utils.isBlank(err)) {
	// break MISSING_BLOCK_LABEL_276;
	// }
	// if (overwrite && err.contains("File exists")) {
	// deleteNative(out);
	// makeSymLink(original, out, overwrite);
	// return;
	// }
	// try {
	// throw new RuntimeException(err);
	// } catch (Exception e) {
	// throw Utils.runtime(e);
	// }
	// }
	//
	// public static File move(File src, File dest) throws RuntimeException {
	// if (!src.exists()) {
	// throw new AssertionError();
	// }
	// File src2 = new File(src.getPath());
	// if (!src2.equals(src)) {
	// throw new AssertionError();
	// }
	// boolean ok = src2.renameTo(dest);
	// if (ok) {
	// return dest;
	// } else {
	// copy(src, dest);
	// delete(src);
	// return dest;
	// }
	// }
	//
	// public static int numLines(File file) {
	// int cnt = 0;
	// BufferedReader r = getReader(file);
	// try {
	// do {
	// String line = r.readLine();
	// if (line == null) break;
	// cnt++;
	// } while (true);
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// return cnt;
	// }
	//
	// public static void prepend(File file, String string) {
	// if ((file.isDirectory() || string == null)) {
	// throw new AssertionError();
	// }
	// if (!file.exists() || file.length() == 0L) {
	// write(file, string);
	// return;
	// }
	// try {
	// File temp = File.createTempFile("prepend", "");
	// write(temp, string);
	// FileInputStream in = new FileInputStream(file);
	// FileOutputStream out = new FileOutputStream(temp, true);
	// copy(in, out);
	// move(temp, file);
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// public static String read(File file)
	// throws RuntimeException
	// {
	// return read(((InputStream) (new FileInputStream(file))));
	// IOException e;
	// e;
	// throw Utils.runtime(e);
	// }
	//
	// public static String read(InputStream in) {
	// return read(((Reader) (getReader(in))));
	// }
	//
	// public static String read(Reader r) {
	// Exception exception;
	// String s;
	// try {
	// BufferedReader reader = (r instanceof BufferedReader) ? (BufferedReader) r : new
	// BufferedReader(r);
	// int bufSize = 8192;
	// StringBuilder sb = new StringBuilder(8192);
	// char cbuf[] = new char[8192];
	// do {
	// int chars = reader.read(cbuf);
	// if (chars == -1) {
	// break;
	// }
	// if (sb.length() == 0 && cbuf[0] == '\uFEFF') {
	// sb.append(cbuf, 1, chars - 1);
	// } else {
	// sb.append(cbuf, 0, chars);
	// }
	// } while (true);
	// s = sb.toString();
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// } finally {
	// close(r);
	// }
	// close(r);
	// return s;
	// throw exception;
	// }
	//
	// public static byte[] readRaw(InputStream raw)
	// {
	// byte trimmed[];
	// byte all[] = new byte[10240];
	// int offset = 0;
	// do
	// {
	// int space = all.length - offset;
	// if(space < all.length / 8)
	// {
	// all = Arrays.copyOf(all, all.length * 2);
	// space = all.length - offset;
	// }
	// int r = raw.read(all, offset, space);
	// if(r == -1)
	// {
	// break;
	// }
	// offset += r;
	// } while(true);
	// trimmed = Arrays.copyOf(all, offset);
	// return trimmed;
	// Exception e;
	// e;
	// throw new RuntimeException(e);
	// }
	//
	// public static String resolveDotDot(String absolutePath)
	// {
	// return (new File(absolutePath)).getCanonicalPath();
	// IOException e;
	// e;
	// throw Utils.runtime(e);
	// }
	//
	// public static String safeFilename(String name) {
	// return safeFilename(name, true);
	// }
	//
	// public static String safeFilename(String name, boolean allowSubDirs) {
	// if (name == null) {
	// return "null";
	// }
	// name = name.trim();
	// if (name.equals("")) {
	// name = "empty";
	// }
	// if (name.length() > 5000) {
	// throw new IllegalArgumentException((new StringBuilder("Name is too long:
	// ")).append(name).toString());
	// }
	// name = name.replace("_", "__");
	// name = name.replace("..", "_.");
	// name = name.replaceAll("[^ a-zA-Z0-9-_.~/\\\\]", "");
	// name = name.trim();
	// name = name.replaceAll("\\s+", "_");
	// if (!allowSubDirs) {
	// name = name.replace("/", "_");
	// name = name.replace("\\", "_");
	// }
	// for (; "./-\\".indexOf(name.charAt(name.length() - 1)) != -1; name = name.substring(0,
	// name.length() - 1)) {}
	// if (name.length() > 50) {
	// name = (new StringBuilder(String.valueOf(name.substring(0, 10)))).append(name.hashCode())
	// .append(name.substring(name.length() - 10)).toString();
	// }
	// return name;
	// }
	//
	// public static void save(Object obj, File file) {
	// if (file.getParentFile() != null) {
	// file.getParentFile().mkdirs();
	// }
	// write(file, XStreamUtils.serialiseToXml(obj));
	// }
	//
	// private static File setDataDir()
	// {
	// String dd = System.getenv("WINTERWELL_DATA");
	// if(!Utils.isBlank(dd))
	// {
	// return (new File(dd)).getAbsoluteFile();
	// }
	// File ddf;
	// String home = System.getProperty("user.home");
	// ddf = new File(home, ".winterwell/data");
	// ddf.mkdirs();
	// if(ddf.exists() && ddf.isDirectory())
	// {
	// return ddf;
	// }
	// File f;
	// dd = "data";
	// f = (new File(dd)).getAbsoluteFile();
	// Log.report((new StringBuilder("Using fallback data directory ")).append(f).toString(),
	// Level.WARNING);
	// return f;
	// Exception e;
	// e;
	// return null;
	// }

}
