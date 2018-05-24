package skps2010;

import java.util.Arrays;
import java.util.Random;

public class Neuron {
	enum Type {
		LAYER1, OUTPUT
	}

	transient Type type;
	double[] chance;
	transient Random r = new Random();

	public Neuron(Neuron n, Type t) {
		type = t;

		switch (t) {
		case LAYER1:
			chance = new double[24];
			break;
		case OUTPUT:
			chance = new double[16];
			break;
		}

		for (int i = 0; i < chance.length; i++) {
			chance[i] = n == null ? r.nextDouble() * 10 - 5 : (n.chance[i] * 9 + r.nextDouble() * 10 - 5) / 10;
		}
	}

	public Neuron(double[] d, Type t) {
		type = t;
		chance = d;
	}

	public double out(double[] d) throws IllegalArgumentException {
		if (d.length != chance.length)
			throw new IllegalArgumentException("陣列長度不一");

		double sum = 0;
		for (int i = 0; i < chance.length; i++) {
			sum += d[i] * chance[i];
		}
		return 1 / (1 + Math.pow(Math.E, -sum));
	}

	public String toString() {
		return "\nchance: " + Arrays.toString(chance);
	}
}
