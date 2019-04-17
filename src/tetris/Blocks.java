//Called Blocks, not Shapes
package tetris;

import java.util.Random;

public class Blocks {

	//enum contains coordinates of each piece
	enum TetrisShape {
		N(new int[][] { {0,0}, {0,0}, {0,0}, {0,0} }), //No shape
		Z(new int[][] { {0,-1}, {0,0}, {-1,0}, {-1,1} }), //Z
		S(new int[][] { {0,-1}, {0,0}, {1,0}, {1,1} }), //S
		I(new int[][] { {0,-1}, {0,0}, {0,1}, {0,2} }), //Line
		T(new int[][] { {-1,0}, {0,0}, {1,0}, {0,1} }), //T
		O(new int[][] { {0,0}, {1,0}, {0,1}, {1,1} }), //Square
		L(new int[][] { {-1,-1}, {0,-1}, {0,0}, {0,1} }), //L
		J(new int[][] { {-1,1}, {0,-1}, {0,0}, {0,1} }); //J

		public int[][] coords; //Coordinate array

		private TetrisShape(int[][] coords) { //Enum constructor
				this.coords = coords;
		}
	}

	private TetrisShape blockShape; //pieceShape > blockShape
	private int[][] coords;

	//Constructor
	public Blocks() {
		coords = new int[4][2]; //4x2 array is the coordinate system
		//Block is never more than 2 wide or 4 tall?
		setBlock(TetrisShape.N); //Default shape is nothing

	}

	public void setBlock(TetrisShape shape) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; ++j) {
				coords[i][j] = shape.coords[i][j];
				//Loops through the 4x2 array and sets coords
				//Coords taken from enumerator, coords in constructor
			}
		}
		blockShape = shape; //set the field var blockShape to this shape
		//Should have coordinates in it.
	}

	private void setX(int index, int x) {
		coords[index][0] = x;
		//Setting proper array coordinates again?
	}

	private void setY(int index, int y) {
		coords[index][1] = y;
		//Setting array coords for y?
	}

	//x > getX ?
	public int getX(int index) {
		return coords[index][0];
		//returns values from coords
	}
	//y > getY ?
	public int getY(int index) {
		return coords[index][1];
		//returns values from coords
	}

	public TetrisShape getShape() {
		return blockShape;
		//Recall that pieceShape is blockShape here
		//return the shape type?
	}

	public void setRandomShape() {
		Random r = new Random();
		//Unsure about why the remainder, found from tutorial
		int x = Math.abs(r.nextInt()) % 7 + 1;
		TetrisShape[] values = TetrisShape.values();
		//values() is from enumerator
		setBlock(values[x]);
		//Set coordinates of each item in the values array?
	}

	public int minX() {
		//Set initial value from the array of coords
		int m = coords[0][0];

		for (int i = 0; i < 4; i++) {
			//if the current index is less, set it to m
			m = Math.min(m, coords[i][0]);
		}

		return m; //returns min
	}

	public int minY() {
		//Set initial value from the array of coords
		int m = coords[0][1];

		for (int i = 0; i < 4; i++) {
			//if the current index is less, set it to m
			m = Math.min(m, coords[i][1]);
		}

		return m; //returns min
	}

	public Blocks rotateLeft() { //Returns a Blocks object
		//blockShape is an object of the enum TetrisShape
	 if (blockShape == TetrisShape.O)
		 return this;

	 Blocks result = new Blocks();
	 result.blockShape = blockShape;

	 for (int i = 0; i < 4; i++) {
		 result.setX(i, getY(i));
		 result.setY(i, -getX(i));
	 }

	 return result;
 }

 public Blocks rotateRight() { //Returns a Blocks object
	 //blockShape is an object of the enum TetrisShape
	 if (blockShape == TetrisShape.O)
		 return this;

	 Blocks result = new Blocks();
	 result.blockShape = blockShape;

	 for (int i = 0; i < 4; i++) {
		 result.setX(i, -getY(i));
		 result.setY(i, getX(i));
	 }

	 return result;
 }
//Rotation Functions implemented 4/17/19 1:38 AM
}