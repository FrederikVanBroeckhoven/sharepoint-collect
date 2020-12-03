package app.view.panels;

import java.awt.Component;
import java.io.File;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import app.model.SASItem;
import app.view.extras.IconSet;
import dashboard.connect.Item;

public class SASTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -2259620466325229549L;

	public enum SASNodeType {
		SAS_FILE,
		COMPOUND,
		INDICATION,
		STUDY,
		R_EVENT,
		SAS_ITEM
	}

	public static class SASTreeCellRenderer extends DefaultTreeCellRenderer {

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
			if (!(dmObj instanceof SASTreeNode)) {
				return c;
			}

			SASTreeNode node = (SASTreeNode) dmObj;

			if (c instanceof JLabel) {

				JLabel label = (JLabel) c;

				if (node != null && node.icon != null) {
					label.setIcon(node.icon);
				}

				if (node.type == SASNodeType.SAS_ITEM) {
					SASTreeNodePanel panel = new SASTreeNodePanel(label, new String[] { "a", "b", "a", "c" });
					return panel;
				}

				return label;

			}
			
			return c;
		}

	}

	public static class SASFileTreeNode extends SASTreeNode {

		private static final long serialVersionUID = 1909582734338170810L;

		public SASFileTreeNode() {
			super(SASNodeType.SAS_FILE, "", null);
		}

		public void loadOK(File file) {
			value = file.getName();
			icon = IconSet.getFileOk(24);
		}

		public void loadError(File file) {
			value = file.getName();
			icon = IconSet.getFileError(24);
		}

	}

	public static class SASREventTreeNode extends SASTreeNode {

		private static final long serialVersionUID = 879867574877525376L;

		public SASREventTreeNode(SASItem sas) {
			super(SASNodeType.R_EVENT, sas.getREvent(), IconSet.getList(24));
			setUserObject(sas);
		}

		public SASItem getSASItem() {
			return (SASItem) getUserObject();
		}

	}

	public static class ItemTreeNode extends SASTreeNode {

		private static final long serialVersionUID = 879867574877525376L;

		public ItemTreeNode(Item item) {
			super(SASNodeType.SAS_ITEM, item.getDisplay(), IconSet.getPin(24));
			setUserObject(item);
		}

		public Item getItem() {
			return (Item) getUserObject();
		}

	}

	public static SASTreeNode createCompound(String value) {
		return new SASTreeNode(SASNodeType.COMPOUND, value, IconSet.getCompound(24));
	}

	public static SASTreeNode createIndication(String value) {
		return new SASTreeNode(SASNodeType.INDICATION, value, IconSet.getVirus(24));
	}

	public static SASTreeNode createStudy(String value) {
		return new SASTreeNode(SASNodeType.STUDY, value, IconSet.getStudy(24));
	}

	public SASTreeNode.SASNodeType type;
	public String value;
	public Icon icon;

	private SASTreeNode(SASTreeNode.SASNodeType type, String value, Icon icon) {
		this.type = type;
		this.value = value;
		this.icon = icon;
	}

	public boolean equals(Object obj) {
		return equals((SASTreeNode) obj);
	}

	public boolean equals(SASTreeNode sas) {
		return sas.type == type && sas.value.equals(value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	@Override
	public String toString() {
		return value;
	}

}