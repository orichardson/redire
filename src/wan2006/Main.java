package wan2006;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import utensils.LOG;
import utensils.Util;
import util.LblTree;
import wan2006.DepTree.Node;
import distance.APTED;
import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.classify.LogisticClassifierFactory;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import edu.stanford.nlp.util.Pair;
import framework.MSR;

public class Main {

	private static Util util;
	
	private static boolean LOAD = false;
	
	private static RVFDataset<Integer,String> trainDataset(String file, boolean verbatim)
	{
		ArrayList<MSR> train_msr = MSR.read(file);
		RVFDataset<Integer, String> training = new RVFDataset<>();
		int loading = 0;
		for(MSR msr : train_msr)
		{
			try
			{
				if(loading == 47)
					System.out.println();
				training.add(new RVFDatum<>(computeVector(msr.first(), msr.second()), msr.isPara() ? 1 : -1));
			}
			catch(NumberFormatException e)
			{
				System.out.println("Error "+e.getMessage());
			}
			if(verbatim)System.out.println(loading++);
		}
		return training;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Util.initialize();
		RVFDataset<Integer, String> testing = null,
									training;
		LogisticClassifier<Integer, String> classifier = null;
		if(LOAD)
		{
			RVFDataset<String, String> loaded = RVFDataset.readSVMLightFormat("./out/wan_train_lightsvm.txt");
			Index<Integer> hi = new HashIndex<>(Arrays.asList(1, -1));
			Index<String> fi = HashIndex.loadFromFilename("./out/wan_feature_index.txt");
			
			training = new RVFDataset<Integer, String>(hi, loaded.getLabelsArray(), fi,
					loaded.getDataArray(), loaded.getValuesArray());
			
			RVFDataset<String, String> loaded2 = RVFDataset.readSVMLightFormat("./out/wan_test_lightsvm.txt");
			testing = new RVFDataset<Integer, String>(hi, loaded2.getLabelsArray(), fi,
					loaded2.getDataArray(), loaded2.getValuesArray());
		}
		else
		{

			String training_file = "data/msr/msr_paraphrase_train.txt";
			String testing_file = "data/msr/msr_paraphrase_test.txt";
			
			testing = trainDataset(testing_file, true);
			training = trainDataset(training_file, true);		
					

			training.writeSVMLightFormat(new File("./out/wan_wn_train_lightsvm.txt"));
			training.featureIndex().saveToFilename("./out/wan_wn_feature_index.txt");
			training.labelIndex().saveToFilename("./out/wan_wn_label_index.txt");
			testing.writeSVMLightFormat(new File("./out/wan_wn_test_lightsvm.txt"));
		}
		
		
		LogisticClassifierFactory<Integer, String> factory = new LogisticClassifierFactory<>();
		classifier = factory.trainClassifier(training);	
		
		System.out.println(stats(classifier, testing));
		
	}
	
	public static <A, B> String stats(Classifier<A, B> c, GeneralDataset<A, B> testd) {
		double acc = c.evaluateAccuracy(testd);
		double prec = -1, rec = -1;

		LOG.q("Accuracy: " + acc);
		LOG.q("Precision & Recall & F-measure");
		for (A val : testd.labelIndex()) {
			Pair<Double, Double> pr = c.evaluatePrecisionAndRecall(testd, val);
			if (prec < 0) {
				prec = pr.first;
				rec = pr.second;
			}
			LOG.q(pr.first + " & " + pr.second + " & " + ((2 * pr.first * pr.second) / (pr.first + pr.second)));
		}
		return "Accuracy: "+ (acc) + "\nPrecision: " +(prec)+ "\nRecall: " + (rec)+ "\nFscore: " + (2 * prec * rec / (rec + prec));

	}
	
	public static DepTree buildTree(Collection<TypedDependency> tds, List<String> tokens)
	{
		DepTree tree = new DepTree();
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		for(String s : tokens)
		{
			nodes.add(tree.new Node(s));
		}
		tree.addNodes(nodes);
		
		for(TypedDependency td : tds)
		{
			tree.link(new Dependency(td));		
		}
		return tree;
	}

	public static ClassicCounter<String> computeVector(String s1, String s2)
	{		
		ClassicCounter<String> features = new ClassicCounter<String>();
		int counter = 0;
		Annotation a1 = Util.annotate(s1), 
					a2 = Util.annotate(s2);
		
		List<String> tokens1 = Util.tokenizePIPE(a1), 
					tokens2 = Util.tokenizePIPE(a2);
		
		List<String> lemma1 = Util.lemmaPIPE(a1),
					lemma2 = Util.lemmaPIPE(a2);
		
		Collection<TypedDependency> tds1 = Util.dependencyPIPE(a1),
					tds2 = Util.dependencyPIPE(a2);
		
		//Unigram Recall and Precision
		int overlap = computeOverlap(tokens1, tokens2);
		//recall
		double globalRecall = overlap/(1.0 * tokens2.size());
		features.incrementCount("Feature"+(counter++), globalRecall);
		//precision
		double globalPrecision = overlap/(1.0 * tokens1.size());
		features.incrementCount("Feature"+(counter++),globalPrecision);	
		
		overlap = computeOverlap(lemma1, lemma2);
		
		features.incrementCount("Feature"+(counter++), overlap/(1.0 * lemma2.size()));
		features.incrementCount("Feature"+(counter++), overlap/(1.0 * lemma1.size()));
		
		//Bleu recall and precision
		
		features.incrementCount("Feature"+(counter++),computeBleu(tokens1, tokens2));
		features.incrementCount("Feature"+(counter++),computeBleu(tokens2, tokens1));
		
		features.incrementCount("Feature"+(counter++),computeBleu(lemma1, lemma2));
		features.incrementCount("Feature"+(counter++),computeBleu(lemma2, lemma1));

		
		//fmeasure
		double fmeasure = (2.0*globalRecall*globalPrecision);
		fmeasure /= (globalRecall + globalPrecision);
		features.incrementCount("Feature"+(counter++),fmeasure);
		
		HashSet<Dependency> dep1 = new HashSet<Dependency>(),
							dep2 = new HashSet<Dependency>(),
							dep1L = new HashSet<Dependency>(),
							dep2L = new HashSet<Dependency>();
		
		//dependency relation recall and precision
		for(TypedDependency td : tds1)
		{
			dep1.add(new Dependency(td));
			dep1L.add(new Dependency(td,lemma1));
		}
			
		for(TypedDependency td :tds2)
		{
			dep2.add(new Dependency(td));
			dep2L.add(new Dependency(td,lemma2));
		}
			
		
		//Want these too fall out of scope.
		{
			double dep1size = dep1.size();
			double dep2size = dep2.size();
			dep1.retainAll(dep2);
			double overlapSize = dep1.size();
			features.incrementCount("Feature"+(counter++),overlapSize/dep1size);
			features.incrementCount("Feature"+(counter++),overlapSize/dep2size);
		}
		//lemmatized dep rel recall and precision
		{
			double dep1size = dep1L.size();
			double dep2size = dep2L.size();
			dep1L.retainAll(dep2L);
			double overlapSize = dep1.size();
			features.incrementCount("Feature"+(counter++),overlapSize/dep1size);
			features.incrementCount("Feature"+(counter++),overlapSize/dep2size);
		}
		
		
		DepTree tree1 = buildTree(tds1, tokens1);
		DepTree tree2 = buildTree(tds2, tokens2);
		
		//tree edit distance
		
		//lemmatized tree edit distance	
		DepTree tree1L = buildTree(tds1, lemma1);
		DepTree tree2L = buildTree(tds2, lemma2);
		
		APTED ted = new APTED((float)1.0, (float)1.0, (float)1.0);
		
		features.incrementCount("Feature"+(counter++),(double) ted.nonNormalizedTreeDist(LblTree.fromString(tree1.toString()), LblTree.fromString(tree2.toString())));
		features.incrementCount("Feature"+(counter++),(double) ted.nonNormalizedTreeDist(LblTree.fromString(tree1L.toString()), LblTree.fromString(tree2L.toString())));
		
		//difference in token length and absolute difference in token length
		features.incrementCount("Feature"+(counter++),(double) (tokens1.size() - tokens2.size()));
		features.incrementCount("Feature"+(counter++),(double) Math.abs(tokens1.size() - tokens2.size()));
		
		String[] toCompute = new String[] { "lesk", "leacock", "lesk", "wupalmer", "resnik", "jiang", "lin", "path" };
		ArrayList<Double> WNSim = Util.lemmatizedWSPIPE(a1, a2, toCompute);
		
		for(int i = 0; i < toCompute.length; i++)
		{
			features.incrementCount(toCompute[i], WNSim.get(i));
		}
		
		return features;
		
	}
	
	private static double computeBleu(List<String> tokens1, List<String> tokens2)
	{
		double computation = 0;
		double wn = 0.25;
		for(int n = 1; n < 4; n++)
		{
			ArrayList<Ngram> g1 = generateNgrams(tokens1, n);
			ArrayList<Ngram> g2 = generateNgrams(tokens2, n);
			
			double ngover = ngramOverlap(g1, g2, n);
			
			computation += wn * Math.log(ngover);	
		}
		
		return Math.exp(computation);
	}
	
	public static int computeOverlap(List<String> t1, List<String> t2)
	{
		HashSet<String> set1 = new HashSet<String>(t1),
						set2 = new HashSet<String>(t2);
		
		set1.retainAll(set2);
		return set1.size();
		
	}
	
	public static ArrayList<Ngram> generateNgrams(List<String> tokens, int n)
	{
		Main main = new Main();
		int max = tokens.size();
		ArrayList<Ngram> grams = new ArrayList<Ngram>();
		for(int i = 0; i <= max - n; i++)
		{
			String[] toGram = new String[n];
			for(int j = i; j < i + n; j++)
			{
				toGram[j-i] = tokens.get(j);
			}
			grams.add(new Ngram(toGram));
		}
		return grams;
	}
	
	public static double ngramOverlap(ArrayList<Ngram> L1, ArrayList<Ngram> L2, int n)
	{
		int overlap = 0;
		
		HashMap<Ngram, Integer> candidate = new HashMap<Ngram, Integer>();
		HashMap<Ngram, Integer> reference = new HashMap<Ngram, Integer>();
		
		for(Ngram g1 : L1)
		{
			if(!candidate.containsKey(g1))
			{
				candidate.put(g1, 1);
			}
			else
			{
				candidate.put(g1, candidate.remove(g1)+1);
			}
			for(Ngram g2 : L2)
			{
				if(g1.equals(g2))
				{
					if(!reference.containsKey(g1))
					{
						reference.put(g1, 1);
					}
					else
					{
						reference.put(g1, reference.remove(g1)+1);
					}
				}
				overlap += g1.equals(g2) ? 1 : 0;
			}
		}
		double ret = 0;
		for(Ngram g1 : L1)
		{
			if(candidate.containsKey(g1) && reference.containsKey(g1))
				ret += Math.min(candidate.get(g1), reference.get(g1));
		}
		return (ret + (n > 1 ? 1.0 : 0.0))/(L1.size() + (n > 1 ? 1.0 : 0.0));
	}
	
	
	
	
}
