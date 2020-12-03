package app.view.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import app.control.SASManager;
import app.view.extras.Dialogs;

public class MenuBar extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 8375663491557655519L;

	private static final String ACTION_OPEN = "open";
	private static final String ACTION_LOGIN = "login";
	private static final String ACTION_EXIT = "exit";

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem exitMenuItem;

	private JMenu connectionMenu;
	private JMenuItem connectMenuItem;

	public MenuBar() {
		init();
	}

	private void init() {

		add(getFileMenu());
		add(getConnectionMenu());

		SASManager.getInstance().onSASItemsLoaded$()
				.doOnNext(loaded -> getConnectMenuItem().setEnabled(loaded))
				.subscribe();

	}

	public JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);

			fileMenu.add(getOpenMenuItem());
			fileMenu.add(new JSeparator(JSeparator.HORIZONTAL));
			fileMenu.add(getExitMenuItem());
		}

		return fileMenu;
	}

	public JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem("Open...");
			openMenuItem.setMnemonic(KeyEvent.VK_O);
			openMenuItem.setActionCommand(ACTION_OPEN);
			openMenuItem.addActionListener(this);
		}

		return openMenuItem;
	}

	public JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_X);
			exitMenuItem.setActionCommand(ACTION_EXIT);
			exitMenuItem.addActionListener(this);
		}

		return exitMenuItem;
	}

	public JMenu getConnectionMenu() {
		if (connectionMenu == null) {
			connectionMenu = new JMenu("Connection");
			connectionMenu.setMnemonic(KeyEvent.VK_C);
			
			connectionMenu.add(getConnectMenuItem());
		}
		return connectionMenu;
	}

	public JMenuItem getConnectMenuItem() {
		if (connectMenuItem == null) {
			connectMenuItem = new JMenuItem("Connect...");
			connectMenuItem.setMnemonic(KeyEvent.VK_N);
			connectMenuItem.setActionCommand(ACTION_LOGIN);
			connectMenuItem.addActionListener(this);

			connectMenuItem.setEnabled(false);

		}
		return connectMenuItem;
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(ACTION_OPEN)) {
			Dialogs.getInstance().showOpenDialog(getRootPane());
		} else if (ae.getActionCommand().equals(ACTION_LOGIN)) {
			Dialogs.getInstance().showLoginDialog(getRootPane());
		} else if (ae.getActionCommand().equals(ACTION_EXIT)) {
			System.exit(0);
		}
	}

}
