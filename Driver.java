import java.io.FileNotFoundException;

/* a simple test class */

public class Driver {

	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("Swapped letters: " + Dictionary.swapLetters("hamster"));
		System.out.println("Swapped letters 2: " + Dictionary.swapLetters2("hamster"));
		System.out.println("Swapped letters 3: " + Dictionary.swapLetters3("hamster"));
		System.out.println("Missing leters: " + Dictionary.missingLetter("hamster"));
		System.out.println("Missing leters 2: " + Dictionary.missingLetter2("hamster"));
		System.out.println("Typos: " + Dictionary.typo("hamster"));
		System.out.println("Replace: " + Dictionary.replace("hamster"));
		System.out.println("Extra letter: " + Dictionary.extraLetter("hamster"));
		System.out.println("Extra letter 2: " + Dictionary.extraLetter2("hamster"));	
		System.out.println("\n");
		
		Dictionary dict = new Dictionary("words_ospd.txt");
//		Dictionary dict = new Dictionary();

		System.out.println("Check morneau: " + dict.check("morneau"));
		dict.add("morneau");
		System.out.println("Add morneau");
		System.out.println("Check morneau: " + dict.check("morneau"));
		dict.remove("morneau");
		System.out.println("Remove morneau");
		System.out.println("Check morneau: " + dict.check("morneau"));
		
		System.out.println("Delete morneau: " + dict.delete("morneau"));
		System.out.println("\n");
		
		String[] suggs = dict.suggest("caft", 20);

		int i = 0;
		for (String s : suggs) {
			System.out.println((i++) + ": " + s);
		}
		
		System.out.println("\n");

		System.out.println("cafe prefix in dict?: " + dict.checkPrefix("cafe"));
		System.out.println("cafe in dict?: " + dict.check("cafe"));
		System.out.println("cafes in dict?: " + dict.check("cafes"));
		
		System.out.println("Delete cafe: " + dict.delete("cafe"));
		dict.print();
		
		System.out.println("cafe prefix in dict?: " + dict.checkPrefix("cafe"));
		System.out.println("cafe in dict?: " + dict.check("cafe"));
		System.out.println("cafes in dict?: " + dict.check("cafes"));
		
		System.out.println();
		System.out.println("zymotic prefix in dict?: " + dict.checkPrefix("zymotic"));
		System.out.println("zymotic in dict?: " + dict.check("zymotic"));
		System.out.println("Delete zymotic: " + dict.delete("zymotic"));
		System.out.println("zymotic prefix in dict?: " + dict.checkPrefix("zymotic"));
		System.out.println("zymotic in dict?: " + dict.check("zymotic"));
		
	}
	
}
