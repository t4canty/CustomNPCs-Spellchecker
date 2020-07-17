package t4canty;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class Gui extends JPanel implements ActionListener{
	private boolean debug = true;
	private JFrame f;
	public Gui() {
		f = new JFrame();
		f.setTitle("CustomNPCs spellchecker");
		f.setSize(new Dimension(529, 559));
		setDoubleBuffered(false);
		
		JTextArea jText = new JTextArea(10, 10);
		jText.setEditable(true);
		jText.setLineWrap(true);
		jText.setWrapStyleWord(true);
		JScrollPane jScroll = new JScrollPane(jText);
		jScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScroll.setPreferredSize(new Dimension(500, 500));
		
		this.add(jScroll);
		
		f.add(this);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		Timer t = new Timer(13, this);
		t.start();
	}
	public static void main(String args[]) {
		new Gui();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(debug) System.out.println(f.getSize());
	}

}
