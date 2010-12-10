//package winterwell.markdown.pagemodel;
//
//import java.util.Arrays;
//import java.util.List;
//
//import junit.framework.TestCase;
//// import winterwell.utils.MarkdownFormatter;
//
///**
// * Test methods in the StringMethods utility class.
// */
//public class MarkdownFormatterTest extends TestCase
//{
//  /**
//   * The local line-end string. \n on unix, \r\n on windows.
//   * I really want to run through all of these tests with both styles.
//   * We'll come back to that sort of a trick.
//   */
//  // public String LINEEND = System.getProperty("line.separator");
//  public static final String LINEEND = "\r\n";
//
//  /**
//   * Test default word wrapping of a long line of normal text.
//   */
//  public void testFormatStringInt ()
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to come to the aid of " +
//      "their coopertino lattes, and " +
//      "begin the process of singing.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to the aid of" + LINEEND + // This line is 30 characters
//      "their coopertino lattes, and" + LINEEND +
//      "begin the process of singing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 30, LINEEND));
//  }
//
//  /**
//   * If the initial part of the line contains some spaces, we use that as
//   * the "indentation" for every other line.
//   * @throws Exception
//   */
//  public void testIndentOfSpaces () throws Exception
//  {
//    final String LONG_LINE = 
//      "    Now is the time for all good " +
//      "chickens to come to the aid of " +
//      "their coopertino lattes, and " +
//      "begin the process of singing.";
//    final String EXPECTED = 
//      "    Now is the time for all good" + LINEEND +
//      "    chickens to come to the aid of" + LINEEND +
//      "    their coopertino lattes, and" + LINEEND +
//      "    begin the process of singing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * Can we maintain the format of text that is already formatted?
//   * @throws Exception
//   */
//  public void testAlreadyFormatted () throws Exception
//  {
//    final String LONG_LINE = 
//      "    Now is the time for all good" + LINEEND +
//      "    chickens to come to the aid of" + LINEEND +
//      "    their coopertino lattes, and" + LINEEND +
//      "    begin the process of singing.";
//    assertEquals (LONG_LINE, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//
//  /**
//   * Formatting a single line is all fine and dandy, but what about
//   * formatting multiple paragraphs, that is, blank lines.
//   * @throws Exception
//   */
//  public void testMultipleParagraphs () throws Exception
//  {
//    final String LONG_LINE = 
//      "    Now is the time for all good " +
//      "chickens to come to their aid." + LINEEND + LINEEND +
//      "  And drink coopertino lattes, and " +
//      "begin the process of singing.";
//    final String EXPECTED = 
//      "    Now is the time for all good" + LINEEND +
//      "    chickens to come to their aid." + LINEEND + LINEEND +
//      "  And drink coopertino lattes," + LINEEND +
//      "  and begin the process of" + LINEEND + "  singing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * What if the section we are formatting, begins with line feeds?
//   * Do we keep 'em? Might as well. :-)
//   * @throws Exception
//   */
//  public void testInitialLineFeeds () throws Exception
//  {
//    final String LONG_LINE = LINEEND + LINEEND + LINEEND +
//      "    Now is the time for all good" + LINEEND +
//      "    chickens to come to the aid of" + LINEEND +
//      "    their coopertino lattes, and" + LINEEND +
//      "    begin the process of singing.";
//    assertEquals (LONG_LINE, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * We need to be able to format bulleted lists appropriately.
//   * @throws Exception
//   */
//  public void testSingleBulletedList () throws Exception
//  {
//    final String LONG_LINE = 
//      "  * Now is the time for all good " +
//      "chickens to come to the aid of " + LINEEND +
//      "their coopertino lattes, and " +
//      "begin the process of singing.";
//    final String EXPECTED = 
//      "  * Now is the time for all good" + LINEEND +
//      "    chickens to come to the aid of" + LINEEND +
//      "    their coopertino lattes, and" + LINEEND +
//      "    begin the process of singing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * What about dealing with multiple bulleted lists.
//   * @throws Exception
//   */
//  public void testMultipleBulletedList () throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to:" + LINEEND + LINEEND +
//      " * Cluck" + LINEEND + 
//      " * Sing" + LINEEND + 
//      " * Drink coopertino lattes.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to:" + LINEEND + LINEEND +
//      " * Cluck" + LINEEND + 
//      " * Sing" + LINEEND + 
//      " * Drink coopertino lattes.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * What about dealing with multiple bulleted lists.
//   * @throws Exception
//   */
//  public void testMultipleDashedBulletedList () throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to:" + LINEEND + LINEEND +
//      " - Cluck" + LINEEND + 
//      " - Sing" + LINEEND + 
//      " - Drink coopertino lattes.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to:" + LINEEND + LINEEND +
//      " - Cluck" + LINEEND + 
//      " - Sing" + LINEEND + 
//      " - Drink coopertino lattes.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * Tests whether we can have nested bulleted lists.
//   * @throws Exception
//   */
//  public void testSubindentedBulletedLists () throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to:" + LINEEND + LINEEND +
//      " * Cluck, cluck, cluck till their little feets hurt:" + LINEEND +
//      "   * Do it again and again and again and again." + LINEEND + 
//      "   * And maybe again and again if their mommy's say so." + LINEEND +
//      "     * We can indent really, really, deep with three levels of subitems." + LINEEND +
//      "     * But we aren't sure if this is getting ridiculous or just plain expected." + LINEEND +
//      " * Sing, sing, sing till their little voices break:" + LINEEND +
//      "   * Do it again and again and again and again." + LINEEND + 
//      "   * And maybe again and again if their mommy's say so." + LINEEND +
//      " * Drink coopertino lattes.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to:" + LINEEND + LINEEND +
//      " * Cluck, cluck, cluck till their" + LINEEND + 
//      "   little feets hurt:" + LINEEND +
//      "   * Do it again and again and" + LINEEND +
//      "     again and again." + LINEEND + 
//      "   * And maybe again and again if" + LINEEND + 
//      "     their mommy's say so." + LINEEND +
//      "     * We can indent really," + LINEEND +
//      "       really, deep with three" + LINEEND +
//      "       levels of subitems." + LINEEND +
//      "     * But we aren't sure if this" + LINEEND +
//      "       is getting ridiculous or " + LINEEND + 
//      "       just plain expected." + LINEEND +
//      " * Sing, sing, sing till their" + LINEEND +
//      "   little voices break:" + LINEEND +
//      "   * Do it again and again and" + LINEEND +
//      "     again and again." + LINEEND + 
//      "   * And maybe again and again if" + LINEEND + 
//      "     their mommy's say so." + LINEEND +
//      " * Drink coopertino lattes.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * Tests whether we can have nested bulleted lists.
//   * @throws Exception
//   */
//  public void testSubindentedBulletedLists2 () throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to:" + LINEEND + LINEEND +
//      " * Cluck, cluck, cluck till their little feets hurt:" + LINEEND + LINEEND +
//      "   * Do it again and again and again and again." + LINEEND +  LINEEND +
//      "   * And maybe again and again if their mommy's say so." + LINEEND + LINEEND +
//      "     * We can indent really, really, deep with three levels of subitems." + LINEEND + LINEEND +
//      "     * But we aren't sure if this is getting ridiculous or just plain expected." + LINEEND + LINEEND +
//      " * Sing, sing, sing till their little voices break:" + LINEEND + LINEEND +
//      "   * Do it again and again and again and again." + LINEEND +  LINEEND +
//      "   * And maybe again and again if their mommy's say so." + LINEEND + LINEEND +
//      " * Drink coopertino lattes.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to:" + LINEEND + LINEEND +
//      " * Cluck, cluck, cluck till their" + LINEEND + 
//      "   little feets hurt:" + LINEEND + LINEEND +
//      "   * Do it again and again and" + LINEEND +
//      "     again and again." + LINEEND +  LINEEND +
//      "   * And maybe again and again if" + LINEEND + 
//      "     their mommy's say so." + LINEEND + LINEEND +
//      "     * We can indent really," + LINEEND +
//      "       really, deep with three" + LINEEND +
//      "       levels of subitems." + LINEEND + LINEEND +
//      "     * But we aren't sure if this" + LINEEND +
//      "       is getting ridiculous or" + LINEEND + 
//      "       just plain expected." + LINEEND + LINEEND +
//      " * Sing, sing, sing till their" + LINEEND +
//      "   little voices break:" + LINEEND + LINEEND +
//      "   * Do it again and again and" + LINEEND +
//      "     again and again." + LINEEND +  LINEEND +
//      "   * And maybe again and again if" + LINEEND + 
//      "     their mommy's say so." + LINEEND + LINEEND +
//      " * Drink coopertino lattes.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * What about dealing with a numeric list?
//   * @throws Exception
//   */
//  public void testSingleNumericList () throws Exception
//  {
//    final String LONG_LINE = 
//      " 2. Now is the time for all good " +
//      "chickens to come to the aid of " +
//      "their coopertino lattes, and " +
//      "begin the process of singing.";
//    final String EXPECTED = 
//      " 2. Now is the time for all good" + LINEEND +
//      "    chickens to come to the aid of" + LINEEND +
//      "    their coopertino lattes, and" + LINEEND +
//      "    begin the process of singing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//
//  /**
//   * What about dealing with multiple bulleted lists.
//   * @throws Exception
//   */
//  public void testMultipleNumericList () throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to:" + LINEEND + LINEEND +
//      " 1. Cluck" + LINEEND + 
//      " 2. Sing" + LINEEND + 
//      " 3. Drink coopertino lattes.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to:" + LINEEND + LINEEND +
//      " 1. Cluck" + LINEEND + 
//      " 2. Sing" + LINEEND + 
//      " 3. Drink coopertino lattes.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * What about dealing with sections that should not be word wrapped, like
//   * the text between brackets (since they are hyperlinks).
//   * @throws Exception
//   */
//  public void testNoWordWrapBracket() throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to come to [the spurious and costly][3] " +
//      "aid of their coopertino cups, " +
//      "and begin sing.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to [the spurious and costly][3]" + LINEEND +
//      "aid of their coopertino cups, and" + LINEEND +
//      "begin sing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  /**
//   * What about dealing with bracketed sections with no extra white space
//   * @throws Exception
//   */
//  public void testNoWordWrapBracket2() throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to come to[the spurious and costly][3] " +
//      "aid of their coopertino cups, " +
//      "and begin sing.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to[the spurious and costly][3]" + LINEEND +
//      "aid of their coopertino cups, and" + LINEEND +
//      "begin sing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//  
//  /**
//   * What about dealing with bold sections that should not be word wrapped.
//   * @throws Exception
//   */
//  public void testNoWordWrapDoubleAsterix() throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to come to **the spurious and costly** " +
//      "aid of their coopertino cups, " +
//      "and begin sing.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to **the spurious and costly**" + LINEEND +
//      "aid of their coopertino cups, and" + LINEEND +
//      "begin sing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//
//  /**
//   * What about dealing with italic sections that should not be word wrapped
//   * @throws Exception
//   */
//  public void testNoWordWrapSingleAsterix() throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to come to *the spurious and costly* " +
//      "aid of their coopertino cups, " +
//      "and begin sing.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to *the spurious and costly*" + LINEEND +
//      "aid of their coopertino cups, and" + LINEEND +
//      "begin sing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//
//  /**
//   * What about dealing with sections that are code should not be broken.
//   * @throws Exception
//   */
//  public void testNoWordWrapCode() throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to come to `the spurious and costly` " +
//      "aid of their coopertino cups, " +
//      "and begin sing.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to `the spurious and costly`" + LINEEND +
//      "aid of their coopertino cups, and" + LINEEND +
//      "begin sing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//
//  /**
//   * What about dealing with double parenthesis sections ... these shouldn't
//   * be broken up.
//   * @throws Exception
//   */
//  public void testNoWordWrapDoubleParens() throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to come to ((the spurious and costly)) " +
//      "aid of their coopertino cups, " +
//      "and begin sing.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to ((the spurious and costly))" + LINEEND +
//      "aid of their coopertino cups, and" + LINEEND +
//      "begin sing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 34, LINEEND));
//  }
//
//
//  /**
//   * If a line, embedded in a paragraph has two spaces at the end of the line,
//   * these need to be honored and maintained.
//   * @throws Exception
//   */
//  public void testLineBreaksHonored () throws Exception
//  {
//    final String LONG_LINE = 
//      "Now is the time for all good " +
//      "chickens to come    " + LINEEND + 
//      "to the aid of their coopertino lattes, and " +
//      "begin the process of singing.";
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come  " + LINEEND +
//      "to the aid of their coopertino" + LINEEND + 
//      "lattes, and begin the process of" + LINEEND +
//      "singing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 33, LINEEND));
//  }
//  
//  /**
//   * A "blockquote" in Markdown can accept > characters at the beginning
//   * of all of the lines. 
//   * @throws Exception
//   */
//  public void testBlockQuoteSimple () throws Exception
//  {
//    final String LONG_LINE = 
//      " > Now is the time for all good " +
//      "chickens to come to the aid of " +
//      "their coopertino <lattes>, and " +
//      "begin the process of singing.";
//    final String EXPECTED = 
//      " > Now is the time for all good" + LINEEND +
//      " > chickens to come to the aid of" + LINEEND +
//      " > their coopertino <lattes>, and" + LINEEND +
//      " > begin the process of singing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (LONG_LINE, 33, LINEEND));
//  }
//
//  /**
//   * A "blockquote" in Markdown can accept > characters at the beginning
//   * of all of the lines. Can we accept a version that is already formatted?
//   * @throws Exception
//   */
//  public void testBlockQuoteAlreadyFormatted () throws Exception
//  {
//    final String EXPECTED = 
//      " > Now is the time for all good" + LINEEND +
//      " > chickens to come to the aid of" + LINEEND +
//      " > their coopertino <lattes>, and" + LINEEND +
//      " > begin the process of singing.";
//    assertEquals (EXPECTED, MarkdownFormatter.format (EXPECTED, 33, LINEEND));
//  }
//  
//  /**
//   * Tests that the "list" interface works if each string does not have
//   * carriage returns.
//   * @throws Exception
//   */
//  public void testListWithoutLinefeeds () throws Exception
//  {
//    final String lineend = System.getProperty("line.separator");
//
//    final List<String> lines = Arrays.asList ( new String[] { 
//      "Now is the time for all good",
//      "chickens to come to the aid of",
//      "their coopertino lattes, and",
//      "begin the process of singing."
//    } );
//    final String EXPECTED = 
//      "Now is the time for all good" + lineend +
//      "chickens to come to the aid of" + lineend + // This line is 30 characters
//      "their coopertino lattes, and" + lineend +
//      "begin the process of singing.";
//    
//    final String RESULTS = MarkdownFormatter.format (lines, 30);
//    assertEquals (EXPECTED, RESULTS);
//  }
//
//  /**
//   * Tests that the "list" interface works if each string has carriage returns.
//   * @throws Exception
//   */
//  public void testListWithLinefeeds () throws Exception
//  {
//    final List<String> lines = Arrays.asList ( new String[] { 
//      "Now is the time for all good chickens to come" + LINEEND,
//      "to the aid of" + LINEEND,
//      "their coopertino lattes, and" + LINEEND,
//      "begin the process of singing."
//    } );
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to the aid of" + LINEEND + // This line is 30 characters
//      "their coopertino lattes, and" + LINEEND +
//      "begin the process of singing.";
//    
//    final String RESULTS = MarkdownFormatter.format (lines, 30);
//    assertEquals (EXPECTED, RESULTS);
//  }
//
//  /**
//   * Tests that we don't break up image tags.
//   * @throws Exception
//   */
//  public void testImageTags () throws Exception
//  {
//    final List<String> lines = Arrays.asList ( new String[] { 
//      "Now is the time for all good chickens to come " +
//      "to the aid ![Some text description](http://www.google.com/images/logo.gif)" + LINEEND,
//      "their coopertino lattes, and" + LINEEND,
//      "begin the process of singing."
//    } );
//    final String EXPECTED = 
//      "Now is the time for all good" + LINEEND +
//      "chickens to come to the aid " + // This line is 30 characters
//      "![Some text description](http://www.google.com/images/logo.gif)" + LINEEND +
//      "their coopertino lattes, and" + LINEEND +
//      "begin the process of singing.";
//    
//    final String RESULTS = MarkdownFormatter.format (lines, 30);
//    assertEquals (EXPECTED, RESULTS);
//  }
//}
