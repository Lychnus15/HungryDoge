package Doge;

import javax.swing.JFrame; 

public class Doge extends JFrame{

	public Doge() {
		add(new Model());
	}
	
	
	public static void main(String[] args) {

		Doge doge = new Doge();
		doge.setVisible(true);
		doge.setTitle("Doge");
		doge.setSize(380,420);
		doge.setDefaultCloseOperation(EXIT_ON_CLOSE);
		doge.setLocationRelativeTo(null);
		
	}
}
