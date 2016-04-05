package baseline;

import java.io.PrintWriter;
import java.util.Arrays;

import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import utensils.Util;

public class Sandbox {

	public static void main(String[] args) {
		Util.initialize();

		String text = "Tom ran to the store to buy some milk";
		String text1 = "Oliver doesn't believe in my code.";

		Annotation a = Util.annotate(text);
		System.out.println(Util.rootLemPIPE(a));
	}

	public static void playWithData() {
		RVFDataset<Integer, String> ds = new RVFDataset<Integer, String>();

		ds.add(makeDatum(0, 0.1, 0.9, 0.2, 0.8));
		ds.add(makeDatum(2, 0.12, 0.9, 0.2, 0.8));
		ds.add(makeDatum(1, 0.13, 0.9, 0.2, 0.8));
		ds.add(makeDatum(5, 0.14, 0.9, 0.2, 0.8));

		System.out.println(Arrays.deepToString(ds.getValuesArray()));
		System.out.println(ds.featureIndex());
		ds.printFullFeatureMatrixWithValues(new PrintWriter(System.out));
		ds.ensureRealValues();

		System.out.println();
		ds.printSVMLightFormat();
//		System.out.println(ds.);

//		Dataset<Integer, Double> ds2 = new Dataset<>();
//		ds2.add(Arrays.asList(0.1, 0.9, 0.2, 0.8), 5);
//		ds2.add(Arrays.asList(0.11, 0.9, 0.2, 0.8), 6);
//		ds2.add(Arrays.asList(0.12, 0.9, 0.2, 0.8), 7);
//		ds2.add(Arrays.asList(0.13, 0.9, 0.2, 0.8), 8);
//
//		System.out.println(Arrays.deepToString(ds2.getDataArray()));
//		System.out.println(ds2.featureIndex());
//		ds2.printSparseFeatureMatrix();
	}

	public static RVFDatum<Integer, String> makeDatum(int l, double... arr) {
		Counter<String> c = new ClassicCounter<>();
		for (int i = 0; i < arr.length; i++)
			c.incrementCount("F" + i, arr[i]);
		return new RVFDatum<>(c, l);
	}

}
