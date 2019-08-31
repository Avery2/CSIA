import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class Point {
	double x, y;

	Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
}

class Line {
	Point a, b;
	double ax, ay, bx, by;

	Line(Point a, Point b) {
		this.a = a;
		this.b = b;
	}

	Line(double ax, double ay, double ba, double by) {
		this.ax = ax;
		this.ay = ay;
		this.bx = ba;
		this.by = by;
	}

	void syncPt() {
		a.x = ax;
		a.y = ay;
		b.x = bx;
		b.y = by;
	}

	void initPt() {
		a = new Point(ax, ay);
		b = new Point(bx, by);
	}

	void syncCoord() {
		ax = a.x;
		ay = a.y;
		bx = b.x;
		by = b.y;
	}

	public double length() {
		return Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
	}
}

public class FlatWorldRun {

	private JFrame frame;
	
	private static double dir = Math.PI / 2;
	private static boolean showTwoDimensions = true;
	private static boolean showMiniMap = true;
	private static boolean showOrientation = true;
	final static double VIEW_WIDTH = .4;
	final static double VIEW_SEPERATION = .01;
	final static int CANVAS_X = 720;
	final static int CANVAS_Y = 720;
	final static double PEN_RADIUS = 0.005;

	public static void main(String[] args) throws IOException {
		StdDraw.setCanvasSize(CANVAS_X, CANVAS_Y);
		StdDraw.setPenRadius(PEN_RADIUS);
		StdDraw.enableDoubleBuffering();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FlatWorldRun window = new FlatWorldRun();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		double cirX = .5, cirY = .5;

		String fileName = "./data.bin";
		File file = new File(fileName);
		FileInputStream reader = new FileInputStream(file);
		FileOutputStream writer = new FileOutputStream(file, true);

		int readByte = reader.read();
		if (readByte == -1) {
			writer.write(128);
			writer.write(128);
		} else {
			cirX = (double) readByte / 255;
			cirY = (double) reader.read() / 255;
		}

		reader.close();
		writer.close();

		Point circleCenter = new Point(cirX, cirY);

		ArrayList<Line> lineArr = new ArrayList<Line>();
		ArrayList<Line> squareArr = new ArrayList<Line>();
		final int NUM_OBJ_LINES = 3;
		final int NUM_OBJ_SQUARES = 1;
		double squareHalfLength, squareX, squareY;
		
		// add obstacle squares to ArrayList
		for (int i = 0; i < NUM_OBJ_SQUARES; i++) {
			squareHalfLength = Math.random() / 4;
			squareX = Math.random();
			squareY = Math.random();
			squareArr.add(new Line(squareX - squareHalfLength, squareY + squareHalfLength, squareX + squareHalfLength, squareY + squareHalfLength));
			squareArr.add(new Line(squareX + squareHalfLength, squareY + squareHalfLength, squareX + squareHalfLength, squareY - squareHalfLength));
			squareArr.add(new Line(squareX + squareHalfLength, squareY - squareHalfLength, squareX - squareHalfLength, squareY - squareHalfLength));
			squareArr.add(new Line(squareX - squareHalfLength, squareY - squareHalfLength, squareX - squareHalfLength, squareY + squareHalfLength));
			squareArr.forEach(x -> {
				x.initPt();
			});
		}

		// add obstacle lines to ArrayList
		for (int i = 0; i < NUM_OBJ_LINES; i++) {
			// lines are (hard coded) limited to center-ish area
			lineArr.add(new Line(new Point((double) (Math.random() / 2 + .25), (double) (Math.random() / 2 + .25)),
					new Point((double) (Math.random() / 2 + .25), (double) (Math.random() / 2 + .25))));
		}
		
//		System.out.println("Circle:");
//		System.out.println("( "+cirX+", "+cirY+" ");
//		
//		System.out.println("Lines:");
//		for (int i = 0; i < lineArr.size(); i++) {
//			StdDraw.line(lineArr.get(i).a.x, lineArr.get(i).a.y, lineArr.get(i).b.x, lineArr.get(i).b.y);
//			System.out.println("Line "+i+": ( "+lineArr.get(i).a.x+", "+lineArr.get(i).a.x+", "+lineArr.get(i).b.x+", "+lineArr.get(i).b.x+" )");
//		}
//		
//		System.out.println("Square:");
//		squareArr.forEach(x -> {
//			StdDraw.line(x.ax, x.ay, x.bx, x.by);
//			System.out.println("Line square: ( "+x.ax+", "+x.ay+", "+x.bx+", "+x.by+" )");
//		});

		while (true) {

			StdDraw.clear();

			if (showTwoDimensions) {
				drawLines(lineArr);
				drawSquares(squareArr);
			}
			drawCircle(circleCenter, fileName);

			for (double i = -VIEW_WIDTH; i < VIEW_WIDTH; i += VIEW_SEPERATION) {
				cirView(circleCenter, i, lineArr, squareArr);
			}

			if (showMiniMap)
				drawMap(circleCenter);

			if (showOrientation)
				StdDraw.line(.1, .1, .1 + Math.cos(dir) / 15, .1 + Math.sin(dir) / 15);

			StdDraw.show();
			StdDraw.pause(60);
		}
	}

	public FlatWorldRun() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(720, 0, 350, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lb2dTitle = new JLabel("2D Graphics");
		lb2dTitle.setBounds(50, 25, 100, 20);
		frame.getContentPane().add(lb2dTitle);

		JLabel lbMapTitle = new JLabel("Mini Map");
		lbMapTitle.setBounds(150, 25, 100, 20);
		frame.getContentPane().add(lbMapTitle);

		JLabel lbOrientTitle = new JLabel("Orientation");
		lbOrientTitle.setBounds(250, 25, 100, 20);
		frame.getContentPane().add(lbOrientTitle);

		JRadioButton show2dT = new JRadioButton("True");
		JRadioButton show2dF = new JRadioButton("False");
		JRadioButton showMapT = new JRadioButton("True");
		JRadioButton showMapF = new JRadioButton("False");
		JRadioButton showOrientT = new JRadioButton("True");
		JRadioButton showOrientF = new JRadioButton("False");

		show2dT.setSelected(true);
		showMapT.setSelected(true);
		showOrientT.setSelected(true);

		show2dT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTwoDimensions = true;
				show2dF.setSelected(false);
			}
		});
		show2dT.setBounds(50, 75, 80, 20);
		frame.getContentPane().add(show2dT);

		show2dF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTwoDimensions = false;
				show2dT.setSelected(false);
			}
		});
		show2dF.setBounds(50, 125, 80, 20);
		frame.getContentPane().add(show2dF);

		showMapT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMiniMap = true;
				showMapF.setSelected(false);
			}
		});
		showMapT.setBounds(150, 75, 80, 20);
		frame.getContentPane().add(showMapT);

		showMapF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMiniMap = false;
				showMapT.setSelected(false);
			}
		});
		showMapF.setBounds(150, 125, 80, 20);
		frame.getContentPane().add(showMapF);

		showOrientT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showOrientation = true;
				showOrientF.setSelected(false);
			}
		});
		showOrientT.setBounds(250, 75, 80, 20);
		frame.getContentPane().add(showOrientT);

		showOrientF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showOrientation = false;
				showOrientT.setSelected(false);
			}
		});
		showOrientF.setBounds(250, 125, 80, 20);
		frame.getContentPane().add(showOrientF);
	}

	public static void cirView(Point p, double dirMod, ArrayList<Line> arr1, ArrayList<Line> arr2) {

		Point p1 = new Point(p.x + Math.cos(dir + dirMod) / 2, p.y + Math.sin(dir + dirMod) / 2);
		Point p2 = new Point(p.x + Math.cos(dir + dirMod) / 10, p.y + Math.sin(dir + dirMod) / 10);

		// Draw actual viewlines
		StdDraw.line(p.x + Math.cos(dir+dirMod) / 2, p.y + Math.sin(dir+dirMod) / 2, p.x + Math.cos(dir+dirMod) / 10, p.y + Math.sin(dir+dirMod) / 10);

		double distance = .4, closest = 10;
		Boolean see = false;
		final double viewLineSize = .5;

		for (int i = 0; i < arr1.size(); i++) {
			if (!see)
				see = doIntersect(p1, p2, arr1.get(i).a, arr1.get(i).b);

			if (doIntersect(p1, p2, arr1.get(i).a, arr1.get(i).b)) {
				distance = distBetween(p2, getIntersection(p1, p2, arr1.get(i).a, arr1.get(i).b));
				if (distance < closest) {
					closest = distance;
				}
			}
		}

		for (int i = 0; i < arr2.size(); i++) {
			if (!see)
				see = doIntersect(p1, p2, arr2.get(i).a, arr2.get(i).b);

			if (doIntersect(p1, p2, arr2.get(i).a, arr2.get(i).b)) {
				distance = distBetween(p2, getIntersection(p1, p2, arr2.get(i).a, arr2.get(i).b));
				if (distance < closest) {
					closest = distance;
				}
			}
		}

		if (see) {
			StdDraw.setPenColor((int) (Math.abs(closest / viewLineSize * 255)),
					(int) (Math.abs(closest / viewLineSize * 255)), (int) (Math.abs(closest / viewLineSize * 255)));
		} else {
			StdDraw.setPenColor(255, 255, 255);
		}
		StdDraw.filledSquare(.5 - dirMod, .9, (VIEW_SEPERATION / 2) + .0005);
		StdDraw.setPenColor();
	}

	public static void drawCircle(Point c, String fileName) throws IOException {

		if (StdDraw.isKeyPressed(87)) // W
			c.y += 0.01;
		if (StdDraw.isKeyPressed(65)) // A
			c.x -= 0.01;
		if (StdDraw.isKeyPressed(83)) // S
			c.y -= 0.01;
		if (StdDraw.isKeyPressed(68)) // D
			c.x += 0.01;
		if (StdDraw.isKeyPressed(69)) // E
			dir -= Math.PI / 50;
		if (StdDraw.isKeyPressed(81)) // Q
			dir += Math.PI / 50;

		if (dir <= 0)
			dir += Math.PI * 2;

		if (showTwoDimensions) {
			StdDraw.line(c.x + Math.cos(dir + .4) / 2, c.y + Math.sin(dir + .4) / 2, c.x + Math.cos(dir + .4) / 10,
					c.y + Math.sin(dir + .4) / 10);
			StdDraw.line(c.x + Math.cos(dir - .4) / 2, c.y + Math.sin(dir - .4) / 2, c.x + Math.cos(dir - .4) / 10,
					c.y + Math.sin(dir - .4) / 10);
			StdDraw.arc(c.x, c.y, .5, ((dir - .4) * 180 / Math.PI), ((dir + .4) * 180 / Math.PI));

			StdDraw.circle(c.x, c.y, .1);
		}

		FileOutputStream clear = new FileOutputStream(new File(fileName), false);
		clear.write((int) ((c.x) * 255));
		clear.write((int) ((c.y) * 255));
		clear.close();

	}

	public static void drawLines(ArrayList<Line> arr) {
		for (int i = 0; i < arr.size(); i++) {
			StdDraw.line(arr.get(i).a.x, arr.get(i).a.y, arr.get(i).b.x, arr.get(i).b.y);
		}
	}

	public static void drawSquares(ArrayList<Line> arr) {
		arr.forEach(x -> {
			StdDraw.line(x.ax, x.ay, x.bx, x.by);
		});
	}

	public static void drawMap(Point c) {
		StdDraw.square(.85, .15, .101);
		StdDraw.setPenColor(255, 255, 255);
		StdDraw.filledSquare(.85, .15, .1);
		StdDraw.setPenColor();
		if ((c.x > 0 && c.x < 1) && (c.y > 0 && c.y < 1)) {
			StdDraw.point(c.x / 5 + .75, c.y / 5 + .05);
		}
	}

	public static double distBetween(Point p1, Point p2) {
		return Math.sqrt(Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2));
	}

	public static Point getIntersection(Point p0, Point p1, Point p2, Point p3) {
		double s1_x, s1_y, s2_x, s2_y;
		s1_x = p1.x - p0.x;
		s1_y = p1.y - p0.y;
		s2_x = p3.x - p2.x;
		s2_y = p3.y - p2.y;

		double t, intersectX, intersectY;
		t = (s2_x * (p0.y - p2.y) - s2_y * (p0.x - p2.x)) / (-s2_x * s1_y + s1_x * s2_y);

		intersectX = p0.x + (t * s1_x);
		intersectY = p0.y + (t * s1_y);

		// Circle intersections
		StdDraw.setPenColor(Color.blue);
		StdDraw.circle(intersectX, intersectY, .01);
		StdDraw.setPenColor();

		return new Point(intersectX, intersectY);
	}

	public static Point getIntersection(Line a, Line b) {
		double s1_x, s1_y, s2_x, s2_y;
		s1_x = a.b.x - a.a.x;
		s1_y = a.b.y - a.a.y;

		s2_x = b.b.x - b.a.x;
		s2_y = b.b.y - b.a.y;

		double t, intersectX, intersectY;
		t = (s2_x * (a.a.y - b.a.y) - s2_y * (a.a.x - b.a.x)) / (-s2_x * s1_y + s1_x * s2_y);

		intersectX = a.a.x + (t * s1_x);
		intersectY = a.a.y + (t * s1_y);

		// Circle intersections
//		StdDraw.circle(intersectX, intersectY, .01);
		return new Point(intersectX, intersectY);
	}

	public static double max(double x, double y) {
		if (x > y)
			return x;
		if (y > x)
			return y;
		return 0;
	}

	public static double min(double x, double y) {
		if (x < y)
			return x;
		if (y < x)
			return y;
		return 0;
	}

	public static Boolean onSegment(Point p, Point q, Point r) {
		if (q.x <= max(p.x, r.x) && q.x >= min(p.x, r.x) && q.y <= max(p.y, r.y) && q.y >= min(p.y, r.y))
			return true;
		return false;
	}

	public static int orientation(Point p, Point q, Point r) {
		double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

		if (val == 0)
			return 0;

		return (val > 0) ? 1 : 2;
	}

	public static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) {
		int o1 = orientation(p1, q1, p2);
		int o2 = orientation(p1, q1, q2);
		int o3 = orientation(p2, q2, p1);
		int o4 = orientation(p2, q2, q1);

		if (o1 != o2 && o3 != o4)
			return true;
		if (o1 == 0 && onSegment(p1, p2, q1))
			return true;
		if (o2 == 0 && onSegment(p1, q2, q1))
			return true;
		if (o3 == 0 && onSegment(p2, p1, q2))
			return true;
		if (o4 == 0 && onSegment(p2, q1, q2))
			return true;
		return false;
	}

	public static boolean doIntersect(Line a, Line b) {
		int o1 = orientation(a.a, a.b, b.a);
		int o2 = orientation(a.a, a.b, b.b);
		int o3 = orientation(b.a, b.b, a.a);
		int o4 = orientation(b.a, b.b, a.b);

		if (o1 != o2 && o3 != o4)
			return true;
		if (o1 == 0 && onSegment(a.a, b.a, a.b))
			return true;
		if (o2 == 0 && onSegment(a.a, b.b, a.b))
			return true;
		if (o3 == 0 && onSegment(b.a, a.a, b.b))
			return true;
		if (o4 == 0 && onSegment(b.a, a.b, b.b))
			return true;
		return false;
	}
}