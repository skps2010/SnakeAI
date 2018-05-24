package skps2010;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.JFrame;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	final int size = 800;
	final int width = 40;
	Queue<Cell> body = new LinkedList<Cell>();
	Cell head;
	Cell food;
	boolean dead;
	/**
	 * 右、下、左、上
	 */
	int face;
	int[] x_ = new int[] { 1, 0, -1, 0 };
	int[] y_ = new int[] { 0, 1, 0, -1 };
	int score = 0;
	int time = 0;
	Random random = new Random();
	boolean again, up, down, left, right;
	AI ai;
	boolean AImode = false;
	int delay = 30;
	int[] foodx = new int[1000];
	int[] foody = new int[1000];
	boolean next = true;
	int bigger = 0;
	Snake AIsnake;

	enum Block {
		EMPTY, BODY, HEAD, FOOD
	};

	Block[][] map = new Block[width][width];

	public static void main(String[] args) {
		Set s=new Set();
		
		while(!s.close) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		
		new Main(s.box.getSelectedIndex() == 1, s.check.isSelected(), s.text.getText());
	}

	public Main(boolean mode, boolean use, String file) {
		for (int i = 0; i < width; i++)
			for (int j = 0; j < width; j++) {
				map[i][j] = Block.EMPTY;
			}

		setTitle("貪吃蛇");
		setSize(size, size + 20);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		AImode = mode;
		if (!use)
			file = null;
		
		if (AImode)
			ai = new AI(this, file);
		else
			AIsnake = AI.snakeFile(this, file);

		for (int i = 0; i < foodx.length; i++) {
			foodx[i] = random.nextInt(width);
			foody[i] = random.nextInt(width);
		}

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				requestFocus();

				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					up = true;
					break;
				case KeyEvent.VK_DOWN:
					down = true;
					break;
				case KeyEvent.VK_LEFT:
					left = true;
					break;
				case KeyEvent.VK_RIGHT:
					right = true;
					break;
				case KeyEvent.VK_ENTER:
					again = true;
					break;
				case KeyEvent.VK_P:
					delay++;
					break;
				case KeyEvent.VK_O:
					if (delay > 0)
						delay--;
					break;
				case KeyEvent.VK_N:
					next = true;
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		while (true)
			set();
	}

	public void set() {
		for (int i = 0; i < width; i++)
			for (int j = 0; j < width; j++) {
				map[i][j] = Block.EMPTY;
			}
		body.clear();
		dead = false;
		face = 3;
		up = false;
		down = false;
		right = false;
		left = false;
		score = 0;
		again = false;
		time = 0;
		spawnFood();

		head = new Cell(width / 2, width / 2, Block.HEAD);
		body.add(new Cell(width / 2, width / 2 + 2, Block.BODY));
		body.add(new Cell(width / 2, width / 2 + 1, Block.BODY));

		while (!dead && !again) {
			run();
			repaint();

			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
		}

		if (AImode)
			ai.spawn();

		while (!again) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

	public void run() {
		time++;

		if (AImode)
			ai.test();
		else if (AIsnake != null)
			AIsnake.move();

		if (face % 2 == 0) {
			if (!up || !down) {
				if (up)
					face = 3;
				if (down)
					face = 1;
			}
		} else {
			if (!left || !right) {
				if (right)
					face = 0;
				if (left)
					face = 2;
			}
		}
		up = false;
		down = false;
		right = false;
		left = false;

		int x = head.x + x_[face];
		int y = head.y + y_[face];

		if (food.x == x && food.y == y) {
			score++;
			if (AImode) {
				ai.aliveTime += 300;
				ai.tester.time = time;
			}
			spawnFood();

			// bigger = 3;
		} else {
			if (bigger == 0) {
				Cell c = body.poll();
				if (map[c.x][c.y] == Block.BODY)
					map[c.x][c.y] = Block.EMPTY;
			} else {
				bigger--;
			}
		}

		body.add(new Cell(head.x, head.y, Block.BODY));

		if (!inMap(x, y)) {
			dead = true;
			return;
		}
		if (map[x][y] == Block.BODY) {
			dead = true;
			return;
		}

		head = new Cell(x, y, Block.HEAD);
		map[food.x][food.y] = Block.FOOD;
	}

	public boolean inMap(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < width;
	}

	public void spawnFood() {

		if (!AImode) {
			int x = random.nextInt(width);
			int y = random.nextInt(width);

			while (map[x][y] != Block.EMPTY || (x == width / 2 && y <= width / 2)) {
				x = random.nextInt(width);
				y = random.nextInt(width);
			}

			food = new Cell(x, y, Block.FOOD);
		}

		else
			food = new Cell(foodx[score], foody[score], Block.FOOD);
	}

	public void paint(Graphics p) {
		Image offscreen = createImage(size, size);
		Graphics2D g = (Graphics2D) offscreen.getGraphics();
		int n = size / width;

		for (int i = 0; i < width; i++)
			for (int j = 0; j < width; j++) {
				switch (map[i][j]) {
				case EMPTY:
					g.setColor(Color.LIGHT_GRAY);
					break;
				case HEAD:
					g.setColor(Color.green);
					break;
				case BODY:
					g.setColor(Color.yellow);
					break;
				case FOOD:
					g.setColor(Color.red);
					break;
				}

				g.fillRect(1 + i * n, 1 + j * n, n - 2, n - 2);
			}

		g.setColor(Color.black);
		g.drawString("time: " + time, 10, 20);
		g.drawString("score: " + score, 10, 40);
		if (AImode && ai != null) {
			g.drawString("gen: " + ai.generation, 10, 60);
			g.drawString("best score: " + ai.bestScore, 10, 80);
			g.drawString("best time: " + ai.bestTime, 10, 100);
		}

		p.drawImage(offscreen, 0, 20, size, size, null);
	}

	class Cell {
		int x, y;

		public Cell(int x, int y, Block b) {
			this.x = x;
			this.y = y;

			map[x][y] = b;
		}
	}
}
