
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;



public class Joc extends Thread implements KeyListener {
	Finestra f; 
	Cotxe c;
	Fantasma fantasma[]=new Fantasma[4];
	Paret paret[]=new Paret[118];
	Moneda moneda[]=new Moneda[324];
	Powerup powerup[]=new Powerup[4];
	int moviment; // 0 inicialitzat, 1 dreta, 2 esquerra, 3 arriba , 4 abaix
	int movimentfantasma[]=new int[4];
	int bloquejadreta;
	int bloquejaarriba;
	int bloquejaesquerra;
	int bloquejaabaix;
	int nivell=1;
	int vides=3;
	int bloquejadretaf[]= new int[4];
	int bloquejaarribaf[]= new int[4];
	int bloquejaesquerraf[]= new int[4];
	int bloquejaabaixf[]= new int[4];
	int efecte; //EFECTE FANTASMES BLAUS =1 SINO =0
	int score=-1; //inicialitzo aqui pel tema de que en cada canvi de nivell es conservi la puntuació
	int scoremonedes; //aquest serveix perquè un cop menjades totes les 167 monedes acabi el nivell.
	int aux,aux2,aux3;
	int random[]= new int[4];
	int millorrecordfuncio;
	int millorrecord1;
	int comencem=0;
	static BufferedImage fotointro;
	static BufferedImage fotogameover;
	
	int left,right,up,down;
	
	Joc(Finestra f) throws IOException {
		f.addKeyListener(this);
		this.f=f;
		millorrecord1=MillorsRecords();
	}
	
	void executa() throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
		inicialitza();
		while(comencem==0) {
			
			f.g.drawImage(fotointro,0,0,f.AMPLE,f.ALT,null);
			f.g.setColor(Color.WHITE);
			f.g.setFont(new Font("Verdana",Font.BOLD,30));
			f.g.drawString("Prem espai per començar",90,450);
			f.g.setColor(Color.BLACK);
			f.g.drawString("Prem espai per començar",90,450);
			f.repaint();
		}
		f.g.setFont(new Font("Verdana",Font.BOLD,12));
		posicioInicial();
		
		
		MillorsRecords();
		while(vides>0) {
			if(scoremonedes>=167) { //amb aquest if s'avança de nivell quan et menges les monedes
				posicioInicial();
				inicialitza();
				moviment=0;
				nivell++;
				score=score-1;
				TimeUnit.SECONDS.sleep(3);
			}
			aux++;
			aux2++;
			calculaMoviments();
			comprovaXocs();
			repintaPantalla();
			
			try {
				Thread.sleep(18); //18 milisegons
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(aux==8) {
				aux=0;
			}
			if(aux2==40) { //aquests dos aux serveixen perquè els fantasmes prenguin decisions (random)
				aux2=0;
			}
			if(efecte==1) { //QUAN AGAFEM POWERUP
				aux3++;
				for(int i=0;i<4;i++) {
					fantasma[i].v=1; //fantasmes més lents durant powerup
				}
				if(aux3==200) { //"temps" de powerup
					aux3=0;
					efecte=0;
					for(int i=0;i<4;i++) {
						fantasma[i].v=2; //aquí tornen a la normalitat
					}
				}
			}
		}
		if(vides==0) {
			f.g.drawImage(fotogameover,220,20,200,100,null);
			f.repaint();
		}
	}
	
	public void playSound(String soundFile) throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException {
	    File f = new File("./" + soundFile);
	    AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());  
	    Clip clip = AudioSystem.getClip();
	    clip.open(audioIn);
	    clip.start();
	} //això és la funció per tenir sorolls 

	
	void posicioInicial() {
		moviment=0; //comença quiet
		for(int i=0;i<4;i++) {
			movimentfantasma[i]=0; //els fantasmes també
		}
		
		left=0;
		up=0;
		down=0;
		right=0;
		
		//aquests bloquejos inicials depenen de la posició inicial
		bloquejaarriba=1;
		bloquejaabaix=0;
		bloquejadreta=0;
		bloquejaesquerra=1;
		
		
		//POSICION INICIALS FANTASMA (BLOQUEJOS INICIALS DE FET)
		bloquejaarribaf[0]=1;
		bloquejaabaixf[0]=0;
		bloquejadretaf[0]=1;
		bloquejaesquerraf[0]=0;
		random[0]=(int) Math.floor(Math.random()*4 + 1); 
		
		bloquejaarribaf[1]=0;
		bloquejaabaixf[1]=1;
		bloquejadretaf[1]=1;
		bloquejaesquerraf[1]=0;
		random[1]=(int) Math.floor(Math.random()*4 + 1);
		
		bloquejaarribaf[2]=0;
		bloquejaabaixf[2]=1;
		bloquejadretaf[2]=0;
		bloquejaesquerraf[2]=1;
		random[2]=(int) Math.floor(Math.random()*4 + 1);
		
		bloquejaarribaf[3]=1;
		bloquejaabaixf[3]=1;
		bloquejadretaf[3]=0;
		bloquejaesquerraf[3]=0;
		random[3]=(int) Math.floor(Math.random()*4 + 1);
		
		
		c=new Cotxe(258,276,20,20,2);
		fantasma[0] = new Fantasma(156+18*17-4,156,20,20,2,1);
		fantasma[1]= new Fantasma(156+18*17-4,156+304-2,20,20,2,2);
		fantasma[2]= new Fantasma(156+18*17-4-302,156+304-2,20,20,2,3);
		fantasma[3]=new Fantasma(250,150,20,20,2,4);
		

	}
	
	void inicialitza(){
		
		millorrecordfuncio=-100;
		aux=0;
		aux3=0;
		scoremonedes=-1;
		efecte=0;
		int t=0;
		for(int j=0;j<18;j++) {
			for(int i=0; i<18;i++) {
				moneda[t]=new Moneda(156+18*i,156+18*j,8,8,1); //visible=1 invisble=0
				t++;
			}
		}
		
		powerup[0]=new Powerup(156+18*17-1,156-1,10,10,1);
		powerup[1]=new Powerup(156+18*17-1,156-1+304,10,10,1);
		powerup[2]=new Powerup(156+18*17-1-304,156-1+304,10,10,1);
		powerup[3]=new Powerup(156+18*17-1-304,156-1,10,10,1); 

		
		//INICI SECCIÓ PARETS
		paret[0]=new Paret(0,0,f.AMPLE,150,0,3);
		
		paret[1]=new Paret(0,0,150,f.ALT,1,2);
		
		paret[2]=new Paret(475,0,100,f.ALT,1,1);
		
		paret[3]=new Paret(0,475,f.AMPLE,100,0,4);
		
		paret[4]=new Paret(170,172,20,30,1,1); 
		
		paret[7]=new Paret(170+285-18,172,20,30,1,2); 
		
		paret[9]=new Paret(148,260,72,10,0,4); 
		
		paret[10]=new Paret(172,170,48,20,0,4); //BE
				
		paret[11]=new Paret(174+70,170,30,10,0,4); //BE
		
		paret[12]=new Paret(172+70,172,20,30,1,1); //BE
				
		paret[13]=new Paret(172+50-20,172,20,30,1,2); //BE
				
		paret[14]=new Paret(172,240-10,48,10,0,3);  //BE
				
		paret[15]=new Paret(174+70,240-10,12,10,0,3); //BE
		
		paret[16]=new Paret(172,198,60-12,6,0,4); // 
		
		paret[17]=new Paret(174+70,194,30,10,0,3); // 
		
		paret[18]=new Paret(172,224,60-12,8,0,3); //  
		
		paret[19]=new Paret(174+70,224,12,8,0,4); // 
		
		paret[20]=new Paret(172+70,226,10,66,1,1); //V DRETA
		
		paret[21]=new Paret(172+40,226,10,12,1,2); //V ESQUERRA
		
		paret[22]=new Paret(170,226,10,12,1,1); //V DRETA	
		
		paret[23]=new Paret(172+70+282-50-10-20+2,226,10,12,1,2); //V esquerra

		paret[24]=new Paret(406,170,48,20,0,4); //H4
		
		paret[25]=new Paret(406,198,48,6,0,3);  // H3
		
		paret[26]=new Paret(174+70+138-20+2,172,20,30,1,2); //  V2
		
		paret[27]=new Paret(406-2,172,20,30,1,1); //  V 1
		
		paret[28]=new Paret(406,224,48,8,0,4); // H 4
		
		paret[29]=new Paret(406,230,48,10,0,3);  // H 3
		
		paret[30]=new Paret(174+70+138-10+2,224+2,10,66,1,2); //  V 2
		
		paret[31]=new Paret(406-2,224+2,20,12,1,1); //  V  1
		
		paret[32]=new Paret(174+70+30+24,170,30,10,0,4); //H 4
		
		paret[33]=new Paret(174+70+30+24+30+24,170,30,10,0,4);//H 4
		
		paret[34]=new Paret(172+70+30+24,172-28,10,30+28,1,1);//V 1
		
		paret[35]=new Paret(172+70+30+24+30+24,172,10,30,1,1);//V 1
		
		paret[36]=new Paret(172+70+30+24+30-10+4,172-28,10,30+28,1,2);//V 2
		
		paret[37]=new Paret(172+70+30-10+4,172,10,30,1,2);//V 2
		
		paret[38]=new Paret(174+70+30+24,194,30,10,0,3); //H 3
		
		paret[39]=new Paret(174+70+30+24+30+24,194,30,10,0,3); //H 3
		
		paret[5]=new Paret(174+70,260,30,10,0,4);//  H 4
		
		paret[8]=new Paret(174+70+30+24,260,30,10,0,4); // H 4
		
		paret[40]=new Paret(174+70+30+24+30+24,260,30,10,0,4);// H 4
		
		paret[41]=new Paret(174+70+30+24+30+24+24+30,260,72,10,0,4);//  H 4
		
		paret[42]=new Paret(148,260+4+18,72,12,0,3); // H 3
		
		paret[43]=new Paret(174+70,260+4,30,12,0,3);//  H 3
		
		paret[44]=new Paret(174+70+30+24,260+4,30,12,0,3); // H 3
		
		paret[45]=new Paret(174+70+30+24+30+24,260+4,30,12,0,3);// H 3
		
		paret[46]=new Paret(174+70+30+24+30+24+24+30,260+4+18,72,12,0,3);//  H 3
		
		paret[47]=new Paret(172+70+54,226,10,48,1,1); // VER DRETA
		
		paret[48]=new Paret(172+70+54+24,226,10,48,1,2); // VER ESQUERRA
		
		paret[49]=new Paret(148+72-10+2,260+2,10,12+18,1,2); // VER ESQUERRA
		
		paret[50]=new Paret(148+72-10+2+54,260+2,10,12,1,2); // VER ESQUERRA
		
		paret[51]=new Paret(148+72-10+2+54+84,260+2,10,12,1,1); // VER DRETA
		
		paret[52]=new Paret(148+72-10+2+54+138,260+2,10,12+18,1,1); // VER DRETA
		
		paret[53]=new Paret(174+70+12+24,240-10,66,10,0,3);//  HORIT 3
		
		paret[54]=new Paret(174+70+12+24,224,66,8,0,4); //   HORIT 4
		
		paret[55]=new Paret(174+70+12+24+66+24,240-10,12,10,0,3);//  HORIT 3
		
		paret[56]=new Paret(174+70+12+24+66+24,224,12,8,0,4); //   HORIT 4
		
		paret[57]=new Paret(172+70+12+24,226,10,12,1,1); // VERT DRETA
		
		paret[58]=new Paret(172+70+12+24+66-10+4,226,10,12,1,2); // VERT ESQUERRA
		
		paret[59]=new Paret(172+70+12+24+66+24,226,10,66,1,1); // VERT DRETA
		
		paret[60]=new Paret(172+70+12-10+4,226,10,66,1,2); // VER ESQUERRA
					
		paret[61]=new Paret(174+70+30+24+30+24+24+30,260+4+18+24+12-4,72,12,0,4);//  H 4 
		
		paret[62]=new Paret(148,260+4+18+24+12-4,72,12,0,4); // H 4 NOU
		
		paret[63]=new Paret(148+72-10+2+54+138,260+2+24+12+18,10,12+18,1,1); // VER DRETA 
		
		paret[64]=new Paret(148+72-10+2,260+2+24+12+18,10,12+18,1,2); // VER ESQUERRA 
		
		paret[65]=new Paret(174+70+30+24+30+24+24+30,260+4+18+24+12-4+18+4,72,12,0,3);//  H 3 
		
		paret[66]=new Paret(148,260+4+18+24+12-4+18+4,72,12,0,3); // H 3 
		
		paret[67]=new Paret(174+70+12+24+66+24,224+62,12,8,0,3); // H 3
		
		paret[68]=new Paret(174+70,224+62,12,8,0,3); // H 3
		
		paret[69]=new Paret(174+70+12+24,260+4+18+24+12-4-20+2,66,10,0,4); //H4

		paret[70]=new Paret(174+70+12+24,260+4+18+24+12-4-20+24+2,66,10,0,3); //H3
		
		paret[71]=new Paret(172+70+12+24,260+4+18+24+12-4-20+2+2,10,30,1,1); // V 1 
		
		paret[72]=new Paret(172+70+12+24+66-10+4,260+4+18+24+12-4-20+2+2,10,30,1,2); //V2
			
		paret[73]=new Paret(148+72-10+2+24+10-4,260+2+24+12+18,10,12+18,1,1); // VER DRETA 
		
		paret[74]=new Paret(148+72-10+2+24+10-4+2,260+2+24+12+18-2,12,10,0,4); // H 4 
		
		paret[75]=new Paret(148+72-10+2+24+10-4+2,260+2+24+12+18-2+12+12,12,10,0,3); // H 3 
		
		paret[76]=new Paret(148+72-10+2+24+10-4+6,260+2+24+12+18,10,12+18,1,2); // VER ESQUERRA 
		
		paret[77]=new Paret(148+72-10+2+24+10-4+126,260+2+24+12+18,10,12+18,1,1); // VER DRETA 
		
		paret[78]=new Paret(148+72-10+2+24+10-4+2+126,260+2+24+12+18-2,12,10,0,4); // H 4 
		
		paret[79]=new Paret(148+72-10+2+24+10-4+2+126,260+2+24+12+18-2+12+12,12,10,0,3); // H 3 
		
		paret[80]=new Paret(148+72-10+2+24+10-4+6+126,260+2+24+12+18,10,12+18,1,2); // VER ESQUERRA 

		paret[81]=new Paret(148+72-10+2+24+10-4,260+2+24+12+18+54,10,12+18,1,1); // VER DRETA 
		
		paret[82]=new Paret(148+72-10+2+24+10-4+2,260+2+24+12+18-2+54,12,10,0,4); // H 4 
		
		paret[83]=new Paret(148+72-10+2+24+10-4+2,260+2+24+12+18-2+12+12+60,30,4,0,3); // H3 

		paret[84]=new Paret(148+72-10+2+24+10-4+6,260+2+24+12+18+54,10,12+18,1,2); // VER ESQUERRA 

		paret[85]=new Paret(148+72-10+2+24+10-4+126,260+2+24+12+18+54,10,12+18,1,1); // VER DRETA NOU

		paret[86]=new Paret(148+72-10+2+24+10-4+2+126,260+2+24+12+18-2+54,12,10,0,4); // H 4 NOU
		
		paret[87]=new Paret(148+72-10+2+24+10-4+2+108,260+2+24+12+18-2+12+12+60,30,4,0,3); // H 3 NOU
		
		paret[88]=new Paret(148+72-10+2+24+10-4+6+126,260+2+24+12+18+54,10,12+18,1,2); // VER ESQUERRA NOU
		
		paret[89]=new Paret(174+70+12+24,260+4+18+24+12-4-20+2+40+14,66,10,0,4);//H4 nova
		
		paret[90]=new Paret(174+70+12+24,260+4+18+24+12-4-20+2+40+12+8,66,10,0,3); //h3 nova
	
		paret[91]=new Paret(172+70+12+24,260+4+18+24+12-4-20+2+2+54,10,12,1,1); //V1 NOVA
		
		paret[92]=new Paret(172+70+12+24+60,260+4+18+24+12-4-20+2+2+54,10,12,1,2); //V2 NOVA
		
		paret[93]=new Paret(172+70+12+24+18,260+4+18+24+12-4-20+2+2+54,10,48,1,1); //v1 nova
		
		paret[94]=new Paret(172+70+12+24+42,260+4+18+24+12-4-20+2+2+54,10,48,1,2); //v2 nova
		
		paret[95]=new Paret(172+70+12+24+20,260+4+18+24+12-4-20+2+40+14+42,30,10,0,3); //H3 NOVA
		
		paret[96]=new Paret(148+72-10+2+24+10-4+2+108,260+2+24+12+18-2+12+12+48,30,4,0,4); //h4 nova
		
		paret[97]=new Paret(148+72-10+2+24+10-4+2+106,260+2+24+12+18-2+12+12+50,10,12,1,1); //v1 nova

		paret[98]=new Paret(148+72-10+2+24+10-4+24,260+2+24+12+18-2+12+12+50,10,12,1,4); //V4 nova
		
		paret[99]=new Paret(148+72-10+2+24+10-4+2,260+2+24+12+18-2+12+12+48,30,4,0,4); //H4 nova
		
		paret[100]=new Paret(148+72-10+2+24+10-4-72,260+2+24+12+18+54,10,12+36,1,1); //v1
		
		paret[101]=new Paret(148+72-10+2+24+10-4-70,260+2+24+12+18+52,48,12,0,4); //H4 
		
		paret[102]=new Paret(148+72-10+2+24+10-4-30,260+2+24+12+18+54,10,12+36,1,2); //v2 
		
		paret[103]=new Paret(148+72-10+2+24+10-4-70,260+2+24+12+18+52+40,48,12,0,3);  //h3 
		
		paret[104]=new Paret(148+72-10+2+24+10-4+162,260+2+24+12+18+54,10,12+36,1,1); //v1
		
		paret[105]=new Paret(148+72-10+2+24+10-4+164,260+2+24+12+18+52,48,12,0,4); //H4 
		
		paret[106]=new Paret(148+72-10+2+24+10-4+204,260+2+24+12+18+54,10,12+36,1,2); //v2 
	
		paret[107]=new Paret(148+72-10+2+24+10-4+164,260+2+24+12+18+52+40,48,12,0,3); //H3 
		
		paret[108]=new Paret(148+72-10+2+24+10-4-70,170+270,48,10,0,4); // h4 
		
		paret[109]=new Paret(148+72-10+2+24+10-4-72,260+2+24+12+144,10,12,1,1); //v1 
		
		paret[110]=new Paret(148+72-10+2+24+10-4-70,260+2+24+12+18+52+80,48,8,0,3); //h3 
		
		paret[111]=new Paret(148+72-10+2+24+10-34,260+2+24+12+144,10,12,1,2); //v2 
		
		paret[112]=new Paret(148+72-10+2+24+10-4-70+234,170+270,48,10,0,4); // h4 nova
		
		paret[113]=new Paret(148+72-10+2+24+10-4-72+234,260+2+24+12+144,10,12,1,1); //v1 nova
		
		paret[114]=new Paret(148+72-10+2+24+10-4-70+234,260+2+24+12+18+52+80,48,8,0,3); //h3 nova 
		
		paret[115]=new Paret(148+72-10+2+24+10-34+234,260+2+24+12+144,10,12,1,2); //v2 nova
		
		paret[6]=new Paret(244,260+2+24+12+144-20,138,20,0,4); 
		
		paret[116]=new Paret(242,260+2+24+12+144-18,20,53,1,1); //V1 
		
		paret[117]=new Paret(242+122,260+2+24+12+144-18,20,53,1,2); //V2 

			//FI SECCIO PARETS
		

		Imatges();
		
	}	
	
	void calculaMoviments() {
			if(left==1 && bloquejaesquerra==0) {
				moviment=2;
				bloquejadreta=0;
			}
			else if(up==1 && bloquejaarriba==0) {
				moviment=3;
				bloquejaabaix=0;
			}
			else if(right==1 && bloquejadreta==0) {
				moviment=1;
				bloquejaesquerra=0;
			}
			else if(down==1 && bloquejaabaix==0) {
				moviment=4;
				bloquejaarriba=0;
			}
			else {
				
			}
		
		
		
			if(moviment==1) { //dreta
				c.mouredreta();
				if(efecte==1) {
					for(int i=0;i<4;i++) {
						bloquejaesquerraf[i]=1;
						
					}
				}
			}
			if(moviment==2) { //esquerra
				c.moureesquerra();
				if(efecte==1) {
					for(int i=0;i<4;i++) {
						bloquejadretaf[i]=1;
					}
				}
			}
			if(moviment==3){ //arriba
				c.mourearriba();
				if(efecte==1) {
					for(int i=0;i<4;i++) {
						bloquejaabaixf[i]=1;
					}
				}
			}
			if(moviment==4) { //abaix
				c.moureabaix();
				if(efecte==1) {
					for(int i=0;i<4;i++) {
						bloquejaarribaf[i]=1;
					}
				}
			}
			
			//CADA CERT TEMPS AUX2=40 DEL BUCLE CANVIEM LA DIRECCIO DELS FANTASMES
			if(aux2==40) {
				random[1]=(int) Math.floor(Math.random()*4 + 1);
			}
			if(aux2==36) {
				random[2]=(int) Math.floor(Math.random()*4 + 1);
			}
			if(aux2==30) {
				random[3]=(int) Math.floor(Math.random()*4 + 1);
			}
			if(aux2==25) {
				random[0]=(int) Math.floor(Math.random()*4 + 1);
			}
			
			
			//AQUEST BUCLE FA QUE NO ES PARIN MAI ELS FANTASMES
			for(int i=0;i<4;i++) {
				while(true) {
					if(random[i]==1 && bloquejadretaf[i]==0) { //dreta
						fantasma[i].mouredreta();
						movimentfantasma[i]=1;
						break;
					}
					else if(random[i]==2 && bloquejaesquerraf[i]==0) { //esquerra
						fantasma[i].moureesquerra();
						movimentfantasma[i]=2;
						break;
					}
					else if(random[i]==3 && bloquejaarribaf[i]==0){ //arriba
						fantasma[i].mourearriba();
						movimentfantasma[i]=3;
						break;
					}
					else if(random[i]==4 && bloquejaabaixf[i]==0) { //abaix
						fantasma[i].moureabaix();
						movimentfantasma[i]=4;
						break;
					}
					else {
						random[i]=(int) Math.floor(Math.random()*4 + 1);
					}
				}
			}		
	}
	
	void comprovaXocs()throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
		int x,y,ample,alt,x2,y2,ample2,alt2,x3,y3,ample3,alt3;
		int cruz=0;
		int cruzf[]=new int[4];
		cruzf[0]=0;
		cruzf[1]=0;
		cruzf[2]=0;
		cruzf[3]=0;
		x=c.x;
		y=c.y;
		ample=c.ample;
		alt=c.alt;
		
		
		//XOC PARET COCO
		
		for(int i=0;i<paret.length;i++) {
			x2=paret[i].x;
			y2=paret[i].y;
			ample2=paret[i].ample;
			alt2=paret[i].alt;
			
			
			if(x>x2+ample2) {}
			else if(x+ample<x2) {}
			else if(y>y2+alt2) {}
			else if(y+alt<y2){}
			else {
				cruz++;
				for(int j=0;j<paret.length;j++) {
					if(j==i) {
						continue;
					}
					x3=paret[j].x;
					y3=paret[j].y;
					ample3=paret[j].ample;
					alt3=paret[j].alt;
					
					
					if(x>x3+ample3) {}
					else if(x+ample<x3) {}
					else if(y>y3+alt3) {}
					else if(y+alt<y3){}
					else {
						cruz++;
						//AQUI HI HA DOS XOCS, VOLEM SABER LA NATURALESA D'AQUESTS
						if(paret[i].tipus==0 && paret[j].tipus==0) { // H - H
							bloquejaarriba=1;
							bloquejaabaix=1;
							bloquejadreta=0;
							bloquejaesquerra=0;
						}
						if(paret[i].tipus==1 && paret[j].tipus==1) { // V - V
							bloquejaarriba=0;
							bloquejaabaix=0;
							bloquejadreta=1;
							bloquejaesquerra=1;
						}
						if(paret[i].tipus==1 && paret[j].tipus==0) { // H - V 
							moviment=0;
							bloquejaarriba=0;
							bloquejaabaix=0;
							bloquejadreta=0;
							bloquejaesquerra=0;
							if(paret[i].bloc==1) {
								bloquejadreta=1;
							}
							if(paret[i].bloc==2) {
								bloquejaesquerra=1;
							}
							if(paret[i].bloc==3) {
								bloquejaarriba=1;
							}
							if(paret[i].bloc==4) {
								bloquejaabaix=1;
							}
							
							
							if(paret[j].bloc==1) {
								bloquejadreta=1;
							}
							if(paret[j].bloc==2) {
								bloquejaesquerra=1;
							}
							if(paret[j].bloc==3) {
								bloquejaarriba=1;
							}
							if(paret[j].bloc==4) {
								bloquejaabaix=1;
							}
						}
					}
					
				}
				
				if(cruz==1) {//NOMES UN XOC AMB PARETS --> |--------- 
					if(paret[i].tipus==1) {
						if(moviment==2) {
							left=0;
							bloquejaesquerra=1;
							moviment=0;
							bloquejadreta=0;
						}
						else if(moviment==1) {
							right=0;
							moviment=0;
							bloquejaesquerra=0;
							bloquejadreta=1;
						}
						else{
							if(paret[i].bloc==1) {
								bloquejadreta=1;
								bloquejaabaix=0;
								bloquejaarriba=0;
								bloquejaesquerra=0;
							}
							if(paret[i].bloc==2) {
								bloquejaesquerra=1;
								bloquejadreta=0;
								bloquejaabaix=0;
								bloquejaarriba=0;
							}
							if(paret[i].bloc==3) {
								bloquejaarriba=1;
								bloquejadreta=0;
								bloquejaabaix=0;
								bloquejaesquerra=0;
							}
							if(paret[i].bloc==4) {
								bloquejaabaix=1;
								bloquejadreta=0;
								bloquejaarriba=0;
								bloquejaesquerra=0;
							}
						}
					}
					
					else {
						if(moviment==3) {
							up=0;
							moviment=0;
							bloquejaabaix=0;
							bloquejaarriba=1;
						}
						else if(moviment==4) {
							down=0;
							moviment=0;
							bloquejaarriba=0;
							bloquejaabaix=1;
							
							
						}
						else{
							if(paret[i].bloc==1) {
								bloquejadreta=1;
								bloquejaabaix=0;
								bloquejaarriba=0;
								bloquejaesquerra=0;
							}
							if(paret[i].bloc==2) {
								bloquejaesquerra=1;
								bloquejadreta=0;
								bloquejaabaix=0;
								bloquejaarriba=0;
							}
							if(paret[i].bloc==3) {
								bloquejaarriba=1;
								bloquejadreta=0;
								bloquejaabaix=0;
								bloquejaesquerra=0;
							}
							if(paret[i].bloc==4) {
								bloquejaabaix=1;
								bloquejadreta=0;
								bloquejaarriba=0;
								bloquejaesquerra=0;
							}
						}
					}
					
				}
			}
			
			
			if(cruz==0) { // SITUACIO CREU +
				bloquejadreta=0;
				bloquejaabaix=0;
				bloquejaarriba=0;
				bloquejaesquerra=0;
			}
			
		}
			
		//////////////////////////////////////////////////////////////////////
		//XOC PARET - FANTASMA
		
		
		for(int k=0;k<4;k++) {
			x=fantasma[k].x;
			y=fantasma[k].y;
			ample=fantasma[k].ample;
			alt=fantasma[k].alt;
			
			for(int i=0;i<paret.length;i++) {
				x2=paret[i].x;
				y2=paret[i].y;
				ample2=paret[i].ample;
				alt2=paret[i].alt;
				
				
				if(x>x2+ample2) {}
				else if(x+ample<x2) {}
				else if(y>y2+alt2) {}
				else if(y+alt<y2){}
				else {
					cruzf[k]++;
					for(int j=0;j<paret.length;j++) {
						if(j==i) {
							continue;
						}
						x3=paret[j].x;
						y3=paret[j].y;
						ample3=paret[j].ample;
						alt3=paret[j].alt;
						
						
						
						if(x>x3+ample3) {}
						else if(x+ample<x3) {}
						else if(y>y3+alt3) {}
						else if(y+alt<y3){}
						else {
							cruzf[k]++;
							//AQUI HI HA DOS XOCS, VOLEM SABER LA NATURALESA D'AQUESTS
							if(paret[i].tipus==0 && paret[j].tipus==0) { // H - H
								bloquejaarribaf[k]=1;
								bloquejaabaixf[k]=1;
								bloquejadretaf[k]=0;
								bloquejaesquerraf[k]=0;
							}
							if(paret[i].tipus==1 && paret[j].tipus==1) { // V - V
								bloquejaarribaf[k]=0;
								bloquejaabaixf[k]=0;
								bloquejadretaf[k]=1;
								bloquejaesquerraf[k]=1;
							}
							if(paret[i].tipus==1 && paret[j].tipus==0) { // H - V 
								bloquejaarribaf[k]=0;
								bloquejaabaixf[k]=0;
								bloquejadretaf[k]=0;
								bloquejaesquerraf[k]=0;
								if(paret[i].bloc==1) {
									bloquejadretaf[k]=1;
								}
								if(paret[i].bloc==2) {
									bloquejaesquerraf[k]=1;
								}
								if(paret[i].bloc==3) {
									bloquejaarribaf[k]=1;
								}
								if(paret[i].bloc==4) {
									bloquejaabaixf[k]=1;
								}
								
								
								if(paret[j].bloc==1) {
									bloquejadretaf[k]=1;
								}
								if(paret[j].bloc==2) {
									bloquejaesquerraf[k]=1;
								}
								if(paret[j].bloc==3) {
									bloquejaarribaf[k]=1;
								}
								if(paret[j].bloc==4) {
									bloquejaabaixf[k]=1;
								}
							}
						}
						
					}
					
					if(cruzf[k]==1) {//NOMES UN XOC AMB PARETS --> |--------- 
						if(paret[i].tipus==1) {
							if(movimentfantasma[k]==2) {
								bloquejaesquerraf[k]=1;
								bloquejadretaf[k]=0;
							}
							else if(movimentfantasma[k]==1) {
								bloquejaesquerraf[k]=0;
								bloquejadretaf[k]=1;
							}
							else{
								if(paret[i].bloc==1) {
									bloquejadretaf[k]=1;
									bloquejaabaixf[k]=0;
									bloquejaarribaf[k]=0;
									bloquejaesquerraf[k]=0;
								}
								if(paret[i].bloc==2) {
									bloquejaesquerraf[k]=1;
									bloquejadretaf[k]=0;
									bloquejaabaixf[k]=0;
									bloquejaarribaf[k]=0;
								}
								if(paret[i].bloc==3) {
									bloquejaarribaf[k]=1;
									bloquejadretaf[k]=0;
									bloquejaabaixf[k]=0;
									bloquejaesquerraf[k]=0;
								}
								if(paret[i].bloc==4) {
									bloquejaabaixf[k]=1;
									bloquejadretaf[k]=0;
									bloquejaarribaf[k]=0;
									bloquejaesquerraf[k]=0;
								}
							}
						}
						
						else {
							if(movimentfantasma[k]==3) {
								bloquejaabaixf[k]=0;
								bloquejaarribaf[k]=1;
							}
							else if(movimentfantasma[k]==4) {
								bloquejaarribaf[k]=0;
								bloquejaabaixf[k]=1;
								
							}
							else{
								if(paret[i].bloc==1) {
									bloquejadretaf[k]=1;
									bloquejaabaixf[k]=0;
									bloquejaarribaf[k]=0;
									bloquejaesquerraf[k]=0;
								}
								if(paret[i].bloc==2) {
									bloquejaesquerraf[k]=1;
									bloquejadretaf[k]=0;
									bloquejaabaixf[k]=0;
									bloquejaarribaf[k]=0;
								}
								if(paret[i].bloc==3) {
									bloquejaarribaf[k]=1;
									bloquejadretaf[k]=0;
									bloquejaabaixf[k]=0;
									bloquejaesquerraf[k]=0;
								}
								if(paret[i].bloc==4) {
									bloquejaabaixf[k]=1;
									bloquejadretaf[k]=0;
									bloquejaarribaf[k]=0;
									bloquejaesquerraf[k]=0;
								}
							}
						}
						
					}
				}
				
				
				if(cruzf[k]==0) { // SITUACIO CREU +
					bloquejadretaf[k]=0;
					bloquejaabaixf[k]=0;
					bloquejaarribaf[k]=0;
					bloquejaesquerraf[k]=0;
				}
				
			}
		}
				
		/////////////////////////////////////////////////////////
		//XOC FANTASMA - COCO
		
		
		for(int i=0;i<4;i++) {
			
			//HITBOx
			x2=fantasma[i].x+5;
			y2=fantasma[i].y+5;
			ample2=fantasma[i].ample-10;
			alt2=fantasma[i].alt-10;
			
			x=c.x;
			y=c.y;
			ample=c.ample;
			alt=c.alt;
				
			if(x>x2+ample2) {}
			else if(x+ample<x2) {}
			else if(y>y2+alt2) {}
			else if(y+alt<y2){}
			else if(efecte==1){
				fantasma[i].x=458;
				fantasma[i].y=410;
				score=score+30; //sumes punts quan et menges un fantasma en powerup
				
			}
			//això serveix perquè no em matin quan estic en powerup
			else {
				if(vides<=1){
					GuardaRecords(score);
				}
				millorrecord1=MillorsRecords();
				playSound("mort.wav");
				vides--;
				if(vides!=0){
					posicioInicial();
					TimeUnit.SECONDS.sleep(3);
					moviment=0;
				}				
			}
		}
		
		
		
		/////////////////////////////////////////////////////////
		//XOC COCO - MONEDA
		

		x=c.x;
		y=c.y;
		ample=c.ample;
		alt=c.alt;
		
		for(int i=0;i<324;i++) {
			x2=moneda[i].x+3;
			y2=moneda[i].y+3;
			ample2=2;
			alt2=2;
			
			if(x>x2+ample2) {}
			else if(x+ample<x2) {}
			else if(y>y2+alt2) {}
			else if(y+alt<y2){}
			else {
				if(moneda[i].visible==0) {
					
				}
				else {
					score++;
					scoremonedes++;
					moneda[i].visible=0;
					if(score%2==0 & score>=0) {
						playSound("avance.wav");
					}
				}
				
				
			}
		}
		
		///////////////////////////////////////////
		//XOC COCO AMB POWERUP
		for(int i=0;i<4;i++){
		if(powerup[i].visible==1) {
			x2=c.x;
			y2=c.y;
			ample2=c.ample;
			alt2=c.alt;
			
			x=powerup[i].x;
			y=powerup[i].y;
			ample=powerup[i].ample;
			alt=powerup[i].alt;
				
			if(x>x2+ample2) {}
			else if(x+ample<x2) {}
			else if(y>y2+alt2) {}
			else if(y+alt<y2){}
			else {
				powerup[i].visible=0;
				efecte=1; //EFECTE FANTASMES BLAUS
				playSound("sopowerup.wav");
			}
		}
	}
		////////////////////////////////////////////////
		//teletransportes per coco
		
		x2=c.x+10;
		y2=c.y+10;
		ample2=c.ample-10;
		alt2=c.alt-10;
		
		x=150;
		y=290;
		ample=4;
		alt=28;
			
		if(x>x2+ample2) {}
		else if(x+ample<x2) {}
		else if(y>y2+alt2) {}
		else if(y+alt<y2){}
		else {
			c.x=450;
			c.y=294;
		}
		
		x2=c.x+10;
		y2=c.y+10;
		ample2=c.ample-10;
		alt2=c.alt-10;
		
		x=480;
		y=290;
		ample=4;
		alt=28;
			
		if(x>x2+ample2) {}
		else if(x+ample<x2) {}
		else if(y>y2+alt2) {}
		else if(y+alt<y2){}
		else {
			c.x=156;
			c.y=294;
		}
		
		
		////////////////////////////////////////////////
		//teletransportes per fantasmes
		for(int i=0;i<4;i++) {
			x2=fantasma[i].x+10;
			y2=fantasma[i].y+10;
			ample2=fantasma[i].ample-10;
			alt2=fantasma[i].alt-10;
			
			x=150;
			y=290;
			ample=4;
			alt=28;
			
			if(x>x2+ample2) {}
			else if(x+ample<x2) {}
			else if(y>y2+alt2) {}
			else if(y+alt<y2){}
			else {
				fantasma[i].x=450;
				fantasma[i].y=294;
			}
			
			x2=fantasma[i].x+10;
			y2=fantasma[i].y+10;
			ample2=fantasma[i].ample-10;
			alt2=fantasma[i].alt-10;
			
			x=480;
			y=290;
			ample=4;
			alt=28;
			
			if(x>x2+ample2) {}
			else if(x+ample<x2) {}
			else if(y>y2+alt2) {}
			else if(y+alt<y2){}
			else {
				fantasma[i].x=156;
				fantasma[i].y=294;
			}
		}
}
	
	void GuardaRecords(int score) throws IOException {
		File file = new File("RECORDS.txt");
		FileWriter fr = new FileWriter(file,true);
		BufferedWriter br = new BufferedWriter(fr);
		br.write(String.valueOf(score)+"\n");
		br.close();
		fr.close();
	}
	
	public int MillorsRecords() throws IOException {
		List<String> records = new ArrayList<String>();
		
		String aux;
		int aux2;
		try{
			BufferedReader reader = new BufferedReader(new FileReader("RECORDS.txt"));
			String line;
			
			while((line=reader.readLine())!=null) {
				records.add(line);
			}
			for(int i=0;i<records.size();i++) {
				aux=records.get(i);
				aux2=Integer.parseInt(aux);
				if(millorrecordfuncio<aux2){
					millorrecordfuncio=aux2;
				}
			}
			reader.close();
			
		}
		catch(Exception e){
			System.err.format("Exception ocurred trying to read '%s'.", "RECORDS.txt");
			e.printStackTrace();
		}
		
		return millorrecordfuncio;
	}
	
	void repintaPantalla() throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		f.g.setColor(Color.BLACK); //això és el color del fons de la finestra
		f.g.fillRect(0,0,f.AMPLE,f.ALT); //això coloca el fons negre en su sitio
		
		
		
		f.g.setColor(Color.BLACK); //això és el color del fons de la finestra
		f.g.fillRect(150,150,325,325);
		
		f.g.setColor(Color.BLACK); 
		f.g.fillRect(170,170,285,285);
		
		f.g.setColor(Color.BLACK); 
		f.g.fillRect(150,294,325,20);
		
		f.g.setColor(Color.BLACK); 
		f.g.fillRect(150,240,325,20);
		
		
		
		
		for(int i=0;i<324;i++) {
			moneda[i].pinta(f.g);
		}
		for(int i=0;i<4;i++) {
			fantasma[i].pinta(f.g,efecte,movimentfantasma[i]);
		}
		
		c.pinta(f.g,aux,moviment); //AMB AIXO FEM QUE ELS COTXES ES PINTIN I BORRIN, DONANT SENSACIÓ DE MOVIMENT
		
		powerup[0].pinta(f.g);
		powerup[1].pinta(f.g);
		powerup[2].pinta(f.g);
		powerup[3].pinta(f.g);
		
		f.g.setColor(Color.WHITE);
		f.g.drawString("SCORE ", 100, 100);
		f.g.drawString("NIVELL", 100, 80);
		f.g.drawString("VIDES",100,60);
		f.g.drawString("HIGH SCORE", 80, 120);
        String scoreaux = String.format("%03d", score);
        String nivellaux=String.format("%03d",nivell);
        String videsaux = String.format("%03d", vides);
        String millorrecordaux = String.format("%03d",millorrecord1);
		f.g.drawString(scoreaux, 170, 100);
		f.g.drawString(nivellaux,170,80);
		f.g.drawString(videsaux, 170, 60);
		f.g.drawString(millorrecordaux,170,120);
		
		
 //INICI SECCIO PINTAR PARETS
		f.g.setColor(Color.BLUE);
		
		f.g.fillRect(172,170,48,20);
		
		f.g.fillRect(172+70,172,20,30);
		
		
		f.g.fillRect(172+50-20,172,20,30);
		f.g.setColor(Color.BLUE);
		f.g.fillRect(172,240-10,48,10); //RETOCAT
		
		f.g.fillRect(170,172,20,30);
		

		f.g.fillRect(170+285-18,172,20,30);
		
		f.g.fillRect(172,198,60-12,6); // H abaix
		f.g.fillRect(172,224,60-12,8); // H arriba 
			
		f.g.fillRect(172+40,226,10,12); //V ESQUERRA
	
		f.g.fillRect(170,226,10,12); //V DRETA
		
		f.g.fillRect(172+70+282-50-10-20+2,226,10,12); //V esquerra
				
		f.g.setColor(Color.BLUE);
		
		f.g.fillRect(180,180,25,20);
		
		f.g.fillRect(180+70,180,24,20);
		
		f.g.fillRect(180+70+24+30,143,22,60);
		
		f.g.fillRect(180+70+24+30+24+24,180,22,20);
		
		f.g.fillRect(420,180,20,20);
		f.g.fillRect(144,260,70,24);
		
		f.g.fillRect(144,320,70,24);
		
		f.g.fillRect(410,260,70,24);
		f.g.fillRect(410,320,70,24);
		f.g.fillRect(284,300,60,26); 
		f.g.fillRect(304,230,20,34);
		f.g.fillRect(172+70+282-50-10-20+2-186,440,110,38);
		f.g.fillRect(172+70+282-50-10-20+2-270,380,40,38);
		f.g.fillRect(172+70+282-50-10-20+2-38,380,40,38);
		f.g.fillRect(172+70+282-50-10-20+2+10-100,390,16,10);
		f.g.fillRect(172+70+282-50-10-20+2+10-200,390,16,10);
		f.g.fillRect(172+70+282-50-10-20+2+10-160,360,30,38);

		f.g.setColor(Color.BLUE);
		f.g.fillRect(143,143,5,325+9);
		f.g.fillRect(143+325+5+4,143,5,325+9);
		f.g.fillRect(143,143,325+9,5);
		f.g.fillRect(143,143+325+9,325+9+5,5);
		
		f.g.fillRect(406,170,48,20); 
		
		f.g.fillRect(406,198,48,6);  
		
		f.g.fillRect(174+70+138-20+2,172,20,30); 
		
		f.g.fillRect(406-2,172,20,30); 
		
		f.g.setColor(Color.BLUE);
		f.g.fillRect(406,224,48,8); //H 4
		
		f.g.fillRect(406,230,48,10);  //H 3
		f.g.fillRect(406-2,224+2,20,12); //  V  1
		f.g.fillRect(174+70,170,30,10);//
		f.g.fillRect(174+70+30+24,170,30,10); //H 4
		f.g.fillRect(174+70+30+24+30+24,170,30,10);//H 4
		
		f.g.fillRect(172+70+30-10+4,172,10,30);//V 2
		f.g.fillRect(172+70+30+24+30+24,172,10,30);//V 1
				
		f.g.fillRect(174+70,194,30,10);//
		f.g.fillRect(174+70+30+24,194,30,10); //H 3
		f.g.fillRect(174+70+30+24+30+24,194,30,10); //H 3
		
		
		f.g.fillRect(172+70+30+24,172-28,10,30+28);//V 1 
		f.g.fillRect(172+70+30+24+30-10+4,172-28,10,30+28);//V 2 
		
		f.g.fillRect(148,260,72,10); //
		f.g.fillRect(174+70,260,30,10);//  H 4
		f.g.fillRect(174+70+30+24,260,30,10); // H 4
		f.g.fillRect(174+70+30+24+30+24,260,30,10);// H 4
		f.g.fillRect(174+70+30+24+30+24+24+30,260,72,10);//  H 4
		
		f.g.fillRect(174+70,260+4,30,12);//  H 3
		
		f.g.fillRect(174+70+30+24,260+4,30,12); // H 3
		
		f.g.fillRect(174+70+30+24+30+24,260+4,30,12);// H 3
		
		f.g.fillRect(172+70+54,226,10,48); // VER DRETA
		f.g.fillRect(172+70+54+24,226,10,48); // VER ESQUERRA
	
		f.g.fillRect(148+72-10+2+54,260+2,10,12); // VER ESQUERRA
		f.g.fillRect(148+72-10+2+54+84,260+2,10,12); // VER DRETA
				
		f.g.fillRect(174+70,240-10,12,10);// HORIT
		
		f.g.fillRect(174+70,224,12,8); //   HORIT
		
		f.g.fillRect(174+70+12+24,240-10,66,10);//  HORIT 3
		
		f.g.fillRect(174+70+12+24,224,66,8); //   HORIT 4
		 
		f.g.fillRect(174+70+12+24+66+24,240-10,12,10);//  HORIT 3
		
		f.g.fillRect(174+70+12+24+66+24,224,12,8); //   HORIT 4
		
		f.g.fillRect(172+70+12+24,226,10,12); // VERT DRETA
		f.g.fillRect(172+70+12+24+66-10+4,226,10,12); // VERT ESQUERRA
		
		f.g.fillRect(174+70+30+24+30+24+24+30,260+4+18,72,12);//  H 3 
		f.g.fillRect(148,260+4+18,72,12); // H 3 RETOCAT 
		f.g.fillRect(148+72-10+2+54+138,260+2,10,12+18); // VER DRETA 
		f.g.fillRect(148+72-10+2,260+2,10,12+18); // VER ESQUERRA 
		
	
		f.g.fillRect(174+70+30+24+30+24+24+30,260+4+18+24+12-4,72,12);//  H 4 
		f.g.fillRect(148,260+4+18+24+12-4,72,12); // H 4 
		f.g.fillRect(148+72-10+2+54+138,260+2+24+12+18,10,12+18); // VER DRETA 
		f.g.fillRect(148+72-10+2,260+2+24+12+18,10,12+18); // VER ESQUERRA 
		f.g.fillRect(174+70+30+24+30+24+24+30,260+4+18+24+12-4+18+4,72,12);//  H 3 
		f.g.fillRect(148,260+4+18+24+12-4+18+4,72,12); // H 3 
	
		
		f.g.fillRect(172+70,226,10,66); //RETOCAT 
		
		f.g.fillRect(172+70+12-10+4,226,10,66);  //RETOCAT 
		
		f.g.fillRect(172+70+12+24+66+24,226,10,66);  //RETOCAT 
		
		f.g.fillRect(174+70+138-10+2,224+2,10,66);  //RETOCAT 
		f.g.fillRect(174+70+12+24+66+24,224+62,12,8); // H 3
		f.g.fillRect(174+70,224+62,12,8); // H 3

		f.g.fillRect(174+70+12+24,260+4+18+24+12-4-20+2,66,10); // H 4 modificar 
		f.g.setColor(Color.BLUE);
		f.g.fillRect(174+70+12+24,260+4+18+24+12-4-20+2,12,3); //h4 nova

		f.g.fillRect(174+70+12+24,260+4+18+24+12-4-20+2+3,12,3); //h3 nova
		f.g.fillRect(174+70+12+24+54,260+4+18+24+12-4-20+2,12,3); //h4 nova
		f.g.fillRect(174+70+12+24+54,260+4+18+24+12-4-20+2+3,12,3); //h3 nova
		f.g.fillRect(174+70+12+24+12,260+4+18+24+12-4-20+2+1,2,4); //v2 nova
		f.g.fillRect(174+70+12+24+12+40,260+4+18+24+12-4-20+2+1,2,4); //v1
		
		f.g.fillRect(174+70+12+24,260+4+18+24+12-4-20+24+2+8,66,3); // H3 RETOCAR 
		f.g.fillRect(174+70+12+24,260+4+18+24+12-4-20+24+2+8-3,66,3); //H4 nova
		f.g.fillRect(172+70+12+24,260+4+18+24+12-4-20+2+2,3,30); // V1 RETOCAR 
		f.g.fillRect(172+70+12+24+3,260+4+18+24+12-4-20+2+2,3,30); //V2 nova
		f.g.fillRect(172+70+12+24+66-10+4+8,260+4+18+24+12-4-20+2+2,3,30); // V2 retocar (172+70+12+24+66-10+4,260+4+18+24+12-4-20+2+2,10,30)
		f.g.fillRect(172+70+12+24+66-10+4+8-3,260+4+18+24+12-4-20+2+2,3,30); //V1 nova
		f.g.fillRect(148+72-10+2+24+10-4,260+2+24+12+18,10,12+18); // VER DRETA 
		
		f.g.fillRect(148+72-10+2+24+10-4+2,260+2+24+12+18-2,12,10); // H 4 
		f.g.fillRect(148+72-10+2+24+10-4+2,260+2+24+12+18-2+12+12,12,10); // H 3 
		
		f.g.fillRect(148+72-10+2+24+10-4+6,260+2+24+12+18,10,12+18); // VER ESQUERRA 
		
		f.g.fillRect(148+72-10+2+24+10-4+126,260+2+24+12+18,10,12+18); // VER DRETA 
		f.g.fillRect(148+72-10+2+24+10-4+2+126,260+2+24+12+18-2,12,10); // H 4 
		f.g.fillRect(148+72-10+2+24+10-4+2+126,260+2+24+12+18-2+12+12,12,10); // H 3 
		f.g.fillRect(148+72-10+2+24+10-4+6+126,260+2+24+12+18,10,12+18); // VER ESQUERRA 
		f.g.fillRect(148+72-10+2+24+10-4,260+2+24+12+18+54,10,12+18); // VER DRETA 
		f.g.fillRect(148+72-10+2+24+10-4+2,260+2+24+12+18-2+54,12,10); // H 4 
		f.g.fillRect(148+72-10+2+24+10-4+2,260+2+24+12+18-2+12+12+54,12,10); // H3 MODIFICAR AQUESTA 
			
		f.g.fillRect(148+72-10+2+24+10-4+6,260+2+24+12+18+54,10,12+18); // VER ESQUERRA 
		f.g.setColor(Color.BLUE);
	
		f.g.fillRect(148+72-10+2+24+10-4+126,260+2+24+12+18+54,10,12+18); // VER DRETA 
		f.g.fillRect(148+72-10+2+24+10-4+2+126,260+2+24+12+18-2+54,12,10); // H 4 
		f.g.fillRect(148+72-10+2+24+10-4+2+126,260+2+24+12+18-2+12+12+54,12,10); // H 3 
		f.g.fillRect(148+72-10+2+24+10-4+6+126,260+2+24+12+18+54,10,12+18); // VER ESQUERRA 
		
		f.g.fillRect(174+70+12+24,260+4+18+24+12-4-20+2+40+14,66,10);//H4 
		
		f.g.fillRect(174+70+12+24,260+4+18+24+12-4-20+2+40+12+8,66,10); //h3
		f.g.fillRect(172+70+12+24,260+4+18+24+12-4-20+2+2+54,10,12); //V1 
		f.g.fillRect(172+70+12+24+60,260+4+18+24+12-4-20+2+2+54,10,12); //V2 
		f.g.setColor(Color.BLUE);
		f.g.fillRect(172+70+12+24+18,260+4+18+24+12-4-20+2+2+54,10,48); //v1 
		f.g.fillRect(172+70+12+24+42,260+4+18+24+12-4-20+2+2+54,10,48); //v2 
		f.g.fillRect(172+70+12+24+20,260+4+18+24+12-4-20+2+40+14+42,30,10); //H3 
		
		
		f.g.fillRect(148+72-10+2+24+10-4+2+108,260+2+24+12+18-2+12+12+60,30,4); // h3 
		f.g.fillRect(148+72-10+2+24+10-4+2+108,260+2+24+12+18-2+12+12+48,30,4); //h4 
		 
		//f.g.setColor(Color.WHITE);
		f.g.fillRect(148+72-10+2+24+10-4+2+106,260+2+24+12+18-2+12+12+50,10,12); //v1

		f.g.fillRect(148+72-10+2+24+10-4+2,260+2+24+12+18-2+12+12+60,30,4); // H3 MODIFICADA 

		f.g.fillRect(148+72-10+2+24+10-4+24,260+2+24+12+18-2+12+12+50,10,12); //V4 

		f.g.fillRect(148+72-10+2+24+10-4+2,260+2+24+12+18-2+12+12+48,30,4); //H4 

		f.g.fillRect(148+72-10+2+24+10-4-72,260+2+24+12+18+54,10,12+36); //v1 
		f.g.fillRect(148+72-10+2+24+10-4-70,260+2+24+12+18+52,48,12); //H4
		f.g.fillRect(148+72-10+2+24+10-4-30,260+2+24+12+18+54,10,12+36); //v2 
		f.g.fillRect(148+72-10+2+24+10-4-70,260+2+24+12+18+52+40,48,12);  //h3
		f.g.fillRect(148+72-10+2+24+10-4+162,260+2+24+12+18+54,10,12+36); //v1 
		f.g.fillRect(148+72-10+2+24+10-4+164,260+2+24+12+18+52,48,12); //H4 
		f.g.fillRect(148+72-10+2+24+10-4+204,260+2+24+12+18+54,10,12+36); //v2 
		f.g.fillRect(148+72-10+2+24+10-4+164,260+2+24+12+18+52+40,48,12); //H3 
		
		f.g.fillRect(148+72-10+2+24+10-4-70,170+270,48,10); // h4 
		f.g.fillRect(148+72-10+2+24+10-4-72,260+2+24+12+144,10,12); //v1 
		f.g.fillRect(148+72-10+2+24+10-4-70,260+2+24+12+18+52+80,48,8); //h3 		
		f.g.fillRect(148+72-10+2+24+10-34,260+2+24+12+144,10,12); //v2 
		
		f.g.fillRect(148+72-10+2+24+10-4-70+234,170+270,48,10); // h4 
		f.g.fillRect(148+72-10+2+24+10-4-72+234,260+2+24+12+144,10,12); //v1 
		f.g.fillRect(148+72-10+2+24+10-4-70+234,260+2+24+12+18+52+80,48,8); //h3  
		f.g.fillRect(148+72-10+2+24+10-34+234,260+2+24+12+144,10,12); //v2 

		f.g.fillRect(242,260+2+24+12+144-18,20,53); //V1 nova
		f.g.fillRect(242+122,260+2+24+12+144-18,20,53); //V2 nova
		f.g.fillRect(244,260+2+24+12+144-20,138,20); //H4 nova
			
		//LINEAS PER TELETRANSPORTE
		f.g.setColor(Color.BLACK);
		f.g.fillRect(142,294,6,20);
		f.g.fillRect(476,294,6,20);
		
		f.repaint();
		
	}
	
	void Imatges(){

		try {
			fotointro = ImageIO.read(new File("fotos/intro7.png"));
			fotogameover = ImageIO.read(new File("fotos/gameoverBO.png"));
			Cotxe.coco1dreta = ImageIO.read(new File("fotos/coco1dreta.png"));
			Cotxe.coco2dreta = ImageIO.read(new File("fotos/coco2dreta.png"));
			Cotxe.coco3 = ImageIO.read(new File("fotos/coco3.png"));
			Cotxe.coco1esquerra = ImageIO.read(new File("fotos/coco1esquerra.png"));
			Cotxe.coco2esquerra = ImageIO.read(new File("fotos/coco2esquerra.png"));
			Cotxe.coco1arriba = ImageIO.read(new File("fotos/coco1arriba.png"));
			Cotxe.coco2arriba = ImageIO.read(new File("fotos/coco2arriba.png"));
			Cotxe.coco1abaix = ImageIO.read(new File("fotos/coco1abaix.png"));
			Cotxe.coco2abaix = ImageIO.read(new File("fotos/coco2abaix.png"));
			Fantasma.fantasma1 = ImageIO.read(new File("fotos/fantasma1.png"));
			Fantasma.fantasma1esq = ImageIO.read(new File("fotos/fantasma1esq.png"));
			Fantasma.fantasma2 = ImageIO.read(new File("fotos/fantasma2.png"));
			Fantasma.fantasma2esq = ImageIO.read(new File("fotos/fantasma2esq.png"));
			Fantasma.fantasma3 = ImageIO.read(new File("fotos/fantasma3.png"));
			Fantasma.fantasma3esq = ImageIO.read(new File("fotos/fantasma3esq.png"));
			Fantasma.fantasma4 = ImageIO.read(new File("fotos/fantasma4.png"));
			Fantasma.fantasma4esq = ImageIO.read(new File("fotos/fantasmaesq4.png"));
			Fantasma.fantasmablau = ImageIO.read(new File("fotos/fantasmablau.png"));
			Moneda.moneda = ImageIO.read(new File("fotos/moneda.png"));
			Powerup.powerup = ImageIO.read(new File("fotos/powerup.png"));
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void keyReleased(KeyEvent e){
		
	}
	public void keyPressed(KeyEvent e){

		if(e.getKeyCode()==KeyEvent.VK_LEFT) {
			left=1;
			up=0;
			down=0;
			right=0;
		}
		else if(e.getKeyCode()==KeyEvent.VK_UP) {
			up=1;
			left=0;
			down=0;
			right=0;
		}
		else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
			right=1;
			left=0;
			up=0;
			down=0;
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN) {
			down=1;
			left=0;
			up=0;
			right=0;
		}
		else if(e.getKeyCode()==KeyEvent.VK_SPACE) {
			comencem=1;
		}
		else {
			
		}
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
}
	
	
