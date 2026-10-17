import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.random.RandomGenerator;

public class Test {
    static void main(String[] args) {
        RandomGenerator rng = RandomGenerator.getDefault();
        System.out.println("TESTS UM DIE FUNKTIONALITAET DES PROGRAMMS ZU ÜBERPRÜFEN\n");
        System.out.println("RANDFALL 1: M=0% & A=1.0");
        String report = randfaelle(1.0, 0, rng);
        System.out.println(report);


        System.out.println();
        System.out.println("RANDFALL 2: M=100% & A=1.7");
        report = randfaelle(1.7, 100, rng);
        System.out.println(report);

        System.out.println();
        System.out.println("RANDFALL 3: M=0% & A=1.7");
        report = randfaelle(1.7, 0, rng);
        System.out.println(report);

        System.out.println();
        System.out.println("88 Simulationslaeufe, der beste wird ausgegeben");


        ArrayList<Simulationsergebnis> simulationsergebnisse = new ArrayList<>();


        HashMap<Double, ArrayList<Simulationsergebnis>> hm = haupt_durchlauf(rng);

        for (List<Simulationsergebnis> l : hm.values()) {
            simulationsergebnisse.addAll(l);
        }

        Simulationsergebnis[] res_array = new Simulationsergebnis[simulationsergebnisse.size()];
        res_array = simulationsergebnisse.toArray(res_array);

        Zielfunktion z = new Zielfunktion(res_array);
        float res = z.calculateBest();

        ArrayList<Simulationsergebnis> best_m = hm.get(res);

        String final_report = "BEST RESULT HAS M = " + res + "\n Here are all the information for each run with M = " + res + ": \n";
        for (Simulationsergebnis s : best_m) {
            System.out.println(s.printReport());
        }

    }

    /**
     * Führt die in der Aufgabe verlangten Randfaelle durch und gibt das Ergebnis als Report zurueck.
     * @param A Anpassungsfaktor
     * @param M Anteil für mittelfristige Maßnahmen
     * @param rng Zufallsgenerator für den Startwert
     * @return Ergebnis der Simulation der Randfälle als Report
     */
    public static String randfaelle(double A, double M, RandomGenerator rng) {
        Simulationsergebnis s = Simulation.simulation(A, M, rng);
        return s.printReport();
    }

    /**
     * Führt die Simulation 88mal durch mit folgenden Regeln:
     * - Werte für M variieren in 10% Schritten von 0-100% (= 0-1)
     * - Werte für A variieren in 0.1 Schritten von 1.0-1.7.
     * Die Ergebnisse werden gespeichert und das beste Ergebnis wird mittels Zielfunktion ausgegeben.
     * @return Das beste Ergebnis.
     */
    public static HashMap<Double, ArrayList<Simulationsergebnis>> haupt_durchlauf(RandomGenerator rng) {
        //ArrayList<Simulationsergebnis> simulationsergebnisse = new ArrayList<>();
        HashMap<Double, ArrayList<Simulationsergebnis>> hm = new HashMap<>();
        ArrayList<Double> a_s = new ArrayList<>();
        ArrayList<Double> m_s = new ArrayList<>();

        for (double i = 1.0 ; i <= 1.7 ; i = i + 0.1) {
            a_s.add(i);
        }

        for (double i = 0.0 ; i <= 1.0 ; i = i + 0.1) {
            m_s.add(i);
        }

        Simulationsergebnis current;

        for (double m : m_s) {
            ArrayList<Simulationsergebnis> simulationsergebnisse = new ArrayList<>();
            for (double a : a_s) {
                current = Simulation.simulation(m, a, rng);
                simulationsergebnisse.add(current);
            }
            hm.put(m, simulationsergebnisse);
        }
        return hm;
    }
}
