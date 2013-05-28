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

public static Mapa_Grid mapa;

double posx,posy;

MeuAgente meuHeroi = null;



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
			// TODO Auto-generated method stub
			mousex = e.getX(); 
			mousey = e.getY();
			

		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getButton()==3){
				int mx = (e.getX()+mapa.MapX)/16;
				int my = (e.getY()+mapa.MapY)/16;
				
				mapa.mapa[my][mx] = 1;
			}
		}
	});
	
	addMouseListener(new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			//System.out.println(" "+arg0.getButton());
			if(arg0.getButton()==3){
				int mx = (arg0.getX()+mapa.MapX)/16;
				int my = (arg0.getY()+mapa.MapY)/16;
				
				mapa.mapa[my][mx] = 1;
			}
			
			if(arg0.getButton()==1){
				int mx = (arg0.getX()+mapa.MapX)/16;
				int my = (arg0.getY()+mapa.MapY)/16;
				
				for(int i = 0;i < listadeagentes.size();i++){
					MeuAgente agente = ((MeuAgente)listadeagentes.get(i));
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
	
	for(int i = 0; i < 50; i++){
		Color cor = Color.black;
		
		switch (rnd.nextInt(4)) {
		case 0:
			cor = Color.red;
			break;
		case 1:
			cor = Color.BLUE;
			break;
		case 2:
			cor = Color.green;
			break;
	
			
		default:
			break;
		}
		
		
		MeuAgente agentetest = new MeuAgente(10+rnd.nextInt(500), 10+rnd.nextInt(500), cor);	
		while(mapa.mapa[(int)(agentetest.Y/16)][(int)(agentetest.X/16)]==1){
			agentetest.X = 10+rnd.nextInt(500);
			agentetest.Y = 10+rnd.nextInt(500);
		}
			
		listadeagentes.add(agentetest);
	}
} // end of GamePanel()

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
	

	
	
	for(int i = 0;i < listadeagentes.size();i++){
		  listadeagentes.get(i).SimulaSe((int)DiffTime);
	}
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
	
	//System.out.println("left "+LEFT);
		
}

}

