package app.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import app.control.SASLoader;
import app.model.SASItem;
import io.reactivex.rxjava3.core.Observable;

public class SASTreePanel extends JPanel {

	private static final long serialVersionUID = -8579554707491198187L;

	private JTree tree;
	private JScrollPane treeScroll;

	private DefaultTreeModel treeModel;

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
				.doOnNext(file -> {
					getMutubaleRoot().setUserObject(file.getName());
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
		DefaultMutableTreeNode compoundNode = new DefaultMutableTreeNode(item.getCompound());
		DefaultMutableTreeNode indicationNode = new DefaultMutableTreeNode(item.getIndication());
		DefaultMutableTreeNode studyNode = new DefaultMutableTreeNode(item.getStudy());
		DefaultMutableTreeNode reventNode = new DefaultMutableTreeNode(item.getREvent());

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
