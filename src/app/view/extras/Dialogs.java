package app.view.extras;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import app.control.BinBucket;
import app.control.SASManager;
import app.control.SharePointAccess;
import app.view.panels.LoginPanel;

public class Dialogs {

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
			SASManager.getInstance().load(openDialog.getSelectedFile());
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

			final String username = (String) loginPanel.getUsernameField().getEditor().getItem();
			final char[] password = loginPanel.getPasswordField().getPassword();

			if (username != null && username.length() > 0 && password != null && password.length > 0) {

				SASManager.getInstance()
						.lastLoaded$()
						.map(item -> SharePointAccess.getInstance().setupEndpoint(item.getListLocation().url))
						.doOnComplete(() -> SharePointAccess.getInstance().connect(username, password))
						.subscribe();

				if (loginPanel.getSaveCheck().isSelected()) {

					BinBucket binBucket = BinBucket.getInstance();

					binBucket.setPass(username, password);
					binBucket.invalidate(true);

//					SharePointDataSource spds = new SharePointDataSource(DOMAIN, SUB_DOMAIN);
//
//					try {
//						
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
