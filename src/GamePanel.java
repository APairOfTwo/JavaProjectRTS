import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class GamePanel extends Canvas implements Runnable
{
private static final int PWIDTH = 800;
private static final int PHEIGHT = 500;
private static final int NUM_AGENTS = 50;

private Thread animator;
private boolean running = false;
private boolean gameOver = false; 

int FPS,SFPS;
int fpscount;

public static Random rnd = new Random();

BufferedImage imagemcharsets;

boolean LEFT, RIGHT,UP,DOWN;

public static int mousex,mousey; 

public static ArrayList<Agente> listadeagentes = new ArrayList<Agente>();
public ArrayList<Integer> selectedIndexes = new ArrayList<Integer>(); 

public static Mapa_Grid mapa;

double posx,posy;

MeuAgente meuHeroi = null;

public int mousePressedX = 0, mousePressedY = 0;
public int mouseReleasedX = 0, mouseReleasedY = 0;
public int mouseDraggedX = 0, mouseDraggedY = 0;

public Rectangle mouseRect;
public boolean drawMouseRect = false;


public static int tempo = 0;
public static int nodosabertos = 0;

public GamePanel()
{

	setBackground(Color.white);
	setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

	// create game components
	setFocusable(true);

	requestFocus(); // JPanel now receives key events	
	
	
	// Adiciona um Key Listner
	addKeyListener( new KeyAdapter() {
		public void keyPressed(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = true;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = true;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = true;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = true;
				}	
			}
		@Override
			public void keyReleased(KeyEvent e ) {
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = false;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = false;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = false;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = false;
				}
			}
	});
	
	addMouseMotionListener(new MouseMotionListener() {
		
		@Override
		public void mouseMoved(MouseEvent e) {
			mousex = e.getX(); 
			mousey = e.getY();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			mouseDraggedX = e.getX()+mapa.MapX;
			mouseDraggedY = e.getY()+mapa.MapY;
		}
	});
	
	addMouseListener(new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getButton() == 3) {
				drawMouseRect = false;
				mouseReleasedX = e.getX()+mapa.MapX;
				mouseReleasedY = e.getY()+mapa.MapY;
				mouseRect = new Rectangle(mousePressedX, mousePressedY, mouseReleasedX-mousePressedX, mouseReleasedY-mousePressedY);
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getButton() == 3) {
				mousePressedX = e.getX()+mapa.MapX;
				mousePressedY = e.getY()+mapa.MapY;
				selectedIndexes.clear();
				drawMouseRect = true;
			}
			if(e.getButton() == 1) {
				int mx = (e.getX()+mapa.MapX)/16;
				int my = (e.getY()+mapa.MapY)/16;
				
				for(int i = 0; i < selectedIndexes.size(); i++){
					MeuAgente agente = ((MeuAgente)listadeagentes.get(selectedIndexes.get(i)));
					agente.objetivox = mx;
					agente.objetivoy = my;
					agente.setouobjetivo = true;
				}
			}
		}
		
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	});
	
	
	try {
		imagemcharsets = ImageIO.read( getClass().getResource("Chara1.png") );
	}
	catch(IOException e) {
		System.out.println("Load Image error:");
	}	
	
//	for(int i = 0; i < 20; i++){
//		Color cor = Color.black;
//		
//		switch (rnd.nextInt(4)) {
//		case 0:
//			cor = Color.red;
//			break;
//		case 1:
//			cor = Color.BLUE;
//			break;
//		case 2:
//			cor = Color.green;
//			break;
//
//			
//		default:
//			break;
//		}
//		
//		
//		listadeagentes.add(new MeuAgente(10+rnd.nextInt(780), 10+rnd.nextInt(480), cor));		
//	}

	mousex = mousey = 0;
	
	mapa = new Mapa_Grid(100,100,50, 32);
	
	//mapa.loadmapfromimage("/imagemlabirinto.png");
	mapa.loadmapfromimage("/imgcastelo.png");
	
	meuHeroi = new MeuAgente(10, 10, Color.blue);
	listadeagentes.add(meuHeroi);
	
	for(int i = 0; i < NUM_AGENTS; i++){
		MeuAgente agentTest;
		
		switch (rnd.nextInt(3)) {
		case 0:
			agentTest = new AgentSwordsman(10+rnd.nextInt(500), 10+rnd.nextInt(500));
			testSpawnPosition(agentTest);
			break;
		case 1:
			agentTest = new AgentLancer(10+rnd.nextInt(500), 10+rnd.nextInt(500));
			testSpawnPosition(agentTest);
			break;
		case 2:
			agentTest = new AgentArcher(10+rnd.nextInt(500), 10+rnd.nextInt(500));
			testSpawnPosition(agentTest);
			break;
		default:
			break;
		}
	}
} // end of GamePanel()

public void testSpawnPosition(Agente ag) {
	while(mapa.mapa[(int)(ag.Y/16)][(int)(ag.X/16)]==1){
		ag.X = 10+rnd.nextInt(500);
		ag.Y = 10+rnd.nextInt(500);
	}
	
	listadeagentes.add(ag);
}

public void startGame()
// initialise and start the thread
{
	if (animator == null || !running) {
		animator = new Thread(this);
		animator.start();
	}
} // end of startGame()

public void stopGame()
// called by the user to stop execution
{ running = false; }


public void run()
/* Repeatedly update, render, sleep */
{
	running = true;
	
	long DifTime,TempoAnterior;
	
	int segundo = 0;
	DifTime = 0;
	TempoAnterior = System.currentTimeMillis();
	
	this.createBufferStrategy(2);
	BufferStrategy strategy = this.getBufferStrategy();
	
	while(running) {
	
		gameUpdate(DifTime); // game state is updated
		Graphics g = strategy.getDrawGraphics();
		gameRender((Graphics2D)g); // render to a buffer
		strategy.show();
	
		try {
			Thread.sleep(0); // sleep a bit
		}	
		catch(InterruptedException ex){}
		
		DifTime = System.currentTimeMillis() - TempoAnterior;
		TempoAnterior = System.currentTimeMillis();
		
		if(segundo!=((int)(TempoAnterior/1000))){
			FPS = SFPS;
			SFPS = 1;
			segundo = ((int)(TempoAnterior/1000));
		}else{
			SFPS++;
		}
	
	}
System.exit(0); // so enclosing JFrame/JApplet exits
} // end of run()

int timerfps = 0;
private void gameUpdate(long DiffTime)
{ 
	
	if(LEFT){
		posx-=200*DiffTime/1000.0;
	}
	if(RIGHT){
		posx+=200*DiffTime/1000.0;
	}	
	if(UP){
		posy-=200*DiffTime/1000.0;
	}
	if(DOWN){
		posy+=200*DiffTime/1000.0;
	}
	
	mapa.Posiciona((int)posx,(int)posy);
	
	if(mouseRect != null) {
		for(int i = 0; i < listadeagentes.size(); i++) {
			if(mouseRect.intersects(listadeagentes.get(i).X, listadeagentes.get(i).Y, 5, 5)) {
				selectedIndexes.add(i);
			}
		}
		mouseRect = null;
	}
	
	for(Agente ag : listadeagentes) {
		ag.SimulaSe((int)DiffTime);
	}
	
//	for(int i = 0; i < selectedIndexes.size(); i++) {
//		listadeagentes.get(selectedIndexes.get(i)).SimulaSe((int)DiffTime);
//	}
}

private void gameRender(Graphics2D dbg)
// draw the current frame to an image buffer
{
	// clear the background
	dbg.setColor(Color.white);
	dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
	
	mapa.DesenhaSe(dbg);
	
//	dbg.setColor(new Color(255,0,0,128));
//	for(int i = 0;i < meuHeroi.aestrela.nodosFechados.size();i++){
//		int nx = meuHeroi.aestrela.nodosFechados.get(i).x*16;
//		int ny = meuHeroi.aestrela.nodosFechados.get(i).y*16;
//		
//		dbg.fillRect(nx-mapa.MapX, ny-mapa.MapY, 16, 16);
//	}
//	
//	dbg.setColor(new Color(0,0,255,128));
//	for(int i = 0;i < meuHeroi.aestrela.nodosAbertos.size();i++){
//		int nx = meuHeroi.aestrela.nodosAbertos.get(i).x*16;
//		int ny = meuHeroi.aestrela.nodosAbertos.get(i).y*16;
//		
//		dbg.fillRect(nx-mapa.MapX, ny-mapa.MapY, 16, 16);
//	}
	
	for(int i = 0;i < listadeagentes.size();i++){
	  listadeagentes.get(i).DesenhaSe(dbg, mapa.MapX, mapa.MapY);
	}
	
	if(meuHeroi.caminho!=null){
	    Stroke stk = dbg.getStroke();
	    dbg.setStroke(new BasicStroke(2));
	    
		for(int j = 0;j < listadeagentes.size();j++){
			MeuAgente agente = (MeuAgente)listadeagentes.get(j);
			dbg.setColor(agente.color);
			for(int i = 0;i < (agente.caminho.length/2)-1;i++){
				dbg.drawLine(agente.caminho[(i*2)]*16+8-mapa.MapX, agente.caminho[(i*2)+1]*16+8 - mapa.MapY, agente.caminho[((i+1)*2)]*16+8-mapa.MapX, agente.caminho[((i+1)*2)+1]*16+8-mapa.MapY);
			}	
		}
	    dbg.setStroke(stk);
	}
	
	dbg.setColor(Color.BLUE);	
	dbg.drawString("FPS: "+FPS+"          Tempo: "+tempo+" nodosAbertos: "+nodosabertos , 10, 10);
	dbg.setColor(Color.RED);
	if(drawMouseRect)
		dbg.drawRect(mousePressedX-mapa.MapX, mousePressedY-mapa.MapY, mouseDraggedX-mousePressedX, mouseDraggedY-mousePressedY);
}

}

