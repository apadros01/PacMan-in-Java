import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
//TOT AQUEST PROGRAMA ÉS PER DIBUIXAR ELS COTXES I FER QUE ES MOGUIN
public class Cotxe {
	int x;int y;int ample;int alt;double v; 
	
	static Image coco1dreta;
	static Image coco2dreta;
	static Image coco3;
	static Image coco1esquerra;
	static Image coco2esquerra;
	static Image coco1arriba;
	static Image coco2arriba;
	static Image coco1abaix;
	static Image coco2abaix;
		
	Cotxe(int x,int y,int ample,int alt,double v) {
		this.x=x;this.y=y;this.ample=ample;this.alt=alt;
		this.v=v;
	}
	void moure() {
		
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
	
	void pinta(Graphics g,int aux,int d) throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		
		if(d==1) {
			if(aux>=1 && aux<=2) {
				g.drawImage(coco1dreta,x,y, ample,alt, null);					
			}
			if(aux>=3 && aux<=4) {
				g.drawImage(coco2dreta,x,y, ample,alt, null);
			}
			if(aux>=5 && aux<=6) {
				g.drawImage(coco3,x,y, ample,alt, null);
			}
			if(aux>=7 && aux<=8) {
				g.drawImage(coco2dreta,x,y, ample,alt, null);
			}
		}
		else if(d==2) {
			if(aux>=1 && aux<=2) {
				g.drawImage(coco1esquerra,x,y, ample,alt, null);
			}
			if(aux>=3 && aux<=4) {
				g.drawImage(coco2esquerra,x,y, ample,alt, null);
			}
			if(aux>=5 && aux<=6) {
				g.drawImage(coco3,x,y, ample,alt, null);
			}
			if(aux>=7 && aux<=8) {
				g.drawImage(coco2esquerra,x,y, ample,alt, null);
			}
		}
		else if(d==3) {
			if(aux>=1 && aux<=2) {
				g.drawImage(coco1arriba,x,y, ample,alt, null);
			}
			if(aux>=3 && aux<=4) {
				g.drawImage(coco2arriba,x,y, ample,alt, null);
			}
			if(aux>=5 && aux<=6) {
				g.drawImage(coco3,x,y, ample,alt, null);
			}
			if(aux>=7 && aux<=8) {
				g.drawImage(coco2arriba,x,y, ample,alt, null);
			}
		}
		else if(d==4) {
			if(aux>=1 && aux<=2) {
				g.drawImage(coco1abaix,x,y, ample,alt, null);
			}
			if(aux>=3 && aux<=4) {
				g.drawImage(coco2abaix,x,y, ample,alt, null);
			}
			if(aux>=5 && aux<=6) {
				g.drawImage(coco3,x,y, ample,alt, null);
			}
			if(aux>=7 && aux<=8) {
				g.drawImage(coco2abaix,x,y, ample,alt, null);
			}
		}
		else {
			g.drawImage(coco3,x,y, ample,alt, null);
		}
	}
}
