public class Zielfunktion {
    public Simulationsergebnis[] results;

    public Zielfunktion(Simulationsergebnis[] results) {
        this.results = results;
    }
    public Zielfunktion() {};

    public void setResults(Simulationsergebnis[] results) {
        this.results = results;
    }

    public double calculateBest() {
        // bestes M errechnen
        // für jedes kriterium ein ranking und dann über alle rankings ein ranking

        return 0;
    }
}