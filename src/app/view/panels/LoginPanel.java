package app.view.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPanel extends JPanel {

	private static final long serialVersionUID = -4178879625119327287L;

	private JLabel usernameLabel;
	private JLabel passwordLabel;
	
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JCheckBox saveCheck;
	
	public LoginPanel() {
		init();
	}

	private void init() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.insets.right = 5;
		gbc.insets.bottom = 5;
		
		add(getUsernameLabel(), gbc);

		gbc.gridy = 1;

		add(getPasswordLabel(), gbc);

		gbc.gridy = 0;
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.insets.right = 0;
		
		add(getUsernameField(), gbc);

		gbc.gridy = 1;

		add(getPasswordField(), gbc);

		gbc.gridy = 2;
		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.insets.bottom = 0;
		
		add(getSaveCheck(), gbc);		
		
	}
	
	public JTextField getUsernameField() {
		if(usernameField == null) {
			usernameField = new JTextField();
		}
		return usernameField;
	}
	
	public JPasswordField getPasswordField() {
		if(passwordField == null) {
			passwordField = new JPasswordField();
		}
		return passwordField;
	}
	
	public JCheckBox getSaveCheck() {
		if(saveCheck == null) {
			saveCheck = new JCheckBox("Save login");
			saveCheck.setEnabled(false);
		}
		return saveCheck;
	}
	
	public JLabel getUsernameLabel() {
		if(usernameLabel == null) {
			usernameLabel = new JLabel("Username:");
		}
		return usernameLabel;
	}
	
	public JLabel getPasswordLabel() {
		if (passwordLabel == null) {
			passwordLabel = new JLabel("Password:");
		}
		return passwordLabel;
	}
	
}
