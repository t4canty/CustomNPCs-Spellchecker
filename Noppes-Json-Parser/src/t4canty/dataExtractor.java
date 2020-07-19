package t4canty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Noppes.Json;
import Noppes.Json.JsonException;

/**
 * Class meant to extract data from the CustomNPCs JSON files, 
 * and store them as strings. Also handles fileIO for these files and calls
 * python spellchecker.
 * 
 * @author t4canty
 *
 */
public class dataExtractor {
	//====Variables====//
	private Json j;
	private String dialougeText;
	private String questText;
	private String questComplete;
	private String path = Paths.get(".").toAbsolutePath().normalize().toString();
	private ArrayList<Json> dialougeOptions;
	private ArrayList<String> dialougeOptionTitles;
	private ArrayList<String> Corrections = new ArrayList<String>();
	private ArrayList<String> misspelledWords = new ArrayList<String>();
	private ArrayList<Integer> wordPositions = new ArrayList<Integer>();
	private static boolean isQuest = false;
	private boolean debug = false;

	//====Constructor====//
	/**
	 * Constructor for DataExtractor object. 
	 * @param f : Pointer to the JSON file. 
	 * @param isQuest : Set if the file to open is a quest file and not a dialouge file.
	 * @throws IOException : Throws IOException if file not found or permission denied.
	 * @throws JsonException : Throws JSONException if malformed JSON is detected. 
	 * @throws InterruptedException : Throws InterruptException if the python subprocess is interrupted.
	 */
	public dataExtractor(File f, boolean isQuest, boolean debug) throws IOException, JsonException, InterruptedException {
		//==Pre-Init==//
		j = new Json.JsonMap().Load(f);
		this.isQuest = isQuest;
		this.debug = debug;
		if(!isQuest) { 
			dialougeOptions = new ArrayList<Json>(); 
			dialougeOptionTitles = new ArrayList<String>();
		}

		//==Reading and writing keys==//
		readKeys(j, isQuest);

		if(debug) {
			System.out.println("DEBUG: Misspelled words");
			for(String s : misspelledWords) {
				System.out.println(s);
			}

			System.out.println("DEBUG : Word Positions");
			for(int i : wordPositions) {
				System.out.println(i);
			}
			System.out.println("DEBUG : Word corrections");
			for(String s2 : Corrections) {
				System.out.println(s2);
			}
		}

		//==Writing Keys==//
		//writeKeys();
	}

	//====Public Methods====//
	/**
	 * Default readKeys method, just reads keys from an existing JSON object.
	 * Not really recommended to use this method - try readKeys(File f) instead.
	 * @param j JSON object to read keys from.
	 * @param isQuest true if the JSON file is a quest file, and not a dialogue file. 
	 * @throws IOException throws IOexception from the reading and writing of temp keys. 
	 * See their definitions for more detail.
	 * @throws InterruptedException Throws InterruptedException when reading temp keys, caused by the
	 * python subprocess being interrupted. 
	 */
	public void readKeys(Json j, boolean isQuest) throws IOException, InterruptedException {
		//==Reading keys from provided JSON object.==//
		if(isQuest) {
			questText = j.get("Text").toString();
			questComplete = j.get("CompleteText").toString();
		}
		else {
			dialougeText = j.get("DialogText").toString();
			Json tmp = j.get("Options");
			for(Json e : tmp.getList()) {
				dialougeOptions.add(e.get("Option"));
			}
		}
		if(debug) System.out.println("Dtext:" + dialougeText);

		//==Now write temp files for the python process==//
		writeTempKeys();
		readTempkeys();
	}

	/**
	 * Reads a JSON object from a file, and extracts necessary keys. 
	 * @param f JSON file to read from. 
	 * @param isQuest true if the JSON file is a quest file, and not a dialogue file. 
	 * @throws IOException throws IOexception from the reading and writing of temp keys. 
	 * See their definitions for more detail.
	 * @throws InterruptedException Throws InterruptedException when reading temp keys, caused by the
	 * python subprocess being interrupted. 
	 * @throws JsonException 
	 */
	public void readKeys(File f, boolean isQuest) throws IOException, InterruptedException, JsonException {
		//==Reading keys from provided JSON object.==//
		Json j = new Json.JsonMap().Load(f);
		readKeys(j, isQuest);
	}

	/**
	 * Not done yet - don't use
	 * @throws IOException
	 */
	public void writeKeys() throws IOException {
		//TODO Finish method, and make more scalable.
		j.put("DialogText", "test");
		j.save(new File(path +"/83_mod.json"));
	}

	//====Private Methods==//
	private void writeTempKeys() throws IOException{
		//==Quest writing==//
		if(isQuest) {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(path +"/tmp.txt")), StandardCharsets.UTF_8);
			writer.write(questText + "\n");
			writer.write("----\n");
			writer.write(questComplete);
			writer.close();
		}

		//==Dialouge writing==//
		else {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(path +"/tmp.txt")), StandardCharsets.UTF_8);
			writer.write(dialougeText + "\n");
			writer.write("----\n");
			for(Json j : dialougeOptions) {
				writer.write(j.get("Title").toString() + "\n");
				writer.write("----\n");
			}
			writer.close();
		}
	}

	private void readTempkeys() throws IOException, InterruptedException {
		//==Build python subprocess==//
		ProcessBuilder pb = new ProcessBuilder("python3", path + "//src//json_spellchecker.py", path +"/tmp.txt", "-d");
		pb.redirectErrorStream(true);
		Process p = pb.start();

		//==Print out python output==//
		String line;
		if(debug) {
			BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while((line = bf.readLine()) != null) {
				System.out.println("DEBUG - Python output:" + line);
			}
		}

		//==Make sure python returns a non-error code==//
		assert p.waitFor() == 0 : "Error - python returned with error code.";

		//==Read python data and parse into java==//
		//Corrections file
		String pOutput = "";
		line = "";
		BufferedReader b2f = new BufferedReader(new FileReader(new File(path + "/correctionsFile.txt")));
		while((line = b2f.readLine()) != null) {
			pOutput += line + " ";
		}
		b2f.close();
		StringTokenizer st = new StringTokenizer(pOutput, "----");
		while(st.hasMoreTokens()) {
			Corrections.add(st.nextToken().trim());
		}

		//Words file
		pOutput = readIndividualKey(new File(path + "/misspelledWords.txt"));
		st = new StringTokenizer(pOutput, "----");
		while(st.hasMoreTokens()) {
			misspelledWords.add(st.nextToken());
		}

		//Positions file
		pOutput = readIndividualKey(new File(path + "/wordPosition.txt"));
		st = new StringTokenizer(pOutput, "----");
		while(st.hasMoreTokens()) {
			wordPositions.add(Integer.parseInt(st.nextToken()));
		}

	}

	private String readIndividualKey(File f) throws IOException {
		String pOutput = "";
		String line = "";
		BufferedReader b2f = new BufferedReader(new FileReader(f));
		while((line = b2f.readLine()) != null) {
			pOutput += line;
		}
		b2f.close();
		return pOutput;
	}

	//====Getters/Setters====//
	public ArrayList<String> getCorrections(){return Corrections;}
	public ArrayList<Integer> getWordPositions(){return wordPositions;}
	public ArrayList<String> getWords(){return misspelledWords;}
	public String getDialougeText() {return dialougeText;}
	public ArrayList<Json> getDialougeOptions() { return dialougeOptions;}
	public ArrayList<String> getDialougeOptionTitles() {return dialougeOptionTitles;}
	public String getQuestComplete() {return questComplete;}
	public String getQuestText() {return questText;}
	public void setDialougeOptions(ArrayList<Json> dialougeOptions) {this.dialougeOptions = dialougeOptions;}
	public void setDialougeText(String dialougeText) {this.dialougeText = dialougeText;}
	public void setDialougeOptionTitles(ArrayList<String> dialougeOptionTitles) {this.dialougeOptionTitles = dialougeOptionTitles;}
	public void setQuestComplete(String questComplete) {this.questComplete = questComplete;}
	public void setQuestText(String questText) {this.questText = questText;}
}
