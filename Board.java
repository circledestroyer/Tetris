package tetris;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import tetris.Blocks.TetrisShape;
import tetris.Sound.*;
import java.io.*;
import java.util.Scanner;

public class Board extends JPanel implements ActionListener {

	//Defining variables
	private static final int WIDTH_BOARD = 10;
	private static final int HEIGHT_BOARD = 20;
	private int DELAY = 400;

	public Timer timer;

	private boolean doneFalling = false;
	private boolean gameStarted = false;
	private boolean gamePaused = false;

	private int linesCleared = 0;
	private int highscore;

	private int currentX = 0;
	private int currentY = 0;

	private JLabel menu;
	public Clip song; //For playing our music

	private Blocks currentBlock; //currentPiece > currentBlock 
	private static final Color[] COLORS = { new Color(0,0,0), new Color(25,45,146),
			new Color(118,53,133), new Color(210,60,119), new Color(255,79,121),
			new Color(255,153,127), new Color(255,66,0), new Color(127,229,255)};
	private TetrisShape[] board; //Array of Tetris shapes called board

	public Board(Tetris parent) {
		setFocusable(true);
		currentBlock = new Blocks();
		timer = new Timer(DELAY,this); //Timer for dropping
		menu = parent.getMenu();

		board = new TetrisShape[WIDTH_BOARD * HEIGHT_BOARD];
		//Constructor! Sets the array to the dimensions of the board
		clearBoard(); // clears all inside
		
		addKeyListener(new MyTetrisAdapter());
		setBackground(Color.black);
		
		//Calling to Sound class, load background song
		song = Sound.loadMusic("tetris_theme.wav"); //Didn't work in another directory, in src/tetris/
		song.loop(Clip.LOOP_CONTINUOUSLY);
		fileReader();
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
		//Offset differently depending on the shape
		currentY = HEIGHT_BOARD - 1 + currentBlock.minY();

		//GAME OVER CONDITION
		if(!tryMove(currentBlock, currentX, currentY - 1)) {
			currentBlock.setBlock(TetrisShape.N);
			timer.stop();
			gameStarted = false;
			menu.setText("Game Over");
			fileReader();
			
			if(linesCleared > highscore) {
				try {
					PrintWriter outputStream = new PrintWriter(new FileOutputStream("highscores.txt"));
					outputStream.print(linesCleared);
					outputStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
				highscore = linesCleared;
				System.out.println("High score is now: " + highscore);
			}
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
				clearCheck += 2; //This will be added to the score

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
				changeLvl(linesCleared); //Updating timer, increasing level
				//System.out.println(this.DELAY);
				menu.setText(String.valueOf(linesCleared));
				currentBlock.setBlock(TetrisShape.N); //Set the current block to No shape?
				repaint();
			}
		}
	}
	
	public void fileReader() {
		File file = new File("highscores.txt");
		try {
			System.out.println("Located highscores.txt...");
			Scanner HScan = new Scanner(file);
			
			highscore = Integer.parseInt(HScan.nextLine());
			System.out.println("High score: " + highscore);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
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
	
	public void changeLvl(int currentScore) {
		//For changing levels, update delay. Smaller delay ===> faster drop
		if (currentScore % 4 == 0 && this.DELAY >= 50) {
			this.DELAY = this.DELAY - 50;
			timer.setDelay(DELAY);
		}
	}
	class MyTetrisAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent ke) {
			if(!gameStarted || currentBlock.getShape() == Blocks.TetrisShape.N) {
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