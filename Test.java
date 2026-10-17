import java.util.random.RandomGenerator;

public class Test {
    static void main() {
        System.out.println("TESTS UM DIE FUNKTIONALITAET DES PROGRAMMS ZU ÜBERPRÜFEN + \n");
        System.out.println("RANDFALL 1: M=0% & A=1.0");

        System.out.println();
        System.out.println("RANDFALL 2: M=100% & A=1.7");

        System.out.println();
        System.out.println("RANDFALL 3: M=0% & A=1.7");

    }

    /**
     * Führt die in der Aufgabe verlangten Randfaelle durch und gibt das Ergebnis als Report zurueck.
     * @param A Anpassungsfaktor
     * @param M Anteil für mittelfristige Maßnahmen
     * @param rng Zufallsgenerator für den Startwert
     * @return Ergebnis der Simulation der Randfälle als Report
     */
    public String randfaelle(double A, double M, RandomGenerator rng) {
        Simulationsergebnis s = Simulation.simulation(A, M, rng);
        return s.printReport();
    }

    public void haupt_durchlauf() {

    }
}
