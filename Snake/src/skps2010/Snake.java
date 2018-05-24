package skps2010;

import java.util.Arrays;

import skps2010.Main.Block;
import skps2010.Neuron.Type;

public class Snake {
	transient Main main;
	final transient int input = 24;
	Neuron[] layer1 = new Neuron[16];
	Neuron[] output = new Neuron[4];
	final transient int[] x_ = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
	final transient int[] y_ = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
	int score, time, generation;

	public Snake(Main m, Snake p, int g) {
		main = m;
		generation = g;

		for (int i = 0; i < layer1.length; i++) {
			layer1[i] = new Neuron(p == null ? null : p.layer1[i], Type.LAYER1);
		}
		for (int i = 0; i < output.length; i++) {
			output[i] = new Neuron(p == null ? null : p.output[i], Type.OUTPUT);
		}
	}

	public Snake(Main m, double[][] l1, double[][] o, int g) {
		main = m;
		generation = g;

		for (int i = 0; i < layer1.length; i++) {
			layer1[i] = new Neuron(l1[i], Type.LAYER1);
		}
		for (int i = 0; i < output.length; i++) {
			output[i] = new Neuron(o[i], Type.OUTPUT);
		}
	}

	public void move() {
		double[] d = new double[input];

		for (int i = 0; i < 8; i++) {

			d[3 * i] = length(x_[i], y_[i], Block.BODY);
			d[3 * i + 1] = length(x_[i], y_[i], Block.EMPTY);
			d[3 * i + 2] = length(x_[i], y_[i], Block.FOOD);
		}

		double[] out = out(d);

		if (out[0] > 0.5)
			main.right = true;
		if (out[1] > 0.5)
			main.down = true;
		if (out[2] > 0.5)
			main.left = true;
		if (out[3] > 0.5)
			main.up = true;
	}

	public double length(int x_, int y_, Block b) {
		int x = main.head.x + x_;
		int y = main.head.y + y_;
		double n = 1;

		while (main.inMap(x, y)) {
			if (b != Block.EMPTY && main.map[x][y] == b)
				return b == Block.FOOD ? 1 : 1 / n;

			x += x_;
			y += y_;
			n++;
		}

		return b == Block.EMPTY ? 1 / n : 0;
	}

	public double[] out(double[] d) {
		if (d.length != input)
			throw new IllegalArgumentException("輸入長度應為: " + input);

		double[] d1 = new double[layer1.length];
		for (int i = 0; i < d1.length; i++)
			d1[i] = layer1[i].out(d);

		double[] d2 = new double[output.length];
		for (int i = 0; i < d2.length; i++)
			d2[i] = output[i].out(d1);

		return d2;
	}

	public String toString() {
		return "score: " + score + "\ntime: " + time + "\nlayer1: " + Arrays.toString(layer1) + "\noutput: "
				+ Arrays.toString(output);
	}
}
