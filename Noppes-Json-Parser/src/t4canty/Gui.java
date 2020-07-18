package t4canty;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class Gui extends JPanel implements ActionListener{
	private boolean debug = true;
	private Point p1;
	private Point p2;
	private JFrame f;
	public Gui() {
		f = new JFrame();
		f.setTitle("CustomNPCs spellchecker");
		f.setSize(new Dimension(800, 559));
		
		this.setLayout(new BorderLayout());
		//setDoubleBuffered(false);

		JPanel buttonsAndStuffPanel = new JPanel();
		JButton nextButton = new JButton("Next");
		JButton autocorrectButton = new JButton("Autocorrect");
		JTextArea numFilesLeft = new JTextArea("Null");
		ButtonGroup autocorrectOptions = new ButtonGroup();
		JPanel autoCorrectButtonPanel = new JPanel();

		JScrollPane buttonScroller = new JScrollPane(autocorrectButton);
		buttonScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		
		JTextArea jText = new JTextArea(10, 10);
		jText.setEditable(false);
		jText.setLineWrap(true);
		jText.setWrapStyleWord(true);
		JScrollPane jScroll = new JScrollPane(jText);
		jScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScroll.setPreferredSize(new Dimension(500, 500));
		
		buttonsAndStuffPanel.setLayout(new BoxLayout(buttonsAndStuffPanel, BoxLayout.PAGE_AXIS));
		buttonsAndStuffPanel.add(nextButton, BorderLayout.WEST);
		buttonsAndStuffPanel.add(autocorrectButton, BorderLayout.EAST);
		buttonsAndStuffPanel.add(numFilesLeft, BorderLayout.PAGE_START);
		
		
		this.add(jScroll, BorderLayout.WEST);
		this.add(buttonsAndStuffPanel, BorderLayout.CENTER);

		f.add(this);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		Timer t = new Timer(13, (ActionListener) this);
		t.start();
	}
	public static void main(String args[]) {
		new Gui();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
}
