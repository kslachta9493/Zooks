import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.sound.midi.*;
import javax.swing.*;
public class Zooks extends JFrame {
	private int life = 100;
	private int masterCount = 0;
	private int level = 0;
	private int[][] world = null;
	private int zX = 0;
	private int zY = 0;
	private int look = 1;
	private int startX = 0;
	private int startY = 0;
	private int numXBlocks;
	private int numYBlocks;
	private BufferedImage bi = null;
	private BufferedImage biZook = null;
	private BufferedImage biNugget = null;
	private BufferedImage biPortal = null;
	private BufferedImage biSpike = null;
	private BufferedImage biGhost = null;
	private BufferedImage biFake = null;
	private int fakeMod = 1;
	private int nuggetMod = 1;
	private int portalMod = 1;
	private int spikeMod = 1;
	private int ghostMod = 1;
	private int healthMod = 1;
	private boolean showPortal = false;
	private ArrayList nuggetList = new ArrayList();
	private ArrayList spikeList = new ArrayList();
	private ArrayList fakeList = new ArrayList();
	private ArrayList ghostList = new ArrayList();
	private ArrayList healthList = new ArrayList();
	private ArrayList ghostLoc = new ArrayList();
	private Point portalPoint = null;
	private Point ghostPoint = null;
	private int bWidth = 0;
	private int bHeight = 0;
	private int counter = 0;
	private boolean forward = true;
	private boolean ghostVisible = false;
	private AffineTransform at = null;
	private boolean falling = false;
	private static final int META_EndofTrack = 47;
	private Sequencer sequencer = null;
	private Sequence sequence = null;
	private Dimension screenSize = null;
	private Action newGame, saveGame, loadGame;
	private String currentGame;
	private int lifesLeft = 3;
	private JMenuItem jmiSave, jmiLoad, jmiNew;
	private JMenuBar jmb;
	public Zooks() throws Exception {
		super("Zooks");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLevel(0);
		bi = initBackBuffer();
		initSpikeImage();
		initZookImage();
		initNuggetImage();
		initPortalImage();
		initGhostImage();
		initHeartImage();
		newGame = new newGameAction("  New Game", "Create a new Game!", new ImageIcon("images/new.png"));
		saveGame = new saveGameAction( "  Save Game", "Save your game!", new ImageIcon("images/new.png"));
		loadGame = new loadGameAction( "  Load Game", "Load your saved game!", new ImageIcon("images/new.png"));
		setJMenuBar(createJMenuBar());
		MyJPanel mjp = new MyJPanel();
		getContentPane().add(mjp);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		pack();
		setLocation(
			screenSize.width / 2 - getWidth() / 2,
			screenSize.height / 2 - getHeight() / 2 - 16
		);
		initSequencer();
		setResizable(true);
		setVisible(true);
		mjp.requestFocusInWindow();
	}
	public static void main(String[] sa) throws Exception {
		new Zooks();
	}
	private JMenuBar createJMenuBar() {
		jmb = new JMenuBar();
		JMenu jmFile = new JMenu(" File ");
		JMenu jmHelp = new JMenu(" Help ");
		JMenu jmPlay = new JMenu(" Zooks Builder ");
		jmiNew = new JMenuItem(newGame);
		jmiLoad = new JMenuItem(loadGame);
		jmiSave = new JMenuItem(saveGame);
		jmFile.add(jmiNew);
		jmFile.add(jmiSave);
		jmFile.add(jmiLoad);
		jmb.add(jmFile);
		return jmb;
	}
	protected void setLevel(int level) throws Exception {
		initWorld("maps\\Zooks." + level + ".map");
		this.level = level;
	}
	protected void initWorld(String map) throws Exception {
		boolean foundZook = false;
		ArrayList al = new ArrayList();
		BufferedReader br = new BufferedReader(new FileReader(map));
		String s = null;
		int ss = 0;
		int row = 0;
		while ((s = br.readLine()) != null) {
			int[] ia = new int[(ss = s.length())];
			for (int i = 0; i < ss; i++) {
				ia[i] = Integer.parseInt(s.substring(i, i + 1));
				if (ia[i] == 2) {
					zX = i;
					zY = row;
					ia[i] = 0;
					foundZook = true;
				}
				if (ia[i] == 3) {
					nuggetList.add(new Point(i, row));
					ia[i] = 0;
				}
				if (ia[i] == 4) {
					fakeList.add(new Point(i, row));
					System.out.print("Fake Found");
				//	ia[i] = 0;
				}
				if (ia[i] == 5) {
					healthList.add(new Point(i, row));
					ia[i] = 0;
				}
				if (ia[i] == 6) {
					ghostList.add(new Point(i, row));
					ghostLoc.add(0);
					ghostVisible = true;
					ia[i] = 0;
				}
				if (ia[i] == 7) {
					spikeList.add(new Point(i, row));
					ia[i] = 0;
				}
				if (ia[i] == 9) {
					portalPoint = new Point(i, row);
					ia[i] = 0;
				}
			}
			al.add(ia);
			row++;
		}
		br.close();
		if (!foundZook) {
			zX = ss / 2;
			zY = 0;
		}
		flipWorld((int[][]) al.toArray((Object[]) new int[al.size()][]));
	}
	private void flipWorld(int[][] naa) {
		world = new int[naa.length][];
		for (int i = 0; i < naa.length; i++) {
			world[naa.length - i - 1] = naa[i];
		}
		zY = world.length - zY - 1;
		for (int i = 0; i < nuggetList.size(); i++) {
			Point p = (Point) nuggetList.get(i);
			nuggetList.set(i, new Point(p.x, world.length - p.y - 1));
		}
		for (int i = 0; i < spikeList.size(); i++) {
			Point p = (Point) spikeList.get(i);
			spikeList.set(i, new Point(p.x, world.length - p.y - 1));
		}
		for (int i = 0; i < fakeList.size(); i++) {
			Point p = (Point) fakeList.get(i);
			fakeList.set(i, new Point(p.x, world.length - p.y - 1));
		}
		for (int i = 0; i < healthList.size(); i++) {
			Point p = (Point) healthList.get(i);
			healthList.set(i, new Point(p.x, world.length - p.y - 1));
		}
		for (int i = 0; i < ghostList.size(); i++) {
			Point p = (Point) ghostList.get(i);
			ghostList.set(i, new Point(p.x, world.length - p.y - 1));
		}
		portalPoint = new Point(portalPoint.x, world.length - portalPoint.y - 1);
	}
	protected BufferedImage initBackBuffer() throws Exception {
		BufferedImage block = ImageIO.read(new File("images\\Zooks." + level + ".gif"));
		BufferedImage blockFake = ImageIO.read(new File("images\\fake." + level + ".gif"));
		bWidth = block.getWidth();
		bHeight = block.getHeight();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		int width = bWidth * world[0].length;
		int height = bHeight * world.length;
    		BufferedImage bi = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		for (int y = 0; y < world.length; y++) {
			for (int x = 0; x < world[0].length; x++) {
				if (world[y][x] == 1) {
					g2d.drawImage(block, x * bWidth, y * bHeight, this);
				} else if (world[y][x] == 4) {
					System.out.println("FAKEWORLDFAKEWORLD");
					g2d.drawImage(blockFake, x * bWidth, y * bHeight, this);
				}
			}
		}
		at = new AffineTransform();
		at.scale(1.0, -1.0);
		at.translate(0.0, -height);
    		return bi;
	}
	protected void initZookImage() throws Exception {
		biZook = ImageIO.read(new File("images\\Zook2.gif"));
	}
	protected void initSpikeImage() throws Exception {
		biSpike = ImageIO.read(new File("images\\Spike." + level + ".gif"));
		spikeMod = biSpike.getWidth() / bWidth;
	}
	protected void initNuggetImage() throws Exception {
		biNugget = ImageIO.read(new File("images\\Nugget." + level + ".gif"));
		nuggetMod = biNugget.getWidth() / bWidth;
	}
	protected void initPortalImage() throws Exception {
		biPortal = ImageIO.read(new File("images\\Portal." + level + ".gif"));
		portalMod = biPortal.getWidth() / bWidth;
		showPortal = false;
	}
	protected void initGhostImage() throws Exception {
		try {
		biGhost = ImageIO.read(new File("images\\Ghost." + level + ".gif"));
		ghostMod = biGhost.getWidth() / bWidth;
		} catch (Exception e) {
		}
	}
	private void initHeartImage() throws Exception {
		try {
			biFake = ImageIO.read(new File("images\\Heart." + level + ".gif"));
			fakeMod = biGhost.getWidth() / bWidth;
		} catch (Exception e) {
		}
	}
	private void initSequencer() throws Exception {
		try {
			if (sequencer != null) {
				if (sequencer.isOpen()) {
					if (sequencer.isRunning()) {
						sequencer.stop();
					}
					sequencer.close();
				}
			} else {
				sequencer = MidiSystem.getSequencer();
				sequencer.addMetaEventListener(new MyMetaEventListener());
			}
			sequencer.open();
			sequence = MidiSystem.getSequence(new File("sound\\Zooks." + level + ".mid"));
			sequencer.setSequence(sequence);
			sequencer.start();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	private class MyJPanel extends JPanel {
		private Point p = new Point();
		public MyJPanel() {
			setLayout(null);
			setBackground(new Color(128, 128, 255));
			MyKeyListener mkl = new MyKeyListener();
			addKeyListener(mkl);
			new javax.swing.Timer(100, new MyActionListener(mkl)).start();
			setFocusable(true);
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			numXBlocks = getWidth() / bWidth;
			numYBlocks = getHeight() / bHeight;
			startX = computeStartX(numXBlocks);
			startY = computeStartY(numYBlocks);
			int dx = (zX - startX) * bWidth;
			int dy = (startY - zY) * bHeight;
			int sx = bWidth * look;
			AffineTransform atx = new AffineTransform();
			atx.translate(-startX * bWidth, -(world.length - startY - 1) * bHeight);
			atx.concatenate(at);
			g2d.drawImage(bi, atx, this);
			for (int i = 0; i < nuggetList.size(); i++) {
				//System.out.println("Nugget " + i);
				Point p = (Point) nuggetList.get(i);
				int ndx = (p.x - startX) * bWidth;
				int ndy = (startY - p.y) * bHeight;
				int nsx = bWidth * (masterCount % nuggetMod);
				g2d.drawImage(
					biNugget,
					ndx,									// dx1
					ndy,									// dy1
					ndx + bWidth,							// dx2
					ndy + bWidth,							// dy2
					nsx,									// sx1
					0,										// sy1
					nsx + bWidth,							// sx2
					bWidth,									// sy2
					this
				);
				//System.out.println(i + ":" + ndx + ":" + ndy + ":" + nsx);
			}
			for (int i = 0; i < spikeList.size(); i++) {
				//System.out.println("Spike " + i);
				Point p = (Point) spikeList.get(i);
				int sdx = (p.x - startX) * bWidth;
				int sdy = (startY - p.y) * bHeight;
				int ssx = bWidth * (masterCount % spikeMod);
				g2d.drawImage(
					biSpike,
					sdx,
					sdy,
					sdx + bWidth,
					sdy + bWidth,
					ssx,
					0,
					ssx + bWidth,
					bWidth,
					this
				);
			}
			if (showPortal) {
				int pdx = (portalPoint.x - startX) * bWidth;
				int pdy = (startY - portalPoint.y) * bHeight;
				int psx = bWidth * (masterCount % portalMod);
				g2d.drawImage(
					biPortal,
					pdx,									// dx1
					pdy,									// dy1
					pdx + bWidth,							// dx2
					pdy + bWidth,							// dy2
					psx,									// sx1
					0,										// sy1
					psx + bWidth,							// sx2
					bWidth,									// sy2
					this
				);
			}
			if (ghostVisible) {
				for (int i = 0; i < ghostList.size(); i++) {
					Point p = (Point) ghostList.get(i);
					int gdx = (p.x - startX) * bWidth;
					int gdy = (startY - p.y) * bHeight;
					int gsx = bWidth * (masterCount % ghostMod);
					int holderX;
					int holderY;
					int direction = (int) ghostLoc.get(i);
					g2d.drawImage(
						biGhost,
						gdx,
						gdy,
						gdx + bWidth,
						gdy + bWidth,
						gsx,
						0,
						gsx + bWidth,
						bWidth,
						this
					);
					if (direction == 1) {
						holderX = p.x - 1;
						holderY = p.y;
						if (p.x <= 0) {
							direction = 0;
						}
					} else {
						holderX = p.x + 1;
						holderY = p.y;
						if (p.x >= 55) {
							direction = 1;
						}
					}
					ghostLoc.set(i, direction);
					Point hp = new Point(holderX, holderY);
					ghostList.set(i, hp);
					//ghostList.get(i).setLocation(holderX, holderY);
				}
			}
			g2d.drawImage(
				biZook,
				dx,											// dx1
				dy,											// dy1
				dx + bWidth,								// dx2
				dy + bWidth,								// dy2
				sx,											// sx1
				0,											// sy1
				sx + bWidth,								// sx2
				bWidth,										// sy2
				this
			);
			for (int i = 0; i < healthList.size(); i++) {
				Point p = (Point) healthList.get(i);
				int hdx = (p.x - startX) * bWidth;
				int hdy = (startY - p.y) * bHeight;
				int hsx = bWidth * (masterCount % spikeMod);
				g2d.drawImage(
					biFake,
					hdx,
					hdy,
					hdx + bWidth,
					hdy + bWidth,
					hsx,
					0,
					hsx + bWidth,
					bWidth,
					this
				);
			}
			g2d.setFont(getFont().deriveFont(32.0f));
			g2d.setPaint(Color.BLACK);
			g2d.drawString(
				"Level: " + level,
				600,
				25
			);
			g2d.setFont(getFont().deriveFont(64.0f));
			g2d.setPaint(Color.BLUE);
			g2d.drawString(
				new Integer(counter).toString(),
				50,
				50
			);
			g2d.setPaint(Color.RED);
			g2d.drawString(
				new Integer(life).toString(),
				1250,
				50
			);
		}
		public Dimension getPreferredSize() {
			return new Dimension(
				(screenSize.width / bWidth - 1) * bWidth,
				(screenSize.height / bHeight - 1) * bHeight
			);
		}
	}
	private class MyActionListener implements ActionListener {
		MyKeyListener mkl;
		public MyActionListener(MyKeyListener mkl) {
			super();
			this.mkl = mkl;
		}
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() instanceof javax.swing.Timer) {
				look = 1;
				if (zY >= 1 && world[zY - 1][zX] == 0 && falling) {
					zY--;
				} else {
					falling = false;
				}
				if (mkl.isRightPressed()) {
					look = 2;
					if (world[zY][zX + 1] == 0) {
						zX++;
					}
				}
				if (mkl.isLeftPressed()) {
					look = 0;
					if (world[zY][zX - 1] == 0) {
						zX--;
					}
				}
				if (zY >= 1 && world[zY - 1][zX] == 0) {
					falling = true;
				}
				if (mkl.isUpPressed()) {
					if (!falling) {
						zY += howHigh(4);
						falling = true;
					}
				}
				for (int i = 0; i < nuggetList.size(); i++) {
					Point p = (Point) nuggetList.get(i);
					if (p.x == zX && p.y == zY) {
						nuggetList.remove(i);
						counter++;
					}
				}
				for (int i = 0; i < spikeList.size(); i++) {
					Point p = (Point) spikeList.get(i);
					if (p.x == zX && p.y == zY) {
						spikeList.remove(i);
						life = life - 10;
						if (life <= 0) {
							life = 0;
							lifesLeft--;
							resetSame();
						}
					}
				}
				for (int i = 0; i < fakeList.size(); i++) {
					Point p = (Point) fakeList.get(i);
					int holder = p.y + 1;
					if (p.x == zX && holder == zY) {
						life = life - 50;
						fakeList.remove(i);
						if (life <= 0) {
							life = 0;
							lifesLeft--;
							resetSame();
						}
					}
				}
				for (int i = 0; i < healthList.size(); i++) {
					Point p = (Point) healthList.get(i);
					if (p.x == zX && p.y == zY) {
						healthList.remove(i);
						life = life + 10;
					}
				}
				if (nuggetList.size() == 0) {
					showPortal = true;
				}
				masterCount++;
				for (int i = 0; i < ghostList.size(); i++) {
					Point p = (Point) ghostList.get(i);
					if (ghostVisible && zX == p.x && zY == p.y) {
						lifesLeft--;
						resetSame();
					}
				}
				if (showPortal && zX == portalPoint.x && zY == portalPoint.y) {
					int x = zX;
					int y = zY;
					reset();
					try {
						boolean levelSet = false;
						while (!levelSet) {
							try {
								setLevel(level + 1);
								levelSet = true;
							} catch (Exception e) {
								level = -1;
							}
						}
						bi = initBackBuffer();
						initSpikeImage();
						initZookImage();
						initNuggetImage();
						initPortalImage();
						initSequencer();
						initGhostImage();
						falling = true;
						zX = x;
						zY = y;

					} catch (Exception e) {
						System.err.println(e);
						System.exit(-1);
					}
				}
				repaint();
			}
		}
		private int computeStartX(int numXBlocks) {
			int half = numXBlocks / 2;
			if (zX < half) {
				return 0;
			}
			if (zX >= world[0].length - half) {
				return world[0].length - numXBlocks;
			}
			return (zX - (numXBlocks / 2));
		}
		private int computeStartY(int numYBlocks) {
			int half = numYBlocks / 2;
			if (zY < half) {
				return numYBlocks - 1;
			}
			if (zY >= world.length - half) {
				return world.length - 1;
			}
			if (!falling) {
				return (zY + half);
			} else {
				return startY;
			}
		}
		private int howHigh(int wouldLike) {
			int gonnaGet = wouldLike;
			if (zY + wouldLike >= world.length) {
				gonnaGet = world.length - zY - 1;
				if (gonnaGet < 0) {
					gonnaGet = 0;
				}
			}
			for (int i = zY; i <= zY + gonnaGet; i++) {
				if (world[i][zX] != 0) {
					gonnaGet = i - zY - 1;
					break;
				}
			}
			return gonnaGet;
		}
	}
	private class MyKeyListener extends KeyAdapter {
		public boolean left = false;
		public boolean right = false;
		public boolean up = false;
		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_UP) {
				up = true;
			}
			if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
				left = true;
			} else {
				if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
					right = true;
				}
			}
			if (ke.getKeyCode() == KeyEvent.VK_N) {
				try {
					level++;
					reset();
					setLevel(level);
					bi = initBackBuffer();
				} catch (Exception ex) {
				}
			}
		}
		public void keyReleased(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
				left = false;
			} else {
				if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
					right = false;
				} else {

				}
			}
			if (ke.getKeyCode() == KeyEvent.VK_UP) {
				up = false;
			}
		}
		public boolean isLeftPressed() {
			boolean b = left;
			return b;
		}
		public boolean isRightPressed() {
			boolean b = right;
			return b;
		}
		public boolean isUpPressed() {
			boolean b = up;
			up = false;
			return b;
		}
	}
	private class MyMetaEventListener implements MetaEventListener {
		public void meta(MetaMessage message) {
			if (message.getType() == META_EndofTrack) {
				if (sequencer.isOpen()) {
					sequencer.start();
				}
			}
		}
	}
	private void reset() {
		spikeList.clear();
		nuggetList.clear();
		healthList.clear();
		fakeList.clear();
		ghostList.clear();
		life = 100;
		if (lifesLeft == 0 && counter != 0) {
			lifesLeft = 3;
			counter = counter - 5;
		}
		if (counter <= 0) {
			counter = 0;
		}
		if(lifesLeft == 0 && counter == 0) {
			JPopupMenu jpm = new JPopupMenu("GAME OVER");
			jpm.add("YOU LOSE SUCKA");
			System.exit(-1);
		}
		showPortal = false;
		ghostVisible = false;
	}
	private void resetSame() {
		reset();
		try {
			setLevel(level);
			bi = initBackBuffer();
			initSpikeImage();
			initGhostImage();
			initNuggetImage();
			initZookImage();
			initSequencer();
			initPortalImage();
			falling = true;
			zX = computeStartX(numXBlocks);
			zY = computeStartY(numYBlocks);
		} catch (Exception e) {
		}
	}
	private int computeStartX(int numXBlocks) {
		int half = numXBlocks / 2;
		if (zX < half) {
			return 0;
		}
		if (zX >= world[0].length - half) {
			return world[0].length - numXBlocks;
		}
		return (zX - (numXBlocks / 2));
	}
	private int computeStartY(int numYBlocks) {
		int half = numYBlocks / 2;
		if (zY < half) {
			return numYBlocks - 1;
		}
		if (zY >= world.length - half) {
			return world.length - 1;
		}
		if (!falling) {
			return (zY + half);
		} else {
			return startY;
		}
	}
	private class newGameAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public newGameAction (String name, String desc, ImageIcon icon) {
			super(name);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(NAME, name);
			putValue(SMALL_ICON, icon);
		}
		public void actionPerformed(ActionEvent e) {
			try {
				currentGame = JOptionPane.showInputDialog("Please enter a name for this game");
				reset();
				setLevel(1);
				bi = initBackBuffer();
			} catch (Exception ex) {
				System.out.println("New Game FAILED");
			}
		}
	}
	private class saveGameAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public saveGameAction (String name, String desc, ImageIcon icon) {
			super(name);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(NAME, name);
			putValue(SMALL_ICON, icon);
		}
		public void actionPerformed(ActionEvent e) {
			try {
				PrintWriter pw;
				if (!currentGame.endsWith(".bsp")) {
					pw = new PrintWriter("saves\\" + currentGame + ".bsp");
				} else {
					pw = new PrintWriter("saves\\" + currentGame);
				}
				pw.println(level);
				pw.println(counter);
				pw.println(lifesLeft);
				pw.flush();
				pw.close();
			} catch (Exception ex) {
				System.out.println("Save Game FAILED");
			}
		}
	}
	private class loadGameAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public loadGameAction (String name, String desc, ImageIcon icon) {
			super(name);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(NAME, name);
			putValue(SMALL_ICON, icon);
		}
		public void actionPerformed(ActionEvent e) {
			try {
				String s, filename;
				JFileChooser jfc = new JFileChooser();
				int i;
				jfc.setCurrentDirectory(new File("C:/cos210/Zooks/saves"));
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = jfc.showOpenDialog(Zooks.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
                			filename = jfc.getName(jfc.getSelectedFile());
					currentGame = filename;
					System.out.println(filename);
					BufferedReader br = new BufferedReader(new FileReader("saves\\" + filename));
					i = 0;
					while ((s = br.readLine()) != null) {
						if (i == 0) {
							level = Integer.parseInt(s);
						} else if (i == 1) {
							counter = Integer.parseInt(s);
						} else if (i == 2) {
							lifesLeft = Integer.parseInt(s);
						}
						i++;
					}
            			} else {

            			}
				reset();
				setLevel(level);
				bi = initBackBuffer();
			} catch (Exception ex) {
				System.out.println("Load Game FAILED");
			}
		}
	}
}
