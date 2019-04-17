package tetris;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris extends JFrame {
	
	private JLabel menu;
	
	public Tetris() {
		menu = new JLabel("0"); //Numer of lines cleared
		add(menu,BorderLayout.SOUTH);
		Board board = new Board(this);
		add(board);
		
		//Starting the game...
		board.start();
		
		setSize(200,400);
		setTitle("Tetris!");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public JLabel getMenu() {
		return menu;
	}
	
	public static void main(String[] args) {
		Tetris myTetris = new Tetris();
		myTetris.setLocationRelativeTo(null);
		myTetris.setVisible(true);
		myTetris.setResizable(false);
	}
	//MOVEMENT ADDED W/ CONTROL 4/17/19 01:09PM
}