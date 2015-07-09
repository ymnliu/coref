package edu.mayo.bsi.nlp.coref.CrScorer;

public class CorefScore implements Comparable<CorefScore>{
	public double p;
	public double r;
	public double f1;
	
	public CorefScore() {
		p = 0; 
		r = 0;
		f1 = 0;
	}
	
	public CorefScore(double p, double r){
		this.p = p;
		this.r = r;
		this.f1 = 2*r*p / (r+p);
	}

	@Override
	public int compareTo(CorefScore thatScore) {
		if (this.f1 > thatScore.f1) {
			return 1;
		}
		else if (this.f1 < thatScore.f1) {
			return -1;			
		}
				
		return 0;
	}
}
