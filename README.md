# Eclipse Markdown Editor Plugin

[![Build Status](https://secure.travis-ci.org/winterstein/Eclipse-Markdown-Editor-Plugin.png)](http://travis-ci.org/winterstein/Eclipse-Markdown-Editor-Plugin)
<a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=369" 
title="Drag and drop into a running Eclipse toolbar area to install Markdown Text Editor">
  <img src="https://marketplace.eclipse.org/sites/all/modules/custom/marketplace/images/installbutton.png"/>
</a>

Edit .md and .txt files with outlines & syntax highlighting.  
Preview HTML.

Please see the website for information:
<http://www.winterwell.com/software/markdown-editor.php>

Eclipse Marketplace entry:
<http://marketplace.eclipse.org/content/markdown-text-editor>  
or install with [Nodeclipse CLI Installer](https://github.com/Nodeclipse/nodeclipse-1/tree/master/org.nodeclipse.ui/templates) `nodeclipse install markdown`

There is also complementary Github Flavoured Markdown Viewer
<https://github.com/satyagraha/gfm_viewer>


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
      
then check `site\target` directory for update site archive `markdown.editor.site-x.x.x.zip` and p2 repository.
Use Help -> Install New Software... -> Add... -> Archive to istall from .zip file.

Increase version

	mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=1.1.0-SNAPSHOT


<a href="http://with-eclipse.github.io/" target="_blank"><img alt="with-Eclipse logo" src="http://with-eclipse.github.io/with-eclipse-1.jpg" /></a>
