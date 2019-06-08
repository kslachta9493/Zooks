import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.geom.*;
public class ZooksBuilder extends JFrame implements ActionListener {
	int[][] grid = {
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0}
	};
	private ZooksBuilderControl zbc = new ZooksBuilderControl(grid);
	private JFileChooser jfc = new JFileChooser();
	private PrintWriter pw;
	private BufferedImage biNugget, biSpike, biGhost, biPortal, biFake, biHeart, biZook, biBlock;
	private Image iNugget, iSpike, iGhost, iPortal, iFake, iHeart, iZook, iBlock;
	public ZooksBuilder() {
		super("ZooksMapBuilder");
		JPanel jp = new JPanel();
		initImages();
		jp.setLayout(new GridLayout(1,4));
		JButton jbOpen = new JButton("Open");
		jbOpen.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					jfc.setCurrentDirectory(new File("C:/cos210/Zooks/maps"));
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					int returnVal = jfc.showOpenDialog(ZooksBuilder.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						System.out.println("OPENED");
						try {
							String filename = jfc.getName(jfc.getSelectedFile());
							System.out.println(filename);
							BufferedReader br = new BufferedReader(new FileReader("maps\\" + filename));
							System.out.println("BUFFERED READER DIDNT FAIL");
							String s = null;
							int ss = 0;
							int row = 0;
							while ((s = br.readLine()) != null) {
								int[] ia = new int[(ss = s.length())];
								for (int i = 0; i < ss - 3; i++) {
									ia[i] = Integer.parseInt(s.substring(i, i + 1));
									grid[row][i] = ia[i];
									System.out.print(ia[1]);
									zbc.repaint();
						
								}
								System.out.println();
								row++;
								if (row == 15) {
									break;
								}
							}
						br.close();
						} catch (Exception e) {
							System.out.println("FAILED");
						}
					}
				}
			}
		);
		JButton jb = new JButton("Reset");
		jb.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					for (int i = 0; i < grid.length; i++) {
						for (int j = 0; j < grid[0].length; j++) {
							grid[i][j] = 0;
						}
					}
					zbc.repaint();
				}
			}
		);
		jp.add(jbOpen);
		jp.add(jb);
		jb = new JButton("Save As...");
		jb.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					jfc.setCurrentDirectory(new File("C:/cos210/Zooks/maps"));
 					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.setDialogTitle("Save As");
					int returnVal = jfc.showSaveDialog(ZooksBuilder.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
                				System.out.println("Saved " + jfc.getSelectedFile());
						String filename = jfc.getSelectedFile().getName();
						try {
							if (filename.endsWith(".map")) {
								pw = new PrintWriter(new File(filename));
							} else {	
								pw = new PrintWriter(new File(filename + ".map"));
							}
						} catch (Exception e) {
						}
            				} else {
            				}
					try {
						for (int i = 0; i < grid.length; i++) {
							pw.print("1");
							for (int j = 0; j < grid[0].length; j++) {
								pw.print(grid[i][j]);
							}
							pw.print("1");
							pw.println();
						}
						pw.print("111111111111111111111111111111111111111111111111111111111");
						pw.flush();
						pw.close();
					} catch (Exception e) {
						System.err.println(e.getMessage());
						System.exit(-1);
					}
				}
			}
		);
		jp.add(jb);
		jb = new JButton("Help");
		jb.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
				String message = "WELCOME TO ZOOKS MAP BUILDER! \n"
      				+ "Black = Solid Blocks \n"
				+ "Orange = THE ZOOK (Player)\n"
				+ "YELLOW = Coins(Gives Points) \n" 
				+ "GREY = Fake Blocks(Does 50 Damage on contact) \n"
				+ "RED = Hearts (Gives 10 Health on contact) \n"
				+ "GREEN = Ghosts (Kills player on contact) \n"
				+ "BLUE = Spikes (Does 10 Damage on contact) \n"
				+ "CYAN = UNUSED (USING THIS WILL JUST MAKE AIR) \n"
				+ "MAGENTA = TARGET (ONLY ONE PER LEVEL OR WILL CRASH)";
			JFrame jfHelp = new JFrame("Zooks Map Builder Help");
			jfHelp.setVisible(true);
			jfHelp.setSize(400,600);
			jfHelp.setLocationRelativeTo(null);
			Container content = jfHelp.getContentPane();
			StyleContext context = new StyleContext();
   			StyledDocument document = new DefaultStyledDocument(context);
    			Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
    			StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
    			StyleConstants.setFontSize(style, 14);
    			StyleConstants.setSpaceAbove(style, 4);
    			StyleConstants.setSpaceBelow(style, 4);
    			try {
      				document.insertString(document.getLength(), message, style);
    			} catch (Exception ex) {
     				System.err.println("Oops");
    			}
 			JTextPane textPane = new JTextPane(document);
    			textPane.setEditable(false);
    			JScrollPane scrollPane = new JScrollPane(textPane);
    			content.add(scrollPane, BorderLayout.CENTER);
				}
			}
		);
		jp.add(jb);
		getContentPane().add(jp, BorderLayout.SOUTH);
		getContentPane().add(zbc, BorderLayout.CENTER);
		setSize(800, 575);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	public static void main(String[] sa) {
		new ZooksBuilder();
	}
	private void initImages() {
		try {
			biNugget = ImageIO.read(new File("images\\Nugget.b.gif"));
			biFake = ImageIO.read(new File("images\\Fake.0.gif"));
			biHeart = ImageIO.read(new File("images\\Heart.0.gif"));
			biZook = ImageIO.read(new File("images\\Zook2.gif"));
			biSpike = ImageIO.read(new File("images\\Spike.0.gif"));
			biPortal = ImageIO.read(new File("images\\Portal.0.gif"));
			biGhost = ImageIO.read(new File("images\\Ghost.0.gif"));
			biBlock = ImageIO.read(new File("images\\Zooks.0.gif"));
		} catch (Exception e) {
		}
	}
	public void actionPerformed(ActionEvent ae) {
	}
	private class ZooksBuilderControl extends JPanel implements MouseListener {
		private static final long serialVersionUID = 1L;
		private int[][] grid;
		private int textX = 8;
		private int gridX = 15;
		private int topY = 32;
		private int xSize = 15;
		private int ySize = 30;
		public ZooksBuilderControl(int[][] grid) {
			super();
			this.grid = grid;
			addMouseListener(this);
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[0].length; j++) {
					Rectangle r = new Rectangle(xSize, ySize);
					r.translate(j * xSize, topY + (i) * ySize);
					g2d.setPaint(Color.BLACK);
					g2d.draw(r);
					int x = j * xSize;
					int y = topY + i * ySize;
					if (x <= 0) {
						x = 1;
					}
					if (y <= 0) {
						y = 1;
					}
					AffineTransform af = new AffineTransform();
					af.setToTranslation(x, y);
					iNugget = biNugget.getScaledInstance(xSize, ySize, 0);
					iGhost = biGhost.getScaledInstance(xSize, ySize, 0);
					iBlock = biBlock.getScaledInstance(xSize, ySize, 0);
					iFake = biFake.getScaledInstance(xSize, ySize, 0);
					iZook = biZook.getScaledInstance(xSize, ySize, 0);
					iPortal = biPortal.getScaledInstance(xSize, ySize, 0);
					iHeart = biHeart.getScaledInstance(xSize, ySize, 0);
					iSpike = biSpike.getScaledInstance(xSize, ySize, 0);
					if (grid[i][j] != 0) {
						if (grid[i][j] == 1) {
							//g2d.setPaint(Color.BLACK);
							g2d.drawImage(iBlock, af, null);
						}
						if (grid[i][j] == 2) {
						//	g2d.setPaint(Color.ORANGE);
							g2d.drawImage(iZook, af, null);
						}
						if (grid[i][j] == 3) {
						//	g2d.setPaint(Color.YELLOW);
							g2d.drawImage(iNugget, af, null);
						}
						if (grid[i][j] == 4) {
						//	g2d.setPaint(Color.GRAY);
							g2d.drawImage(iFake, af, null);
						}
						if (grid[i][j] == 5) {
						//	g2d.setPaint(Color.RED);
							g2d.drawImage(iHeart, af, null);
						}
						if (grid[i][j] == 6) {
						//	g2d.setPaint(Color.GREEN);
							g2d.drawImage(iGhost, af, null);
						}
						if (grid[i][j] == 7) {
						//	g2d.setPaint(Color.BLUE);
							g2d.drawImage(iSpike, af, null);
						}
						if (grid[i][j] == 8) {
						//	g2d.setPaint(Color.CYAN);
							g2d.drawImage(iBlock, af, null);
						}
						if (grid[i][j] == 9) {
						//	g2d.setPaint(Color.MAGENTA);
							g2d.drawImage(iPortal, af, null);
						}
					}
				}
			}
		}
		public void mouseClicked(MouseEvent me) {
		}
		public void mouseEntered(MouseEvent me) {
		}
		public void mouseExited(MouseEvent me) {
		}
		public void mousePressed(MouseEvent me) {
		}
		public void mouseReleased(MouseEvent me) {
			int x = me.getX();
			int y = me.getY();
			int c;
			if (x > gridX) {
				c = 1 + ((x - gridX) / xSize);
			} else {
				c = 0;
			}
			int r = 0;
			System.out.println(x + " : " + c);
			if (y > topY) {
				r = (y - topY) / ySize + 2;
			} else {
				r = 0;
			}
			r = r - 2;
			System.out.println(r);
			if (c >= 0 && c < 54 && r >= 0 && r < 15) {
				try {
					grid[r][c] = grid[r][c] + 1;
					if (grid[r][c] == 10) {
						grid[r][c] = 0;
					}
				} catch (Exception e) {
				}
			}
			repaint();
		}
	}
}