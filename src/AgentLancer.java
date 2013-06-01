import java.awt.Color;
import java.awt.Graphics2D;


public class AgentLancer extends MeuAgente {
	
	public static final int SPEED = 80;

	public AgentLancer(int x, int y) {
		super(x, y, SPEED, Color.GREEN);
		super.ATK_RADIUS = 30;
		super.VISION_RADIUS = 200;
	}

	public void DesenhaSe(Graphics2D dbg, int XMundo, int YMundo){
		super.DesenhaSe(dbg, XMundo, YMundo);
		dbg.drawOval((int)(X-ATK_RADIUS/2)-XMundo, (int)(Y-ATK_RADIUS/2)-YMundo, ATK_RADIUS, ATK_RADIUS);
		dbg.setColor(Color.GREEN.darker());
		dbg.drawOval((int)(X-VISION_RADIUS/2)-XMundo, (int)(Y-VISION_RADIUS/2)-YMundo, VISION_RADIUS, VISION_RADIUS);
	}
}
