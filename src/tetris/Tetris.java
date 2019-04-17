import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JFrame;

public class Tetris extends JFrame {
	private JLabel menu;
	
	public Tetris()
	{
		menu = new JLabel("0");
		add(menu, BorderLayout.SOUTH);
		Board board = new Board(this);
		add(board);
		
		//start lines down
		board.start();
		
		//add one piece
		board.newPiece();
		board.repaint();
		
		setSize(200,400);
		setTitle("Tetris");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
	}
	
	
	public JLabel getMenu()
	{
	 return menu;	
	}
	
	public static void main(String [] args)
	{
		Tetris myTetris = new Tetris();
		myTetris.setLocationRelativeTo(null);
		myTetris.setVisible(true);
		
		
	}
	

}
