import java.util.random.RandomGenerator;

// This module is purely static. It represents a climate simulation covering weather changes over
// 100 years and relative budget. This module only exposes the actual simulation function taking the
// share M, initial A and a random number generator.
public class Simulation {
	// The days a year has in this simulation.
	private static final int DAYS_PER_YEAR = 90;
	// The temperature of a year given in degree C starts off between the two start values. Every
	// day it changes by up to the step size.
	private static final double START_TEMP_MIN = 25, START_TEMP_MAX = 30, MAX_TEMP_STEP = 0.6;
	// The start value range of water. Each day it changes by up to the step size.
	private static final double START_WATER_MIN = -1, START_WATER_MAX = 1, MAX_WATER_STEP = 0.03;
	// The factor of how much M influences temperature resistance and a divisor to keep reasonable
	// values.
	private static final double TEMP_THRESHOLD_WIDENING = 35, TEMP_COST_DIVISOR = 3600;
	// The factor of how much M influences water reserves and a divisor to keep values reasonable.
	private static final double WATER_THRESHOLD_WIDENING = 1, WATER_COST_DIVISOR = 40;
	// The maximum amount the climate can change from one year to the next.
	private static final double MAX_YEARLY_GROWTH = 0.0035;

	// Calculates the immediate costs arising due to high temperatures. M influences which
	// temperatures can be tolerated.
	private static double temperatureCosts(double tempAdj, double m) {
		double threshold = START_TEMP_MAX + m * TEMP_THRESHOLD_WIDENING;
		return Math.max(0, (tempAdj - threshold) / TEMP_COST_DIVISOR);
	}

	// Calculates the immediate costs arising due to water shortage. M influences how soon water
	// shortages become an issue. Costs can never be negative.
	private static double waterCosts(double waterAdj, double m) {
		double threshold = START_WATER_MIN - m * WATER_THRESHOLD_WIDENING;
		return Math.max(0, (threshold - waterAdj) / WATER_COST_DIVISOR);
	}

	// Calculates the new A from the old one and the average share of money spent on the root cause
	// in the last 10 years.
	// A will never go down to reflect how climate change can not be reverted.
	private static double newA(double oldA, double avgRootCauseShares) {
		double missingShare = 1 - avgRootCauseShares;
		return oldA * (1 + MAX_YEARLY_GROWTH * missingShare * missingShare);
	}

	// Averages a value range given by the combination of a double array, a start index and an end
	// index.
	private static double average(double[] values, int from, int to) {
		double sum = 0;
		for (int i = from; i <= to; i++) sum += values[i];
		return sum / (to - from + 1);
	}

	// Simulates a year with a set share M and climate factor A. The random number generator is
	// given so tests can use a seed for comparability. A simulation starts off with a random value
	// within range for both temperature and water level. Every day at most the respective step size
	// is
	// added or subtracted from the last day's value, creating a random walk.
	// The return value is the share used for immediate measures necessitated by extreme weather
	// events like heat or drought.
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

	// Runs 100 years of simulation, each with the climate factor produced by the last decade. If
	// there is no information about part of the last decade it will take the average of the years
	// it has access to. The results are returned as a Simulationsergebnis.
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
