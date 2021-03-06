from spellchecker import SpellChecker
import sys
import spellchecker
from string import punctuation
from os import path

wordsToWrite = []
reccomendedFix = []
s = []
nums = []
debug = False
hasOther = False


def main():
	global debug
	global wordsToWrite
	global reccomendedFix
	global hasOther
	global nums
	if len(sys.argv) == 1:
		print("Usage: spellchecker.py <file> <optionsFile>\n NOTE: this tool is not meant to be used by itself - try using json-parser.jar instead.")
		sys.exit(-1)
	if "-h" in sys.argv:
		print("Usage: spellchecker.py <file> <optionsFile>\n NOTE: this tool is not meant to be used by itself - try using json-parser.jar instead.")
		sys.exit(0)
	if "-d" in sys.argv:
		debug = True
		sys.argv.remove("-d")
	if len(sys.argv) == 3:
		hasOther = True;

	if not path.exists(sys.argv[1]):
		print("ERROR: System cannot find the file specified.")
		sys.exit(-1)
	if hasOther:
		if not path.exists(sys.argv[2]) and hasOther:
			print("ERROR: System cannot find the options file.")
			sys.exit(-1)
	
	for n in range(0,9):
		nums.append(str(n))
	
	s = readFile(sys.argv[1])
	print("DEBUG: Returned s" + str(s))
	spellcheck(s)
	writeFile("")
	print("DEBUG: hasOther:" + str(hasOther))
	if(hasOther):
		s.clear()
		s = readFile(sys.argv[2])
		wordsToWrite.clear()
		reccomendedFix.clear()
		spellcheck(s)
		writeFile("other_")
    
def readFile(file):
	global debug
	global newline
	if debug: print("DEBUG: READING FILE")
	with open(file) as f:
		s = f.read()
		if debug: print("-------DEBUG-------\n" + s)
		return s.split()

def writeFile(file):
	global debug
	global wordsToWrite
	global reccomendedFix
	global s	

	if debug: 
		print("DEBUG: WRITING FILE")
		print("DEBUG: WORDSTOWRITE:" + ", ".join(wordsToWrite))
	CorrectionsFile = open(file + "correctionsFile.txt", "w")
	mispelledWordsFile = open(file + "misspelledWords.txt", "w")
	CorrectionsFile.write("")
	mispelledWordsFile.write("")
	if not len(wordsToWrite) == 0:
		i = 0
		for i in range(0, len(wordsToWrite)):
			mispelledWordsFile.write("----\n")
			CorrectionsFile.write("----\n")
			mispelledWordsFile.write(wordsToWrite[i] + "\n")
			print("DEBUG: RECCOMENDED FIX:" + ", ".join(reccomendedFix[i]))
			for word in reccomendedFix[i]:
				CorrectionsFile.write(word + "\n")
			i += 1
	CorrectionsFile.close()
	mispelledWordsFile.close()

def spellcheck(s):
	global wordsToWrite
	global reccomendedFix
	global debug
	global newline
	global nums
	if debug: 
		print("DEBUG:SPELLCHECKING FILE")
	
	spell = SpellChecker()
	for word in s:
		if word != "----":
			word = removePunctuation(list(word))
			isNotNum = True
			for c in word:
				if c in nums:
					isNotNum = False
					break
			if isNotNum and len(word) != 0:
				if word not in spell:
					needToFormat = False
					if(word[0].isupper()):
						needToFormat = True
					print("Word " + word + " not in spell.")
					
					wordsToWrite.append(word)
					
					SubFix = []
					for sbs in spell.candidates(word):
						if needToFormat:
							if(word.isupper()): SubFix.append(sbs.upper())
							else: SubFix.append(MakeUpper(list(sbs)))
						else:
							SubFix.append(sbs)
					
					reccomendedFix.append(SubFix)

def getStringFormat(s):
	cases = []
	for substring in s:
		if substring.islower():
			cases.append(0)
		else:
			cases.append(1)
	return cases

def formatString(s, cases):
	modS = ""
	i = 0
	for i in range(0, len(cases)):
		if cases[i] == 0:
			modS += s[i].lower();
		else:
			modS +=  s[i].upper();
		i += 1
	return modS
	
def MakeUpper(s):
	s[0] = s[0].upper();
	return "".join(s)

def removePunctuation(s):
	global debug
	remove = []
	for c in s:
		if(c in punctuation or c == " ") and c != "'":
			remove.append(c)
	for rs in remove:
		s.remove(rs)
	s = "".join(s)
	return s
	
print("running python")
main()

