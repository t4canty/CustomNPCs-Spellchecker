from spellchecker import SpellChecker
import sys
import spellchecker

wordsToWrite = []
positionOfWords = []
reccomendedFix = []
s = ""

debug = False

def main():
	global debug
	if(len(sys.argv) == 1):
		print("Usage: spellchecker.py <file> [-r]\n -r: auto-corrects all matches and replaces file content with auto-corrections.\n NOTE: this tool is not meant to be used by itself - try using json-parser.jar instead.")
		sys.exit(-1)
	if "-h" in sys.argv:
		print("Usage: spellchecker.py <file> [-r]\n -r: auto-corrects all matches and replaces file content with auto-corrections.\n NOTE: this tool is not meant to be used by itself - try using json-parser.jar instead.")
		sys.exit(0)
	if "-d" in sys.argv:
		debug = True
		
	readFile()
	spellcheck()
	writeFile()
    
def readFile():
    global s
    global debug
    with open(sys.argv[1]) as f:
       s = f.read()
       if debug:
       	print("-------DEBUG-------\n" + s)
       s = s.split()
def writeFile():
	global wordsToWrite
	global reccomendedFix
	global positionOfWords
	global s	
	if not "-r" in sys.argv:
		with open(sys.argv[1] + "_revisions.txt", "w" ) as file:
			if not (len(wordsToWrite) == 0):
				file.write("\n--------Misspelled words for" + sys.argv[1] + "--------\n")
				i = 0
				for i in range(0, len(wordsToWrite)):
					file.write(wordsToWrite[i] + " at " +  str(positionOfWords[i]) +"\n\n")
					file.write("Reccomended fix:" + reccomendedFix[i] + "\n")
	else:
		with open("tmp.txt", "w") as f:
			f.write(" ".join(s))
			if(debug):
				print("-------DEBUG-------\n" + " ".join(s))
		
def spellcheck():
	global wordsToWrite
	global reccomendedFix
	global positionOfWords
	global s
    
	spell = SpellChecker()
	c = 0
	for word in s:
		if word not in spell and word != "\n" and word != "----":
			if not "-r" in sys.argv:
				wordsToWrite.append(word)
				positionOfWords.append(c)
				reccomendedFix.append(spell.correction(word))
			else:
				s.insert(c, spell.correction(word))
				s.remove(word)
		c += 1
main()