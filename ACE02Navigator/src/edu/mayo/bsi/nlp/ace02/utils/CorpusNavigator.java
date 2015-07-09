package edu.mayo.bsi.nlp.ace02.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * @author Sijia Liu
 * Navigate Ace2 corpus. Getting filtered file lists.
 * Jun 19 2015
 *
 */

public class CorpusNavigator {

	public static ArrayList<Path> getCorpusXmlFileList(Path acePath) throws IOException {
		
		return getCorpusFileList(acePath, "*.xml");
	}
	
	public static ArrayList<Path> getCorpusFileList(Path acePath, String filterString) throws IOException {
		// e.g. for npaper directory, create npaperIdent as Ident file output dir
		ArrayList<Path> fileList = new ArrayList<Path>();
		
		// DirectoryStream and filter *.xml
		// parse xml and write the result idt files to outPath
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(acePath, filterString)) {
			for (Path idtFileEntry : ds) {
				fileList.add(idtFileEntry);
			}
		}
		Collections.sort(fileList);
		return fileList;
	}

}
