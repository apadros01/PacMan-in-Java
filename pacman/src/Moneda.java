import java.awt.Graphics;
import java.awt.Image;

public class Moneda {
	int x;
	int y;
	int ample;
	int alt;
	int visible;
	
	static Image moneda;
	
	Moneda(int x,int y,int ample,int alt, int visible) {
		this.x=x;
		this.y=y;
		this.ample=ample;
		this.alt=alt;
		this.visible=visible;
	}
	
	void pinta(Graphics g) {
		if(visible==1) {
			g.drawImage(moneda,x,y, ample,alt, null);
		}	
	}
}
