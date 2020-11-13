package app.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import app.Main;
import app.control.Log;

public class MainFrame extends JFrame implements ActionListener {

	private static final String ACTION_OPEN = "open";
	private static final String ACTION_LOGIN = "login";
	private static final String ACTION_LOGOUT = "logout";
	private static final String ACTION_EXIT = "exit";

	private static final long serialVersionUID = -295434742076759366L;

	private JPanel statusBar;
	private JToolBar toolBar;
	private JProgressBar progressBar;

	private JPanel treePanel;

	public MainFrame() throws HeadlessException {
		super("SharePoint Collect");
		init();
	}

	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new GridBagLayout());

		setJMenuBar(createMenuBar());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		getContentPane().add(getToolBar(), gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(5, 5, 5, 5);

		getContentPane().add(getTreePanel(), gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.weighty = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 5, 5, 5);

		getContentPane().add(getStatusBar(), gbc);

		Log.getInstance().onUserError$()
				.doOnNext(m -> Dialogs.getInstance().showErrorDialog(m, this.getTreePanel()))
				.subscribe();

		Log.getInstance().onGlobalProgress$()
				.doOnNext(progress -> {

					JProgressBar bar = getProgressBar();

					switch (progress.state) {
						case DONE: {
							bar.setForeground(Color.green.darker());
							bar.setString(progress.label);
							break;
						}
						case FAIL: {
							bar.setForeground(Color.RED);
							bar.setValue(100);
							bar.setString(progress.label);
							break;
						}
						case PROGRESS:
						default: {
							bar.setForeground(null);
							double dval = (double) Math.round(progress.percent * 10000) / 100;
							bar.setValue((int) dval);
							bar.setString(Double.toString(dval) + "%");
						}
					}

				})
				.subscribe();

		pack();

	}

	public JPanel getStatusBar() {

		if (statusBar == null) {

			statusBar = new JPanel();

			statusBar.setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();

			JSeparator sep = new JSeparator();

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 0;

			statusBar.add(sep, gbc);

			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 0;
			gbc.weighty = 1;
			gbc.insets = new Insets(5, 0, 0, 0);

			statusBar.add(getProgressBar(), gbc);

		}
		return statusBar;

	}

	public JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar(0, 100);

			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			progressBar.setForeground(null);
			progressBar.setString("nothing loaded...");

			progressBar.setMinimumSize(new Dimension(200, progressBar.getPreferredSize().height));
			progressBar.setPreferredSize(progressBar.getMinimumSize());
		}
		return progressBar;
	}

	public JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			String imgLocation = "resources/images/open-folder.png";

			URL imageURL = Main.class.getClassLoader().getResource(imgLocation);

			JButton open = new JButton(loadImage(imageURL, 32, 32));
			open.setActionCommand(ACTION_OPEN);
			open.addActionListener(this);

			toolBar.add(open);
			
			toolBar.addSeparator();

			String imgLocation2 = "resources/images/login.png";

			URL imageURL2 = Main.class.getClassLoader().getResource(imgLocation2);

			JButton login = new JButton(loadImage(imageURL2, 32, 32));
			login.setActionCommand(ACTION_LOGIN);
			login.addActionListener(this);
			login.setEnabled(false);

			toolBar.add(login);

			String imgLocation3 = "resources/images/logout.png";

			URL imageURL3 = Main.class.getClassLoader().getResource(imgLocation3);

			JButton logout = new JButton(loadImage(imageURL3, 32, 32));
			logout.setActionCommand(ACTION_LOGOUT);
			logout.addActionListener(this);
			logout.setEnabled(false);

			toolBar.add(logout);

			
			toolBar.setFloatable(false);

		}
		return toolBar;
	}

	public JPanel getTreePanel() {
		if (treePanel == null) {
			treePanel = new SASTreePanel();
			treePanel.setPreferredSize(new Dimension(640, 480));
		}
		return treePanel;
	}

	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();

		JMenu file = new JMenu("File");

		JMenuItem open = new JMenuItem("Open...");
		open.setMnemonic(KeyEvent.VK_O);
		open.setActionCommand(ACTION_OPEN);
		open.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		open.addActionListener(this);

		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_X);
		exit.setActionCommand(ACTION_EXIT);
		exit.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		exit.addActionListener(this);

		file.add(open);
		file.add(new JSeparator(JSeparator.HORIZONTAL));
		file.add(exit);

		bar.add(file);

		return bar;
	}

	private static ImageIcon loadImage(URL url, int sizex, int sizey) {
		ImageIcon icon = new ImageIcon(url);
		Image image = icon.getImage();
		Image scaled = image.getScaledInstance(sizex, sizey, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(ACTION_OPEN)) {
			Dialogs.getInstance().showOpenDialog(getTreePanel());
		} else if (ae.getActionCommand().equals(ACTION_LOGIN)) {
			Dialogs.getInstance().showLoginDialog(getTreePanel());
		} else if (ae.getActionCommand().equals(ACTION_LOGOUT)) {
			// implement
		} else if (ae.getActionCommand().equals(ACTION_EXIT)) {
			System.exit(0);
		}
	}

}
