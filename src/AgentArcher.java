import java.awt.Color;
import java.awt.Graphics2D;


public class AgentArcher extends MeuAgente {
	
	public static final int SPEED = 50;

	public AgentArcher(int x, int y) {
		super(x, y, SPEED, Color.BLUE);
		super.ATK_RADIUS = 100;
		super.VISION_RADIUS = 120;
	}

	public void DesenhaSe(Graphics2D dbg, int XMundo, int YMundo){
		super.DesenhaSe(dbg, XMundo, YMundo);
		dbg.drawOval((int)(X-ATK_RADIUS/2)-XMundo, (int)(Y-ATK_RADIUS/2)-YMundo, ATK_RADIUS, ATK_RADIUS);
		dbg.setColor(Color.BLUE.darker());
		dbg.drawOval((int)(X-VISION_RADIUS/2)-XMundo, (int)(Y-VISION_RADIUS/2)-YMundo, VISION_RADIUS, VISION_RADIUS);
	}
}
