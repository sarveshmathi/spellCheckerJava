import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class SpellChecker {
	
	private ArrayList<String> dictionary;
	
	public SpellChecker(String dictionaryFileName) {
		loadDictionary(dictionaryFileName);
	}
	
/**
 * Loads words from the dictionary file into an ArrayList
 * @param fileName
 */
	public void loadDictionary(String fileName) {
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
	 * Returns true if the input word is found in the dictionary
	 * @param word
	 * @return
	 */
	
	boolean checkWord(String word) {
		if (dictionary.contains(word.toLowerCase()) || word.toLowerCase().equals("a") || word.toLowerCase().equals("i")) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		SpellChecker sc = new SpellChecker("engDictionary.txt");
		WordRecommender wr = new WordRecommender("engDictionary.txt");
		Scanner userInput = new Scanner(System.in);
		System.out.println("Please enter the name of the file to spell check: ");
		String fileName = userInput.nextLine().trim();
		File f = new File(fileName);
		try {
			Scanner text = new Scanner(f);
			StringBuilder output = new StringBuilder();
			while (text.hasNext()) {
				String word = text.next();
				if (sc.checkWord(word)) { 
					output.append(word + " "); //if word is in dictionary it is added to output without any changes
				} else {
					System.out.println("The word " + "\"" + word + "\"" + " is mispelled.");
					ArrayList<String> alternateSuggestions = wr.getWordSuggestions(word, 2, 0.8, 4);
					if (alternateSuggestions.isEmpty()) {
						System.out.println("There are 0 suggestions in out dictionary for this word."
								+ "\nPress ‘a’ for accept as is, ‘t’ for type in manually.");	
					} else {
						System.out.println(wr.prettyPrint(alternateSuggestions));
						System.out.println("Press ‘r’ for replace, ‘a’ for accept as is, ‘t’ for type in manually");
					}
					String userChoice = userInput.next().toLowerCase().trim();
					while(userChoice.equals("r") && alternateSuggestions.isEmpty()) {
						System.out.println("You have entered an invalid input, please try again: ");
						userChoice = userInput.next().trim();
					}
					while(!userChoice.equals("r") && !userChoice.equals("a") && !userChoice.equals("t")) {
						System.out.println("You have entered an invalid input, please try again: ");
						userChoice = userInput.next().trim();
					}
					if (userChoice.equals("r")) {
						System.out.println("Your word will now be replaced with one of the suggestions.\n" + 
								"Enter the number corresponding to the word that you want to use for replacement.");
						while (!userInput.hasNextInt()) {
							System.out.println("That is an invalid entry, please try again: ");
						 
						 } 
						int choice = userInput.nextInt(); 
		
						while ( choice > alternateSuggestions.size() || choice < 1) {
							System.out.println("That is an invalid choice, please try again:");
							choice = userInput.nextInt();
						}
						String replacement = alternateSuggestions.get(choice - 1);
						output.append(replacement + " ");
						System.out.println("\"" +  word + "\"" + " has been replaced with " + "\"" + replacement + "\".");
					} else if (userChoice.equals("a")) {
						output.append(word + " ");	
						System.out.println("\"" +  word + "\"" + " has not been corrected.");
					} else if (userChoice.equals("t")) {
						System.out.println("Please type the word that will be used as the replacement in the output file.");						
						String userReplacement = userInput.next().toLowerCase();
						output.append(userReplacement + " ");
						System.out.println("\"" +  word + "\"" + " has been replaced with " + "\"" + userReplacement + "\".");
					} 
				}
			}		
			String newFileName = fileName.substring(0, fileName.indexOf(".")) + "_chk." + fileName.substring(fileName.indexOf(".") + 1);
			FileWriter fw;
			try {
				fw = new FileWriter(newFileName, false);
				PrintWriter pw = new PrintWriter(fw);
				pw.println(output.toString());
				pw.flush();
				System.out.println("We're done! Your spell-checked file has been saved with the name " + "\"" + newFileName + "\".");
				pw.close();
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userInput.close();
			text.close();	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
