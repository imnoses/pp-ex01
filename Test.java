import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.random.RandomGenerator;

public class Test {
    public static void main(String[] args) {
        RandomGenerator rng = RandomGenerator.getDefault();         // Zufallsgenerator

        System.out.println("TESTS UM DIE FUNKTIONALITAET DES PROGRAMMS ZU ÜBERPRÜFEN\n");

        // RANDFALL 1
        System.out.println("RANDFALL 1: M=0% & A=1.0");
        String report = randfaelle(1.0, 0, rng);
        System.out.println(report);

        // RANDFALL 2
        System.out.println();
        System.out.println("RANDFALL 2: M=100% & A=1.7");
        report = randfaelle(1.7, 1, rng);
        System.out.println(report);

        // RANDFAL 3
        System.out.println();
        System.out.println("RANDFALL 3: M=0% & A=1.7");
        report = randfaelle(1.7, 0, rng);
        System.out.println(report);


        // 88 SIMULATIONSLAEUFE
        System.out.println();
        System.out.println("88 Simulationslaeufe, der beste wird ausgegeben");

        // Hashmap speichert pro M alle Simulationsergebnisse
        // Wichtig dass die geordnet ist (=LINKED), weil das Zielfunktion.java voraussetzt
        LinkedHashMap<Double, ArrayList<Simulationsergebnis>> hm = haupt_durchlauf(rng);

        // Arraylist mit allen Simulationsergebnissen
        ArrayList<Simulationsergebnis> simulationsergebnisse = new ArrayList<>();
        for (List<Simulationsergebnis> l : hm.values()) {
            simulationsergebnisse.addAll(l);
        }

        // ArrayList to array für die Zielfunktion
        Simulationsergebnis[] res_array = new Simulationsergebnis[simulationsergebnisse.size()];
        res_array = simulationsergebnisse.toArray(res_array);
        Zielfunktion z = new Zielfunktion(res_array);
        double res = z.calculateBest();

        // Arraylist mit allen Simulationsergebnissen für das beste M
        ArrayList<Simulationsergebnis> best_m = hm.get(res);

//        String final_report = "BESTES ERGEBNIS HAT M = " + res + "\n Hier sind alle Ergebnisse mit M = " + res + ": \n";
//        for (Simulationsergebnis s : best_m) {
//            System.out.println(s.printReport());
//        }
        System.out.println(final_report(best_m, res));

    }

    /**
     * Führt die in der Aufgabe verlangten Randfaelle durch und gibt das Ergebnis als Report zurueck.
     * @param A Anpassungsfaktor
     * @param M Anteil für mittelfristige Maßnahmen
     * @param rng Zufallsgenerator für den Startwert
     * @return Ergebnis der Simulation der Randfälle als Report
     */
    public static String randfaelle(double A, double M, RandomGenerator rng) {
        Simulationsergebnis s = Simulation.simulation(M, A, rng);
        return s.printReport();
    }

    /**
     * Führt die Simulation 88mal durch mit folgenden Regeln:
     * - Werte für M variieren in 10% Schritten von 0-100% (= 0-1)
     * - Werte für A variieren in 0.1 Schritten von 1.0-1.7.
     * Die Ergebnisse werden gespeichert und das beste Ergebnis wird mittels Zielfunktion ausgegeben.
     * @return Das beste Ergebnis.
     */
    public static LinkedHashMap<Double, ArrayList<Simulationsergebnis>> haupt_durchlauf(RandomGenerator rng) {
        //ArrayList<Simulationsergebnis> simulationsergebnisse = new ArrayList<>();
        LinkedHashMap<Double, ArrayList<Simulationsergebnis>> hm = new LinkedHashMap<>();
        ArrayList<Double> a_s = new ArrayList<>();
        ArrayList<Double> m_s = new ArrayList<>();

        // Rundungsfehler, wenn man es mit double macht...
//        for (double i = 1.0 ; i <= 1.7 ; i = i + 0.1) {
//            a_s.add(i);
//        }
//
//        for (double i = 0.0 ; i <= 1.0 ; i = i + 0.1) {
//            m_s.add(i);
//        }

        //Stattdessen mit int:
        for (int i = 10 ; i <= 17 ; i++) {
            a_s.add(i / 10.0);
        }

        for (int i = 0 ; i <= 10 ; i++) {
            m_s.add(i / 10.0);
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

    /**
     * Generiert den Report für die Simulationsergebnisse mit dem besten m
     * @param ergebnisse Alle Simulationsergebnisse des besten Ms
     * @return Report als String
     */
    public static String final_report(ArrayList<Simulationsergebnis> ergebnisse, double best_m) {
        double minA = Double.POSITIVE_INFINITY;
        double maxA = Double.NEGATIVE_INFINITY;

        StringBuilder sb = new StringBuilder();

        sb.append("ERGEBNISSE FÜR DAS BESTE M: ").append(best_m).append("\n");

        for (Simulationsergebnis s : ergebnisse) {
            if (s.getA() < minA) minA = s.getA();
            if (s.getA() > maxA) maxA = s.getA();
        }

        sb.append("A nach 100 Jahren ").append(minA).append("-").append(maxA).append("\n\n");

        sb.append("Avg10Years: \n");
        for (int i = 0 ; i < 10 ; i++) {
            double minAvg10Years = Double.POSITIVE_INFINITY;
            double maxAvg10Years = Double.NEGATIVE_INFINITY;
            for (Simulationsergebnis s : ergebnisse) {
                double current = s.getAvgTenYears()[i];
                if (current < minAvg10Years) minAvg10Years = current;
                if (current > maxAvg10Years) maxAvg10Years = current;
            }

            // Beispiel i=3:
            // 31-40
            int anfang = i * 10 + 1;
            int ende = (i+1)*10;

            sb.append("Jahre ").append(anfang).append("-").append(ende).append(": ").append(minAvg10Years).append(" - ").append(maxAvg10Years).append("\n");
        }

        sb.append("\n Bugtetüberschreitungen: \n");
        boolean uebAufgetreten = false;

        for (int jahr = 0 ; jahr < 100 ; jahr++) {
            double minUeberschreitung = Double.POSITIVE_INFINITY;
            double maxUeberschreitung = Double.NEGATIVE_INFINITY;
            boolean jahrBetroffen = false;

            for (Simulationsergebnis s : ergebnisse) {
                double current = s.getUeberschritten()[jahr];
                if (current > 0) {
                    jahrBetroffen = true;
                    uebAufgetreten = true;
                }
                if (current < minUeberschreitung) minUeberschreitung = current;
                if (current > maxUeberschreitung) maxUeberschreitung = current;
            }

            if (jahrBetroffen) {
                sb.append("Für Jahr ").append(jahr+1).append(" hat es Überschreitungen von ").append(minUeberschreitung).append(" - ").append(maxUeberschreitung).append(" gegeben! \n");
            }

        }

        if (!uebAufgetreten) {
            sb.append("Keine Bugtetüberschreitungen aufgetreten!");
        }


        return sb.toString();
    }
}
