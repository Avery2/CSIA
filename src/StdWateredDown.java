import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class StdWateredDown {
	
    private static Color penColor;
    private static final int DEFAULT_SIZE = 512;
    private static int width  = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;
    private static BufferedImage offscreenImage, onscreenImage;
    private static Graphics2D offscreen, onscreen;
    private static JFrame frame;
	
	private static void init() {
        if (frame != null) frame.setVisible(false);
        frame = new JFrame();
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen  = onscreenImage.createGraphics();
        offscreen.setColor(Color.WHITE);
        offscreen.fillRect(0, 0, width, height);
        clear();
        ImageIcon icon = new ImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);
        frame.setContentPane(draw);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void clear() {
        offscreen.setColor(Color.RED);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);
        draw();
    }

    private static void draw() {
    	onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }
    
    public static void main(String[] args) {
    	
    	/*
		Runnable doGui = new Runnable() {
			
			@Override
			public void run() {
				
				JFrame frame;
				
				frame = new JFrame();
				frame.setBounds(50, 00, 350, 200);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setLayout(null);
				
				frame.setVisible(true);
				
			}
			
		};
		
//		Thread creation (for no reason really)
		Thread t1 = new Thread(doGui);
		t1.start();
		*/
    	
    	init();
    }
    
}
