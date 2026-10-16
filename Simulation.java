import java.util.random.RandomGenerator;

public class Simulation {
	private static final int DAYS_PER_YEAR = 90;
	private static final double START_TEMP_MIN = 25, START_TEMP_MAX = 30, MAX_TEMP_STEP = 0.6;
	private static final double START_WATER_MIN = -1, START_WATER_MAX = 1, MAX_WATER_STEP = 0.03;
	private static final double TEMP_THRESHOLD_WIDENING = 35, TEMP_COST_DIVISOR = 3600;
	private static final double WATER_THRESHOLD_WIDENING = 1, WATER_COST_DIVISOR = 40;
	private static final double MAX_YEARLY_GROWTH = 0.0035;

	private static double temperatureCosts(double tempAdj, double m) {
		double threshold = START_TEMP_MAX + m * TEMP_THRESHOLD_WIDENING;
		return Math.max(0, (tempAdj - threshold) / TEMP_COST_DIVISOR);
	}

	private static double waterCosts(double waterAdj, double m) {
		double threshold = START_WATER_MIN - m * WATER_THRESHOLD_WIDENING;
		return Math.max(0, (threshold - waterAdj) / WATER_COST_DIVISOR);
	}

	private static double newA(double oldA, double avgRootCauseShares) {
		double missingShare = 1 - avgRootCauseShares;
		return oldA * (1 + MAX_YEARLY_GROWTH * missingShare * missingShare);
	}

	private static double average(double[] values, int from, int to) {
		double sum = 0;
		for (int i = from; i <= to; i++) sum += values[i];
		return sum / (to - from + 1);
	}

	private static double simulateYear(double M, double A, RandomGenerator rng) {
		double temp = rng.nextDouble(START_TEMP_MIN, START_TEMP_MAX);
		double water = rng.nextDouble(START_WATER_MIN, START_WATER_MAX);
		double immediateCostSum = 0;

		for (int day = 0; day < DAYS_PER_YEAR; day++) {
			temp += rng.nextDouble(-MAX_TEMP_STEP, MAX_TEMP_STEP);
			water += rng.nextDouble(-MAX_WATER_STEP, MAX_WATER_STEP);

			immediateCostSum += temperatureCosts(temp * A, M);
			immediateCostSum += waterCosts(water * A, M);
		}
		return immediateCostSum;
	}

	public static Simulationsergebnis simulation(double M, double A, RandomGenerator rng) {
		double[] immediateCosts = new double[100];
		double[] rootCauseShare = new double[100];
		double[] ueberschritten = new double[100];
		for (int i = 0; i < 100; i++) {
			immediateCosts[i] = simulateYear(M, A, rng);
			rootCauseShare[i] = Math.max(0, 1 - M - immediateCosts[i]);
			ueberschritten[i] = Math.max(0, M + immediateCosts[i] - 1);
			A = newA(A, average(rootCauseShare, Math.max(0, i - 9), i));
		}
		double[] avgTenYears = new double[10];
		for (int i = 0; i < 10; i++) avgTenYears[i] = average(immediateCosts, i * 10, (i * 10) + 9);

		return new Simulationsergebnis(A, avgTenYears, ueberschritten);
	}
}
