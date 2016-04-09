package utensils;

public class LOG {
	public static int VERB = 5;

	public static void m(String str) {
		log(2, str);
	}
	public static void o(String str) {
		log(0, str);
	}
	public static void q(String str) {
		log(10, str);
	}

	public static void log(int vlevel, String str) {
		if (vlevel <= VERB)
			System.out.println(str);
	}
	public static void err(int vlevel, String str) {
		if (vlevel <= VERB)
			System.err.println(str);
	}
}
