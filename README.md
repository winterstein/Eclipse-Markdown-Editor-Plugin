# Eclipse Markdown Test Editor Plugin

Edit .md and .txt files with outlines & syntax highlighting.  
Preview HTML.

Please see the website for information:
http://www.winterwell.com/software/markdown-editor.php

## Eclipse Dev Details

Main Editor class `winterwell.markdown.editors.MarkdownEditor` defined as

      <editor
            name="Markdown Editor"
            extensions="txt,md"
            icon="icons/notepad.gif"
            contributorClass="winterwell.markdown.editors.ActionBarContributor"
            class="winterwell.markdown.editors.MarkdownEditor"
            id="winterwell.markdown.editors.MarkdownEditor">
      </editor>
