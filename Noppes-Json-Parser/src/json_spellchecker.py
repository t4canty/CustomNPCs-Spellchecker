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
		print("Usage: spellchecker.py <file> \n NOTE: this tool is not meant to be used by itself - try using json-parser.jar instead.")
		sys.exit(-1)
	if "-h" in sys.argv:
		print("Usage: spellchecker.py <file> [-r]\n NOTE: this tool is not meant to be used by itself - try using json-parser.jar instead.")
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
	
	if not len(wordsToWrite) == 0:
		posFile = open("wordPosition.txt", "w")
		CorrectionsFile = open("correctionsFile.txt", "w")
		mispelledWordsFile = open("misspelledWords.txt", "w")
		i = 0
		for i in range(0, len(wordsToWrite)):
			mispelledWordsFile.write("----\n")
			posFile.write("----\n")
			CorrectionsFile.write("----\n")
			mispelledWordsFile.write(wordsToWrite[i] + "\n")
			posFile.write(str(positionOfWords[i]) + "\n")
			for word in reccomendedFix[i]:
				CorrectionsFile.write(word + "\n")
			i += 1
		posFile.close()
		CorrectionsFile.close()
		mispelledWordsFile.close()
def spellcheck():
	global wordsToWrite
	global reccomendedFix
	global positionOfWords
	global autoCorrected
	global s
    
	spell = SpellChecker()
	unknown  = spell.unknown(s)
	c = 0
	for word in unknown:
		if word != "\n" and word != "----":
			wordsToWrite.append(word)
			positionOfWords.append(c)
			reccomendedFix.append(spell.candidates(word))
		c += 1
main()