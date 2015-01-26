package winterwell.markdown.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import winterwell.markdown.Activator;
import winterwell.markdown.pagemodel.MarkdownPage;
import winterwell.markdown.pagemodel.MarkdownPage.Header;
import winterwell.markdown.preferences.MarkdownPreferencePage;
import winterwell.markdown.views.MarkdownPreview;


/**
 * Text editor with markdown support.
 * @author Daniel Winterstein
 */
public class MarkdownEditor extends TextEditor implements IDocumentListener 
{

	/**
	 * Maximum length for a task tag message
	 */
	private static final int MAX_TASK_MSG_LENGTH = 80;
	private ColorManager colorManager;
	private MarkdownContentOutlinePage fOutlinePage = null;
	
	IDocument oldDoc = null;
	
	private MarkdownPage page;
	
	
	private boolean pageDirty = true;
	
	private ProjectionSupport projectionSupport;
	private final IPreferenceStore pStore;
	private IPropertyChangeListener prefChangeListener;
	

	public MarkdownEditor() {
		super();
		pStore = Activator.getDefault().getPreferenceStore();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new MDConfiguration(colorManager, getPreferenceStore()));
	}

	
	@Override
	public void createPartControl(Composite parent) {
		// Over-ride to add code-folding support 
		super.createPartControl(parent);
		if (getSourceViewer() instanceof ProjectionViewer) {
		    ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();
		    projectionSupport = new ProjectionSupport(viewer,getAnnotationAccess(),getSharedColors());
		    projectionSupport.install();
		    //turn projection mode on
		    viewer.doOperation(ProjectionViewer.TOGGLE);
		}
	}
	
	/**
	 *  Returns the editor's source viewer. May return null before the editor's part has been created and after disposal.
	 */
	public ISourceViewer getViewer() {
		return getSourceViewer();
	}
	
	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
//		if (true) return super.createSourceViewer(parent, ruler, styles);
		// Create with code-folding
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		SourceViewerDecorationSupport decSupport = getSourceViewerDecorationSupport(viewer);
//		SourceViewer viewer = (SourceViewer) super.createSourceViewer(parent, ruler, styles);
		// Setup word-wrapping		
		final StyledText widget = viewer.getTextWidget();
		// Listen to pref changes
		prefChangeListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(MarkdownPreferencePage.PREF_WORD_WRAP)) {
					widget.setWordWrap(MarkdownPreferencePage.wordWrap());
				}
			}			
		};
		pStore.addPropertyChangeListener(prefChangeListener);
		// Switch on word-wrapping
		if (MarkdownPreferencePage.wordWrap()) {
			widget.setWordWrap(true);
		}		
		return viewer;	
	}
	
	public void dispose() {
		if (pStore != null) {
			pStore.removePropertyChangeListener(prefChangeListener); 
		}
		colorManager.dispose();
		super.dispose();		
	}
	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	public void documentChanged(DocumentEvent event) {
		pageDirty  = true;
	}
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		// Detach from old
		if (oldDoc!= null) {
			oldDoc.removeDocumentListener(this);
			if (doc2editor.get(oldDoc) == this) doc2editor.remove(oldDoc);
		}
		// Set
		super.doSetInput(input);		
		// Attach as a listener to new doc
		IDocument doc = getDocument();
		oldDoc = doc;
		if (doc==null) return;		
		doc.addDocumentListener(this);
		doc2editor.put(doc, this);
		// Initialise code folding
		haveRunFolding = false;
		updateSectionFoldingAnnotations(null);
	}

	@Override
	protected void editorSaved() {
		if (MarkdownPreview.preview != null) {
			// Update the preview when the file is saved
			MarkdownPreview.preview.update();
		}
	}

	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage= new MarkdownContentOutlinePage(getDocumentProvider(), this);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getEditorInput());
			}
			return fOutlinePage;
		}
		return super.getAdapter(required);
	}
	public IDocument getDocument() {
		IEditorInput input = getEditorInput();
		IDocumentProvider docProvider = getDocumentProvider();		
		return docProvider==null? null : docProvider.getDocument(input);
	}
	/**
	 * 
	 * @return The {@link MarkdownPage} for the document being edited, or null
	 * if unavailable.
	 */
	public MarkdownPage getMarkdownPage() {
		if (pageDirty) updateMarkdownPage();
		return page;
	}

	public int getPrintColumns() {
		 return getPreferenceStore().getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN);		
	}

	/**
	 * @return The text of the editor's document, or null if unavailable.
	 */
	public String getText() {
		IDocument doc = getDocument();
		return doc==null? null : doc.get();
	}

	private void updateMarkdownPage() {
		String text = getText();
		if (text==null) text="";
		page = new MarkdownPage(text);
		pageDirty = false;
	}

	void updateTaskTags(IRegion region) {
	try {	
		boolean useTags = pStore.getBoolean(MarkdownPreferencePage.PREF_TASK_TAGS);
		if (!useTags) return;		
		// Get task tags
//		IPreferenceStore peuistore = EditorsUI.getPreferenceStore();
////		IPreferenceStore pStore_jdt = org.eclipse.jdt.core.compiler.getDefault().getPreferenceStore();
//		String tagString = peuistore.getString("org.eclipse.jdt.core.compiler.taskTags");
		String tagString = pStore.getString(MarkdownPreferencePage.PREF_TASK_TAGS_DEFINED);
		List<String> tags = Arrays.asList(tagString.split(","));
		// Get resource for editor
		IFile docFile = getResource(this);
		// Get existing tasks
		IMarker[] taskMarkers = docFile.findMarkers(IMarker.TASK, true, IResource.DEPTH_INFINITE);
		List<IMarker> markers = new ArrayList<IMarker>(Arrays.asList(taskMarkers));
//		Collections.sort(markers, c) sort for efficiency
		// Find tags in doc
		List<String> text = getMarkdownPage().getText();
		for(int i=1; i<=text.size(); i++) {
			String line = text.get(i-1); // wierd off-by-one bug
			for (String tag : tags) {
				tag = tag.trim();
				int tagIndex = line.indexOf(tag);
				if (tagIndex == -1) continue;
				IMarker exists = updateTaskTags2_checkExisting(i, tagIndex, line, markers);
				if (exists!=null) {
					markers.remove(exists);
					continue;
				}
				IMarker marker = docFile.createMarker(IMarker.TASK);			    
				//Once we have a marker object, we can set its attributes
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
				String msg = line.substring(line.indexOf(tag), Math.min(tagIndex+MAX_TASK_MSG_LENGTH, line.length()-1));
				marker.setAttribute(IMarker.MESSAGE, msg);
				marker.setAttribute(IMarker.LINE_NUMBER, i);									
			}
		}
		// Remove old markers
		for (IMarker m : markers) {
			try {
				m.delete();		
			} catch (Exception ex) {
			//
			}			
		}
	} catch (Exception ex) {
		// 
	}
	}

	/**
	 * Find an existing marker, if there is one.
	 * @param i
	 * @param tagIndex
	 * @param line
	 * @param markers
	 * @return 
	 */
	private IMarker updateTaskTags2_checkExisting(int i, int tagIndex,
			String line, List<IMarker> markers) {		
		String tagMessage = line.substring(tagIndex).trim();
		for (IMarker marker : markers) {
			try {
			    Integer lineNum = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);
			    if (i != lineNum) continue;
			    String txt = ((String) marker.getAttribute(IMarker.MESSAGE)).trim();
			    if (tagMessage.equals(txt)) return marker;
			} catch (Exception ex) {
				// Ignore
			}
		}
		return null;
	}


	private IFile getResource(MarkdownEditor markdownEditor) {
		IPathEditorInput input = (IPathEditorInput) getEditorInput();
		IPath path = input.getPath();		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IFile[] files = root.findFilesForLocation(path);
		if (files.length != 1) return null;
		IFile docFile = files[0];		
		return docFile;
	}


	/**
	 * @param doc
	 * @return
	 */
	public static MarkdownEditor getEditor(IDocument doc) {
		return doc2editor.get(doc);
	}

	private static final Map<IDocument, MarkdownEditor> doc2editor = new HashMap<IDocument, MarkdownEditor>();


	/**
	 * @param region 
	 * 
	 */
	public void updatePage(IRegion region) {
//		if (!pageDirty) return;
		updateTaskTags(region);
		updateSectionFoldingAnnotations(region);
	}
	
	
	private static final Annotation[] ANNOTATION_ARRAY = new Annotation[0];

	private static final Position[] POSITION_ARRAY = new Position[0];
	
	private boolean haveRunFolding = false;
	private Map<Annotation, Position> oldAnnotations = new HashMap<Annotation, Position>(0);

	/**
	 * @param region can be null
	 */
	private void updateSectionFoldingAnnotations(IRegion region) {
		if (!haveRunFolding) region = null; // Do the whole doc
		if ( ! (getSourceViewer() instanceof ProjectionViewer)) return;
		ProjectionViewer viewer = ((ProjectionViewer)getSourceViewer());
		MarkdownPage mPage = getMarkdownPage();
		List<Header> headers = mPage.getHeadings(null);
		// this will hold the new annotations along
		// with their corresponding positions
		Map<Annotation, Position> annotations = new HashMap<Annotation, Position>();
		IDocument doc = getDocument();
		updateSectionFoldingAnnotations2(doc, headers, annotations, doc.getLength());
		// Filter existing ones
		Position[] newValues = annotations.values().toArray(POSITION_ARRAY);		
		List<Annotation> deletedAnnotations = new ArrayList<Annotation>();
		for(Entry<Annotation, Position> ae : oldAnnotations.entrySet()) {
			Position oldp = ae.getValue();
			boolean stillExists = false;
			for (Position newp : newValues) {
				if (oldp.equals(newp)) {
					annotations.remove(newp);
					stillExists = true;
					break;
				}
			}
			if (!stillExists && intersectsRegion(oldp, region)) {
				deletedAnnotations.add(ae.getKey());
			}
		}
		// Filter out-of-region ones
		for(Annotation a : annotations.keySet().toArray(ANNOTATION_ARRAY)) {
			Position p = annotations.get(a);
			if (!intersectsRegion(p , region)) annotations.remove(a);
		}
		// Adjust the page
	    ProjectionAnnotationModel annotationModel = viewer.getProjectionAnnotationModel();
	    if (annotationModel==null) return;
		annotationModel.modifyAnnotations(deletedAnnotations.toArray(ANNOTATION_ARRAY), annotations, null);
		// Remember old values
		oldAnnotations.putAll(annotations);
		for (Annotation a : deletedAnnotations) {
			oldAnnotations.remove(a);	
		}		
		haveRunFolding = true;
	}


	/**
	 * @param p
	 * @param region
	 * @return true if p overlaps with region, or if region is null
	 */
	private boolean intersectsRegion(Position p, IRegion region) {
		if (region==null) return true;
		if (p.offset > region.getOffset()+region.getLength()) return false;
		if (p.offset+p.length < region.getOffset()) return false;
		return true;
	}


	/**
	 * Calculate where to fold, sticking the info into newAnnotations
	 * @param doc 
	 * @param headers
	 * @param newAnnotations
	 * @param endParent
	 */
	private void updateSectionFoldingAnnotations2(IDocument doc, List<Header> headers,
			Map<Annotation, Position> newAnnotations, int endParent) {
		for (int i=0; i<headers.size(); i++) {
			Header header = headers.get(i);
			ProjectionAnnotation annotation = new ProjectionAnnotation();
			try {
				int line = header.getLineNumber();
				int start = doc.getLineOffset(line);
				int end = (i==headers.size()-1)? endParent
						: doc.getLineOffset(headers.get(i+1).getLineNumber());
				Position position = new Position(start, end-start);
				newAnnotations.put(annotation, position);
				// Recurse
				List<Header> subHeaders = header.getSubHeaders();
				if (subHeaders.size() > 0) {
					updateSectionFoldingAnnotations2(doc, subHeaders, newAnnotations, end);
				}
			} catch (Exception ex) {
				System.out.println(ex);
			}			
		}		
	}


}



/*

  <?xml version="1.0" encoding="UTF-8" ?> 
- <templates>
  <template name="updateSWT" description="Performs an update to an SWT control in the SWT thread" context="java" enabled="true">${control}.getDisplay().syncExec(new Runnable() { public void run() { ${control}.${cursor} } });</template> 
  <template name="findView" description="Find a workbench view by ID" context="java" enabled="true" deleted="false">${viewType} ${view} = null; IWorkbenchWindow ${window} = PlatformUI.getWorkbench().getActiveWorkbenchWindow(); if (${window} != null) { IWorkbenchPage ${activePage} = ${window}.getActivePage(); if (${activePage} != null) ${view} = (${viewType}) ${activePage}.findView("${viewID}"); } if (${view} != null) { ${cursor}//${todo}: Add operations for opened view }</template> 
  <template name="getActiveEditor" description="Retrieves the currently active editor in the active page" context="java" enabled="true" deleted="false">IEditorPart ${editor} = null; IWorkbenchWindow ${window} = PlatformUI.getWorkbench().getActiveWorkbenchWindow(); if (${window} != null) { IWorkbenchPage ${activePage} = ${window}.getActivePage(); if (${activePage} != null) ${editor} = ${activePage}.getActiveEditor(); } if (${editor} != null) { ${cursor}//${todo}: Add operations for active editor }</template>
   IEditorPart editor = null; 
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow(); 
		if (window != null) { 
			IWorkbenchPage activePage = window.getActivePage(); 
			if (activePage != null) editor = activePage.getActiveEditor(); 
		} 
		if (editor != null) { 
			// todo: Add operations for active editor 			
		}
		
  <template name="openDialog" description="Creates and opens a JFace dialog" context="java" enabled="true" deleted="false">${dialogType} ${dialog} = new ${dialogType}(${cursor}); ${dialog}.create(); //${todo}: Complete dialog creation if (${dialog}.open() == Dialog.OK) { //${todo}: Perform actions on success };</template> 
  <template name="scanExtensionRegistry" description="Scans the extension registry for extensions of a given extension point" context="java" enabled="true" deleted="false">IExtensionRegistry ${registry} = Platform.getExtensionRegistry(); IExtensionPoint ${point} = ${registry}.getExtensionPoint(${pluginId}, ${expointId}); IExtension[] ${extensions} = ${point}.getExtensions(); for (int ${index} = 0; ${index} < ${extensions}.length; ${index}++) { IConfigurationElement[] ${elements} = ${extensions}[${index}].getConfigurationElements(); for (int ${index2} = 0; ${index2} < ${elements}.length; ${index2}++) { IConfigurationElement ${element} = ${elements}[${index2}]; String ${attValue} = ${element}.getAttribute(${attName}); ${cursor}//${todo}: Implement processing for configuration element } }</template> 
  <template name="showView" description="finds a workbench view by ID and shows it" context="java" enabled="true" deleted="false">${viewType} ${view} = null; IWorkbenchWindow ${window} = PlatformUI.getWorkbench().getActiveWorkbenchWindow(); if (${window} != null) { IWorkbenchPage ${activePage} = ${window}.getActivePage(); if (${activePage} != null) try { ${view} = (${viewType}) ${activePage}.showView("${viewID}"); } catch (${Exception} e) { // ${todo}: handle exception } } if (${view} != null) { ${cursor} }</template> 
  <template name="signalError" description="Shows an error message in the editors status line" context="java" enabled="true" deleted="false">IEditorActionBarContributor ${contributor} = ${editor}.getEditorSite().getActionBarContributor(); if (${contributor} instanceof EditorActionBarContributor) { IActionBars ${actionBars} = ((EditorActionBarContributor) ${contributor}).getActionBars(); if (${actionBars} != null) { IStatusLineManager ${manager} = ${actionBars}.getStatusLineManager(); if (${manager} != null) ${manager}.setErrorMessage(msg); } }</template> 
  </templates>

*/