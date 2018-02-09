package homework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class homework {
	public static void main(String[] args) {
		
		String way = new InputStream().getWayOfSearch();
		if (way.equals("BFS")) {
			File f = new File("output.txt");
			try {
				if (f.createNewFile()){
				    System.out.println("File is created!");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BFS bfs = new BFS();
			bfs.bfsSearch();
			if (bfs.getResult().size() == 0) {
				System.out.println("FAIL");
				try{
				    PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
				    writer.println("FAIL");
				    writer.close();
				} catch (IOException e) {
					e.printStackTrace();		
				}
			} else {
				try{
				    PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
					System.out.println("OK");
				    writer.println("OK");
				    int[][] printResult = bfs.getResult().get(0);
					for (int i = 0; i < printResult.length; i++) {
						for (int j = 0; j < printResult[i].length; j++) {
							System.out.print(bfs.getResult().get(0)[i][j]);
						    writer.print(bfs.getResult().get(0)[i][j]);
						}
						System.out.println();
						writer.println();
					}
				    writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		} else if (way.equals("DFS")){
			DFS dfs = new DFS();
			dfs.dfsSearch();
		} else {
			CA ca = new CA();
			if (ca.initial()) {
				ca.printSolution();
			} else {
				File f = new File("output.txt");
				try {
					if (f.createNewFile()){
					    System.out.println("File is created!");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
				
				PrintWriter writer = null;
				try {
					writer = new PrintWriter("output.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				System.out.println("FAIL");
				writer.println("FAIL");
			    writer.close();
			}
		}
}
}
