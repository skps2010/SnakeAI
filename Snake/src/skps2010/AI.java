package skps2010;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AI {
	Main main;
	int generation = 0;
	ArrayList<Snake> snakes = new ArrayList<Snake>();
	final int amount = 10000;
	Snake tester;
	int id = amount - 1;
	int bestScore = 0;
	int bestTime = 0;
	int aliveTime = 0;
	Snake winner;
	String file;
	Snake startSnake;

	public AI(Main m, String file) {
		main = m;
		startSnake = snakeFile(m, file);

		spawn();
	}

	public static Snake snakeFile(Main main, String file) {
		try {
			if (file != null) {
				BufferedReader in = new BufferedReader(new FileReader(file));
				double[][] l1 = new double[16][24];
				double[][] o = new double[4][16];

				while (true) {
					if (in.readLine().contains("layer1"))
						break;
				}

				for (int i = 0; i < l1.length; i++) {
					String s = in.readLine();
					String[] d = s.replace("chance: [", "").replace("], ", "").replace("]]", "").split(", ");

					for (int j = 0; j < d.length; j++) {
						l1[i][j] = Double.parseDouble(d[j]);
					}
				}
				in.readLine();

				for (int i = 0; i < o.length; i++) {
					String s = in.readLine();
					String[] d = s.replace("chance: [", "").replace("], ", "").replace("]]", "").split(", ");

					for (int j = 0; j < d.length; j++) {
						o[i][j] = Double.parseDouble(d[j]);
					}
				}

				in.close();
				return new Snake(main, l1, o, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void spawn() {
		id++;
		if (tester != null) {
			tester.score = main.score;
		}

		if (id >= amount) {
			newGeneration();
			id = 0;
		}

		if (id == 0 && main.next) {
			main.delay = 10;
			main.next = false;
		} else
			main.delay = 0;
		main.again = true;
		aliveTime = 500;
		tester = snakes.get(id);
	}

	public void newGeneration() {
		generation++;

		/*
		 * for (int i = 0; i < main.foodx.length; i++) { main.foodx[i] =
		 * main.random.nextInt(main.width); main.foody[i] =
		 * main.random.nextInt(main.width); }
		 */

		if (snakes.isEmpty()) {
			if (startSnake != null) {
				snakes.add(startSnake);

				for (int i = 1; i < amount; i++) {
					snakes.add(new Snake(main, startSnake, generation));
				}
			} else {
				for (int i = 0; i < amount; i++) {
					snakes.add(new Snake(main, null, generation));
				}
			}
		} else {
			snakes.sort((Snake s1, Snake s2) -> compare(s1, s2));

			for (int i = 0; i < amount * 99 / 100; i++) {
				snakes.remove(snakes.size() - 1);
			}

			for (int i = 0; i < amount * 99 / 100; i++) {
				snakes.add(new Snake(main, snakes.get(i / 99), generation));
			}

			bestScore = snakes.get(0).score;
			bestTime = snakes.get(0).time;

			if (snakes.get(0) != winner) {
				winner = snakes.get(0);

				try {
					export();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			/*
			 * if (generation - winner.generation > 30) { main.foodx[winner.score] =
			 * main.random.nextInt(main.width); main.foody[winner.score] =
			 * main.random.nextInt(main.width); }
			 */

		}
	}

	public void test() {
		tester.move();

		if (main.time > aliveTime)
			main.dead = true;
	}

	public static int compare(Snake s1, Snake s2) {
		return s2.score * 120 - s2.time - (s1.score * 120 - s1.time);
	}

	public void export() throws IOException {
		String folder = "./AI/";
		File f = new File("./AI");
		if (!f.exists())
			f.mkdirs();

		if (file == null) {
			int i = 0;
			while (true) {
				if (!new File(folder + i + ".txt").exists())
					break;
				i++;
			}
			file = folder + i + ".txt";
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(file, true));

		out.newLine();
		out.write("generation: " + generation);
		out.newLine();
		out.write(winner.toString());

		out.close();
	}
}
