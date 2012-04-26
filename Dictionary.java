import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.TreeMap;

/* A case insensitive dictionary */
public class Dictionary {

	private Node root;

	public Dictionary() {
		root = new Node();
	}

	public Dictionary(String filename) throws FileNotFoundException {
		this();
		Scanner scan = new Scanner(new File(filename));
		while (scan.hasNext()) {
			String word = scan.nextLine().toLowerCase().trim();
			if (!word.isEmpty()) {
				add(word);
			}
		}
	}

	/* Add word to the dictionary */
	public void add(String word) {
		add(word.toLowerCase().trim(), root);
	}

	private void add(String word, Node node) {

		if (word.length() > 0) {
			char ch = word.charAt(0);
			int index = (int) ch - (int) 'a';
			if (node.children[index] == null) {
				node.children[index] = new Node(ch);
			}
			add(word.substring(1, word.length()), node.children[index]);

		} else {
			node.valid = true;
		}

	}

	/* Check to see if word is in the dictionary */
	public boolean check(String word) {
		return check(word.toLowerCase().trim(), root);
	}

	private boolean check(String word, Node node) {
		if (word.length() > 0) {
			char ch = word.charAt(0);
			int index = (int) ch - (int) 'a';
			if (node.children[index] == null) {
				return false;
			}
			return check(word.substring(1, word.length()), node.children[index]);

		} else {
			return node.valid;
		}

	}

	/* Check to see if prefix is a prefix of any word in the dictionary */
	public boolean checkPrefix(String prefix) {
		return checkPrefix(prefix.toLowerCase().trim(), root);
	}

	private boolean checkPrefix(String prefix, Node node) {
		if (prefix.length() > 0) {
			char ch = prefix.charAt(0);
			int index = (int) ch - (int) 'a';

			if (node.children[index] == null) {
				return false;
			}
			return checkPrefix(prefix.substring(1, prefix.length()),
					node.children[index]);

		} else {
			return true;
		}
	}

	/*
	 * Return an array of numSuggestion words that are in the dictionary, that
	 * are as close as possible to the target word. If word is in the
	 * dictionary, however, then the returned array should have a length of 1
	 * (regardless of the parameter numSuggestions) and should only contain the
	 * original word
	 */
	public String[] suggest(String word, int numSuggestions) {
		String[] suggestions = null;
		
		word = word.toLowerCase().trim();
		
		if(dictEmpty()) {
			System.err.println("The dictionary is empty.");
			return suggestions;
		}
		
		int maxSuggs = numSuggestions * 2;
		
		if (check(word)) {
			suggestions = new String[1];
			suggestions[0] = word;
		} else {

			LinkedHashSet<String> set = new LinkedHashSet<String>();

			for (String s : swapLetters(word)) {
				if (set.size() == maxSuggs) {
					break;
				}
				if (check(s)) {
					//System.out.println("swap: " + s);
					set.add(s);
				}
			}

			if (set.size() < maxSuggs) {
				for (String s : extraLetter(word)) {
					if (set.size() == maxSuggs) {
						break;
					}
					if (check(s)) {
						//System.out.println("extra: " + s);
						set.add(s);
					}
				}
			}
			
			if (set.size() < maxSuggs) {
				for (String s : typo(word)) {
					if (set.size() == maxSuggs) {
						break;
					}
					if (check(s)) {
						//System.out.println("typo: " + s);
						set.add(s);
					}
				}
			}

			if (set.size() < maxSuggs) {
				for (String s : missingLetter(word)) {
					if (set.size() == maxSuggs) {
						break;
					}
					if (check(s)) {
						//System.out.println("missing: " + s);
						set.add(s);
					}
				}
			}

			// recurse most accurate prefix
			if (set.size() < maxSuggs) {
				findNode(set, word, root, 0, maxSuggs);
			}

			if (set.size() < maxSuggs) {
				for (String s : missingLetter2(word)) {
					if (set.size() == maxSuggs) {
						break;
					}
					if (check(s)) {
						//System.out.println("missing2: " + s);
						set.add(s);
					}
				}
			}

			if (set.size() < maxSuggs) {
				for (String s : swapLetters2(word)) {
					if (set.size() == maxSuggs) {
						break;
					}
					if (check(s)) {
						//System.out.println("swap2: " + s);
						set.add(s);
					}
				}
			}

			if (set.size() < maxSuggs) {
				for (String s : extraLetter2(word)) {
					if (set.size() == maxSuggs) {
						break;
					}
					if (check(s)) {
						//System.out.println("extra2: " + s);
						set.add(s);
					}
				}
			}
			
			if (set.size() < maxSuggs) {
				for (String s : replace(word)) {
					if (set.size() == maxSuggs) {
						break;
					}
					if (check(s)) {
						//System.out.println("replace: " + s);
						set.add(s);
					}
				}
			}

			if (set.size() < maxSuggs) {
				for (String s : swapLetters3(word)) {
					if (set.size() == maxSuggs) {
						break;
					}
					if (check(s)) {
						//System.out.println("swap3: " + s);
						set.add(s);
					}
				}
			}

			// need more suggestions - recurse less accurate prefixes
			if (set.size() < maxSuggs) {
				findNode2(set, word, root, 0, maxSuggs);
			}
			
			if (set != null) {
				// sort suggestions
				ArrayList<Ranker> sorted = new ArrayList<Ranker>();
				for(String s : set) {
					sorted.add(new Ranker(word, s));
				}
				
				Collections.sort(sorted);
				suggestions = new String[numSuggestions];
				for(int i = 0; i < suggestions.length; i++) {
					suggestions[i] = sorted.get(i).sugg;
				}
					
			}

		}
		return suggestions;
	}

	// similar to checkPrefix; find starting node for completing words from
	// prefix
	private void findNode(HashSet<String> set, String word, Node node,
			int index, int numSuggestions) {

		if (index < word.length()) {
			char ch = word.charAt(index);
			int i = (int) ch - (int) 'a';
			if (node.children[i] != null) {
				findNode(set, word, node.children[i], index + 1, numSuggestions);
			}
		} else {
			addToSet(set, word, node, numSuggestions);
		}

	}

	// similar to checkPrefix; find starting node for completing words from
	// prefix
	private void findNode2(HashSet<String> set, String word, Node node,
			int index, int numSuggestions) {

		if (index < word.length()) {
			char ch = word.charAt(index);
			int i = (int) ch - (int) 'a';
			if (node.children[i] != null) {
				findNode2(set, word, node.children[i], index + 1,
						numSuggestions);
			}
			// addToList(set, word.substring(0, index), node, numSuggestions);
		}
		addToSet(set, word.substring(0, index), node, numSuggestions);
	}

	// similar to print; add suggestion to list
	private void addToSet(HashSet<String> set, String word, Node node,
			int numSuggestions) {
		if (set.size() == numSuggestions) {
			return;
		}

		if (node.valid) {
			set.add(word);
			//System.out.println("prefix: " + word);
		}
		for (int i = 0; i < 26; i++) {
			if (node.children[i] == null) {
				continue;
			}
			addToSet(set, word + node.children[i].ch, node.children[i],
					numSuggestions);
		}
	}

	/* Delete the word from the dictionary. */
	public boolean delete(String word) {
		return delete(word.toLowerCase().trim(), root);
	}

	private boolean delete(String word, Node node) {
		if (word.length() > 0) {
			char ch = word.charAt(0);
			int index = (int) ch - (int) 'a';
			if (index < 0 || index > 25) {
				return false;
			}
			if (node.children[index] == null) {
				return false;
			}
			boolean exists = delete(word.substring(1, word.length()),
					node.children[index]);

			if (!exists) {
				return false;
			}

			boolean hasChild = false;

			Node[] childs = node.children[index].children;
			for (Node n : childs) {
				if (n != null) {
					hasChild = true;
					break;
				}
			}

			if (!node.children[index].valid && !hasChild) {
				node.children[index] = null;
			}

			return exists;
		} else {
			if (node.valid) {
				node.valid = false;
				return true;
			}
			return false;
		}

	}

	/* Print all the words in the dictionary in alphabetical order */
	public void print() {
		print(root, "");
	}

	private void print(Node node, String word) {
		if (node.valid) {
			//System.out.println(word);
		}
		for (int i = 0; i < 26; i++) {
			if (node.children[i] == null) {
				continue;
			}
			print(node.children[i], word + node.children[i].ch);
		}
	}
	
	/* Remove a word from the dictionary */
	public void remove(String word) {
		delete(word);
	}

	private class Node {
		public Character ch;
		public Node[] children;
		public boolean valid;

		public Node() {
			children = new Node[26];
			valid = false;
		}

		public Node(Character ch) {
			this();
			this.ch = ch;
		}

		public String toString() {
			return "" + ch;
		}
	}

	public static LinkedHashSet<String> swapLetters(String word) {
		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse word-1, swapping with following letter
		for (int swapIndex = word.length() - 2; swapIndex >= 0; swapIndex--) {
			suggestions.add(word.substring(0, swapIndex)
					+ word.charAt(swapIndex + 1) + word.charAt(swapIndex)
					+ word.substring(swapIndex + 2, word.length()));
		}

		return suggestions;
	}
	
	public static LinkedHashSet<String> swapLetters2(String word) {
		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse word-2, swapping with following letter
		for (int swapIndex = word.length() - 3; swapIndex >= 0; swapIndex--) {
			suggestions.add(word.substring(0, swapIndex)
					+ word.charAt(swapIndex + 2) + word.charAt(swapIndex + 1)
					+ word.charAt(swapIndex)
					+ word.substring(swapIndex + 3, word.length()));
		}

		return suggestions;
	}
	
	public static LinkedHashSet<String> swapLetters3(String word) {
		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse word-2, swapping with following letter
		for (int swapIndex = word.length() - 4; swapIndex >= 0; swapIndex--) {
			suggestions.add(word.substring(0, swapIndex)
					+ word.charAt(swapIndex + 3) + word.charAt(swapIndex + 1)
					+ word.charAt(swapIndex + 2) + word.charAt(swapIndex)
					+ word.substring(swapIndex + 4, word.length()));
		}

		return suggestions;
	}

	public static LinkedHashSet<String> missingLetter(String word) {
		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse word, picking a different letter to remove every time
		for (int removeIndex = word.length() - 1; removeIndex >= 0; removeIndex--) {

			// build new word without missing letter
			suggestions.add(word.substring(0, removeIndex)
					+ word.substring(removeIndex + 1, word.length()));
		}

		return suggestions;
	}
	
	public static LinkedHashSet<String> missingLetter2(String word) {
		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse word, picking a different letter to remove every time
		for (int removeIndex = word.length() - 1; removeIndex >= 0; removeIndex--) {

			if(removeIndex+2 <= word.length()) {
				// build new word without missing letter
				suggestions.add(word.substring(0, removeIndex)
						+ word.substring(removeIndex + 2, word.length()));
			}
			
		}

		return suggestions;
	}

	public static LinkedHashSet<String> replace(String word) {
		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse word, picking a different index to insert at every time
		for (int addIndex = word.length() - 1; addIndex >= 0; addIndex--) {

			// a = 97; z = 122
			for (int letter = 97; letter <= 122; letter++) {

				// build new word without extra letter
				suggestions.add(word.substring(0, addIndex) + (char) letter
						+ word.substring(addIndex + 1, word.length()));
			}

		}

		return suggestions;
	}
	
	public static LinkedHashSet<String> extraLetter(String word) {
		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse word, picking a different index to insert at every time
		for (int addIndex = word.length() - 1; addIndex >= 0; addIndex--) {

			// a = 97; z = 122
			for (int letter = 97; letter <= 122; letter++) {

				// build new word without extra letter
				suggestions.add(word.substring(0, addIndex) + (char) letter
						+ word.substring(addIndex, word.length()));
			}

		}

		return suggestions;
	}
	
	public static LinkedHashSet<String> extraLetter2(String word) {
		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse word, picking a different index to insert at every time
		for (int addIndex = word.length(); addIndex >= 0; addIndex--) {

			// a = 97; z = 122
			for (int letter = 97; letter <= 122; letter++) {
				for (int letter2 = 97; letter2 <= 122; letter2++) {
					// build new word without extra letter
					suggestions.add(word.substring(0, addIndex) + (char) letter
							+ (char) letter2
							+ word.substring(addIndex, word.length()));
				}

				// build new word without extra letter
				suggestions.add(word.substring(0, addIndex) + (char) letter
						+ word.substring(addIndex, word.length()));
			}

		}

		return suggestions;
	}

	public static LinkedHashSet<String> typo(String word) {
		TreeMap<Character, char[]> typos = makeTyposConstant();

		LinkedHashSet<String> suggestions = new LinkedHashSet<String>();

		// traverse every possible likeliness (maxTypoOptions = 10)
		for (int likeliness = 0; likeliness < 10; likeliness++) {

			// traverse word, picking a different letter to remove every time
			for (int changeIndex = word.length() - 1; changeIndex >= 0; changeIndex--) {
				StringBuilder sb = new StringBuilder();
				boolean stop = false;

				// traverse word and build new word without removed
				for (int j = 0; j < word.length(); j++) {
					if (j == changeIndex) {
						char curr = word.charAt(j);
						char[] likelyLetters = typos.get(curr);
						if (likelyLetters.length > likeliness) {
							sb.append(likelyLetters[likeliness]);
						} else {
							stop = true;
							break;
						}
					} else {
						sb.append(word.charAt(j));
					}
				}
				if (!stop) {
					suggestions.add(sb.toString());
				}
			}

		}

		return suggestions;
	}
	
	private static TreeMap<Character, char[]> makeTyposConstant() {
		// near misses on keyboard and vowels
		char[] a = { 's', 'q', 'z', 'w', 'x', 'e', 'i', 'o', 'u' };
		char[] b = { 'v', 'n', 'g', 'h' };
		char[] c = { 'k', 'x', 'v', 'd', 'f' };
		char[] d = { 's', 'f', 'e', 'x', 'c', 'r' };
		char[] e = { 'r', 'w', 'd', 's', 'f', 'a', 'i', 'o', 'u', 'y' };
		char[] f = { 'g', 'd', 'r', 'c', 'v', 't' };
		char[] g = { 'j', 'h', 'f', 't', 'v', 'b', 'y' };
		char[] h = { 'g', 'j', 'y', 'b', 'n', 'u' };
		char[] i = { 'u', 'o', 'k', 'j', 'a', 'e', 'y'};
		char[] j = { 'g', 'h', 'k', 'u', 'n', 'm', 'i' };
		char[] k = { 'c', 'j', 'l', 'i', 'm', 'o' };
		char[] l = { 'k', 'o', 'p' };
		char[] m = { 'n', 'j', 'k' };
		char[] n = { 'm', 'b', 'h', 'j' };
		char[] o = { 'p', 'i', 'l', 'k', 'a', 'e', 'u', 'y' };
		char[] p = { 'o', 'l' };
		char[] q = { 'w', 'a', 's' };
		char[] r = { 't', 'e', 'f', 'd' };
		char[] s = { 'a', 'd', 'w', 'z', 'x', 'q', 'e' };
		char[] t = { 'r', 'y', 'g', 'f', 'h' };
		char[] u = { 'y', 'i', 'j', 'h', 'k', 'a', 'e', 'o', 'u' };
		char[] v = { 'b', 'c', 'f', 'g' };
		char[] w = { 'q', 'e', 's', 'a' };
		char[] x = { 'z', 'c', 's', 'd' };
		char[] y = { 't', 'u', 'h', 'g' };
		char[] z = { 'x', 'a', 's' };

		TreeMap<Character, char[]> typos = new TreeMap<Character, char[]>();

		typos.put('a', a);
		typos.put('b', b);
		typos.put('c', c);
		typos.put('d', d);
		typos.put('e', e);
		typos.put('f', f);
		typos.put('g', g);
		typos.put('h', h);
		typos.put('i', i);
		typos.put('j', j);
		typos.put('k', k);
		typos.put('l', l);
		typos.put('m', m);
		typos.put('n', n);
		typos.put('o', o);
		typos.put('p', p);
		typos.put('q', q);
		typos.put('r', r);
		typos.put('s', s);
		typos.put('t', t);
		typos.put('u', u);
		typos.put('v', v);
		typos.put('w', w);
		typos.put('x', x);
		typos.put('y', y);
		typos.put('z', z);

		return typos;
	}
	
	public static class Ranker implements Comparable<Ranker>{

		String sugg;
		int points;
		
		public Ranker(String input, String sugg) {
			this.sugg = sugg;
			this.points = rank(input, sugg);
		}
		
		@Override
		public int compareTo(Ranker r) {

			// sort in descending order
			if(this.points < r.points) {
				return -1;
			} else if (this.points > r.points) {
				return 1;
			} else {
				return 0;
			}
		}
		
		/*
		 * Algorithm: Levenshtein Distance 
		 * Reference:
		 * http://www.java2s.com/Code/Java/Data-Type/FindtheLevenshteindistancebetweentwoStrings.htm
		 */
		public static int rank(String s, String t) {

		      int lenS = s.length();
		      int lenT = t.length();

		      if (lenS == 0) {
		          return lenT;
		      } else if (lenT == 0) {
		          return lenS;
		      }

		      // swap
		      if (lenS > lenT) {
		          String tmp = s;
		          s = t;
		          t = tmp;
		          lenS = lenT;
		          lenT = t.length();
		      }

		      int p[] = new int[lenS+1]; //'previous' cost array, horizontally
		      int d[] = new int[lenS+1]; // cost array, horizontally
		      int _d[]; //placeholder to assist in swapping p and d

		      // indexes into strings s and t
		      int i; // iterates through s
		      int j; // iterates through t

		      char t_j; // jth character of t

		      int cost; // cost

		      for (i = 0; i<=lenS; i++) {
		          p[i] = i;
		      }

		      for (j = 1; j<=lenT; j++) {
		          t_j = t.charAt(j-1);
		          d[0] = j;

		          for (i=1; i<=lenS; i++) {
		              cost = s.charAt(i-1)==t_j ? 0 : 1;
		              // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
		              d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
		          }

		          // copy current distance counts to 'previous row' distance counts
		          _d = p;
		          p = d;
		          d = _d;
		      }

		      return p[lenS];
		  }
		
	}
	
	public static String arrayToString(String[] list) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String s : list) {
			sb.append(s + ", ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	private boolean dictEmpty() {
		boolean empty = true;
		
		for (Node n : root.children) {
			if(n != null) {
				empty = false;
				break;
			}
		}
		
		return empty;
	}
}
