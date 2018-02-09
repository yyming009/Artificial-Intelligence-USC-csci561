package hw2;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class gameAgent {
	
	private int rows;
	private int cols;
	private double timeRemain;
	private int numOfFruit;
	
	private int[][] board;
	private int[] position;
	
	long startTime;
	long endTime;
	long runTime;

	//constructor of initial variables
	public gameAgent(int rows, int cols, int numOfFruit, double timeRemain, int[][] board) {
		this.rows = rows;
		this.cols = cols;
		this.numOfFruit = numOfFruit;
		
		this.timeRemain = timeRemain;
		runTime = (long) (timeRemain * 0.2 * 1000);
		
		this.board = board;
		
		
//		for (int p = 0; p < rows; p++) {
//			for (int q = 0; q < cols; q++) {
//				System.out.print(board[p][q]);
//			}
//			System.out.println("");
//		}
	} 
	
	public int[] getPosition() {
		return position;
	}
	
	public int[][] getResultBoard () {
		
//		for (int p = 0; p < rows; p++) {
//			for (int q = 0; q < cols; q++) {
//				System.out.print(board[p][q]);
//			}
//			System.out.println("");
//		}
		if (rows <= 2 || timeRemain <= 5) {
			position = greedyPick(board);
		} else {
			position = minimaxPick(board);
		}
		
		int i = position[0];
		int j = position[1];
		System.out.println(i);
		System.out.println(j);
		System.out.println(position[2]);

		pick(board, i, j);
		downFill(board);
		return board;
	}
	
	//main program to trigger the a/b algorithm
	public int[] minimaxPick(int[][] tempBoard){
		
		int[] result = new int[3];
		
		int[][] scorePenal = scorePenal(tempBoard);
		
		//set up the search depth
		int depth;
		if (timeRemain <= 20) {
			depth = 2;
		} else if (timeRemain <= 120){
			depth = 3;
		} else {
			depth = 4;
		}
		
		
		int myScore = 0;
		int opScore = 0;
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		
		startTime = System.currentTimeMillis();
		System.out.println(startTime);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int[][] copyBoard = copyBoard(tempBoard);
				if (tempBoard[i][j] != -1) {
					myScore = scorePenal[i][j] * scorePenal[i][j];
					pick(copyBoard, i, j);
					downFill(copyBoard);
					
					if (!isFinished(copyBoard)) {
						opScore = getMin(copyBoard, depth-1, max, min);
					}
					
					if(myScore - opScore > max){
						max = myScore - opScore;
						result[0] = i;
						result[1] = j;	
						result[2] = scorePenal[i][j];
					}
				}
			}
		}
		return result;
	}
	
	//minimax algorithm - get minimum layer value 
	private int getMin(int[][] tempBoard, int depth, int max, int min) {
		
		if(depth <= 1) {
			int greedy = greedyPick(tempBoard)[2];
			return greedy * greedy;
		}
		
		int[][] scorePenal = new int[rows][cols];
		scorePenal = scorePenal(tempBoard);
		
		int myScore = 0;
		int opScore = 0;
		int p = Integer.MAX_VALUE;
		
		int[] minPosition = new int[2];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int[][] copyBoard = copyBoard(tempBoard);
				if (tempBoard[i][j] != -1) {
					opScore = scorePenal[i][j] * scorePenal[i][j];
					pick(copyBoard, i, j);
					downFill(copyBoard);
			
					if(!isFinished(copyBoard)) {
						myScore = getMax(copyBoard, depth-1, max, min);
						//System.out.println(myScore);

					}
					
					if (p > myScore - opScore) {
						p = myScore - opScore;
						minPosition[0] = i;
						minPosition[1] = j;
					}
					
					if(p <= max) {
						return opScore;
					}
					min = Math.min(min, p);
					
					endTime = System.currentTimeMillis();
					System.out.println(endTime - startTime);
					if (endTime - startTime > runTime && timeRemain > 20) {
						return opScore;
					}
				}
			}
		}
		return scorePenal[minPosition[0]][minPosition[1]] * scorePenal[minPosition[0]][minPosition[1]];			
	}
	
	//minimax algorithm - get maximum layer value 
	private int getMax(int[][] tempBoard, int depth, int max, int min) {
		
		if(depth <= 1) {
			int greedy = greedyPick(tempBoard)[2];
			return greedy * greedy;
		}
		
		int[][] scorePenal = new int[rows][cols];
		scorePenal = scorePenal(tempBoard);
		
		int myScore = 0;
		int opScore = 0;
		int p = Integer.MIN_VALUE;
		
		int[] maxPosition = new int[2];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int[][] copyBoard = copyBoard(tempBoard);
				if (tempBoard[i][j] != -1) {
					myScore = scorePenal[i][j] * scorePenal[i][j];
					pick(copyBoard, i, j);
					downFill(copyBoard);
			
					if(!isFinished(copyBoard)) {
						opScore = getMin(copyBoard, depth-1, max, min);
						//System.out.println(opScore);
					}
					
					if (p < myScore - opScore) {
						p = myScore - opScore;
						maxPosition[0] = i;
						maxPosition[1] = j;
					}
				
					if(p >= min) {
						return myScore;
					}
					max = Math.max(max, p);
					
					endTime = System.currentTimeMillis();
					System.out.println(endTime - startTime);
					if (endTime - startTime > runTime && timeRemain > 20) {
						return opScore;
					}
				}
			}
		}
		return scorePenal[maxPosition[0]][maxPosition[1]] * scorePenal[maxPosition[0]][maxPosition[1]];		
	}
	
	private int[][] copyBoard(int[][] board) {
		int[][] copyBoard = new int[board.length][board.length];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				copyBoard[i][j] = board[i][j];
			}
		}
		return copyBoard;
	}
	
	//pick a specific position and to see if neighbors can be popped
	private int pick(int[][] optBoard, int row, int col) {
		int numOfPop = 0;
		
		Queue<Integer> queue = new LinkedList<Integer>();
		List<Integer> list = new LinkedList<Integer>();
		
		if (optBoard[row][col] != -1) {
			int order = row * cols + col;
			queue.offer(order);
			int curFruit = -1;
			int tempOrder = 0;
			
			curFruit = optBoard[row][col];
			
			
			while (!queue.isEmpty()) {
				tempOrder = queue.poll();
				int r = tempOrder / cols;
				int c = tempOrder % cols;
				optBoard[r][c] = -1;

				if(!list.contains(tempOrder)) {
					list.add(tempOrder);
					numOfPop++;
					//System.out.println(list.size());
				}
				
				if (r + 1 < rows && optBoard[r + 1][c] == curFruit) {
					queue.offer(tempOrder + cols);
				}
				if (r - 1 >= 0 && optBoard[r - 1][c] == curFruit) {
					queue.offer(tempOrder - cols);
				}
				if (c + 1 < cols && optBoard[r][c + 1] == curFruit) {
					queue.offer(tempOrder + 1);
				}
				if (c - 1 >= 0 && optBoard[r][c - 1] == curFruit) {
					queue.offer(tempOrder - 1);
				}
			}
		}
		return numOfPop;
	}
	
	//calculate and record score in every position board
	private int[][] scorePenal(int[][] tempBoard) {
		int[][] scorePenal = new int[rows][cols];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int[][] copyBoard = copyBoard(tempBoard);
				if (tempBoard[i][j] == -1) {
					scorePenal[i][j] = -1;
				} else {
					scorePenal[i][j] = pick(copyBoard, i, j);
				}
			}
		}
		
		return scorePenal;
	}
	
	//get a array from greedy choose, greedyChoosePosi[0] and greedyChoosePosi[1] are rows and cols, greedyChoosePosi[2] is the max score;
	private int[] greedyPick(int[][] tempBoard) {
		int[] greedyChoosePosi = new int[3];
		int[][] scorePenal = scorePenal(tempBoard);
		int max = -1;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if(max < scorePenal[i][j]){
					max = scorePenal[i][j];
					greedyChoosePosi[0] = i;
					greedyChoosePosi[1] = j;
					greedyChoosePosi[2] = max;
				}
			}
		}
		
		return greedyChoosePosi;
	}	
	
	//fall down the hanging fruit
	private void downFill(int[][] tempBoard) {
		for (int col = 0; col < cols; col++) {
			int r = rows - 1;
			for (int row = rows - 1; row >= 0; row--) {
				if (tempBoard[row][col] != -1) {
					tempBoard[r][col] = tempBoard[row][col];
					r--;
				}
			}
			
			while (r >= 0) {
				tempBoard[r][col] = -1;
				r--;
			}
		}
	}
	
	//check if the game has been finished
	private boolean isFinished(int[][] tempBoard){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				if(tempBoard[i][j] != -1)
					return false;
			}
		}
		return true;
	}
}
