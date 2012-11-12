
package winterwell.markdown.pagemodel;

import java.util.List;

import winterwell.utils.StrUtils;

/**
 * Formats a string that is compatible with the Markdown syntax.
 * Strings must not include headers.
 * 
 * @author Howard Abrams
 */
public class MarkdownFormatter
{
  // Expect everyone to simply use the public static methods...
  private MarkdownFormatter ()
  {
  }
  
  /**
   * Formats a collection of lines to a particular width and honors typical
   * Markdown syntax and formatting. 
   * 
   * The method <i>assumes</i> that if the first line ends with a line
   * termination character, all the other lines will as well.
   * 
   * @param lines     A list of strings that should be formatted and wrapped.
   * @param lineWidth The width of the page
   * @return          A string containing each 
   */
  public static String format (List<String> lines, int lineWidth)
  {
    if (lines == null)
      return null;      // Should we return an empty string?
    
    final String lineEndings;
    if ( lines.get(0).endsWith ("\r\n") )
      lineEndings = "\r\n";
    else if ( lines.get(0).endsWith ("\r") )
      lineEndings = "\r";
    else
      lineEndings = StrUtils.LINEEND;
    
    final StringBuilder buf = new StringBuilder();
    for (String line : lines) {
      buf.append (line);
      buf.append (' ');     // We can add extra spaces with impunity, and this
                            // makes sure our lines don't run together.
    }
    return format ( buf.toString(), lineWidth, lineEndings );
  }
  

  /**
   * Formats a string of text. The formatting does line wrapping at the 
   * <code>lineWidth</code> boundary, but it also honors the formatting
   * of initial paragraph lines, allowing indentation of the entire
   * paragraph.
   * 
   * @param text       The line of text to format
   * @param lineWidth  The width of the lines
   * @return           A string containing the formatted text.
   */
  public static String format ( final String text, final int lineWidth)
  {
    return format(text, lineWidth, StrUtils.LINEEND);
  }
  
  /**
   * Formats a string of text. The formatting does line wrapping at the 
   * <code>lineWidth</code> boundary, but it also honors the formatting
   * of initial paragraph lines, allowing indentation of the entire
   * paragraph.
   * 
   * @param text       The line of text to format
   * @param lineWidth  The width of the lines
   * @param lineEnding The line ending that overrides the default System value
   * @return           A string containing the formatted text.
   */
  public static String format (final String text, final int lineWidth, final String lineEnding)
  {
    return new String( format(text.toCharArray (), lineWidth, lineEnding));
  }
  
  /**
   * The available cursor position states as it sits in the buffer.
   */
  private enum StatePosition { 
    /** The beginning of a paragraph ... the start of the buffer */
    BEGIN_FIRST_LINE, 
    
    /** The beginning of the next line, which may be completely ignored. */
    BEGIN_OTHER_LINE, 
    
    /** The beginning of a new line that will not be ignored, but appended. */
    BEGIN_NEW_LINE, 
    
    /** The middle of a line. */
    MIDDLE_OF_LINE 
  }

  /**
   * The method that does the work of formatting a string of text. The text,
   * however, is a character array, which is more efficient to work with.
   * 
   * TODO: Should we make the format(char[]) method public?
   * 
   * @param text       The line of text to format
   * @param lineWidth  The width of the lines
   * @param lineEnding The line ending that overrides the default System value
   * @return           A string containing the formatted text.
   */
  static char[] format ( final char[] text, final int lineWidth, final String lineEnding )
  {
    final StringBuilder word   = new StringBuilder();
    final StringBuilder indent = new StringBuilder();
    final StringBuilder buffer = new StringBuilder(text.length + 10);
    
    StatePosition state = StatePosition.BEGIN_FIRST_LINE;
    int lineLength = 0;

    // There are times when we will run across a character(s) that will 
    // cause us to stop doing word wrap until we get to the 
    // "end of non-wordwrap" character(s).
    //
    // If this string is set to null, it tells us to "do" word-wrapping.
    char endWordwrap1 = 0;
    char endWordwrap2 = 0;
    
    // We loop one character past the end of the loop, and when we get to
    // this position, we assign 'c' to be 0 ... as a marker for the end of
    // the string...
    
    for (int i = 0; i <= text.length; i++)
    {
      final char c;
      if (i < text.length)
        c = text[i];
      else
        c = 0;
      
      final char nextChar;
      if (i+1 < text.length)
        nextChar = text[i+1];
      else
        nextChar = 0;
      
      // Are we actually word-wrapping?
      if (endWordwrap1 != 0) {
        // Did we get the ending sequence of the non-word-wrap?  
        if ( ( endWordwrap2 == 0 && c == endWordwrap1 ) || 
             ( c == endWordwrap1 && nextChar == endWordwrap2 ) )
          endWordwrap1 = 0;
        buffer.append (c);
        lineLength++;
        
        if (endWordwrap1 == 0 && endWordwrap2 != 0) {
          buffer.append (nextChar);
          lineLength++;
          i++;
        }
        continue;
      }

      // Check to see if we got one of our special non-word-wrapping
      // character sequences ...
      
      if ( c == '['  ) {                           //    [Hyperlink]
        endWordwrap1 = ']';
      }
      else if ( c == '*' && nextChar == '*' ) {    //    **Bold**
        endWordwrap1 = '*';
        endWordwrap2 = '*';
      }                                            //    *Italics*
      else if ( c == '*' && state == StatePosition.MIDDLE_OF_LINE ) {
        endWordwrap1 = '*';
      }
      else if ( c == '`' ) {                       //    `code`
        endWordwrap1 = '`';
      }
      else if ( c == '(' && nextChar == '(' ) {    //    ((Footnote))
        endWordwrap1 = ')';
        endWordwrap2 = ')';
      }
      else if ( c == '!' && nextChar == '[' ) {    //    ![Image]
        endWordwrap1 = ')';
      }
      
      // We are no longer doing word-wrapping, so tidy the situation up...
      if (endWordwrap1 != 0) {
        if (word.length() > 0)
          lineLength = addWordToBuffer (lineWidth, lineEnding, word, indent, buffer, lineLength);
        else if (buffer.length() > 0 && buffer.charAt (buffer.length()-1) != ']' )
          buffer.append(' ');
        // We are adding an extra space for most situations, unless we get a
        // [link][ref] where we want them to be together without a space.
        
        buffer.append (c);
        lineLength++;
        continue;
      }

      // Normal word-wrapping processing continues ...
      
      if (state == StatePosition.BEGIN_FIRST_LINE)
      {
        if ( c == '\n' || c == '\r' ) { // Keep, but ignore initial line feeds
          buffer.append (c);
          lineLength = 0;
          continue;
        }

        if (Character.isWhitespace (c))
          indent.append (c);
        else if ( (c == '*' || c == '-' || c == '.' ) &&
                Character.isWhitespace (nextChar) )
          indent.append (' ');
        else if ( Character.isDigit (c) && nextChar == '.' &&
                Character.isWhitespace (text[i+2]))
          indent.append (' ');
        else if ( c == '>' )
          indent.append ('>');
        else
          state = StatePosition.MIDDLE_OF_LINE;

        // If we are still in the initial state, then put 'er in...
        if (state == StatePosition.BEGIN_FIRST_LINE) {
          buffer.append (c);
          lineLength++;
        }
      }
      
      // While it would be more accurate to explicitely state the range of
      // possibilities, with something like:
      //    EnumSet.range (StatePosition.BEGIN_OTHER_LINE, StatePosition.MIDDLE_OF_LINE ).contains (state)
      // We know that what is left is just the BEGIN_FIRST_LINE ...
      
      if ( state != StatePosition.BEGIN_FIRST_LINE )
      {
        // If not the middle of the line, then it must be at the first of a line
        // Either   BEGIN_OTHER_LINE  or  BEGIN_NEW_LINE
        if (state != StatePosition.MIDDLE_OF_LINE)
        {
          if ( Character.isWhitespace(c) || c == '>' || c == '.' )
            word.append (c);
          else if ( ( ( c == '*' || c == '-' ) && Character.isWhitespace (nextChar) ) ||
                    ( Character.isDigit(c) && nextChar == '.' && Character.isWhitespace( text[i+2] ) ) ) {
            word.append (c);
            state = StatePosition.BEGIN_NEW_LINE;
          }
          else {
            if (state == StatePosition.BEGIN_NEW_LINE) {
              buffer.append (word);
              lineLength = word.substring ( word.indexOf("\n")+1 ).length();
            }
            word.setLength (0);
            state = StatePosition.MIDDLE_OF_LINE;
          }
        }
        
        if (state == StatePosition.MIDDLE_OF_LINE)
        {
          // Are we at the end of a word? Then we need to calculate whether
          // to wrap the line or not.
          //
          // This condition does double duty, in that is also serves to
          // ignore multiple spaces and special characters that may be at
          // the beginning of the line.
          if ( Character.isWhitespace(c) || c == 0 ) 
          {
            if ( word.length() > 0) {
              lineLength = addWordToBuffer (lineWidth, lineEnding, word, indent, buffer, lineLength);
            }
            // Do we we two spaces at the end of the line? Honor this...
            else if ( c == ' ' && ( nextChar == '\r' || nextChar == '\n' ) &&
                    state != StatePosition.BEGIN_OTHER_LINE ) {
              buffer.append ("  ");
              buffer.append (lineEnding);
              lineLength = 0;
            }

            if ( c == '\r' || c == '\n' ) {
              state = StatePosition.BEGIN_OTHER_LINE;
              word.append(c);
            }
            
            // Linefeeds are completely ignored and just treated as whitespace,
            // unless, of course, there are two of 'em... and of course, end of
            // lines are simply evil on Windows machines.

            if ( (c == '\n' && nextChar == '\n') ||    // Unix-style line-ends
                    ( c == '\r' && nextChar == '\n' &&    // Windows-style line-ends
                            text[i+2] == '\r' && text[i+3] == '\n' )  ) 
            {
              state = StatePosition.BEGIN_FIRST_LINE;
              word.setLength(0);
              indent.setLength (0);
              lineLength = 0;

              if (c == '\r') { // If we are dealing with Windows-style line-ends,
                i++;           // we need to skip past the next character...
                buffer.append("\r\n");
              } else
                buffer.append(c);
            }

          } else {
            word.append (c);
            state = StatePosition.MIDDLE_OF_LINE;
          }
        }
      }
    }
    
    return buffer.toString().toCharArray();
  }

  /**
   * Adds a word to the buffer, performing word wrap if necessary.
   * @param lineWidth    The current width of the line
   * @param lineEnding   The line ending to append, if necessary
   * @param word         The word to append
   * @param indent       The indentation string to insert, if necesary
   * @param buffer       The buffer to perform all this stuff to
   * @param lineLength   The current length of the current line
   * @return             The new length of the current line
   */
  private static int addWordToBuffer (final int lineWidth, final String lineEnding, 
                                      final StringBuilder word, 
                                      final StringBuilder indent, 
                                      final StringBuilder buffer, int lineLength)
  {
    if ( word.length() + lineLength + 1 > lineWidth )
    {
      buffer.append (lineEnding);
      buffer.append (indent);
      buffer.append (word);

      lineLength = indent.length() + word.length();
    }
    else {
      if ( lineLength > indent.length() )
        buffer.append (' ');
      buffer.append (word);
      lineLength += word.length() + 1;
    }
    word.setLength (0);
    return lineLength;
  }
}
