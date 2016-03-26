package utensils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/******************************************************************************
 * Original Comments: N516: Numbers
 *
 * Note: we ignore the "Names with Prefix" and "Constant Separator" rules from
 * http://www.archives.gov/research_room/genealogy/census/soundex.html
 * 
 * Restructured to be reasonably readable
 *
 ******************************************************************************/

public class Soundex {

	public static final HashMap<Character, Character> LOOKUP = new HashMap<>();

	static {
		for (char c : "BFPV".toCharArray())
			LOOKUP.put(c, '1');
		for (char c : "CGJKQSXZ".toCharArray())
			LOOKUP.put(c, '2');
		for (char c : "DT".toCharArray())
			LOOKUP.put(c, '3');
		for (char c : "MN".toCharArray())
			LOOKUP.put(c, '5');
		LOOKUP.put('L', '4');
		LOOKUP.put('R', '6');
	}

	public static String soundex(String s) {
		char[] x = s.toUpperCase().toCharArray();
		char firstLetter = x[0];

		// convert letters to numeric code
		for (int i = 0; i < x.length; i++)
			x[i] = LOOKUP.getOrDefault(x[i], '0');

		// remove duplicates
		String output = "" + firstLetter;
		for (int i = 1; i < x.length; i++)
			if (x[i] != x[i - 1] && x[i] != '0')
				output += x[i];

		// pad with 0's or truncate
		output = output + "0000";
		return output.substring(0, 4);
	}

	public static List<String> codeAll(List<String> things) {
		List<String> ret = new ArrayList<String>(things.size());

		for(String t : things)
			ret.add(soundex(t));

		return ret;
	}

}