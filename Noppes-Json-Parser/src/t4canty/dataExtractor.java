package t4canty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Noppes.Json;
import Noppes.Json.JsonException;

public class dataExtractor {
	private Json j;
	private String dailougeText;
	private ArrayList<Json> dialougeOptions;
	private ArrayList<String> dialougeOptionTitles;
	private String questText;
	private String questComplete;
	private static boolean isQuest = false;
	public dataExtractor(File f, boolean isQuest) throws IOException, JsonException, InterruptedException {
		j = new Json.JsonMap().Load(f);
		this.isQuest = isQuest;
		if(!isQuest) { 
			dialougeOptions = new ArrayList<Json>(); 
			dialougeOptionTitles = new ArrayList<String>();
		}
		readKeys();
		writeTempKeys();
		readTempkeys();
		
		if(isQuest) {
			System.out.println("Returned Quest Text:" + questText);
			System.out.println("Returned Complete Text:" + questComplete);
		}else {
			System.out.println("Returned Dialouge Text:" + dailougeText);
			System.out.println("Returned Dialouge Titles:");
			for(String s : dialougeOptionTitles) {
				System.out.println(s);
			}
		}
		//writeKeys();
	}

	public void readKeys() {
		if(isQuest) {
			questText = j.get("Text").toString();
			questComplete = j.get("CompleteText").toString();
		}
		else {
			dailougeText = j.get("DialogText").toString();
			Json tmp = j.get("Options");
			for(Json e : tmp.getList()) {
				dialougeOptions.add(e.get("Option"));
			}
		}
		System.out.println("Dtext:" + dailougeText);
	}
	public void writeKeys() throws IOException {
		j.put("DialogText", "test");
		j.save(new File("/home/tom/git/json-checker/83_mod.json"));
	}
	private void writeTempKeys() throws IOException{
		if(isQuest) {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File("/home/tom/git/json-checker/tmp.txt")), StandardCharsets.UTF_8);
			writer.write(questText + "\n");
			writer.write("----\n");
			writer.write(questComplete);
			writer.close();
		}
		else {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File("/home/tom/git/json-checker/tmp.txt")), StandardCharsets.UTF_8);
			writer.write(dailougeText + "\n");
			writer.write("----\n");
			for(Json j : dialougeOptions) {
				writer.write(j.get("Title").toString() + "\n");
				writer.write("----\n");
			}
			writer.close();
		}
	}
	private void readTempkeys() throws IOException, InterruptedException {
		String path = Paths.get(".").toAbsolutePath().normalize().toString();
		ProcessBuilder pb = new ProcessBuilder("python3", path + "//src//json_spellchecker.py", "/home/tom/git/json-checker/tmp.txt", "-d");
		pb.redirectErrorStream(true);

		Process p = pb.start();

		BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while((line = bf.readLine()) != null) {
			System.out.println("DEBUG - Python output:" + line);
		}

		assert p.waitFor() == 0 : "Error - python returned with error code.";

		String pOutput = "";
		BufferedReader b2f = new BufferedReader(new FileReader(new File("/home/tom/git/json-checker/tmp.txt")));
		while((line = b2f.readLine()) != null) {
			pOutput += line;
		}

		StringTokenizer st = new StringTokenizer(pOutput, "----");

		if(isQuest) {
			questText = st.nextToken();
			questComplete = st.nextToken();
		}else {
			dailougeText = st.nextToken();
			while(st.hasMoreTokens()) {
				dialougeOptionTitles.add(st.nextToken());
			}
		}
	}
//	public static void main(String args[]) {
//		if(args.length == 0) {
//			System.err.println("Error: Not enough Arguments");
//			System.out.println("Usage: noppes-spellcheck.jar <json file> [-q] [-h]\n -q: set if the file is a quest file\n -h prints this help\nProtip - this tool works best with bash scripting.");
//			System.exit(-1);
//		}
//		for(String s : args) {
//			if (s.equalsIgnoreCase("-h")) {
//				System.out.println("Usage: noppes-spellcheck.jar <json file> [-q] [-h]\n -q: set if the file is a quest file\n -h prints this help\nProtip - this tool works best with bash scripting.");
//				System.exit(0);
//			}
//			else if(s.equalsIgnoreCase("-q")) {
//				isQuest = true;
//			}
//		}
//		try {
//			new dataExtractor(new File(args[0]), isQuest);
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
