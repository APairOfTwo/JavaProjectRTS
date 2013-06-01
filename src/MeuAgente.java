import java.awt.Color;
import java.awt.Graphics2D;


public class MeuAgente extends Agente {
	
	int attackRadius;
	
	StateMachine state = StateMachine.IDLE;
	
	Color color;
	int speed = 0;
	int auxSpeed = 0;
	double ang = 0;
	
	int estado = 0;
	
	double oldx = 0;
	double oldy = 0;
	
	int timeria = 0;
	
	boolean colidiu = false;
	
	AEstrela aestrela;

	int caminho[] = null;
	
	boolean setouobjetivo = false;
	int objetivox = 0;
	int objetivoy = 0;
	
	
	int afasta = 0;
	double afastaang = 0;
	
	
	public MeuAgente(int x, int y, int speed, Color color) {
		X = x;
		Y = y;
		this.speed = speed;
		this.auxSpeed = speed;
		aestrela = new AEstrela(GamePanel.mapa);
		
		this.color = color;
	}
	
	@Override
	public void SimulaSe(int DiffTime) {
		timeria+=DiffTime;
		
		if(setouobjetivo==true){
			setaObjetivo(objetivox,objetivoy);
			setouobjetivo = false;
		}
		
		switch (state) {
		case IDLE:
			//qdo seta um objetivo, muda para marching
			//se inimigo entra no raio de visão, muda para chasing
			System.out.println("IDLE");
			break;
		case MARCHING:
			//qdo chega no objetivo, muda para idle
			//se inimigo entra no raio de visão, muda para chasing
			System.out.println("MARCHING");
			break;
		case CHASING:
			//se inimigo entra no raio do personagem, muda para attacking
			//se inimigo sai do raio de visão, muda para idle
			System.out.println("CHASING");
			break;
		case ATTACKING:
			//se life menor do que X, muda para fleeing
			//se mata o inimigo, muda para idle
			System.out.println("ATTACKING");
			break;
		case FLEEING:
			//seta como objetivo a enfermaria
			//se life maior do que X, muda para idle
			System.out.println("FLEEING");
			break;
		default:
			break;
		}
		
		if(aestrela.iniciouAestrela){
			if(aestrela.achoufinal==false){
				 int[] retorno = aestrela.continuapath();
				 if(retorno!=null){
					 AEstrela atmp = new AEstrela(GamePanel.mapa);
					 int[] caminho2 = atmp.StartAestrela((int)(X/16),(int)(Y/16), retorno[0],retorno[1],500);
					 int[] ctmp =  retorno;
					 
					 for(int i = 0; i < (caminho2.length/2);i++){
						 int pat1x = caminho2[i*2];
						 int pat1y = caminho2[i*2+1];
						  for(int j = 0; j < (retorno.length/2);j++){
							  if(pat1x==retorno[j*2]&&pat1y==retorno[j*2+1]){
								  caminho = new int[i*2+((retorno.length/2)-j)*2];
								  for(int z = 0; z < i*2;z++){
									  caminho[z] = caminho2[z];
								  }
								  for(int z = 0; z < ((retorno.length/2)-j)*2;z++){
									  caminho[i*2+z] = retorno[j*2+z];
								  }
								  System.out.println(" caminho "+caminho.length);
								  aestrela.achoufinal = true;
								  estado = 0;
								  return;
							  }
						  }
					 }
				 }
				 
			}		
		}
		oldx = X;
		oldy = Y;
		
		if(timeria>20){
			calculaIA(DiffTime);
			timeria = 0;
		}
		
		X+=Math.cos(ang)*speed*DiffTime/1000.0;
		Y+=Math.sin(ang)*speed*DiffTime/1000.0;
		
		int bx = (int)(X/16); 
		int by = (int)(Y/16);
		int bxold = (int)(oldx/16); 
		int byold = (int)(oldy/16);
		
		if(GamePanel.mapa.mapa[by][bx]==1){
			
			if(GamePanel.mapa.mapa[byold][bx]==0){
				Y = oldy;
			}else if(GamePanel.mapa.mapa[by][bxold]==0){
				X = oldx;
			}else{
				Y = oldy;
				X = oldx;
			}
		}
		
		afasta = 0;
		
		for(int i = 0; i < GamePanel.listadeagentes.size();i++){
		    Agente agente = GamePanel.listadeagentes.get(i);
		    
		    if(agente!=this){
			    
			    double dax = X - agente.X;
			    double day = Y - agente.Y ;
			    
			    double dista = dax*dax + day*day;
			    
			    if(dista<100){
			    	//X = oldx;
			    	//Y = oldy;
			    	if(dista<25){
				    	afasta = 2;
				    	colidiu = true;
				    	afastaang = Math.atan2(day, dax);
				    	break;
			    	}else{
				    	afasta = 1;
				    	colidiu = true;
				    	afastaang = Math.atan2(day, dax);
			    	}
			    }
		    }
		}
		
	}

	@Override
	public void DesenhaSe(Graphics2D dbg, int XMundo, int YMundo) {
		dbg.setColor(color);
		
		dbg.fillOval((int)(X-5)-XMundo, (int)(Y-5)-YMundo, 10, 10);
		
		double linefx = X + 5*Math.cos(ang);
		double linefy = Y + 5*Math.sin(ang);dbg.drawLine((int)X-XMundo,(int)Y-YMundo, (int)linefx-XMundo, (int)linefy-YMundo);
	}
	
	public void setaObjetivo(int objetivox,int objetivoy){
		long tempoinicio = System.currentTimeMillis();
		caminho = aestrela.StartAestrela((int)(X/16),(int)(Y/16), objetivox,objetivoy,100);
		GamePanel.tempo = (int)(System.currentTimeMillis() - tempoinicio);
		GamePanel.nodosabertos = aestrela.nodosAbertos.size()+aestrela.nodosFechados.size();
		estado = 0;
	}

	public void calculaIA(int DiffTime){
		if(caminho!=null&&(estado*2)<caminho.length){
			double dx = ((caminho[estado*2]*16)+8)-X;
			double dy = ((caminho[estado*2+1]*16)+8)-Y;
			
			double dist = dx*dx + dy*dy;
			
			
			if(dist < 64){
				estado++;
			}else{
				ang = Math.atan2(dy, dx);
			}
			speed = auxSpeed;
			
			if(afasta > 0) {
				double ang1 = Math.toDegrees(ang);
				double ang2 = Math.toDegrees(afastaang); 
				if(ang1 > ang2)
					ang = Math.toRadians(ang1 + ((ang1-ang2)/2));
				else
					ang = Math.toRadians(ang2 + ((ang2-ang1)/2));
			}
		} else {
			speed = 0;	
		}
	}
}
