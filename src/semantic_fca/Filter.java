package semantic_fca;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Filter {

	private static String[] stopwords = { "a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all",
			"almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",
			"an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at",
			"back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being",
			"below", "beside", "besides", "between", "beyond", "bill", "both", "but", "by", "call", "can", "cannot",
			"cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "done", "down", "due", "during",
			"each", "eg", "eight", "either", "eleven","else", "elsewhere", "enough", "etc", "even", "ever", "every",
			"everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five",
			"for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had",
			"has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself",
			"him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it",
			"its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile",
			"might", "mill", "mine", "more", "moreover", "most", "mostly", "much", "must", "my", "myself",
			"neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now",
			"nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours",
			"ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem",
			"seemed", "seeming", "seems", "serious", "set", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty",
			"so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take",
			"ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby",
			"therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three",
			"through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two",
			"un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when",
			"whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which",
			"while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet",
			"you", "your", "yours", "yourself", "yourselves", "the", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
			"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	
	public static boolean isStopWord(String word) {
		List<String> words = new ArrayList<String>();
		words = Arrays.asList(stopwords);
		return words.contains(word);
	}
}
