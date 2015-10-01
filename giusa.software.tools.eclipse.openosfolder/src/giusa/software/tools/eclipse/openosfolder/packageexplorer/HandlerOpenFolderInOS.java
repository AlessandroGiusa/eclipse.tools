/**
 * 
 */
package giusa.software.tools.eclipse.openosfolder.packageexplorer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * This class figures out, which element is selected implicit or explicit and
 * opens the underlying resource in the operating system. Currently it is
 * possible to get information out of the:
 * 
 * <pre>
 * - org.eclipse.jdt.ui.PackageExplorer
 * - org.eclipse.ui.views.ResourceNavigator
 * - org.eclipse.cdt.ui.CView
 * 
 * for the - org.eclipse.ui.navigator.ProjectExplorer it was planed also but somehow it's not possible to attach 
 * a new context menu to it
 * </pre>
 * 
 * -> Shortcuts are <b>Ctrl+Alt+O</b> <br>
 * -> Used command is: giusa.software.tools.eclipse.openosfolder.command <br>
 * <br>
 * For opening the folder of OS the {@link Desktop} class is used.
 * 
 * 
 * @author alessandro.giusa@gmail.com
 * @version 1.0
 * 
 */
public class HandlerOpenFolderInOS extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (Desktop.isDesktopSupported()) {
			try {
				final ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getSelection();
				File fileToOpen = null;
				if (selection != null && selection instanceof TreeSelection) {
					final TreeSelection treeSelection = (TreeSelection) selection;
					final Object obj = treeSelection.getFirstElement();
					try {
						if (obj instanceof IFile) {
							fileToOpen = this.getFileObj(((IFile) obj).getLocation());
							
						} else if (obj instanceof IProject) {
							fileToOpen = this.getFileObj(((IProject) obj).getLocation());
							
						} else if (obj instanceof IJavaProject) {
							fileToOpen = this.getFileObj(((IJavaProject) obj).getResource().getLocation());
							
						} else if (obj instanceof IJavaElement) {
							final IResource res = ((IJavaElement) obj).getResource();
							if (res != null) {
								fileToOpen = this.getFileObj(res.getLocation());
							} else {
								fileToOpen = this.getFileObj(((IJavaElement) obj).getPath());
							}
						} else if (obj instanceof IFolder) {
							fileToOpen = this.getFileObj(((IFolder) obj).getLocation());
							
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
						this.displayError(e.getMessage());
					}
					// check if the path is known and open if possible
					if (fileToOpen != null) {
						this.openFolder(fileToOpen);
					} else {
						this.displayWarning("For the selected element no support available OR no element selected");
					}
				} else {
					fileToOpen = this.getFileObj(this.getActivatedEditor());
					if (fileToOpen != null) {
						this.openFolder(fileToOpen);
					} else {
						this.displayWarning("For the selected element no support available OR no element selected");
					}
					return null;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			this.displayWarning("Feature under your Platfrom not available");
		}
		return null;
	}

	/**
	 * Opens the file with the {@link Desktop} class
	 * 
	 * @param file
	 */
	private void openFolder(final File file) {
		if (file != null) {
			final Desktop desktop = Desktop.getDesktop();
			try {
				desktop.open(file);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Displays a warning
	 * 
	 * @param message
	 */
	private void displayWarning(final String message) {
		if (message != null) {
			final MessageBox box = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					SWT.OK | SWT.ICON_WARNING);
			box.setMessage(message);
			box.setText("Sorry:-(");
			box.open();
		}
	}
	
	private void displayError(final String message) {
		if (message != null) {
			final MessageBox box = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					SWT.OK | SWT.ICON_ERROR);
			box.setMessage(message);
			box.setText("Sorry:-(");
			box.open();
		}
	}

	/**
	 * Get the location of the opened active editor
	 */
	private IPath getActivatedEditor() {
		try {
			final IEditorPart part = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (part != null) {
				final Object obj = part.getEditorInput().getAdapter(FileEditorInput.class);
				if (obj != null && obj instanceof FileEditorInput) {
					final FileEditorInput fie = (FileEditorInput) obj;
					return fie.getFile().getLocation();
				}
			}
		} catch (NullPointerException e) {
		}
		return null;
	}

	/**
	 * Get the file object based on the {@link IPath} which was passed
	 * 
	 * @param ipath
	 * @return
	 */
	private File getFileObj(final IPath ipath) {
		if (ipath != null) {
			try {
				if (ipath.toFile().isFile()) {
					String path = ipath.toFile().getAbsolutePath();
					path = path.replaceAll("\\\\", "/");
					path = path.substring(0, path.lastIndexOf("/"));
					return new File(path);
				} else if (ipath.toFile().isDirectory()) {
					return ipath.toFile();
				}
			} catch (IndexOutOfBoundsException e) {
			}
		}
		return null;
	}
}
