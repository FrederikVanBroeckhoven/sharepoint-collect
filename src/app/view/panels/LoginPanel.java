package app.view.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import app.control.BinBucket;

public class LoginPanel extends JPanel implements ItemListener {

	private static final String ACTION_USERNAME = "user";

	private static final long serialVersionUID = -4178879625119327287L;
	
	private JLabel usernameLabel;
	private JLabel passwordLabel;

	private JComboBox<String> usernameField;
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
		gbc.anchor = GridBagConstraints.CENTER;

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

		gbc.gridy = 3;
		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.insets.bottom = 0;

		add(getSaveCheck(), gbc);

		repopulate();

	}

	public void repopulate() {

		JComboBox<String> field = getUsernameField();

		field.removeAllItems();
		BinBucket.getInstance().getUsers$()
				.doOnSubscribe((e) -> field.removeItemListener(this))
				.doOnNext(user -> {
					field.addItem(user);
				})
				.doOnComplete(() -> {
					field.setSelectedItem(null);
					field.addItemListener(this);
				})
				.subscribe();

		getPasswordField().setText("");

	}

	public JComboBox<String> getUsernameField() {
		if (usernameField == null) {
			usernameField = new JComboBox<String>();
			usernameField.setEditable(true);
			usernameField.addItemListener(this);
			usernameField.setActionCommand(ACTION_USERNAME);
		}
		return usernameField;
	}

	public JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.setPreferredSize(getUsernameField().getPreferredSize());
			// https://stackoverflow.com/questions/26975275/fill-a-jpasswordfield-programmatically-without-creating-a-string-object
		}
		return passwordField;
	}

	public JCheckBox getSaveCheck() {
		if (saveCheck == null) {
			saveCheck = new JCheckBox("Save login");
		}
		return saveCheck;
	}

	public JLabel getUsernameLabel() {
		if (usernameLabel == null) {
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
	
	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getStateChange() == ItemEvent.SELECTED) {
			String user = (String) getUsernameField().getSelectedItem();
			char[] pass = BinBucket.getInstance().getPass(user);
			if (pass != null) {
				getPasswordField().setText(new String(pass));
			}
		}
	}

}
