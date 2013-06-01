import java.awt.Color;
import java.awt.Graphics2D;


public class AgentSwordsman extends MeuAgente {
	
	public static final int SPEED = 100;

	public AgentSwordsman(int x, int y) {
		super(x, y, SPEED, Color.RED);
		super.ATK_RADIUS = 12;
		super.VISION_RADIUS = 200;
	}

	public void DesenhaSe(Graphics2D dbg, int XMundo, int YMundo){
		super.DesenhaSe(dbg, XMundo, YMundo);
		dbg.drawOval((int)(X-ATK_RADIUS/2)-XMundo, (int)(Y-ATK_RADIUS/2)-YMundo, ATK_RADIUS, ATK_RADIUS);
		dbg.setColor(Color.RED.darker());
		dbg.drawOval((int)(X-VISION_RADIUS/2)-XMundo, (int)(Y-VISION_RADIUS/2)-YMundo, VISION_RADIUS, VISION_RADIUS);
	}
}
