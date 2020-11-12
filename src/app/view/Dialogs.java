package app.view;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import app.control.SASLoader;

public class Dialogs {

	private static Dialogs dialogs;
	
	public static Dialogs getInstance() {
		if(dialogs == null) {
			dialogs = new Dialogs();
		}
		
		return dialogs;
	}

	private JFileChooser openDialog;

	private Dialogs() {
	}
	
	public void showOpenDialog(JComponent parent) {
		if(openDialog == null) {
			openDialog = new JFileChooser();
			
			FileFilter jsonFilter = new FileNameExtensionFilter("JSON files", "json");
			openDialog.addChoosableFileFilter(jsonFilter);
			openDialog.setFileFilter(jsonFilter);
			
		}
		
		int response = openDialog.showOpenDialog(parent);
		
		if(response == JFileChooser.APPROVE_OPTION) {
			SASLoader.getInstance().load(openDialog.getSelectedFile());
		}
	}
	
	public void showErrorDialog(String message, JComponent parent) {
		JOptionPane.showMessageDialog(parent,
			    message,
			    "Error",
			    JOptionPane.ERROR_MESSAGE);
	}
	
	
}
