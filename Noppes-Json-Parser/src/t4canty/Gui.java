package t4canty;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import Noppes.Json.JsonException;

public class Gui extends JPanel implements ActionListener{
	private boolean debug = true;
	private JFrame f;
	public Gui(File file) throws IOException, JsonException, InterruptedException {
		f = new JFrame();
		f.setTitle("CustomNPCs spellchecker");
		f.setSize(new Dimension(800, 559));
		
		this.setLayout(new BorderLayout());
		
		dataExtractor dataE = new dataExtractor(file, false);
		//setDoubleBuffered(false);
		
		JTextField currentMisspelledWord = new JTextField( dataE.getWords().get(0));
		JPanel buttonsAndStuffPanel = new JPanel();
		JButton nextButton = new JButton("Next");
		JButton autocorrectButton = new JButton("Edit");
		JTextArea numFilesLeft = new JTextArea("Null");
		ButtonGroup autocorrectOptions = new ButtonGroup();
		JPanel autoCorrectButtonPanel = new JPanel();
		
		StringTokenizer st = new StringTokenizer(dataE.getCorrections().get(0), " ");
		autoCorrectButtonPanel.setLayout(new GridLayout(st.countTokens(), 1));
		while(st.hasMoreTokens()) {
			JRadioButton jb = new JRadioButton(st.nextToken());
			jb.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("you pressed da button - do stuff later");
					
				}
			});
			autocorrectOptions.add(jb);
			autoCorrectButtonPanel.add(jb);
		}
		
		JScrollPane buttonScroller = new JScrollPane(autocorrectButton);
		buttonScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		
		JTextArea jText = new JTextArea(10, 10);
		jText.setText("Dialouge Text: \n" + dataE.dailougeText + "\n Dialouge Options: \n");
		jText.setEditable(false);
		jText.setLineWrap(true);
		jText.setWrapStyleWord(true);
		JScrollPane jScroll = new JScrollPane(jText);
		jScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScroll.setPreferredSize(new Dimension(500, 500));
		
		JScrollPane buttonScroll = new JScrollPane(autoCorrectButtonPanel);
		buttonScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		buttonsAndStuffPanel.setLayout(new BorderLayout());
		
		JPanel nextButtons = new JPanel();
		nextButtons.setLayout(new GridLayout(2, 1));
		nextButtons.add(nextButton);
		nextButtons.add(autocorrectButton);
		
		buttonsAndStuffPanel.add(nextButtons, BorderLayout.PAGE_START);
		buttonsAndStuffPanel.add(buttonScroll, BorderLayout.CENTER);
		buttonsAndStuffPanel.add(numFilesLeft, BorderLayout.PAGE_END);
		
		
		
		this.add(currentMisspelledWord, BorderLayout.PAGE_START);
		this.add(jScroll, BorderLayout.WEST);
		this.add(buttonsAndStuffPanel, BorderLayout.CENTER);

		f.add(this);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		Timer t = new Timer(13, (ActionListener) this);
		t.start();
	}
	public static void main(String args[]) {
		try {
		new Gui(new File(args[0]));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
}
