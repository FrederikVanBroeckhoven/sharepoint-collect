package app.view.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.javatuples.Pair;

import app.control.SASManager;
import app.control.SharePointAccess;
import app.control.SharePointConnection.State;
import app.model.SASItem;
import app.view.panels.SASTreeNode.ItemTreeNode;
import app.view.panels.SASTreeNode.SASFileTreeNode;
import app.view.panels.SASTreeNode.SASNodeType;
import io.reactivex.rxjava3.core.Observable;

public class SASTreePanel extends JPanel implements TreeModelListener {

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

		SASManager.getInstance().onFileLoaded$()
				.withLatestFrom(
						SASManager.getInstance().onSASItemsLoaded$(),
						(file, loaded) -> Pair.with(file, loaded))
				.doOnNext(pair -> {
					if (pair.getValue1()) {
						getRoot().loadOK(pair.getValue0());
					} else {
						getRoot().loadError(pair.getValue0());
					}
					getTree().setRootVisible(true);
					refreshTree();
				})
				.subscribe();

		SASManager.getInstance().onItemsLoaded$()
				.doOnNext(sass -> {
					SharePointAccess.getInstance().disconnect();
					clearTree();
				})
				.flatMap(sass -> Observable.fromArray(sass))
				.map(sas -> createBranch(sas))
				.filter(branch -> branch != null)
				.doOnNext(branch -> mergeTree(branch, getRoot()))
				.subscribe();

		SASManager.getInstance().onItemsListed$()
				.map(listed -> Pair.with(
						listed.getValue0(),
						listed.getValue1().stream().filter(item -> item.hasAttachments()).collect(Collectors.toList())))
				.doOnNext(listed -> {
					traverseREventNodes(getRoot(), (node) -> {
						if (node.getSASItem().getListLocation().equals(listed.getValue0().getListLocation())) {
							listed.getValue1().forEach(item -> {
								getMutableTreeModel().insertNodeInto(new ItemTreeNode(item), node, node
										.getChildCount());
							});
						}
					});
				})
				.flatMap(listed -> Observable.fromIterable(listed.getValue1())
						.map(item -> Pair.with(listed.getValue0(), item)))
				.doOnNext(locItem -> SharePointAccess.getInstance().listAttachments(
						locItem.getValue0(),
						locItem.getValue1()))
				.subscribe();

		SASManager.getInstance().onAttachmentsListed$()
				.doOnNext(listed -> {
					System.out.println("Got attachments for " + listed.getValue1().getDisplay() + " @ " + listed
							.getValue0().getListLocation().name + ": ");
					listed.getValue2().forEach(att -> System.out.println(att.getPath() + '/' + att.getName()));
				})
				.subscribe();

		SharePointAccess.getInstance().onStateChanged$()
				.filter(conn -> conn.state == State.CONNECTED)
				.switchMap(conn -> {
					return traverseREventNodes$(getRoot())
							.distinct()
							.map(node -> node.getSASItem())
							.filter(sas -> sas.getListLocation().url.equals(conn.url));
				})
				.doOnNext(sas -> SharePointAccess.getInstance().list(sas))
				.subscribe();

	}

	public TreeModel getTreeModel() {
		return getMutableTreeModel();
	}

	public JTree getTree() {
		if (tree == null) {
			tree = new JTree(getTreeModel());
			tree.setRootVisible(false);
			tree.setCellRenderer(new SASTreeNode.SASTreeCellRenderer());
		}
		return tree;
	}

	public JScrollPane getTreeScroll() {
		if (treeScroll == null) {
			treeScroll = new JScrollPane(getTree());
		}
		return treeScroll;
	}

	private SASFileTreeNode getRoot() {
		return (SASFileTreeNode) getTreeModel().getRoot();
	}

	private DefaultTreeModel getMutableTreeModel() {
		if (treeModel == null) {
			treeModel = new DefaultTreeModel(new SASFileTreeNode());
			treeModel.addTreeModelListener(this);
		}
		return treeModel;
	}

	private DefaultMutableTreeNode createBranch(SASItem item) {

		SASTreeNode compoundNode = SASTreeNode.createCompound(item.getCompound());
		SASTreeNode indicationNode = SASTreeNode.createIndication(item.getIndication());
		SASTreeNode studyNode = SASTreeNode.createStudy(item.getStudy());
		SASTreeNode reventNode = new SASTreeNode.SASREventTreeNode(item);

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

	private Observable<SASTreeNode.SASREventTreeNode> traverseREventNodes$(SASTreeNode start) {
		return Observable.create(
				consumer -> {
					traverseREventNodes(start, node -> consumer.onNext(node));
				});
	}

	private void traverseREventNodes(SASTreeNode start, Consumer<SASTreeNode.SASREventTreeNode> visitor) {

		if (start.type == SASNodeType.R_EVENT) {
			visitor.accept((SASTreeNode.SASREventTreeNode) start);
			return;
		}
		Iterator<TreeNode> children = start.children().asIterator();
		while (children.hasNext()) {
			TreeNode child = children.next();
			traverseREventNodes((SASTreeNode) child, visitor);
		}
	}

	private synchronized void mergeTree(DefaultMutableTreeNode source, DefaultMutableTreeNode target) {
		Iterator<TreeNode> targetChildren = target.children().asIterator();
		while (targetChildren.hasNext()) {
			DefaultMutableTreeNode targetChild = (DefaultMutableTreeNode) targetChildren.next();
			if (targetChild.equals(source)) {
				Iterator<TreeNode> sourceChildren = source.children().asIterator();
				while (sourceChildren.hasNext()) {
					DefaultMutableTreeNode sourceChild = (DefaultMutableTreeNode) sourceChildren.next();
					mergeTree(sourceChild, targetChild);
				}
				return;
			}
		}
		getMutableTreeModel().insertNodeInto(source, target, target.getChildCount());
	}

	private synchronized void clearTree() {
		getRoot().removeAllChildren();
	}

	private synchronized void refreshTree() {
		getMutableTreeModel().reload(getRoot());
	}

	@Override
	public void treeNodesChanged(TreeModelEvent arg0) {
		expandAll();
	}

	@Override
	public void treeNodesInserted(TreeModelEvent arg0) {
		expandAll();
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent arg0) {
		expandAll();
	}

	@Override
	public void treeStructureChanged(TreeModelEvent arg0) {
		expandAll();
	}

}
