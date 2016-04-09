package utensils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import framework.NormalizedTypedDependency;

/**
 * 
 * Provides wrappers for any NLP utility we need. Primarily using Stanford NLP files.
 *
 */
public class Util {

	private static StanfordCoreNLP pipeline;
	private static ILexicalDatabase db = new NictWordNet();
	private static HashMap<String, RelatednessCalculator> WSMETRICES = new HashMap<>();

	public static void initialize() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse");
		pipeline = new StanfordCoreNLP(props);

		WSMETRICES.put("hirst", new HirstStOnge(db));
		WSMETRICES.put("leacock", new LeacockChodorow(db));
		WSMETRICES.put("lesk", new Lesk(db));
		WSMETRICES.put("wupalmer", new WuPalmer(db));
		WSMETRICES.put("resnik", new Resnik(db));
		WSMETRICES.put("jiang", new JiangConrath(db));
		WSMETRICES.put("lin", new Lin(db));
		WSMETRICES.put("path", new Path(db));

	}

	public static void wordnet() {
		WS4JConfiguration.getInstance().setMFS(true);
	}

	public static String rootLem(Sentence sent) {
		Collection<TypedDependency> dependencies = dependency(sent);

		for (TypedDependency td : dependencies) {
			if (td.reln().toString().equals("root")) {
				return new Sentence(td.dep().word()).lemmas().get(0);
			}
		}

		return "";
	}

	public static String rootLemPIPE(Annotation a) {
		for (CoreMap core : a.get(SentencesAnnotation.class))
			return core.get(CollapsedDependenciesAnnotation.class).getFirstRoot().lemma();

		return "NOPE";

	}

	public static ArrayList<Double> lemmatizedWS(Sentence sentence1, Sentence sentence2, String... metric_names) {
		ArrayList<Double> similarities = new ArrayList<Double>();

		for (String name : metric_names)
			similarities.add(WSMETRICES.get(name).calcRelatednessOfWords(rootLem(sentence1), rootLem(sentence2)));

		return similarities;
	}
	public static ArrayList<Double> lemmatizedWSPIPE(Annotation sentence1, Annotation sentence2,
			String... metric_names) {
		ArrayList<Double> similarities = new ArrayList<Double>();

		for (String name : metric_names)
			similarities
					.add(WSMETRICES.get(name).calcRelatednessOfWords(rootLemPIPE(sentence1), rootLemPIPE(sentence2)));

		return similarities;
	}

	public static Annotation annotate(String text) {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		return document;
	}

	public static List<String> tokenize(Sentence s) {
		return s.words();
	}

	public static List<String> tokenizePIPE(Annotation annotation) {
		List<String> tokens = new ArrayList<String>();
		for (CoreLabel token : annotation.get(TokensAnnotation.class)) {
			tokens.add(token.get(TextAnnotation.class));
		}
		return tokens;
	}

	public static List<String> tagPos(Sentence s) {
		return s.posTags();
	}

	public static List<String> tagPosPIPE(Annotation annotation) {
		List<String> pos = new ArrayList<String>();
		for (CoreLabel core : annotation.get(TokensAnnotation.class))
			pos.add(core.get(PartOfSpeechAnnotation.class));
		return pos;
	}

	public static List<String> lemma(Sentence s) {
		return s.lemmas();
	}

	public static List<String> lemmaPIPE(Annotation annotation) {
		List<String> lemmas = new ArrayList<String>();
		for (CoreLabel core : annotation.get(TokensAnnotation.class))
			lemmas.add(core.get(LemmaAnnotation.class));

		return lemmas;
	}

	public static Collection<TypedDependency> dependency(Sentence s) {
		return s.dependencyGraph().typedDependencies();
	}

	public static Collection<TypedDependency> dependencyPIPE(Annotation annotation) {
		SemanticGraph sg = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
				.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
		return sg.typedDependencies();
	}

	public static HashSet<NormalizedTypedDependency> getDependencySet(Sentence annotation) {
		HashSet<NormalizedTypedDependency> tdset = new HashSet<NormalizedTypedDependency>();
		for (TypedDependency td : dependency(annotation)) {
			NormalizedTypedDependency ntd = new NormalizedTypedDependency(td.reln().toString(), td.gov().toString(),
					td.dep().toString());
			tdset.add(ntd);
		}

		return tdset;

	}
	public static HashSet<NormalizedTypedDependency> getDependencySetPIPE(Annotation a) {
		HashSet<NormalizedTypedDependency> tdset = new HashSet<NormalizedTypedDependency>();
		for (CoreMap core : a.get(SentencesAnnotation.class))
			for (TypedDependency td : core.get(CollapsedDependenciesAnnotation.class).typedDependencies()) {
				NormalizedTypedDependency ntd = new NormalizedTypedDependency(td.reln().toString(), td.gov().toString(),
						td.dep().toString());
				tdset.add(ntd);
			}

		return tdset;

	}

}
