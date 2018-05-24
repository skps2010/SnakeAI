package skps2010;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Set extends JFrame {
	private static final long serialVersionUID = 1L;
	JComboBox<String> box;
	JCheckBox check;
	JTextField text;
	boolean close = false;

	public Set() {
		setTitle("設定");
		setSize(200, 150);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container pn = getContentPane();
		pn.setLayout(new FlowLayout());

		box = new JComboBox<String>();
		box.addItem("普通模式");
		box.addItem("AI訓練模式");
		box.setSelectedIndex(0);
		pn.add(box);

		text = new JTextField("input.txt");
		text.setEditable(false);

		check = new JCheckBox("使用預設AI        ");
		check.setSelected(false);
		check.addItemListener((ItemEvent e) -> {
			text.setEditable(e.getStateChange() == ItemEvent.SELECTED);
		});
		pn.add(check);

		pn.add(new JLabel("預設AI位置："));
		pn.add(text);

		JButton button = new JButton("   開始   ");
		button.addActionListener((ActionEvent e) -> {
			dispose();
			close = true;
		});
		pn.add(button);

		setVisible(true);
	}
}
