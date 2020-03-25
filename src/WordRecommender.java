import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class recommends alternate words for misspelled words.
 * @author sarvesh
 *
 */

public class WordRecommender {

	private String fileName;
	private ArrayList<String> dictionary;

	public ArrayList<String> getDictionary() {
		return dictionary;
	}

	public WordRecommender(String fileName) {
		this.fileName = fileName;
		loadDictionary();
	}

	/**
	 * Loads words from the dictionary file into an ArrayList
	 */

	private void loadDictionary() {
		dictionary = new ArrayList<String>();
		File f = new File(fileName);
		Scanner dict;
		try {
			dict = new Scanner(f);
			while(dict.hasNextLine()) {
				String word = dict.nextLine().trim();	
				dictionary.add(word);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/**
	 * Given two words, this function computes two measures of similarity (from left and right) and returns the average.
	 * @param word1
	 * @param word2
	 * @return
	 */

	public double getSimilarityMetric(String word1, String word2) {
		int leftSimilarity = 0;
		int rightSimilarity = 0;
		StringBuilder word1reverse = new StringBuilder(word1).reverse();
		StringBuilder word2reverse = new StringBuilder(word2).reverse();

		for (int i = 0; i < Math.min(word1.length(), word2.length()); i++) {
			if (word1.charAt(i) == word2.charAt(i)) {
				leftSimilarity++;
			} 		
			if (word1reverse.toString().charAt(i) == word2reverse.toString().charAt(i)) {
				rightSimilarity++;
			}
		}
		return (leftSimilarity+rightSimilarity)/2.0;	
	}

	/**
	 * Given an incorrect word, returns a list of topN (according to getSimilarityMetric) 
	 * legal word suggestions from the dictionary, in which each word is +/- ​n​ characters of the incorrect word
	 * and has at least commonPercent of the letters in common.
	 * @param word
	 * @param n
	 * @param commonPercent​ should be a double between 0.0, corresponding to 0%, and 1.0, corresponding to 100%.
	 * @param topN
	 * @return
	 */

	public ArrayList<String> getWordSuggestions(String word, int n, double commonPercent, int topN) {

		ArrayList<String> candidateWords = new ArrayList<String>();

		for (String dictWord : dictionary) {
			if ((dictWord.length() >= word.length() - n && dictWord.length() <= word.length() + n) 
					&& getCommonPercent(word, dictWord) >= commonPercent) {
				candidateWords.add(dictWord);
				//System.out.println(dictWord + " " + getCommonPercent(word, dictWord));
			}
		}


		ArrayList<String> candidateWordsCopy = new ArrayList<String>(candidateWords);
		ArrayList<String> wordSuggestions = new ArrayList<String>();

		for (int i = 0; i < topN; i++) {
			if (candidateWords.size() <= topN && i == candidateWords.size()) {
				break;
			}
			String wordWithMaxSimilarity = findWordWithMaxSimilarity(word, candidateWordsCopy);
			candidateWordsCopy.remove(wordWithMaxSimilarity);
			wordSuggestions.add(wordWithMaxSimilarity);
		}
		
		return wordSuggestions;
	}

	/**
	 * Returns the number of letters that are common across the two words
	 * divided by the total number of letters in word1 and word2 (with each letter counting exactly once).
	 * @param word1
	 * @param word2
	 * @return
	 */

	public double getCommonPercent(String word1, String word2) {
		ArrayList<Character> word1chars = new ArrayList<Character>(); 
		ArrayList<Character> word2chars = new ArrayList<Character>();
		double noOfCommonLetters = 0;
		int totalNonIdenticalLettersPerWord = 0;
		for (int i = 0; i < word1.length(); i++) {
			Character c = word1.charAt(i);
			if (!word1chars.contains(c)) {
				word1chars.add(c);
				totalNonIdenticalLettersPerWord++;
			}
		}
		for (int i = 0; i < word2.length(); i++) {
			Character c = word2.charAt(i);
			if (!word2chars.contains(c)) {
				word2chars.add(c);
				totalNonIdenticalLettersPerWord++;
			}
		}
		for (Character c : word1chars) {
			if (word2chars.contains(c)) {
				noOfCommonLetters++;
			}
		}
		return noOfCommonLetters/(totalNonIdenticalLettersPerWord-noOfCommonLetters);	
	}


	/**
	 * Returns the word in the candidateWords list that has the max similarity to the input word, calculated
	 * by the getSimilarityMetric method.
	 * @param word
	 * @param candidateWords
	 * @return
	 */

	public String findWordWithMaxSimilarity(String word, ArrayList<String> candidateWords) {
		int maxIndex = 0;
		double maxSimilarity = -1;
		for (int i = 0; i < candidateWords.size(); i++) {
			double similarity = getSimilarityMetric(word, candidateWords.get(i));
			if (similarity > maxSimilarity) {
				maxIndex = i;
				maxSimilarity = similarity;
			}
		}
		return candidateWords.get(maxIndex);
	}

	/**
	 * Given a word and a list of words from a dictionary, returns the list of words in the 
	 * dictionary that have at least (>=) n letters in common. For the purposes of this method, 
	 * we will only consider the distinct letters in the word. The position of the letters doesn’t matter.
	 * @param word
	 * @param listOfWords
	 * @param n
	 * @return
	 */

	public ArrayList<String> getWordsWithCommonLetters(String word, ArrayList<String> listOfWords, int n) {
		ArrayList<Character> wordChars = new ArrayList<Character>();
		ArrayList<String> returnList = new ArrayList<String>();

		for (int i = 0; i < word.length(); i++) {
			Character c = word.charAt(i);
			if (!wordChars.contains(c)) {
				wordChars.add(c);
			}
		}

		for (String w : listOfWords) {
			ArrayList<Character> wChars = new ArrayList<Character>();
			for (int i = 0; i < w.length(); i++) {
				Character c = w.charAt(i);
				if (!wChars.contains(c)) {
					wChars.add(c);
				}
			}
			int commonLetters = 0;
			for (Character c : wChars) {
				if (wordChars.contains(c)) {
					commonLetters++;
				}
			}
			if (commonLetters >= n) {
				returnList.add(w);
			}
		}
		return returnList;
	}

	/**
	 * This method takes an ArrayList and returns a String which when printed will 
	 * have the list elements with a number in front of them
	 * @param list
	 * @return
	 */

	public String prettyPrint(ArrayList<String> list) {
		String toPrint = "";
		for (int i = 0; i < list.size(); i++) {
			toPrint = toPrint + (i + 1) + ". " + list.get(i) + "\n";
		}
		return toPrint.trim();
	}

	public static void main(String[] args) {
		WordRecommender wr = new WordRecommender("engDictionary.txt");
		//System.out.print(wr.prettyPrint(new ArrayList<String> (Arrays.asList("biker", "tiger", "bigger")))); 
		//System.out.println(wr.getSimilarityMetric("frenchtoast", "roast"));
		//System.out.println("common percent: " + wr.getCommonPercent("sientificaly", "identifications"));
		//System.out.println("Max similarity word: " + wr.findWordWithMaxSimilarity("helloer", new ArrayList<String> (Arrays.asList("hello", "yellow", "fellow"))));
		System.out.println(wr.getWordSuggestions("zabra", 2, 0.75, 5));
		//System.out.println(wr.getWordsWithCommonLetters("cardiovascular", new ArrayList<String> (Arrays.asList("bang", "mange", "gang", "cling", "loo")), 4));
	}

}
