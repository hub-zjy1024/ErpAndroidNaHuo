package utils;

public class Myuuid {
	public static String create(int lastLen){
		String s= System.currentTimeMillis()+ String.valueOf(Math.random()).substring(2,2+lastLen);
		return s;
	}
}
