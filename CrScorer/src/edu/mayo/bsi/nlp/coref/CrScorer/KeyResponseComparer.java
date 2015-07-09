package edu.mayo.bsi.nlp.coref.CrScorer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

import Jama.Matrix;
import edu.mayo.bsi.nlp.ace02.utils.CorpusNavigator;
import edu.mayo.bsi.nlp.coref.CrScorer.helpers.PairType;
import edu.mayo.bsi.nlp.coref.CrScorer.helpers.ParentPtrTree;

public class KeyResponseComparer {
	public Path keyDirPath;
	public Path responseDirPath;
	
	public ArrayList<Path> keyFileList;
	public ArrayList<Path> responseFileList;
	
	public final String keyFileExtension = "norm.idt";
	public final String responseFileExtension = "sgm.tmx.rdc.xml.idt";
	
	public KeyResponseComparer(String keyDirPathString, String ResponseDirPathString) throws IOException {
		keyDirPath = Paths.get(keyDirPathString).toAbsolutePath();
		responseDirPath = Paths.get(ResponseDirPathString).toAbsolutePath();
		
		init(keyDirPath, responseDirPath);
	}
	
	public KeyResponseComparer(Path _keyDirPath, Path _responseDirPath) throws IOException {
		init(_keyDirPath, _responseDirPath);
	}
	
	public void init(Path _keyDirPath, Path _responseDirPath) throws IOException {
		keyDirPath = _keyDirPath;
		responseDirPath = _responseDirPath;
		
		keyFileList = CorpusNavigator.getCorpusFileList(keyDirPath, "*." + keyFileExtension);
		responseFileList = CorpusNavigator.getCorpusFileList(responseDirPath, "*." + responseFileExtension);
	}
	
	
	public double[] runEvalutationMUC() throws IOException{
		
		double totalF1 = 0;
		double totalPresicion = 0;
		double totalRecall = 0;
		
		for(Path keyPath : keyFileList){
			try {
				Path responsePath = fromKeyToResponsePath(keyPath);
				CorefScore score = getResponseScore(keyPath, responsePath);
				if (Double.isNaN(score.f1)) {
					score.f1 = 0;
				}
				totalPresicion += score.p;
				totalRecall += score.r;
				totalF1 += score.f1;
				System.out.println(keyPath.getFileName() + ": \t" + String.format("%.3f %.3f %.3f",score.p, score.r, score.f1));
			} catch (Exception e) {
				//System.out.println("Error: " + keyPath.getFileName());
				e.printStackTrace();
			}
		}
		
		double[] dScore = new double[3];
		dScore[0] = totalPresicion / (double)keyFileList.size();
		dScore[1] = totalRecall / (double)keyFileList.size();
		dScore[2] = totalF1 / (double)keyFileList.size();
		return dScore;
	}
	
	public CorefScore getResponseScore(Path keyPath, Path responsePath) throws IOException{
		Vector<String[]> bufI = new Vector<String[]>();
		Vector<String[]> bufN = new Vector<String[]>();
		try(BufferedReader brKeyReader = Files.newBufferedReader(keyPath, StandardCharsets.UTF_8)){
			String l;
			while ((l = brKeyReader.readLine()) != null) {
				if (l.startsWith("#")) continue;
				String[] s = l.split("[\\s+\\t]");
				PairType pt = PairType.valueOf(s[0]);
				if (pt==PairType.IDENT) bufI.add(s);
				else bufN.add(s);
			}
		}
		
		int[][] pK = new int[bufI.size()][2];
		for (int i = 0; i < pK.length; i++) {
			String[] s = bufI.get(i);
			pK[i][0] = Integer.parseInt(s[1]);
			pK[i][1] = Integer.parseInt(s[3]);
		}
		ParentPtrTree key = new ParentPtrTree(pK);
//		int[] a = new int[key.getSize()];
//		key.equivCls(a);

		bufI.clear(); bufN.clear();
		
		try(BufferedReader brResponseReader = Files.newBufferedReader(responsePath, StandardCharsets.UTF_8)){
			String l;
			while ((l = brResponseReader.readLine()) != null) {
				if (l.startsWith("#")) continue;
				String[] s = l.split("[\\s+\\t]");
				PairType pt = PairType.valueOf(s[0]);
				if (pt==PairType.IDENT) bufI.add(s);
				else bufN.add(s);
			}
		} 

		int[][] pR = new int[bufI.size()][2];
		for (int i = 0; i < pR.length; i++) {
			String[] s = bufI.get(i);
			pR[i][0] = Integer.parseInt(s[1]);
			pR[i][1] = Integer.parseInt(s[3]);
		}
		ParentPtrTree res = new ParentPtrTree(pR);

		Matrix m = MatScore.partition(key, res);
		CorefScore score = MatScore.MUC(m);
		//System.out.println("MUC                = " + score) ;
		return score;
	}
	
	public Path fromKeyToResponsePath(Path keyFilePath){
		String keyFileString = keyFilePath.getFileName().toString();
		String commonString = keyFileString.substring(0, keyFileString.indexOf(keyFileExtension) - 1);
		return responseDirPath.resolve(commonString + "." + responseFileExtension);
	}
	
	public static void main(String[] args) throws IOException {
		KeyResponseComparer krc = new KeyResponseComparer(args[0], args[1]);
		double[] score = krc.runEvalutationMUC();
		System.out.println("Final Score: " + String.format("%.3f", score[2]));
	}
}
