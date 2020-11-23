package app.view.panels;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.javatuples.Pair;

import app.control.SASLoader;
import app.model.SASItem;
import app.view.extras.IconSet;
import io.reactivex.rxjava3.core.Observable;

public class SASTreePanel extends JPanel {

	private static final long serialVersionUID = -8579554707491198187L;

	private JTree tree;
	private JScrollPane treeScroll;

	private DefaultTreeModel treeModel;

	private static enum SASNodeType {
		SAS_FILE,
		COMPOUND,
		INDICATION,
		STUDY,
		R_EVENT,
		ATTACHMENT
	}

	private static class SASNode {

		public SASNodeType type;
		public String value;
		public Icon icon;

//		public SASNode(SASNodeType type, String value) {
//			this(type, value, null);
//		}
//
		public SASNode(SASNodeType type, String value, Icon icon) {
			super();
			this.type = type;
			this.value = value;
			this.icon = icon;
		}

		public boolean equals(Object obj) {
			return equals((SASNode) obj);
		}

		public boolean equals(SASNode sas) {
			return sas.type == type && sas.value.equals(value);
		}

		@Override
		public String toString() {
			return value;
		}

	}

	private static class SASNodeRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = -2633553301821609L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object obj, boolean sel, boolean exp, boolean leaf,
				int row, boolean focus) {

			// OK, not the cleanest code, but definitely the safest :)

			Component c = super.getTreeCellRendererComponent(tree, obj, sel, exp, leaf, row, focus);

			if (!(obj instanceof DefaultMutableTreeNode)) {
				return c;
			}

			DefaultMutableTreeNode dmObj = (DefaultMutableTreeNode) obj;
			if (!(dmObj.getUserObject() instanceof SASNode)) {
				return c;
			}

			SASNode node = (SASNode) dmObj.getUserObject();

			if (!(c instanceof JLabel)) {
				return c;
			}

			JLabel label = (JLabel) c;
			if (node != null && node.icon != null) {
				label.setIcon(node.icon);
			}

			return label;
		}

	}

	public SASTreePanel() {
		init();
	}

	private void init() {

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;

		add(getTreeScroll(), gbc);

		SASLoader.getInstance().onSASFileLoad$()
				.withLatestFrom(
						SASLoader.getInstance().onSASItemsLoaded$(),
						(file, loaded) -> Pair.with(file,
								loaded
										? IconSet.getInstance().getImageIcon(IconSet.ICON_FILE_OK, 24)
										: IconSet.getInstance().getImageIcon(IconSet.ICON_FILE_ERROR, 24)))
				.doOnNext(pair -> {
					getMutubaleRoot().setUserObject(
							new SASNode(SASNodeType.SAS_FILE, pair.getValue0().getName(), pair.getValue1()));
					getTree().setRootVisible(true);
					refreshTree();
				})
				.subscribe();

		SASLoader.getInstance().onSASItemsLoad$()
				.doOnNext(sass -> {
					clearTree();
					refreshTree();
				})
				.flatMap(sass -> Observable.fromArray(sass))
				.map(sas -> createBranch(sas))
				.doOnNext(branch -> mergeTree(branch, getMutubaleRoot()))
				// don't over-do the tree refresh and expansion.
				.throttleLatest(100, TimeUnit.MILLISECONDS, true)
				.doOnNext(branch -> {
					refreshTree();
					expandAll();
				})
				.subscribe();

	}

	public TreeModel getTreeModel() {
		return getMutableTreeModel();
	}

	public JTree getTree() {
		if (tree == null) {
			tree = new JTree(getTreeModel());
			tree.setRootVisible(false);
			tree.setCellRenderer(new SASNodeRenderer());
		}
		return tree;
	}

	public JScrollPane getTreeScroll() {
		if (treeScroll == null) {
			treeScroll = new JScrollPane(getTree());
		}
		return treeScroll;
	}

	private DefaultMutableTreeNode getMutubaleRoot() {
		return (DefaultMutableTreeNode) getTreeModel().getRoot();
	}

	private DefaultTreeModel getMutableTreeModel() {
		if (treeModel == null) {
			treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
		}
		return treeModel;
	}

	private DefaultMutableTreeNode createBranch(SASItem item) {
		DefaultMutableTreeNode compoundNode = new DefaultMutableTreeNode(
				new SASNode(SASNodeType.COMPOUND, item.getCompound(), IconSet.getCompound(24)));
		DefaultMutableTreeNode indicationNode = new DefaultMutableTreeNode(
				new SASNode(SASNodeType.INDICATION, item.getIndication(), IconSet.getUser(24)));
		DefaultMutableTreeNode studyNode = new DefaultMutableTreeNode(
				new SASNode(SASNodeType.STUDY, item.getStudy(), IconSet.getStudy(24)));
		DefaultMutableTreeNode reventNode = new DefaultMutableTreeNode(
				new SASNode(SASNodeType.R_EVENT, item.getREvent(), IconSet.getList(24)));

		compoundNode.add(indicationNode);
		indicationNode.add(studyNode);
		studyNode.add(reventNode);

		return compoundNode;
	}

	private void expandAll() {
		int i = 0;
		while (i != getTree().getRowCount()) {
			for (; i < getTree().getRowCount(); i++) {
				getTree().expandRow(i);
			}
		}
	}

	private synchronized void mergeTree(DefaultMutableTreeNode source, DefaultMutableTreeNode target) {
		Iterator<TreeNode> targetChildren = target.children().asIterator();
		while (targetChildren.hasNext()) {
			DefaultMutableTreeNode targetChild = (DefaultMutableTreeNode) targetChildren.next();
			if (targetChild.getUserObject().equals(source.getUserObject())) {
				Iterator<TreeNode> sourceChildren = source.children().asIterator();
				while (sourceChildren.hasNext()) {
					DefaultMutableTreeNode sourceChild = (DefaultMutableTreeNode) sourceChildren.next();
					mergeTree(sourceChild, targetChild);
				}
				return;
			}
		}
		target.add(source);
	}

	private synchronized void clearTree() {
		getMutubaleRoot().removeAllChildren();
	}

	private synchronized void refreshTree() {
		getMutableTreeModel().reload(getMutubaleRoot());
	}

}
