package edu.mayo.bsi.nlp.coref.CrScorer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class Test {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		String tmpStr = "string = \"international monetary fund\"";
		String tmpStr2 = tmpStr.toLowerCase().substring(tmpStr.indexOf("\"") + 1, tmpStr.lastIndexOf("\""));
		String tmpStr3[] = tmpStr.split("[\n\r\b\t\\- \\.]+");
		
		String tmpStr4 = tmpStr3[tmpStr3.length -1];
		System.out.println(tmpStr4);
		
		tmpStr = "23-219";
		tmpStr3 = tmpStr.split("-");
		System.out.println(tmpStr3[0]);
		
		
		//parseAceXmlDir(Paths.get("/data5/bsi/nlp/s110067.sharp/workspace-sj/data/ace2_train/npaper"));
	}
	
	public static void parseAceXmlDir(Path acePath) throws ParserConfigurationException, SAXException, IOException {
		// create xxxIdent at the same level as ace02Path.
		// e.g. for npaper directory create npaperIdent as Ident file output dir
		Path outDir = acePath.getParent().resolve(acePath.getFileName() + "Ident");
		if (Files.notExists(outDir)) {
			Files.createDirectories(outDir);
		}
				
		// DirectoryStream and filter *.xml
		// parse xml and write the result idt files to outPath
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(acePath, "*.xml")) {
			for (Path xmlFileEntry : ds) {
				
				String xmlString = xmlFileEntry.getFileName().toString();
				
				String outPathString = xmlString.substring(0, xmlString.indexOf(".sgm")) + ".key.idt";
				System.out.println(xmlString +  " -> " + outPathString );
				//Path outPath = outDir.resolve(outPathString);
			}
		}
	}
}
