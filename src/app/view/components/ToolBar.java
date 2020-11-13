package app.view.components;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import app.Main;
import app.control.SASLoader;
import app.view.Dialogs;

public class ToolBar extends JToolBar implements ActionListener {

	private static final String ACTION_OPEN = "open";
	private static final String ACTION_LOGIN = "login";
	private static final String ACTION_LOGOUT = "logout";
	
	private static final long serialVersionUID = 4356167833588367418L;

	private JButton openButton;
	private JButton loginButton;
	private JButton logoutButton;
	
	public ToolBar() {
		init();
	}

	private void init() {

		add(getOpenButton());

		addSeparator();

		add(getLoginButton());
		add(getLogoutButton());

		SASLoader.getInstance().onSASItemsLoaded$()
				.doOnNext(loaded -> getLoginButton().setEnabled(loaded))
				.subscribe();

		setFloatable(false);
	}
	
	public JButton getOpenButton() {
		if (openButton == null) {
			String imgLocation = "resources/images/open-folder.png";
			URL imageURL = Main.class.getClassLoader().getResource(imgLocation);

			openButton = new JButton(loadImage(imageURL, 32, 32));
			openButton.setActionCommand(ACTION_OPEN);
			openButton.addActionListener(this);

		}
		return openButton;
	}

	public JButton getLoginButton() {
		if (loginButton == null) {
			String imgLocation = "resources/images/login.png";
			URL imageURL = Main.class.getClassLoader().getResource(imgLocation);

			loginButton = new JButton(loadImage(imageURL, 32, 32));
			loginButton.setActionCommand(ACTION_LOGIN);
			loginButton.addActionListener(this);
		}
		return loginButton;
	}

	public JButton getLogoutButton() {
		if (logoutButton == null) {
			String imgLocation = "resources/images/logout.png";
			URL imageURL = Main.class.getClassLoader().getResource(imgLocation);

			logoutButton = new JButton(loadImage(imageURL, 32, 32));
			logoutButton.setActionCommand(ACTION_LOGOUT);
			logoutButton.addActionListener(this);
			logoutButton.setEnabled(false);
		}
		return logoutButton;
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(ACTION_OPEN)) {
			Dialogs.getInstance().showOpenDialog(getRootPane());
		} else if (ae.getActionCommand().equals(ACTION_LOGIN)) {
			Dialogs.getInstance().showLoginDialog(getRootPane());
		} else if (ae.getActionCommand().equals(ACTION_LOGOUT)) {
			// implement
		}
	}

	private static ImageIcon loadImage(URL url, int sizex, int sizey) {
		ImageIcon icon = new ImageIcon(url);
		Image image = icon.getImage();
		Image scaled = image.getScaledInstance(sizex, sizey, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}


}
