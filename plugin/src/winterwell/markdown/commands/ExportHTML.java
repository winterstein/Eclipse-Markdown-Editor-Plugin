package winterwell.markdown.commands;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;

import winterwell.markdown.editors.MarkdownEditor;
import winterwell.markdown.util.FileUtils;

public class ExportHTML extends Action {

	public ExportHTML() {
		super("Export to HTML");
	}

	@Override
	public void run() {
		IEditorPart ed = ActionBarContributor.getActiveEditor();
		if (!(ed instanceof MarkdownEditor)) {
			return;
		}
		MarkdownEditor editor = (MarkdownEditor) ed;
		IEditorInput i = editor.getEditorInput();
		if (i instanceof IPathEditorInput) {
			IPathEditorInput input = (IPathEditorInput) i;
			IPath path = input.getPath();
			path = path.removeFileExtension();
			path = path.addFileExtension("html");
			File file = path.toFile();
			String html = editor.getMarkdownPage().html();
			FileUtils.write(file, html);
		}
	}
}
