from spellchecker import SpellChecker
import sys
import spellchecker
from string import punctuation
from os import path

wordsToWrite = []
positionOfWords = []
reccomendedFix = []
s = []
punctuationObjects = []
debug = False
hasOther = False

newline = []

def main():
	global debug
	global wordsToWrite
	global reccomendedFix
	global positionOfWords
	global hasOther
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
	
	s = readFile(sys.argv[1])
	print("DEBUG: Returned s" + str(s))
	spellcheck(s)
	writeFile("")
	print("DEBUG: hasOther:" + str(hasOther))
	if(hasOther):
		s.clear()
		s = readFile(sys.argv[2])
		wordsToWrite.clear()
		positionOfWords.clear()
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
		c = 0;
		for char in s:
			if char == "\n":
				newline.append(c)
			c += 1
		return s.split()

def writeFile(file):
	global debug
	global wordsToWrite
	global reccomendedFix
	global positionOfWords
	global s	
	
	if debug: 
		print("DEBUG: WRITING FILE")
		print("DEBUG: WORDSTOWRITE:" + ", ".join(wordsToWrite))
	
	posFile = open(file + "wordPosition.txt", "w")
	CorrectionsFile = open(file + "correctionsFile.txt", "w")
	mispelledWordsFile = open(file + "misspelledWords.txt", "w")
	posFile.write("")
	CorrectionsFile.write("")
	mispelledWordsFile.write("")
	if not len(wordsToWrite) == 0:
		
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

def spellcheck(s):
	global wordsToWrite
	global reccomendedFix
	global positionOfWords
	global autoCorrected
	global debug
	global newline
	if debug: print("DEBUG:SPELLCHECKING FILE")
    
	spell = SpellChecker()
	c = 0
	newlineIndex = 0
	print("Newlines:" + str(newline))
	for word in s:
		if word != "----":
			print("Current C:" + str(c))
			wordLength = len(word)
			word = removePunctuation(list(word))
			
			if word not in spell:
				word = addPunctuation(list(word))
				needToFormat = False
				if(word[0].isupper()):
					needToFormat = True
				
				wordsToWrite.append(word)
				print("Added word pos at" + str(c))
				positionOfWords.append(c)
				
				SubFix = []
				for sbs in spell.candidates(word):
					if needToFormat:
						if(word.isupper()): SubFix.append(sbs.upper())
						else: SubFix.append(MakeUpper(list(sbs)))
					else:
						SubFix.append(sbs)
				
				reccomendedFix.append(SubFix)
			c += wordLength + 1
			if(c >= newline[newlineIndex]):
				c += 1
				newlineIndex += 1
				print("Added a newline at " + str(c))	

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
	s[0].upper();
	return "".join(s)

def removePunctuation(s):
	global debug
	global punctuationObjects
	punctuationObjects.clear()
	for i in range(0, len(s)):
		#if debug :print("Checking letter" + str(i+1) + "/" + str(len(s)) + " : " + s[i])
		if s[i] in punctuation:
			punctuationObjects.append(punctuationObject(s[i], i)) 
	
	for p in punctuationObjects:
		s.remove(p.punctuationLetter)
	s = "".join(s)
	if debug: print("DEBUG: Removed String:" + s)
	return s

def addPunctuation(s):
	global punctuationObjects
	for i in punctuationObjects:
		s.insert(i.position, i.punctuationLetter)
	s = "".join(s)
	print("Returned word:" + s)
	return s
	
class punctuationObject:
	def __init__(self, punctuationLetter, position):
		self.punctuationLetter = punctuationLetter
		self.position = position
print("running python")
main()

