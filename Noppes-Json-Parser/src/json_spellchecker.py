from spellchecker import SpellChecker
import sys
import spellchecker
from string import punctuation

wordsToWrite = []
positionOfWords = []
reccomendedFix = []
s = ""
punctuationObjects = []

debug = False

def main():
	global debug
	if(len(sys.argv) == 1):
		print("Usage: spellchecker.py <file> \n NOTE: this tool is not meant to be used by itself - try using json-parser.jar instead.")
		sys.exit(-1)
	if "-h" in sys.argv:
		print("Usage: spellchecker.py <file> [\n NOTE: this tool is not meant to be used by itself - try using json-parser.jar instead.")
		sys.exit(0)
	if "-d" in sys.argv:
		debug = True
		
	readFile()
	spellcheck()
	writeFile()
    
def readFile():
    print("DEBUG: READING FILE")
    global s
    global debug
    with open(sys.argv[1]) as f:
       s = f.read()
       if debug:
       	print("-------DEBUG-------\n" + s)
       s = s.split()

def writeFile():
	print("DEBUG: WRITING FILE")
	global wordsToWrite
	global reccomendedFix
	global positionOfWords
	global s	
	
	print("DEBUG: WORDSTOWRITE:" + " , ".join(wordsToWrite))
	
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
	print("DEBUG:SPELLCHECKING FILE")
	global wordsToWrite
	global reccomendedFix
	global positionOfWords
	global autoCorrected
	global s
    
	spell = SpellChecker()
	c = 0
	for word in s:
		if word != "\n" and word != "----":
			wordLength = len(word)
			word = removePunctuation(list(word))
			
			if word not in spell and len(word) != 0:
				word = addPunctuation(list(word))
				needToFormat = False
				if(word[0].isupper()):
					needToFormat = True
				
				wordsToWrite.append(word)
				positionOfWords.append(c)
				
				SubFix = []
				for sbs in spell.candidates(word):
					if needToFormat:
						SubFix.append(MakeUpper(list(sbs)))
					else:
						SubFix.append(sbs)
				
				reccomendedFix.append(SubFix)
			c += wordLength + 1

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
	punctuationObjects.clear()
	for i in range(0, len(s)):
		print("Checking letter" + str(i) + "/" + str(len(s)) + " : " + s[i])
		if s[i] in punctuation:
			print("Found letter:" + s[i])
			punctuationObjects.append(punctuationObject(s[i], i)) 
	
	for p in punctuationObjects:
		s.remove(p.punctuationLetter)
	s = "".join(s)
	print("DEBUG: Removed String:" + s)
	return s
def addPunctuation(s):
	for i in punctuationObjects:
		s.insert(i.position, i.punctuationLetter)
	s = "".join(s)
	return s
	
class punctuationObject:
	def __init__(self, punctuationLetter, position):
		self.punctuationLetter = punctuationLetter
		self.position = position
main()