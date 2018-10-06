import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/**
 * @author abhanshu 
 * This class is a template for implementation of 
 * HW1 for CS540 section 2
 */
/**
 * Data structure to store each node.
 */
class Location {
	private int x;
	private int y;
	private Location parent;

	public Location(int x, int y, Location parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Location getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Location) {
			Location loc = (Location) obj;
			return loc.x == x && loc.y == y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * (hash + x);
		hash = 31 * (hash + y);
		return hash;
	}
}

public class KingsKnightmare {
	// represents the map/board
	private static boolean[][] board;
	// represents the goal node
	private static Location king;
	// represents the start node
	private static Location knight;
	// y dimension of board
	private static int n;
	// x dimension of the board
	private static int m;

	// enum defining different algo types
	enum SearchAlgo {
		BFS, DFS, ASTAR;
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			// loads the input file and populates the data variables
			SearchAlgo algo = loadFile(args[0]);
			if (algo != null) {
				switch (algo) {
				case DFS:
					executeDFS();
					break;
				case BFS:
					executeBFS();
					break;
				case ASTAR:
					executeAStar();
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Implementation of Astar algorithm for the problem
	 */
	private static void executeAStar() {

		// Location to keep track of the current node
		Location curr = null;

		// Boolean to check if goal node is reached
		boolean goalReached = false;

		// 2D array to keep track of the explored node
		boolean explored[][] = new boolean[n][m];

		// Frontier Priority Queue
		PriorityQ<Location> frontier = new PriorityQ<Location>();

		// Int variable to store the Manhattan distance
		int manDist = Math.abs(knight.getX() - king.getX()) + Math.abs(knight.getY() - king.getY());

		// Adding the knight to the frontier and keeping its Manhattan distance
		frontier.add(knight, manDist);

		// Run the loop while the frontier is not empty
		while (!frontier.isEmpty()) {

			// Getting the first node from the front of the frontier
			curr = frontier.poll().getKey();

			// If loop to check if the current node equals to king's position,
			// i.e. goal node
			if (curr.equals(king)) {
				goalReached = true;
				break;
			}

			// If loop to check if the loop has been explored
			// Knight's moves for nodes that haven't been expanded yet
			if (explored[curr.getY()][curr.getX()] == false) {
				int[] xKnight = { 2, 1, -1, -2, -2, -1, 1, 2 };
				int[] yKnight = { 1, 2, 2, 1, -1, -2, -2, -1 };

				// For loop to go through the possible child nodes
				for (int i = 0; i < 8; i++) {
					int nextX = curr.getX() + xKnight[i];
					int nextY = curr.getY() + yKnight[i];

					// If loop to check that only possible child nodes are added
					if (((nextX >= 0) && (nextX < m)) && ((nextY >= 0) && (nextY < n))) {
						// New Location variable next to get a child
						Location next = new Location(nextX, nextY, curr);

						// If loop to add node only if there is no obstacle and
						// a node has not been explored already
						if ((board[nextY][nextX] == false) && (explored[nextY][nextX] == false)) {

							// Location for generation of the child node
							Location childNode = next;
							int cost = 0;

							while (childNode.getParent() != knight) {
								cost++;
								childNode = childNode.getParent();
							}

							cost++;

							// Re-adjusting the Manhattan distance based on the
							// current child node
							manDist = Math.abs(next.getX() - king.getX()) + Math.abs(next.getY() - king.getY());
							// Adding the child node to the frontier, and adding
							// three times the cost to the Manhattan distance
							frontier.add(next, ((cost * 3) + manDist));
						}
					}
				}
				// Marking the current node as explored
				explored[curr.getY()][curr.getX()] = true;
			}
		}
		// If loop to check if the goal node has been found
		if (goalReached) {

			// An array list to find the solution path
			ArrayList<Location> path = new ArrayList<Location>();

			while (!curr.equals(knight)) {
				path.add(curr);
				curr = curr.getParent();
			}

			path.add(knight);
			Collections.reverse(path);

			for (int i = 0; i < path.size(); i++) {
				System.out.println(path.get(i).getX() + " " + path.get(i).getY());
			}
		} else {
			System.out.println("NOT REACHABLE");
		}

		// Int variable to keep the expanded node count
		int nodesExpanded = 0;

		// For loop to count the expanded node
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				// ArrayList changed to true if a node has been explored
				if (explored[i][j] == true) {
					nodesExpanded++;
				}
			}
		}
		System.out.println("Expanded nodes: " + nodesExpanded);

	}

	/**
	 * Implementation of BFS algorithm
	 */
	private static void executeBFS() {

		// Location to keep track of the current node
		Location curr = null;

		// Frontier Linked List
		Queue<Location> frontier = new LinkedList<Location>();

		// boolean ArrayList to keep a track of all the expanded nodes
		boolean[][] explored = new boolean[n][m];

		// boolean to check if goal node has been found
		boolean goalReached = false;

		// Adding the knight to the frontier
		frontier.add(knight);

		// Run the loop while the frontier is not empty
		while (!frontier.isEmpty()) {

			// Getting the first node from the front of the frontier
			curr = frontier.remove();

			// If loop to check if the node has been explored
			// Knight's moves for nodes that haven't been expanded yet
			if (explored[curr.getY()][curr.getX()] == false) {
				int[] xKnight = { 2, 1, -1, -2, -2, -1, 1, 2 };
				int[] yKnight = { 1, 2, 2, 1, -1, -2, -2, -1 };

				// For loop to go through the possible child nodes
				for (int i = 0; i < 8; i++) {
					int nextX = curr.getX() + xKnight[i];
					int nextY = curr.getY() + yKnight[i];

					// If loop to check that only possible child nodes are added
					if (((nextX >= 0) && (nextX < m)) && ((nextY >= 0 && nextY < n))) {
						// New Location variable next to get a child
						Location next = new Location(nextX, nextY, curr);

						// If loop to add node only if there is no obstacle and
						// a node has not been explored already
						if ((board[nextY][nextX] == false) && (explored[nextY][nextX] == false)) {

							// If we reach the king's position, goal reached and
							// exit loop
							if (next.equals(king)) {
								goalReached = true;
								break;
							}

							// If the frontier does not contain the child, add
							// it to the frontier
							if (!(frontier.contains(next))) {
								frontier.add(next);
							}
						}
					}
				}
				// Change the current node's boolean to true
				explored[curr.getY()][curr.getX()] = true;
				if (goalReached) {
					break;
				}
			}
		}

		// If loop if the goal has been reached
		if (goalReached) {
			// Finding the solution path to the goal node
			ArrayList<Location> path = new ArrayList<Location>();

			while (!curr.equals(knight)) {
				path.add(curr);
				curr = curr.getParent();
			}

			path.add(knight);
			Collections.reverse(path);
			path.add(king);

			for (int i = 0; i < path.size(); i++) {
				System.out.println(path.get(i).getX() + " " + path.get(i).getY());
			}
		} else {
			System.out.println("NOT REACHABLE");
		}

		// Int counter for the expande nodes
		int nodesExpanded = 0;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				// ArrayList changed to true if a node has been explored
				if (explored[i][j] == true) {
					nodesExpanded++;
				}
			}
		}
		System.out.println("Expanded nodes: " + nodesExpanded);
	}

	/**
	 * Implemention of DFS algorithm
	 */
	private static void executeDFS() {

		// Location variable to keep track of the current node
		Location curr = null;

		// A frontier for the nodes to be expanded
		Stack<Location> frontier = new Stack<Location>();

		// boolean ArrayList to keep a track of all the expanded nodes
		boolean[][] explored = new boolean[n][m];

		// boolean to keep track when the goal node has been reached
		boolean goalReached = false;

		// Adding the knight to the frontier
		frontier.push(knight);

		// Run the loop while the frontier is not empty
		while (!frontier.isEmpty()) {
			// Getting the first node from the front of the frontier
			curr = frontier.pop();

			// If loop to check if the node has been explored
			// Knight's moves for nodes that haven't been expanded yet
			if (explored[curr.getY()][curr.getX()] == false) {
				int[] xKnight = { 2, 1, -1, -2, -2, -1, 1, 2 };
				int[] yKnight = { 1, 2, 2, 1, -1, -2, -2, -1 };

				// For loop for the possible successors
				for (int i = 0; i < 8; i++) {
					int nextX = curr.getX() + xKnight[i];
					int nextY = curr.getY() + yKnight[i];

					// Making sure that the child's location is on the board
					if (((nextX >= 0) && (nextX < m)) && ((nextY >= 0 && nextY < n))) {
						Location next = new Location(nextX, nextY, curr);

						// Checking if there isn't an obstable
						if ((board[nextY][nextX] == false) && (explored[nextY][nextX] == false)) {
							if (next.equals(king)) {
								goalReached = true;
								break;
							}

							// Only add a child if it hasn't been added to the
							// frontier already
							if (!(frontier.contains(next))) {
								frontier.add(next);
							}
						}
					}
				}
				// The current node is declared to true
				explored[curr.getY()][curr.getX()] = true;
				if (goalReached) {
					break;
				}
			}
		}

		// Finding the solution path when the goal node has been found
		if (goalReached) {
			ArrayList<Location> path = new ArrayList<Location>();

			while (!curr.equals(knight)) {
				path.add(curr);
				curr = curr.getParent();
			}

			path.add(knight);

			Collections.reverse(path);

			path.add(king);

			for (int i = 0; i < path.size(); i++) {
				System.out.println(path.get(i).getX() + " " + path.get(i).getY());
			}
		} else {
			System.out.println("NOT REACHABLE");
		}

		// Finding the number of expanded nodes in the solution path
		int nodesExpanded = 0;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				// ArrayList changed to true if a node has been explored
				if (explored[i][j] == true) {
					nodesExpanded++;
				}
			}
		}
		System.out.println("Expanded nodes: " + nodesExpanded);
	}

	/**
	 * 
	 * @param filename
	 * @return Algo type This method reads the input file and populates all the
	 *         data variables for further processing
	 */
	private static SearchAlgo loadFile(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			SearchAlgo algo = SearchAlgo.valueOf(sc.nextLine().trim().toUpperCase());
			n = sc.nextInt();
			m = sc.nextInt();
			sc.nextLine();
			board = new boolean[n][m];
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					if (line.charAt(j) == '1') {
						board[i][j] = true;
					} else if (line.charAt(j) == 'S') {
						knight = new Location(j, i, null);
					} else if (line.charAt(j) == 'G') {
						king = new Location(j, i, null);
					}
				}
			}
			sc.close();
			return algo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
