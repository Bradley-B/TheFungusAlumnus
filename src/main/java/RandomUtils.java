import java.util.HashMap;
import java.util.Map;

public class RandomUtils {

	final static Map<Character, String> regionalIndicators = new HashMap<Character, String>(); 
	final static String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	static {
		char[] alphabet = RandomUtils.alphabet.toCharArray();
		int regionalInicatorA = Integer.parseInt("1F1E6", 16);
		for(int i=0;i<regionalIndicators.size();i++) {
			char character = alphabet[i];
			String value = "U+"+Integer.toHexString(regionalInicatorA+i);
			regionalIndicators.put(character, value);
		}
	}
	
	
	public static boolean hasUniqueCharacters(String str) { 
        // Assuming string can have characters a-z 
        // this has 32 bits set to 0 
        int checker = 0; 
  
        for (int i = 0; i < str.length(); i++) { 
            int bitAtIndex = str.charAt(i) - 'a'; 
  
            // if that bit is already set in checker, 
            // return false 
            if ((checker & (1 << bitAtIndex)) > 0) 
                return false; 
  
            // otherwise update and continue by 
            // setting that bit in the checker 
            checker = checker | (1 << bitAtIndex); 
        } 
  
        // no duplicates encountered, return true 
        return true; 
    } 
}
