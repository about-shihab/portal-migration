package com.iict.buet.customer_portal.util;

import java.util.List;
import org.apache.commons.lang.RandomStringUtils;

public final class StringUtils {
	private StringUtils(){
		
	}
	public static String getRandomNumber(int size) {
		return RandomStringUtils.random(size, false, true);
	}

	public static String getListToCommaSeparatedString(List<String> strings) {
		StringBuilder commaSeparatedString = new StringBuilder();
		for (int i = 0; i < strings.size(); i++) {
			commaSeparatedString.append("'");
			commaSeparatedString.append(strings.get(i));
			commaSeparatedString.append("'");
			if (i != (strings.size() - 1)) {
				commaSeparatedString.append(",");
			}
		}
		return commaSeparatedString.toString();
	}

	public static String getStringArrayToCommaSeparatedString(String[] args){
        StringBuilder commaSeparatedString = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            commaSeparatedString.append(args[i]);
            if(i != (args.length - 1)){
                commaSeparatedString.append(",");
            }
        }
        return commaSeparatedString.toString();
    }

	public static String[] getCommaSeparatedStringToStringArray(String s){
		if(s != null) {
			return s.split(",");
		}
		return new String[]{""};
	}

}
