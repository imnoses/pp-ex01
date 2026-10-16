public class Simulationsergebnis {
	private float A;
	private float[] avgTenYears;
	private float[] ueberschritten; //fuer jedes Jahr um wv prozent überscritten wurde

	public Simulationsergebnis(float A, float[] avgTenYears, float[] ueberschritten) {
		this.A = A;
		this.avgTenYears = avgTenYears;
		this.ueberschritten = ueberschritten;
	}

		
	public String printReport() {
		int i = 0; 
		String out = "A nach 100 Jahren: " + A + "\n";
		for (float val : avgTenYears) {
			int i += 1; 
			out += i + " - ";
			i+= 9;
			out += " Jahre: " + val +"\n";
		}
		for (int i = 0; i < ueberschritten.length; i++) {
		{
			if(ueberschritten[i] > 0) out += "Jahr " + i + " : um " + ueberschritten[i] + "ueberschritten\n";


		}
			
	}



}

	public float getA() {
		return A;
	}

	public float[] getAvgTenYears() {
		return avgTenYears;
	}

	public float[] getUeberschritten() {
		return ueberschritten;
	}