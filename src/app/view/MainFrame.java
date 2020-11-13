package app.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import app.control.Log;
import app.view.components.ToolBar;
import app.view.panels.SASTreePanel;
import app.view.panels.StatusBar;
import app.view.components.MenuBar;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -295434742076759366L;

	private JPanel statusBar;
	private JToolBar toolBar;
	private JMenuBar menuBar;
	private JPanel treePanel;

	public MainFrame() throws HeadlessException {
		super("SharePoint Collect");
		init();
	}

	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new GridBagLayout());

		setJMenuBar(getSASMenuBar());

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
				.doOnNext(m -> Dialogs.getInstance().showErrorDialog(m, getRootPane()))
				.subscribe();

		pack();

	}

	public JPanel getStatusBar() {
		if (statusBar == null) {
			statusBar = new StatusBar();
		}
		return statusBar;

	}

	public JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new ToolBar();
		}
		return toolBar;
	}

	public JMenuBar getSASMenuBar() {
		if (menuBar == null) {
			menuBar = new MenuBar();
		}
		return menuBar;
	}
	
	public JPanel getTreePanel() {
		if (treePanel == null) {
			treePanel = new SASTreePanel();
			treePanel.setPreferredSize(new Dimension(640, 480));
		}
		return treePanel;
	}


}
