package t4canty;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private ButtonGroup autocorrectOptions;
	private JButton autocorrectButton;
	private JButton nextButton;
	private JTextField currentMisspelledWord;
	private int currentIndexOfWord = 0 ;
	private dataExtractor dataE;
	private JPanel autoCorrectButtonPanel;
	private JTextArea jText;
	private JButton prevButton;
	private JButton saveButton;
	private JButton nextFile;
	private JButton prevFile;
	private JLabel numFilesLeft;
	private int currentFileNum = 1;
	private static int numFiles;
	
	public Gui(File file) throws IOException, JsonException, InterruptedException {
		f = new JFrame();
		f.setTitle("CustomNPCs spellchecker");
		f.setSize(new Dimension(800, 559));
		
		this.setLayout(new BorderLayout());
		
		dataE = new dataExtractor(file, false);
		setDoubleBuffered(false);
		
		currentMisspelledWord = new JTextField( dataE.getWords().get(currentIndexOfWord));
		JPanel buttonsAndStuffPanel = new JPanel();
		nextButton = new JButton("next");
		autocorrectButton = new JButton("edit");
		prevButton = new JButton("previous");
		saveButton = new JButton("save");
		numFilesLeft = new JLabel("Null");
		nextFile = new JButton("Next File");;
		prevFile = new JButton("Previous File");
		
		autoCorrectButtonPanel = new JPanel();
		addButtons(currentIndexOfWord);
		
		autocorrectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {jText.setEditable(!jText.isEditable());}
		});
		
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(currentIndexOfWord < dataE.getWords().size() -1) currentIndexOfWord++;
				else currentIndexOfWord = 0;
				currentMisspelledWord.setText(dataE.getWords().get(currentIndexOfWord));
				addButtons(currentIndexOfWord);
			}
		});
		
		JScrollPane buttonScroller = new JScrollPane(autocorrectButton);
		buttonScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		
		
		jText = new JTextArea(10, 10);
		jText.setText(dataE.dailougeText);
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
		nextButtons.setLayout(new GridLayout(3, 2));
		nextButtons.add(nextButton);
		nextButtons.add(autocorrectButton);
		nextButtons.add(prevButton);
		nextButtons.add(saveButton);
		nextButtons.add(nextFile);
		nextButtons.add(prevFile);
		
		buttonsAndStuffPanel.add(nextButtons, BorderLayout.PAGE_START);
		buttonsAndStuffPanel.add(buttonScroll, BorderLayout.CENTER);
		buttonsAndStuffPanel.add(numFilesLeft, BorderLayout.PAGE_END);
		
		
		
		this.add(currentMisspelledWord, BorderLayout.PAGE_START);
		this.add(jScroll, BorderLayout.WEST);
		this.add(buttonsAndStuffPanel, BorderLayout.CENTER);

		f.add(this);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		Timer t = new Timer(34, (ActionListener) this);
		t.start();
	}
	public static void main(String args[]) {
		numFiles = args.length;
		
		try {
		Gui g = new Gui(new File(args[0]));
		g.numFilesLeft.setText(g.currentFileNum + "/" + numFiles);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(currentFileNum == 1) prevFile.setEnabled(false);
		if(currentFileNum == numFiles) nextFile.setEnabled(false);
	}
	
	private void addButtons(int index) {
		for(Component c : autoCorrectButtonPanel.getComponents()) {
			autoCorrectButtonPanel.remove(c);
		}
		autocorrectOptions = new ButtonGroup();
		StringTokenizer st = new StringTokenizer(dataE.getCorrections().get(index), " ");
		autoCorrectButtonPanel.setLayout(new GridLayout(st.countTokens(), 1));
		while(st.hasMoreTokens()) {
			JRadioButton jb = new JRadioButton(st.nextToken());
			final String jbString = jb.getText();
			jb.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {					
					jText.setText(dataE.dailougeText);
					currentMisspelledWord.setText(jbString);
					String replacedText = jText.getText().substring(0, 
							dataE.getWordPositions().get(currentIndexOfWord)) + jbString 
							+ jText.getText().substring(
									dataE.getWordPositions().get(currentIndexOfWord) + dataE.getWords().get(currentIndexOfWord).length());
					jText.setText(replacedText);
				}
			});
			autocorrectOptions.add(jb);
			autoCorrectButtonPanel.add(jb);
			autoCorrectButtonPanel.revalidate();
		}
	}
}
