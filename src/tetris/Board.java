package tetris;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener  {

	//Given Dimensions & other board stuff
	private static final int WIDTH_BOARD = 10;
	private static final int HEIGHT_BOARD = 20;
	private JLabel menu;
	
	//Timer from javax.swing - calls ActionEvents from java.awt.event
	private Timer timer;
	
	//Game States
	private boolean doneFalling = false;
	private boolean gameStarted = false;
	private boolean gamePaused = false;
	
	private int clearedLines = 0; //0 lines cleared at start. GET GOOD
	
	//TODO
	private int currentX = 0;
	private int currentY = 0;
	
	//Interface stuff
	private JLabel menubar;
	
	//Shape stuff
	private Blocks currentBlock;
	private TetrisShapes[] board; //TODO Chris's Enumerator
	private static final Color[] COLORS = { new Color(0,0,0), new Color(204,102,102),
			new Color(102,204,102), new Color(102,102,204), new Color(204,204,102),
			new Color(204,102,204), new Color(102,204,204), new Color(218,170,0)}
	
	public Board(Tetris parent) {
		setFocusable(true); //Something from awt
		currentShape = new Blocks();
		timer = new Timer(400,this); //Timer from javax.swing Googled how to do this
		menu = parent.getMenu(); //Since parent (Tetris) extends JFrame
		board = new TetrisShapes[WIDTH_BOARD * HEIGHT_BOARD]; //TODO
		clearBoard();
		
	}
	
	public int squareWidth() {
		return (int) getSize().getWidth() / WIDTH_BOARD;
	}
	
	public int squareHeight() {
		return (int) getSize().getWidth() / HEIGHT_BOARD;
	}
	
	public TetrisShapes shapePos(int x, int y) {
		return board[y * WIDTH_BOARD + x];
	}
	
	private void clearBoard() {
		for (int i = 0; i < HEIGHT_BOARD * WIDTH_BOARD; i++) {
			board[i] = TetrisShapes.Nothing;
		}
	}
	
	private void dropBlock() {
		for(int i = 0; i < 4; i++) {
			int x = currentX + currentBlock.x(i);
			int y = currentY - currentBlock.y(i);
			board[y * WIDTH_BOARD + x] = currentBlock.getShape(); //getShape from Shapes
			
		}
		
		clearFullLine();
		
		if (doneFalling == false) {
			newBlock();
		}
	}
	
	public void newBlock() {
		currentBlock.setRandomShape();
		currentX = WIDTH_BOARD / 2 + 1;
		currentY = HEIGHT_BOARD - 1 + currentBlock.minY();
	
		if(!testMove(currentBlock, currentX, currentY - 1)) {
			currentBlock.setBlocks(TetrisShapes.Nothing);
			timer.stop();
			gameStarted = false;
			menu.setText("GAME OVER");
		}
	}
	
	private void moveDown() {
		if(!testMove(currentBlock, currentX, currentY - 1)) {
			dropBlock();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent a) {
		if(doneFalling) {
			doneFalling = false;
			newBlock();
		}
		else {
			moveDown();
		}
	}
	
	private void drawSquare(Graphics g, int x, int y, TetrisShapes shape) {
		Color color = COLORS[shape.ordinal()];
		g.setColor(color);
		g.fillRect(x+1, y+1, squareWidth()-2, squareHeight()-2);
		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1,x,y);
		g.drawLine(x, y,x + squareWidth() - 1,y);
		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1,x + squareWidth() -1,y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,x + squareWidth()-1,y+1);
		
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		
		int boardTop = (int) size.getHeight() - HEIGHT_BOARD * squareHeight();
		
		for(int i = 0; i < HEIGHT_BOARD; i++) {
			for(int j = 0; j < WIDTH_BOARD; ++j) {
				TetrisShapes shape = shapePos(j,HEIGHT_BOARD - i - 1);
			
			if(shape != TetrisShapes.Nothing) {
				drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
			}
		}
	}
	
	if(currentBlock.getShape() != TetrisShapes.Nothing) {
		for(int i = 0; i<4; ++i) {
			int x = currentX + currentBlock.x(i);
			int y = currentY + currentBlock.y(i);
			drawSquare(g, x * squareWidth(), boardTop + (HEIGHT_BOARD - y - 1)*squareHeight(),
					currentBlock.getShape());
		}
	}
	
	}
	public void start() {
		if(gamePaused) return;
		
		gameStarted = true;
		doneFalling = true;
		clearedLines = 0;
		clearBoard();
		newBlock();
		timer.start();
		
	}
	
	public void pause() {
		if (gameStarted == false) return;
		
		gamePaused = !gamePaused; //Whatever it is, switch
		
		if (gamePaused) {
			timer.stop();
			menu.setText("Paused");
		}
		else {
			timer.start();
			menu.setText(String.valueOf(clearedLines));
		}
		
		repaint();
	}
	
	private boolean testMove(Shape newPiece, int x2, int y2) {
		for(int i = 0; i<4; ++i) {
			int x = x2 + newBlock.x(i);
			int y = y2 + newBlock.y(i);
		}
		//now check if the move is possible
		if(x < 0 || x >= WIDTH_BOARD || y < 0 || y >= HEIGHT_BOARD) {
			return false;
		}
		//If a piece is there, can't move there
		if(shapePos(x,y) != TetrisShapes.Nothing) {
			return false;
		}
		
		//Before the current block is generated, it's move has to be validated
		currentBlock = newPiece;
		currentX = x2;
		currentY = y2;
		return true;
	}
	
	private void clearFullLine() {
		int numFullLines = 0;
		
		for (int i = HEIGHT_BOARD - 1; i >=0; --i) {
			//assume full, continue testing
			boolean fullLine = true;
			
			for(int j = 0; j < HEIGHT_BOARD; ++j) {
				//If there's no shape for any of the coordinates, its not full
				if(shapePos(j,i) == TetrisShapes.Nothing) {
					fullLine = false;
					break;
				}
			}
			
			if (fullLine) {
				numFullLines++;
				
				for (int k = i; k < HEIGHT_BOARD; ++k) {
					for(int j = 0; j < WIDTH_BOARD; ++j) {
						board[k*WIDTH_BOARD + j] = shapePos(j,k+1);
					}
				}
			}
			if (numFullLines > 0) {
				clearedLines += numFullLines;
				menu.setText(String.valueOf(clearedLines));
				doneFalling = true;
				currentBlock.setBlocks(TetrisShapes.NoShape);
				repaint();
			}
		}
	}
	
	private void dropDown() {
		int y2 = currentY;
		
		while (y2 > 0) {
			if(!testMove(currentBlock,currentX,currentY -1)) {
				break;
			}
			--y2;
		}
		dropBlock();
	}
	
}

