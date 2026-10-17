public class Simulationsergebnis {
    private double A;
    private double[] avgTenYears;
    private double[] ueberschritten; //fuer jedes Jahr um wv prozent überscritten wurde

    public Simulationsergebnis(double A, double[] avgTenYears, double[] ueberschritten) {
        this.A = A;
        this.avgTenYears = avgTenYears;
        this.ueberschritten = ueberschritten;
    }


    public String printReport() {
        int i = 0;
        String out = "A nach 100 Jahren: " + A + "\n";
        for (double val : avgTenYears) {
            out += (i + 1) + " - " + (i + 10) + " Jahre: " + val + "\n";
            i += 10;
        }
        for (i = 0; i < ueberschritten.length; i++) {
            if (ueberschritten[i] > 0)
                out += "Jahr " + (i + 1) + ": um " + ueberschritten[i] + " ueberschritten\n";
        }
        return out;
    }

    public double getA() {
        return A;
    }

    public double[] getAvgTenYears() {
        return avgTenYears;
    }

    public double[] getUeberschritten() {
        return ueberschritten;
    }


}

