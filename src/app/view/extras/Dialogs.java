package app.view.extras;

import java.io.File;
import java.net.URI;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import app.control.BinBucket;
import app.control.SASLoader;
import app.view.panels.LoginPanel;
import dashboard.connect.sharepoint.SharePointDataSource;
import dashboard.connect.sharepoint.SharePointItem;

public class Dialogs {

	private static final String DOMAIN = "https://zonnehoedbe.sharepoint.com";
	private static final String SUB_DOMAIN = "dev";

	private static Dialogs dialogs;

	public static Dialogs getInstance() {
		if (dialogs == null) {
			dialogs = new Dialogs();
		}

		return dialogs;
	}

	private JFileChooser openDialog;
	private LoginPanel loginPanel;

	private Dialogs() {
	}

	public void showOpenDialog(JComponent parent) {
		if (openDialog == null) {
			openDialog = new JFileChooser();

			FileFilter jsonFilter = new FileNameExtensionFilter("JSON files", "json");
			openDialog.addChoosableFileFilter(jsonFilter);
			openDialog.setFileFilter(jsonFilter);

		}

		int response = openDialog.showOpenDialog(parent);

		if (response == JFileChooser.APPROVE_OPTION) {
			SASLoader.getInstance().load(openDialog.getSelectedFile());
		}
	}

	public void showErrorDialog(String message, JComponent parent) {
		JOptionPane.showMessageDialog(parent,
				message,
				"Error",
				JOptionPane.ERROR_MESSAGE,
				IconSet.getStop(32));
	}

	public void showLoginDialog(JComponent parent) {

		if (loginPanel == null) {
			loginPanel = new LoginPanel();
		} else {
			loginPanel.repopulate();
		}

		String username = null;
		char[] password = null;

		while (true) {

			int result = JOptionPane.showOptionDialog(
					parent,
					loginPanel,
					"Login",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE,
					null,
					new String[] { "Login", "Cancel" },
					"Login");

			if (result != JOptionPane.OK_OPTION) {
				break;
			}

			username = (String) loginPanel.getUsernameField().getEditor().getItem();
			password = loginPanel.getPasswordField().getPassword();

			if (username != null && username.length() > 0 && password != null && password.length > 0) {

				System.out.println("loggin in with " + username + ":" + new String(password));
				
				if (loginPanel.getSaveCheck().isSelected()) {

					BinBucket binBucket = BinBucket.getInstance();

					binBucket.setPass(username, password);
					binBucket.invalidate(true);

//					SharePointDataSource spds = new SharePointDataSource(DOMAIN, SUB_DOMAIN);
//
//					try {
//						spds.getCredentials().aquire(username, new String(password));
//
//						List<SharePointItem> items = spds.listItems("Test Lijst");
//
//						for (SharePointItem sharePointItem : items) {
//
//							System.out.println(sharePointItem);
//
//						}
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

					for (int i = 0; i < password.length; i++) {
						password[i] = 0;
					}

				}

				break;
			}

			showErrorDialog("Please, provide a username and password to login", parent);

		}

	}

}
