package app.view.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import app.control.SASManager;
import app.view.extras.Dialogs;
import app.view.extras.IconSet;

public class ToolBar extends JToolBar implements ActionListener {

	private static final String ACTION_OPEN = "open";
	private static final String ACTION_LOGIN = "login";
	
	private static final long serialVersionUID = 4356167833588367418L;

	private JButton openButton;
	private JButton loginButton;
	
	public ToolBar() {
		init();
	}

	private void init() {

		add(getOpenButton());

		addSeparator();

		add(getLoginButton());

		
		
		SASManager.getInstance().onSASItemsLoaded$()
				.doOnNext(loaded -> getLoginButton().setEnabled(loaded))
				.subscribe();

		setFloatable(false);
	}
	
	public JButton getOpenButton() {
		if (openButton == null) {
			openButton = new JButton(IconSet.getOpen(32));
			openButton.setActionCommand(ACTION_OPEN);
			openButton.addActionListener(this);

		}
		return openButton;
	}

	public JButton getLoginButton() {
		if (loginButton == null) {
			loginButton = new JButton(IconSet.getLogin(32));
			loginButton.setActionCommand(ACTION_LOGIN);
			loginButton.addActionListener(this);
			loginButton.setEnabled(false);
		}
		return loginButton;
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(ACTION_OPEN)) {
			Dialogs.getInstance().showOpenDialog(getRootPane());
		} else if (ae.getActionCommand().equals(ACTION_LOGIN)) {
			Dialogs.getInstance().showLoginDialog(getRootPane());
		}
	}

}
