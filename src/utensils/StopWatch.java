package utensils;

import java.text.DecimalFormat;

public class StopWatch {
	private static DecimalFormat DF = new DecimalFormat("0.000");

	long totalms;
	long start;

	public StopWatch() {
		totalms = 0;
		start = -1;
	}

	public void go() {
		start = System.currentTimeMillis();
	}

	public void stop() {
		if (start > 0)
			totalms += (System.currentTimeMillis() - start);
		start = -1;
	}

	public long check() {
		if (start < 0)
			return totalms;
		return totalms + (System.currentTimeMillis() - start);
	}

	public double checkS() {
		return check() / 1000d;
	}

	public String checkSF() {
		return DF.format(checkS());
	}

}
