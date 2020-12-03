package app.view.panels;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import app.view.extras.IconSet;

public class SASTreeNodePanel extends JPanel {

	private static final long serialVersionUID = -784805728852259993L;

	private JLabel itemLabel;
	private Map<String, JLabel> fileIconLabels;

	public SASTreeNodePanel(JLabel wrapped, String[] ids) {
		this.itemLabel = wrapped;
		init(ids);
	}

	private void init(String[] ids) {

		setOpaque(false);

		FlowLayout fl = new FlowLayout(FlowLayout.LEFT, 0, 0);
		setLayout(fl);

		add(getItemLabel());
		
		for (int i = 0; i < ids.length; i++) {
			add(getIconLabel(ids[i]));
		}

	}

	public JLabel getItemLabel() {
		return itemLabel;
	}

	public JLabel getIconLabel(String id) {

		if (fileIconLabels == null) {
			fileIconLabels = new HashMap<String, JLabel>();
		}

		JLabel fileIconLabel = fileIconLabels.get(id);

		if (fileIconLabel == null) {
			fileIconLabel = new JLabel(IconSet.getFileGeneric(24));
			fileIconLabels.put(id, fileIconLabel);
		}

		return fileIconLabel;
	}

}
