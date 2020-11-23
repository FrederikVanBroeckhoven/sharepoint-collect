package app.view.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;

import app.control.Log;

public class StatusBarPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JProgressBar progressBar;

	public StatusBarPanel() {
		init();
	}

	private void init() {
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		JSeparator sep = new JSeparator();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		add(sep, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 0, 0, 0);

		add(getProgressBar(), gbc);

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

}
