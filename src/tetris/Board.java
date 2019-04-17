tryMovepackage Tetris;

import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import Tetris.Blocks.TetrisShape;

public class Board extends JPanel implements ActionListener {

	//Defining variables
	private static final int WIDTH_BOARD = 10;
	private static final int HEIGHT_BOARD = 20;
	private static final int DELAY = 400;

	private Timer timer;

	private boolean doneFalling = false;
	private boolean gameStarted = false;
	private boolean gamePaused = false;

	private int linesCleared = 0;

	private int currentX = 0;
	private int currentY = 0;

	private JLabel menu;

	private Blocks currentBlock; //currentPiece > currentBlock
	private static final Color[] COLORS = { new Color(0,0,0), new Color(204,102,102),
			new Color(102,204,102), new Color(102,102,204), new Color(204,204,102),
			new Color(204,102,204), new Color(102,204,204), new Color(218,170,0)};
	private TetrisShape[] board; //Array of Tetris shapes called board

	public Board(Tetris parent) {
		setFocusable(true);
		currentBlock = new Blocks();
		timer = new Timer(DELAY,this); //Timer for dropping
		menu = parent.getMenu();

		board = new TetrisShape[WIDTH_BOARD * HEIGHT_BOARD];
		//Constructor! Sets the array to the dimensions of the board
		clearBoard(); // clears all inside
	}

	public int squareWidth() {
		//Dimensions of each little block in a shape
		return (int) getSize().getWidth() / WIDTH_BOARD; //From awt.dimension
	}

	public int squareHeight() {
		return (int) getSize().getHeight() / HEIGHT_BOARD; //From awt.dimension
	}

	public TetrisShape shapeAt(int x, int y) {
		//Returns a type of shape based on position
		return board[y * WIDTH_BOARD + x];
	}

	private void clearBoard() {
		for (int i = 0; i < HEIGHT_BOARD * WIDTH_BOARD; i++) {
			board[i] = TetrisShape.N;
		}
	}

	private void pieceDropped() {
		for (int i = 0; i < 4; i++) {
			int x = currentX + currentBlock.getX(i);
			int y = currentY - currentBlock.getY(i);
			board[y* WIDTH_BOARD + x] = currentBlock.getShape();
			//This method drops a piece, updates coordinates
			//AND updates the board array
		}

		clearLine(); //Check for full lines and remove them if needed

		if (!doneFalling) {
			newBlock();
		}
	}

	//newPiece > newBlock
	public void newBlock() {
		currentBlock.setRandomShape();
		currentX = WIDTH_BOARD /2 + 1;
		//CurrentY depends on dimension of the new block
		//Offsent differently depending on the shape
		currentY = HEIGHT_BOARD - 1 + currentBlock.minY();

		//GAME OVER CONDITION
		if(!tryMove(currentBlock, currentX, currentY - 1)) {
			currentBlock.setBlock(TetrisShape.N);
			timer.stop();
			gameStarted = false;
			menu.setText("Game Over");
		}
	}

	private void moveDown() {
		if (!tryMove(currentBlock, currentX, currentY -1)) {
			pieceDropped();
		}
	}


	@Override
	public void actionPerformed(ActionEvent ae) {
		if (doneFalling) {
			doneFalling = false;
			newBlock();
		}
		else {
			moveDown();
		}
	}

	private void drawSquare(Graphics g, int x, int y, TetrisShape shape) {
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
				Blocks.TetrisShape shape = shapeAt(j,HEIGHT_BOARD - i - 1);

			if(shape != Blocks.TetrisShape.N) {
				drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
				}
			}
		}

		if(currentBlock.getShape() != Blocks.TetrisShape.N) {
			for(int i = 0; i<4; ++i) {
				//If its not NoShape:
				//Loop through the currentBlocks coords and paint
				int x = currentX + currentBlock.getX(i);
				int y = currentY - currentBlock.getY(i); //NOTE currentY - currentBlock fixed the mirror problem
				drawSquare(g, x * squareWidth(), boardTop + (HEIGHT_BOARD - y - 1)*squareHeight(),
						currentBlock.getShape());
			}
		}
	}

	public void start() {
		if(gamePaused) {
			//Check for pause
			return;
		}
		//If not paused, run the start procedure
		gameStarted = true; //Set status
		doneFalling = false; //NOT done falling
		linesCleared = 0; //Reset score
		clearBoard();
		newBlock();
		timer.start();
	}

	public void pause() {
		if (!gameStarted) {
			return;
		}
		if (gamePaused) {
			timer.stop();
			menu.setText("Paused");
		}
		else {
			timer.start();
			menu.setText(String.valueOf(linesCleared));
		}

		repaint(); //Essentially refreshing
	}

	private boolean tryMove(Blocks newB, int newX, int newY) {
		//Returns a boolean, takes in a Block, and coords
		for(int i = 0; i < 4; ++i) {
			//Set coordinates relative to current getX/getY positions
			int x = newX + newB.getX(i);
			int y = newY - newB.getY(i);

			//Test if the new coords are valid
			if ( x < 0 || x >= WIDTH_BOARD || y < 0 || y >= HEIGHT_BOARD) {
				return false;
			}

			if (shapeAt(x,y) != TetrisShape.N) {
				return false;
			}

		}

		//Passed all tests, set current coords to new coords
		currentBlock = newB;
		currentX = newX;
		currentY = newY;
		repaint();
		return true;
	}

	private void clearLine() {
		int clearCheck = 0;

		//Checking if the line is full
		for (int i = HEIGHT_BOARD -1; i >= 0; --i) {
			//Assume its full, but test with loops
			boolean lineFull = true;
			//For each y,
			for (int j = 0; j < WIDTH_BOARD; ++j) {
				//For each x,
				if (shapeAt(j,i) == TetrisShape.N) {
					lineFull = false;
					break;
					//If the shape isn't NOTHING, continue
					//If there is a NOTHING, break
				}
			}

			if (lineFull) {
				++clearCheck;

				for (int k = i; k < HEIGHT_BOARD -1; ++k ) {
					for (int j = 0; j < WIDTH_BOARD; ++j) {
						board[k * WIDTH_BOARD + j] = shapeAt(j, k+1);
					}
				}
			}

			if (clearCheck > 0) {
				//If its greater than zero, the line is full
				//Do this procedure for a full line...
				linesCleared = linesCleared + clearCheck; //Update score
				menu.setText(String.valueOf(linesCleared));
				currentBlock.setBlock(TetrisShape.N); //Set the current block to No shape?
				repaint();
			}
		}
	}

	private void dropDown() {
		int newY = currentY;

		while (newY > 0) {
			if(!tryMove(currentBlock, currentX, newY -1)) {
				//If the test move at the current position fails...
				break;
			}
			--newY; //Decrease newY
		}
		pieceDropped(); //call pieceDropped, which updates coords as a block falls
	}
	class MyTetrisAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent ke) {
			if(!gameStarted || currentBlock.getShape() == Blocks.TetrisShapes.N) {
				return;
			}
			int keyCode = ke.getKeyCode();
			if(keyCode == 'p' || keyCode == 'P') {
				pause();
			}
			if(gamePaused) {
				return;
			}
			switch(keyCode) {
			case KeyEvent.VK_LEFT:
				tryMove(currentBlock, currentX - 1, currentY);
				break;
			case KeyEvent.VK_RIGHT:
				tryMove(currentBlock, currentX + 1, currentY);
				break;
			case KeyEvent.VK_DOWN:
				tryMove(currentBlock.rotateRight(),currentX,currentY);
				break;
			case KeyEvent.VK_UP:
				tryMove(currentBlock.rotateLeft(), currentX, currentY);
				break;
			case KeyEvent.VK_SPACE:
				dropDown();
				break;
			case 'd':
				moveDown();
				break;
			case'D':
				moveDown();
				break;

			}
		}
	}
}
