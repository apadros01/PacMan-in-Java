import java.awt.Graphics;
import java.awt.Image;
public class Fantasma {
	int x;int y;int ample;int alt;double v; 
	int tipus;
	static Image fantasma1;
	static Image fantasma1esq;
	static Image fantasma2;
	static Image fantasma2esq;
	static Image fantasma3;
	static Image fantasma3esq;
	static Image fantasma4;
	static Image fantasma4esq;
	static Image fantasmablau;
	
	Fantasma(int x,int y,int ample,int alt,double v,int tipus) {
		this.x=x;this.y=y;this.ample=ample;this.alt=alt;
		this.v=v;
		this.tipus=tipus;
	}
	
	void mouredreta() {
		x+=v;
	}
	void moureesquerra() {
		x-=v;
	}
	void moureabaix() {
		y+=v;
	}
	void mourearriba() {
		y-=v;
	}
	
	void pinta(Graphics g,int efecte, int sentit) {
		if(efecte==0) {
			if(tipus==1 & sentit==2) {
				g.drawImage(fantasma1esq,x,y, ample,alt, null);
			}
			
			else if(tipus==1 & sentit!=2) {
				g.drawImage(fantasma1,x,y, ample,alt, null);
			}
			else if(tipus==2 & sentit==2) {
				g.drawImage(fantasma2esq,x,y, ample,alt, null);
			}
			else if(tipus==2 & sentit!=2) {
				g.drawImage(fantasma2,x,y, ample,alt, null);
			}
			else if(tipus==3 & sentit==2) {
				g.drawImage(fantasma3esq,x,y, ample,alt, null);
			}
			else if(tipus==3 & sentit!=2) {
				g.drawImage(fantasma3,x,y, ample,alt, null);
			}
			else if(tipus==4 & sentit==2) {
				g.drawImage(fantasma4esq,x,y, ample,alt, null);
			}
			else if(tipus==4 & sentit!=2) {
				g.drawImage(fantasma4,x,y, ample,alt, null);
			}
		}
		else {
			g.drawImage(fantasmablau,x,y, ample,alt, null);
		}
		
	}
}
