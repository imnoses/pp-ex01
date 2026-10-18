/**
 * Es wird aus allen Simulationsergebnissen anhand von berechnung das overall beste M ermittelt
 */
public class Zielfunktion {
    // die 88 Simulationsergebnisse
    private Simulationsergebnis[] results;

    // Konstruktor für die Zielfunktionsklasse mit results array als parameter
    public Zielfunktion(Simulationsergebnis[] results) {
        this.results = results;
    }

    // setter Methode
    public void setResults(Simulationsergebnis[] results) {
        this.results = results;
    }

    /**
     * das kernstück der klasse; berechnet aus allen 88 Simulationsergbnissen den besten M wert
     * @return bestes M
     */
    public double calculateBest() {
        // bestes M errechnen
        // für jedes kriterium ein ranking und dann über alle rankings ein ranking

        // arrays für die 4 krieterien über alle 11 M werte
        double[] budgetUber = new double[11];
        double[] sofortM = new double[11];
        double[] gleichmaessigkeit = new double[11];
        double[] endA = new double[11];

        // es gibt 11 verschiedene Werte für M -> alle durchgehen
        for (int mIndex = 0; mIndex < 11; mIndex++) {
            int start = mIndex * 8;

            // summen je M Wert
            double budgetSum = 0;
            double avgTenYearSum = 0;
            double aSum = 0;
            double abweichungenSum = 0;

            // 8 verschiede Anfangswerte für A -> alle durchggehen
            for (int aIndex = 0; aIndex < 8; aIndex++) {
                Simulationsergebnis r = results[start + aIndex];

                // per Simulation, bis zu 100 Uberschreitungen -> alle durchgehen & summieren
                for (double value : r.getUeberschritten()) {
                    budgetSum += value;
                }

                // durschnittswerte summieren
                for (double tenY : r.getAvgTenYears()) {
                    avgTenYearSum += tenY;
                }

                // Endwerte von A (nach 100 Jahren)
                aSum += r.getA();

                // abweichungen
                double[] tenYears = r.getAvgTenYears();

                // arithmetisches mittel von den 10 Zeitblöcken
                double avg = 0;
                for (double value : tenYears)
                    avg += value;

                // länge ist sonst 10
                avg /= tenYears.length;

                for (double value : tenYears) {
                    // abweichungen sollen positiv sein
                    abweichungenSum += Math.abs(value - avg);
                }
            }

            // durch 8 für die 8 Simulationen pro M
            budgetUber[mIndex] = budgetSum / 8.0;
            sofortM[mIndex] = avgTenYearSum / (8 * 10); // * 10 für die Zeitblöcke
            endA[mIndex] = aSum / 8.0;
            gleichmaessigkeit[mIndex] = abweichungenSum / (8 * 10); // * 10 für die Zeitblöcke
        }

        // jede kategorie Ranken
        int[] budgetRank = rank(budgetUber);
        int[] sofortRank = rank(sofortM);
        int[] gleichRank = rank(gleichmaessigkeit);
        int[] aRank = rank(endA);


        // biherigen besten Score
        double bestOverall = Double.MAX_VALUE; // große zahl -> erster score muss kleiner sein!
        int bestMIndex = 0; // aktuell bestes M

        for (int mIndex = 0; mIndex < 11; mIndex++) {
            // 4 Kriterien -> 4 Ränge
            // durchschnitt der 4

            // Gewichten von a, damit es mehr Einfluss hat
            double a_gewichtet = 5 * aRank[mIndex];

            double overall = (budgetRank[mIndex] + sofortRank[mIndex]
                    + gleichRank[mIndex] + a_gewichtet) / 4.0;

            double strafe = 0;

            // Wenn es es bei diesem M Bugtetüberschreigungen gab...
            if (budgetUber[mIndex] > 0) {
                strafe = 100 + budgetRank[mIndex]*5;
                overall=overall+strafe;
            }

            // den besten Wert speichern
            if (overall < bestOverall) {
                bestOverall = overall;
                bestMIndex = mIndex;
            }
        }

        return bestMIndex / 10.0;

    }

    /**
     * Hilfsmethode zum Ranken der Kriterien
     * @param values alle werte
     * @return geranted array
     */
    private int[] rank(double[] values) {
        int[] ranks = new int[values.length]; // ranking array
        // alle werte durchgehen
        for (int i = 0; i < values.length; i++) {
            ranks[i] = 1;
            // mit allen anderen werten vergleichen
            for (int j = 0; j < values.length; j++)
                if (values[j] < values[i]) {
                    ranks[i]++; // pro wert der kleiner ist rank erhöhen
                }
        }
        return ranks;
    }

}