package vertical_spawn_control_client.minecraft;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.tree.TreeNode;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import foghrye4.swing.tree.JsonSerializableTreeNode;
import foghrye4.swing.tree.TreeNodeCollection;
import foghrye4.swing.tree.TreeNodeIntegerLeaf;
import foghrye4.swing.tree.TreeNodeStringLeaf;
import foghrye4.swing.tree.TreeNodeValueHolder;
import vertical_spawn_control_client.json.SerializedJsonType;
import vertical_spawn_control_client.ui.JTreeNodeTextField;
import vertical_spawn_control_client.ui.UIComponentsProvider;

public class Item implements JsonSerializableTreeNode, UIComponentsProvider, TreeNodeValueHolder {

	public final TreeNodeCollection<JsonSerializableTreeNode> parent;
	final Vector<TreeNode> childs = new Vector<TreeNode>();
	private String id = "minecraft:lava_bucket";
	TreeNodeIntegerLeaf count = new TreeNodeIntegerLeaf(this, "Count", 1);
	ItemNBT nbt = new ItemNBT(this);
	JTextField inputField = new JTreeNodeTextField(this);
	JButton removeButton = new JButton("Remove");

	public Item(TreeNodeCollection<JsonSerializableTreeNode> parentIn, JsonReader reader) throws IOException {
		this(parentIn);
		this.readFromJson(reader);
	}

	public Item(TreeNodeCollection<JsonSerializableTreeNode> parentIn) {
		parent = parentIn;
		inputField.setBorder(BorderFactory.createLineBorder(Color.black));
		inputField.setText(id);
		inputField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				id = inputField.getText();
				PresetParser.get().tree.updateUI();
			}
		});
		removeButton.addActionListener(a -> {
			parent.remove(Item.this);
			PresetParser.get().clearUI();
		});
		collectNodes();
	}
	
	private void collectNodes() {
		childs.addElement(count);
		childs.addElement(nbt);
	}

	@Override
	public void removeComponents(JLayeredPane panel) {
		panel.remove(inputField);
		panel.remove(removeButton);
		panel.repaint();
		panel.getParent().repaint();
	}

	@Override
	public void addComponents(JLayeredPane panel, Rectangle rectangle) {
		inputField.setBounds(rectangle);
		rectangle.setLocation(rectangle.x, rectangle.y + rectangle.height);
		removeButton.setBounds(rectangle);
		panel.add(inputField, JLayeredPane.POPUP_LAYER);
		panel.add(removeButton, JLayeredPane.POPUP_LAYER);
		panel.getParent().repaint();
	}

	@Override
	public void writeTo(JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("id");
		writer.value(id);
		count.writeTo(writer);
		nbt.writeTo(writer);
		writer.endObject();
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return childs.elementAt(childIndex);
	}

	@Override
	public int getChildCount() {
		return childs.size();
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		return childs.indexOf(node);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Enumeration<TreeNode> children() {
		return childs.elements();
	}

	@Override
	public String toString() {
		return "id:" + id;
	}

	public JsonSerializableTreeNode setId(String string) {
		id = string;
		inputField.setText(id);
		return this;
	}

	@Override
	public void readFromJson(JsonReader reader) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				id = reader.nextString();
			} else if (name.equals("Count")) {
				count.setValue(reader.nextInt());
			} else if (name.equals("tag")) {
				nbt.readFromJson(reader);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
	}

	@Override
	public SerializedJsonType getSerializedJsonType() {
		return SerializedJsonType.OBJECT;
	}

	@Override
	public boolean accept(String text) {
		String[] pair = text.split(":", 2);
		if (pair.length != 2 || pair[0].length() == 0 || pair[1].length() == 0)
			return false;
		id = text;
		return true;
	}
}
