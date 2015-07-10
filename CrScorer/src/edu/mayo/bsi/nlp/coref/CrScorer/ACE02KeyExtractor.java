package edu.mayo.bsi.nlp.coref.CrScorer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.mayo.bsi.nlp.ace02.annotation.NamedEntity;
import edu.mayo.bsi.nlp.ace02.readers.AceReader;
import edu.mayo.bsi.nlp.ace02.utils.CorefClusters;
import edu.mayo.bsi.nlp.ace02.utils.CorefFunctions;
import edu.mayo.bsi.nlp.ace02.utils.Pair;

/**
 * Created: Jun 19 2015, Sijia
 * 
 * Extract ACE 02 Mention-ENtity keys form corpus
 * */
public class ACE02KeyExtractor extends AceReader {

	protected static List<Pair> mentionSpanList;
	protected static HashMap<Pair, String> bytespanIdM;
	static HashMap<String, List<Pair>> idMentionListM;
	
	public Path outputDir;
	public Path inputDir;
	public String mentionOutputSuffixString = "Ident";
	public String entityOutputSuffixString = "Ent";
	
	public final static String keyFileExtensionString = "key.idt";
	public final static String responseFileExtensionString = "norm.idt";

	List<String> pronounList;
	List<String> thirdRefList;
	List<String> maPronounsList;
	List<String> fePronounsList;
	List<String> nePronounsList;
	List<String> singPronounList;
	List<String> pluralList;
	List<String> otherPronounsList;
	boolean debug;
	boolean nootherpron;
	boolean xmlonly = true;
	
	/**
	 * constructor, a few lists of pronouns are constructed
	 */
	public ACE02KeyExtractor(boolean debug, boolean nootherpron) {
		this.debug = debug;
		this.nootherpron = nootherpron;
		init();
	}

	public ACE02KeyExtractor(boolean debug) {
		this.debug = debug;
		init();
	}

	public ACE02KeyExtractor() {
		init();
	}
	
	public ACE02KeyExtractor(Path inPath) {
		init();
		inputDir = inPath;
		outputDir = inputDir.getParent().resolve(inputDir.getFileName() + mentionOutputSuffixString);

	}

	private void init() {
		idMentionListM = new HashMap<String, List<Pair>>();
		bytespanIdM = new HashMap<Pair, String>();
		mentionSpanList = new ArrayList<Pair>();
		String[] thirdRefArray = { "himself", "herself", "itself", "themselves" };
		String[] thirdPronArray = { "he", "his", "him", "she", "her", "it",
				"its", "hers", "they", "their", "them", };
		String[] maPronounsArray = { "he", "his", "him", "himself" };
		String[] fePronounsArray = { "she", "her", "hers", "herself" };
		String[] nePronounsArray = { "it", "its", "itself", "they", "their",
				"them", "these", "those", "themselves" };
		String[] singPronounArray = { "this", "that", "he", "his", "him",
				"himself", "she", "her", "hers", "herself", "it", "its",
				"itself" };
		String[] pluralArray = { "they", "their", "them", "these", "those",
				"themselves" };
		String[] otherPronouns = { "you", "yourself", "youselves", "yours",
				"your", "i", "me", "my", "mine", "we", "our", "ourselves",
				"us", "'s" };
		otherPronounsList = Arrays.asList(otherPronouns);
		thirdRefList = Arrays.asList(thirdRefArray);
		pronounList = Arrays.asList(thirdPronArray);
		maPronounsList = Arrays.asList(maPronounsArray);
		// System.out.println("true or false:::::: "+maPronounsList.contains("he"));
		fePronounsList = Arrays.asList(fePronounsArray);
		nePronounsList = Arrays.asList(nePronounsArray);
		singPronounList = Arrays.asList(singPronounArray);
		pluralList = Arrays.asList(pluralArray);
	}


	public ArrayList<CorefClusters> getMentionKeyFromReader() {
		ArrayList<CorefClusters> keyDataList = CorefFunctions.getKeyData(
				new ArrayList<String>(), bytespanIdM, idMentionM, idMentionListM,
				mentionSpanList);
		return keyDataList;
	}

	public static void writeAceMentionKeyIdent(Path outPath, List<CorefClusters> lCorefClusters) 
			throws ParserConfigurationException,SAXException, IOException {
		try(BufferedWriter bw = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8)){
			for (CorefClusters corefCluster : lCorefClusters) {
				bw.write("IDENT " + corefCluster + "\n");
			}
		}
	}

	
	// extract and write ACE 02 mention key
	public void parseAce02KeyDir() throws ParserConfigurationException, SAXException, IOException {
		// create xxxIdent at the same level as ace02Path.
		// e.g. for npaper directory, create npaperIdent as Ident file output dir
		//System.out.println("Key output dir: \t" + outputDir);

		if (Files.notExists(outputDir)) {
			Files.createDirectories(outputDir);
		}

		// DirectoryStream and filter *.xml
		// parse xml and write the result idt files to outPath
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(inputDir, "*.xml")) {
			for (Path xmlFileEntry : ds) {
				ACE02KeyExtractor ace02KeyExtractor = new ACE02KeyExtractor();		
				List<CorefClusters> lCorefClusters = ace02KeyExtractor.extractAce02Key(xmlFileEntry);
								
				//e.g. 9801.476.sgm.tmx.rdc.xml  -->  9801.476.key.idt
				String xmlFileName = xmlFileEntry.getFileName().toString();
				String outFileName = xmlFileName.substring(0, xmlFileName.indexOf(".sgm")) + "." + keyFileExtensionString;
				Path outPath = outputDir.resolve(outFileName);
				//System.out.println("writing Ace02 Key: \t" + outPath.getFileName());

				ACE02KeyExtractor.writeAceMentionKeyIdent(outPath, lCorefClusters);
			}
		}
	}
	
	

	public HashMap<String, NamedEntity> extractAce2EntityKey(Path xmlFilePath)
			throws ParserConfigurationException, SAXException, IOException {
		//System.out.println("extracting Ace02 Key: \t" + xmlFilePath.getFileName());
		processDocument(null, xmlFilePath.toString());
		
		return idNeM;
	}
	
	public ArrayList<CorefClusters> extractAce02Key(Path xmlFilePath)
			throws ParserConfigurationException, SAXException, IOException {
		//System.out.println("extracting Ace02 Key: \t" + xmlFilePath.getFileName());
		processDocument(null, xmlFilePath.toString());
		CorefFunctions.fillMentionList(idMentionM, mentionSpanList, bytespanIdM);
		CorefFunctions.fillIdMenListM(idMentionListM, idNeM);
		
		return getMentionKeyFromReader();
	}
		
	/**
	 * @param args[0]: input ace02 directory. e.g. /data5/bsi/nlp/s110067.sharp/workspace-sj/data/ace2_train/npaper
	 * output: if e.g. npaperIdent directory of ident files correspondingly.
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		System.out.println("Reading Dir: \t" + args[0]);
		ACE02KeyExtractor ace02KeyExtractor = new ACE02KeyExtractor(Paths.get(args[0]));
		ace02KeyExtractor.parseAce02KeyDir();
		System.out.println("All done!");
	}
}
