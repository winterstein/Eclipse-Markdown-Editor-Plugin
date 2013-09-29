# Eclipse Markdown Test Editor Plugin

[![Build Status](https://secure.travis-ci.org/winterstein/Eclipse-Markdown-Editor-Plugin.png)](http://travis-ci.org/winterstein/Eclipse-Markdown-Editor-Plugin)

Edit .md and .txt files with outlines & syntax highlighting.  
Preview HTML.

Please see the website for information:
<http://www.winterwell.com/software/markdown-editor.php>

Eclipse Marketplace entry:
<http://marketplace.eclipse.org/content/markdown-text-editor>

## Eclipse Dev Details

![](overview.png)

Main Editor class `winterwell.markdown.editors.MarkdownEditor` defined as

      <editor
            name="Markdown Editor"
            extensions="txt,md"
            icon="icons/notepad.gif"
            contributorClass="winterwell.markdown.editors.ActionBarContributor"
            class="winterwell.markdown.editors.MarkdownEditor"
            id="winterwell.markdown.editors.MarkdownEditor">
      </editor>

### Build

	mvn package
      
then check `site\target` directory for update site archive `markdown.editor.site-x.x.x.zip` and p2 repository
