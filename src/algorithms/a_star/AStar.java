package algorithms.a_star;

import java.util.PriorityQueue;

import Ares.Common.Location;
import Ares.Common.World.Grid;
import Ares.Common.World.World;

// Augmented version from basic implementation of A* on the web: http://www.codebytes.in/2015/02/a-shortest-path-finding-algorithm.html

public class AStar 
{
	public static final int DIAGONAL_COST = 1;
	public static final int V_H_COST = 1;

	static class Cell
	{  
		int heuristicCost = 0; //Heuristic cost
		int moveCost = 0;
		int finalCost = 0; //G+H
		int i, j;
		Cell parent; 

		Cell(int i, int j)
		{
			this.i = i;
			this.j = j; 
		}

		@Override
		public String toString()
		{
			return "["+this.i+", "+this.j+"]";
		}
	}

	//Blocked cells are just null Cell values in grid
	static Cell [][] grid;

	static PriorityQueue<Cell> open;

	static boolean closed[][];
	static int startI, startJ;
	static int endI, endJ;

	public static void setBlocked(int i, int j)
	{
		grid[i][j] = null;
	}

	public static void setStartCell(int i, int j)
	{
		startI = i;
		startJ = j;
	}

	public static void setEndCell(int i, int j)
	{
		endI = i;
		endJ = j; 
	}

	static void checkAndUpdateCost(Cell current, Cell t, int cost)
	{
		if(t == null || closed[t.i][t.j])return;
		int t_final_cost = t.heuristicCost+cost;

		boolean inOpen = open.contains(t);
		if(!inOpen || t_final_cost<t.finalCost)
		{
			t.finalCost = t_final_cost;
			t.parent = current;
			if(!inOpen)open.add(t);
		}
	}

	public static void do_A_star()
	{
		//add the start location to open list.
		open.add(grid[startI][startJ]);

		Cell current;

		while(true)
		{
			current = open.poll();
			if(current==null)break;
			closed[current.i][current.j]=true; 

			if(current.equals(grid[endI][endJ]))
			{
				// Finished, reached end
				return; 
			} 

			Cell t;  
			int cost = 0;
			if(current.i-1>=0)
			{
				t = grid[current.i-1][current.j];
				
				if (t != null)
				{
					cost = t.moveCost;
				}
				checkAndUpdateCost(current, t, current.finalCost + cost); 

				if(current.j-1>=0)
				{      
					t = grid[current.i-1][current.j-1];
					
					if (t != null)
					{
						cost = t.moveCost;
					}
					checkAndUpdateCost(current, t, current.finalCost + cost);  
				}

				if(current.j+1<grid[0].length)
				{
					t = grid[current.i-1][current.j+1];				
					
					if (t != null)
					{
						cost = t.moveCost;
					}
					checkAndUpdateCost(current, t, current.finalCost + cost); 
				}
			} 

			if(current.j-1>=0)
			{
				t = grid[current.i][current.j-1];				
				
				if (t != null)
				{
					cost = t.moveCost;
				}
				checkAndUpdateCost(current, t, current.finalCost + cost);  
			}

			if(current.j+1<grid[0].length)
			{
				t = grid[current.i][current.j+1];
				
				if (t != null)
				{
					cost = t.moveCost;
				}
				checkAndUpdateCost(current, t, current.finalCost + cost);  
			}

			if(current.i+1<grid.length)
			{
				t = grid[current.i+1][current.j];
				
				if (t != null)
				{
					cost = t.moveCost;
				}
				checkAndUpdateCost(current, t, current.finalCost + cost);  

				if(current.j-1>=0)
				{
					t = grid[current.i+1][current.j-1];
					
					if (t != null)
					{
						cost = t.moveCost;
					}
					checkAndUpdateCost(current, t, current.finalCost + cost);  
				}

				if(current.j+1<grid[0].length)
				{
					t = grid[current.i+1][current.j+1];
					
					if (t != null)
					{
						cost = t.moveCost;
					}
					checkAndUpdateCost(current, t, current.finalCost + cost);  
				}  
			}
		} 
	}

	/***
	 * 
	 * This function runs A*. Not optimized (no caching - new grid created each time it's ran!)
	 * 
	 * @param world - World object
	 * @param start_row - start location's row coordinates
	 * @param start_col - start location's column coordinates
	 * @param end_row - end location's row coordinates
	 * @param end_col - end location's column coordinates
	 */
	public static int last_path_length = 0;
	public static int last_path_cost = 0;
	public static Location test(World world_wrapper, int start_row, int start_col, int end_row, int end_col)
	{
		Grid[][] world = world_wrapper.getGrid();
		
		int max_rows = world.length;
		int max_cols = world[0].length;

		//Set up the world
		grid = new Cell[max_rows][max_cols];
		closed = new boolean[max_rows][max_cols];

		open = new PriorityQueue<>((Object o1, Object o2) -> {
			Cell c1 = (Cell)o1;
			Cell c2 = (Cell)o2;

			return c1.finalCost<c2.finalCost?-1:
				c1.finalCost>c2.finalCost?1:0;
		});

		//Set start position
		setStartCell(start_row, start_col);  //Setting to 0,0 by default. Will be useful for the UI part

		//Set End Location
		setEndCell(end_row, end_col); 

		for(int i=0;i<max_rows;++i){
			for(int j=0;j<max_cols;++j)
			{
				grid[i][j] = new Cell(i, j);
				grid[i][j].heuristicCost = Math.abs(i-endI)+Math.abs(j-endJ);
				grid[i][j].moveCost = world[i][j].getMoveCost();				
				
				// Set blocked cells. Simply set the cell values to nullfor blocked cells.
				if (world[i][j].isKiller())
				{
					grid[i][j] = null;
				}
			}
		}
		grid[start_row][start_col].finalCost = 0;

		// Run A star
		do_A_star(); 
		
		//Display map
		//System.out.println("Grid: ");
//		for(int i=0;i<max_rows;++i)
//		{
//			for(int j=0;j<max_cols;++j)
//			{
//				if(i==start_row&&j==start_col)System.out.print("SO  "); //Source
//				else if(i==end_row && j==end_col)System.out.print("DE  ");  //Destination
//				else if(grid[i][j]!=null)System.out.printf("%-3d ", grid[i][j].finalCost);
//				else System.out.print("BL  "); 
//			}
//			//System.out.println();
//		} 

		//System.out.println();

		//Trace back the path 
		if(closed[endI][endJ])
		{
			//System.out.println("Path: ");
			Cell current = grid[endI][endJ];
			Cell prev = null;
			//System.out.print(current);
			while(current.parent != null)
			{
				//System.out.print(" -> "+current.parent);
				prev = current;
				current = current.parent;
				AStar.last_path_cost+=prev.finalCost;
				AStar.last_path_length ++;
			} 
			//System.out.println();

			return new Location(prev.i, prev.j);
		}
		else 
		{
			System.out.println("No possible path");
			return null;
		}
		
	}
}
