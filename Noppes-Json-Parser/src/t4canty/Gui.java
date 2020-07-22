package t4canty;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

/**
 * Class to handle all Gui aspects of the program. 
 * @author t4canty
 *
 */
public class Gui extends JPanel implements ActionListener{
	//====Varibles==//
	private static boolean debug = true;
	private static boolean isQuest = false;
	private int currentIndexOfWord = 0 ;
	private int currentFileNum = 1;
	private int optionsIndex = 0;
	private boolean isOptions = false;
	private ButtonGroup autocorrectOptions;
	private JButton autocorrectButton;
	private JButton nextButton;
	private JButton viewOptions;
	private JButton viewCompletedText;
	private JButton prevButton;
	private JButton saveButton;
	private JButton nextFile;
	private JButton prevFile;
	private JButton reset;
	private JFrame f;
	private JTextField currentMisspelledWord;
	private JPanel autoCorrectButtonPanel;
	private JPanel optionsPanel;
	private JTextArea jText;
	private JLabel numFilesLeft;
	private JScrollPane jScroll;
	private static ArrayList<File> fileList;
	private dataExtractor dataE;
	/**
	 * Constructor for the gui. 
	 * @param file - the first JSON file to read from when constructing the object. 
	 * @throws IOException Throws IOException when the file cannot be acessed.
	 * @throws JsonException Throws JSON Exception when the JSON is malformed.
	 * @throws InterruptedException Throws InterruptedException when the python subprocess is interrupted.
	 */
	public Gui(File file) throws IOException, JsonException, InterruptedException {
		//==Pre-init==//
		f = new JFrame();
		f.setTitle("CustomNPCs spellchecker");
		f.setSize(new Dimension(800, 559));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setLayout(new BorderLayout());

		dataE = new dataExtractor(file, isQuest, debug);
		//setDoubleBuffered(false);

		//==Setting up components==//
		JPanel buttonsAndStuffPanel = new JPanel();
		JPanel nextButtons = new JPanel();
		optionsPanel = new JPanel();

		jText = new JTextArea(10, 10);
		jText.setText(dataE.getDialougeText());
		jText.setEditable(false);
		jText.setLineWrap(true);
		jText.setWrapStyleWord(true);

		currentMisspelledWord = new JTextField( dataE.getWords().get(currentIndexOfWord));
		numFilesLeft = new JLabel();

		viewOptions = new JButton("Options");
		viewCompletedText = new JButton("Completed Text");
		nextButton = new JButton("next");
		autocorrectButton = new JButton("edit");
		prevButton = new JButton("previous");
		saveButton = new JButton("save");
		nextFile = new JButton("Next File");;
		prevFile = new JButton("Previous File");
		reset = new JButton("Reset");

		autoCorrectButtonPanel = new JPanel();

		jScroll = new JScrollPane(jText);
		JScrollPane buttonScroll = new JScrollPane(autoCorrectButtonPanel);

		addOptions(optionsPanel);
		addButtons(currentIndexOfWord, false); //adds correction suggestions on the side menu.

		//==Setting up ActionListeners==//
		autocorrectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {jText.setEditable(!jText.isEditable());}
		});

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isOptions) {
					if(optionsIndex < dataE.getOptionMisspelledWords().size() -1) optionsIndex++;
					else optionsIndex = 0;
					currentMisspelledWord.setText(dataE.getDialougeOptionTitles().get(optionsIndex));
					addButtons(optionsIndex, true);
				}
				else{
					if(currentIndexOfWord < dataE.getWords().size() -1) currentIndexOfWord++;
					else currentIndexOfWord = 0;
					currentMisspelledWord.setText(dataE.getWords().get(currentIndexOfWord));
					addButtons(currentIndexOfWord, false);
				}
			}
		});

		prevButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(currentIndexOfWord > 0) currentIndexOfWord--;
				else currentIndexOfWord = dataE.getWords().size() -1;
				currentMisspelledWord.setText(dataE.getWords().get(currentIndexOfWord));
				addButtons(currentIndexOfWord, false);
			}
		});

		viewOptions.addActionListener(this);
		viewOptions.setActionCommand("options");

		if(isQuest) { 
			viewOptions.setEnabled(false);
			if(dataE.getOptionMisspelledWords().size() == 0) {
				viewCompletedText.setEnabled(false);
			}
		}
		else{
			viewCompletedText.setEnabled(false);
			if(dataE.getOptionMisspelledWords().size() == 0) {
				viewOptions.setEnabled(false);
			}
		}
		
		//==Setting up component options==//
		jScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScroll.setPreferredSize(new Dimension(500, 500));
		buttonScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);



		nextButtons.setLayout(new GridLayout(5, 2));
		nextButtons.add(nextButton);
		nextButtons.add(autocorrectButton);
		nextButtons.add(prevButton);
		nextButtons.add(saveButton);
		nextButtons.add(nextFile);
		nextButtons.add(prevFile);
		nextButtons.add(viewOptions);
		nextButtons.add(viewCompletedText);
		nextButtons.add(reset);


		buttonsAndStuffPanel.setLayout(new BorderLayout());
		buttonsAndStuffPanel.add(nextButtons, BorderLayout.PAGE_START);
		buttonsAndStuffPanel.add(buttonScroll, BorderLayout.CENTER);
		buttonsAndStuffPanel.add(numFilesLeft, BorderLayout.PAGE_END);

		this.add(currentMisspelledWord, BorderLayout.PAGE_START);
		this.add(jScroll, BorderLayout.CENTER);
		this.add(buttonsAndStuffPanel, BorderLayout.EAST);

		f.add(this);
		f.pack();
		f.setVisible(true);

		//==Setting up the ticking timer==//
		Timer t = new Timer(34, (ActionListener) this);
		t.start();
	}

	public static void main(String args[]) {
		if(args.length == 0) {
			System.err.println("Error: Not enough Arguments");
			System.out.println("Usage: noppes-spellcheck.jar <json file1> <json file2> ... <json fileN> [-q] [-h]\n -q: set if the file is a quest file\n -h prints this help\nProtip - this tool works best with bash scripting.");
			System.exit(-1);
		}

		for(String s : args) {
			if (s.equalsIgnoreCase("-h")) {
				System.out.println("Usage: noppes-spellcheck.jar <json file> [-q] [-h]\n -q: set if the file is a quest file\n -h prints this help\nProtip - this tool works best with bash scripting.");
				System.exit(0);
			}
			else if(s.equalsIgnoreCase("-q")) {
				isQuest = true;
			}else if(s.equalsIgnoreCase("-d")){
				debug = true;
			}

			fileList = new ArrayList<File>();

			for(String f : args) {
				if(debug) System.out.println("Current file:" + f);
				if(!f.equals("-q") && !f.equals("-d")) {
					File newFile = new File(f);
					if(!newFile.exists() || !newFile.canWrite() || !newFile.canRead()) { 
						if(!newFile.exists()) System.err.println("ERROR: JSON file does not exist.");
						else System.err.println("ERROR: JSON file not read/writeable, exiting.");
						System.exit(-1);
					}
					else {fileList.add(newFile);}
				}
			}

		}

		try {
			Gui g = new Gui(new File(args[0]));
			g.numFilesLeft.setText(g.currentFileNum + "/" + fileList.size());
			System.out.println(g.dataE.getOptionMisspelledWords().size());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	//====Public Methods====//
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(currentFileNum == 1) prevFile.setEnabled(false);
		if(currentFileNum == fileList.size()) nextFile.setEnabled(false);

		if(arg0.getActionCommand() != null) {
			if(arg0.getActionCommand().equals("options")) {
				isOptions = !isOptions;
				if(isOptions) {
					this.remove(jScroll);
					this.add(optionsPanel, BorderLayout.CENTER);
					addButtons(0, true);
				}else {
					this.remove(optionsPanel);
					this.add(jScroll, BorderLayout.CENTER);
					addButtons(0, false);
				}
				this.revalidate();
				repaint();
			}
		}
	}

	//====Private Methods====//
	private void addButtons(int index, boolean isOption) {
		//==Remove previous buttons from the panel==//
		for(Component c : autoCorrectButtonPanel.getComponents()) {
			autoCorrectButtonPanel.remove(c);
		}

		//==Setup==//
		autocorrectOptions = new ButtonGroup(); //Clear the button group
		StringTokenizer st;
		if(isOption) st = new StringTokenizer(dataE.getOptionCorrections().get(index), " ");
		else st = new StringTokenizer(dataE.getCorrections().get(index), " "); //Create a string tokenizer to run through the corrections
		autoCorrectButtonPanel.setLayout(new GridLayout(st.countTokens(), 1)); // set up the layout for the panel. 

		//==Adding buttons==//
		while(st.hasMoreTokens()) {
			JRadioButton jb = new JRadioButton(st.nextToken());
			String jbString = jb.getText();
			jb.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {					
					if(isOptions) {
						JTextField jtext = (JTextField) optionsPanel.getComponent(optionsIndex);
						jtext.setText(jbString);
						currentMisspelledWord.setText(jbString);
					}else {
						jText.setText(dataE.getDialougeText());
						currentMisspelledWord.setText(jbString);
						jText.setText(jText.getText().replace(dataE.getWords().get(currentIndexOfWord), jbString));
					}
				}
			});
			autocorrectOptions.add(jb);
			autoCorrectButtonPanel.add(jb);
			autoCorrectButtonPanel.revalidate();
		}
	}
	private void addOptions(JPanel j) {
		j.setLayout(new GridLayout(dataE.getDialougeOptionTitles().size(), 1));
		for(String s : dataE.getDialougeOptionTitles()) {
			JTextField jTemp = new JTextField(s);
			jTemp.setEditable(true);
			j.add(jTemp);
		}
	}
}
