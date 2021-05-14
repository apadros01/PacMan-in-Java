import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Finestra extends Frame implements WindowListener {

	private static final long serialVersionUID = 1L;
	int AMPLE=600; //quan compilem el programa, surt una finestra, amb això direm les mides.
	int ALT=600;
	
	Image img;
	Graphics g;
	
	public static void main(String[] args) throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
		new Finestra();
	}
	Finestra() throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
		super("PACMAN");
		this.setSize(AMPLE, ALT);
		this.setVisible(true);
		setLocationRelativeTo(null); //això fa que aparegui al mig de la pantalla
		addWindowListener(this);
		img=createImage(AMPLE,ALT);
		g=img.getGraphics();
		Joc joc=new Joc(this);
		joc.executa();
		
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics g) {
		g.drawImage(img,0,0,null);
	}
	
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowClosing(WindowEvent e){System.exit(0);}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
}