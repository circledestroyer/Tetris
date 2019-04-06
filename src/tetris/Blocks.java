import java.util.*;
public class Blocks {

	enum TetrisShapes//NEED FOR CHECKPOINT 1
	{//creates the different shapes
	Nothing(new int [][] {{0,0}, {0,0},{0,0}, {0,0}}),
	Z(new int [][] {{0,-1},{0,0},{-1,0},{-1,1}}),
	S(new int [][] {{0,-1},{0,0},{1,0},{1,1}}),
	I(new int [][] {{0,-1},{0,0},{0,1},{0,2}}),
	T(new int [][] {{-1,0},{0,0},{1,0},{0,1}}),
	O(new int [][] {{0,0},{1,0},{0,1},{1,1}}),
	l(new int [][] {{-1,-1},{0,-1},{0,0},{0,1}}),
	J(new int [][] {{1,-1},{0,-1},{0,0},{0,1}});
	
		public int [][] coordinate;//declares the coordinate system
		
		private TetrisShapes(int[][] coordinate) 
		{
		 this.coordinate = coordinate;	//sets the coordinate system for the blocks
		}
		
	}

private TetrisShapes pieceShape;
private int [][] coordinate;

public Blocks()//blocks constructor sets the range of space used for the shapes
{
	coordinate = new int[4][2];
	
}
public void setBlocks(TetrisShapes block)
{
	for(int k = 0; k < 4; k++)
	{
	 for(int i = 0; i < 2; i++)
	 {
		 coordinate[k][i] = block.coordinate[k][i];//Goes through and set the
		 //spaces being used into the coordinate system.
	 }
	}
	
	pieceShape = block;
}	
	private void setX(int index, int x)
	{
		coordinate[index][0] = x;
	}
	private void setY(int index, int y)
	{
	 coordinate[index][1] = y;	
	}
	public int x(int index)
	{
		return coordinate[index][0];
	}
	public int y(int index)
	{
		return coordinate[index][1];
	}
	
	public TetrisShapes getShape()
	{
		return pieceShape;//returns the shape being used
	}
	public void setRandomShape()//randomly chooses the shape that will 
	//be given to the player to use.
	{
		Random r = new Random();
		int x = Math.abs(r.nextInt()) % 7 + 1;
		TetrisShapes [] values = TetrisShapes.values();
		setBlocks(values[x]);
		
	}
	
	
	public int minX() 
	{
		int m = coordinate[0][0];
		for(int i = 0; i < 4;i++)
		{
			m = Math.min(m, coordinate[i][0]);
		}
		return m;
	}
	
	public int minY() 
	{
		int m = coordinate[0][1];
		for(int i = 0; i < 4;i++)
		{
			m = Math.min(m, coordinate[i][1]);
		}
		return m;
	}


public Blocks rotateLeft()//NEED FOR CHECKPOINT 1
{
	if(pieceShape == TetrisShapes.S)
	{
		return this;
	}
	Blocks result = new Blocks();
	result.pieceShape = pieceShape;
	
	for(int i = 0; i < 4; i++)
	{
		result.setX(i,y(i));
		result.setY(i, -x(i));
		
	}
		return result;
}
  
public Blocks rotateRight()//NEED FOR CHECKPOINT 1
{
	if(pieceShape == TetrisShapes.S)
	{
		return this;
	}
	Blocks result = new Blocks();
	result.pieceShape = pieceShape;
	
	for(int i = 0; i < 4; i++)
	{
		result.setX(i, -y(i));
		result.setY(i, x(i));
		
	}
		return result;
}


}


