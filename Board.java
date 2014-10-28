// The Board class defines a mxn grid populated with Players, and
// the operations necessary for iterated prisoner's dilemma games in
// a particular environment.
// This breed of PD games permits Players in the grid to know the 
// outcomes of games within their spacial and temporal horizons.
// Players have memories and neighborhoods that they know something
// about, and base their decisions to cooperate or defect upon that
// information. 
//
// A Board is the environment on which they play, and the board
// implements the rules of the game.
//
// @author Jessica Sorrell
// @version 25-Oct-2014
//
import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.*;
import java.lang.StringBuilder;

public class Board {


    // hidden data members
    private Random prng = new Random();     
    private ArrayList<ArrayList<Player>> world;      //the game grid
    private ArrayList<Float> certainties[][]; //b values
    private int space_horizon;     //will implement later
    // currently players can only see North, South, East, and West
   
    
    public Board (ArrayList<ArrayList<Player>> world, 
		  int space_horizon){ 

	this.world = world;
	this.space_horizon = space_horizon;
    } 
    
    // default constructor
    public Board (){

    }

    
    /**
     * round runs a single round of this game. 
     * The payoffs for each player playing with its nearest neighbors
     * are calculated and life points are updated accordingly. Those
     * whose life points fall to 0 or below are removed from the game
     * and replaced with a new Player born from a nearest neighbor,
     * chosen at random. 
     **/
    void round (){

	int world_width = world.size();
	int world_height = world.get(0).size();

	// Play PD Games and deal damage
	for (int i = 0; i < world_width; i ++ ){

	    ArrayList<Player> column = world.get(i);

	    if( i-1 < 0 ){

	    }
	    ArrayList<Player> left_column = 
		world.get((((i-1) % world_width) +
			    world_width) % world_width);

	    ArrayList<Player> right_column = 
		world.get((((i-1) % world_width) +
			    world_width) % world_width);

	    for (int j = 0; j < world_height; j++ ){
		
		Player player = column.get(j);
		Player north = column.get((((j+1) % world_height) 
					   + world_height) %
					  world_height);
		Player east = right_column.get(j);
		Player south = column.get((((j+1) % world_height) 
					   + world_height) % 
					  world_height);
		Player west = left_column.get(j);
	
		int loss_of_life = 
		    payoffs( player, north )[0] +
		    payoffs( player, east )[0] + 
		    payoffs( player, south )[0] + 
		    payoffs( player, west )[0];
		
		player.increaseLP (loss_of_life);

	    }		
	}
	// update the board
	circleOfLife();
    } 


    /**
     * circleOfLife traverses the board and removes dead Players.
     * Dead Players are replaced with a new Player probabilistically 
     * born from a nearby player.
     **/
    void circleOfLife(){

	ArrayList<ArrayList<Player>> a_whole_new_world = world;

	int world_width = world.size();
	int world_height = world.get(0).size();

	for (int i = 0; i < world_width; i ++ ){

	    ArrayList<Player> column = world.get(i);

	    for (int j = 0; j < world_height; j++ ){
		
		Player player = column.get(j);
		
		if (player.getLP() <= 0){

		    float chooseParent = prng.nextFloat();
		    Player parent;
		    // if random number is < .25, choose player
		    // to the north
		    if (chooseParent < .25 ) {
			parent = 
			    world.get(i).get((((j-1) % world_height) 
					      + world_height) %
					     world_height);
		    }
		    // if .25 < x < .5, choose player to the east
		    else if (chooseParent < .5 ){
			parent = 
			    world.get((((i-1) % world_width) +
				       world_width) 
				      % world_width).get(j);
		    }
		    // if .5 < x < .75, choose player to the south
		    else if (chooseParent < .75 ){
			parent = 
			    world.get(i).get((((j+1) % world_height) 
					      + world_height) %
					     world_height);
		    }
		    // if .75 < x < 1, choose player to the west
		    else {
			parent = 
			    world.get((((i-1) % world_width) +
					world_width) 
				      % world_width).get(j);
		    }

		    a_whole_new_world.get(i).set(j, parent.birth());
		}
	    }
	}
	this.world = a_whole_new_world;
    }
    

    /**
     * payoffs calculates the payoffs or two players in a PD game.
     * If both players cooperate, both lose 1 life point. If both
     * players defect, both lose 2 life points. If one player
     * cooperates and the other defects, then the cooperating player
     * loses 3 life points and the defecting player loses 0 points.
     *
     * @params  player1  player of the PD game
     * @params  player2  player of the PD game
     * @return  payoffs  an int array of payoffs
     **/
    int[] payoffs( Player player1, Player player2){
	
	int decision1 = player1.getDecision();
	int decision2 = player2.getDecision();
	
	if (decision1 > decision2){
	    return new int[]{0, -3};
	}

	else if (decision1 == decision2){

	    if( decision1 > 0){
		return new int[]{-1, -1};
	    }
			       
	    else{
		return new int[]{-2, -2};
	    }
	}

	else {
	    return new int[]{-3, 0};
	}
    }
    
    
    /**
     * printBoard prints the current certainties of all players
     * as well as the current life points of all players
     **/

    void printBoard(){


	System.out.println("Certainties and LP of current players");
	System.out.println("(certainties, lp):");

	int world_width = world.size();
	int world_height = world.get(0).size();

	StringBuilder[] rowStates = new StringBuilder[world_height];
	//	for (int i = 0; i < world_width; i ++ ){
	

	for (int j = 0; j < world_height; j++ ){
	    rowStates[j] = new StringBuilder();
	}
	    
	for (int j = 0; j < world_height; j++ ){
	    
	    rowStates[j].append(Integer.toString(j));
	    rowStates[j].append(": \t");
	 
	    for (int i = 0; i < world_width; i++ ){
		rowStates[j].append("( ");
		rowStates[j].append
		    (Float.toString
		     (world.get(i).get(j).getCertainty()));
		rowStates[j].append(", ");
		rowStates[j].append
		     (Float.toString
		      (world.get(i).get(j).getLP()));
		rowStates[j].append(") ");
	    }
	    rowStates[j].append("\n");
	}
	for (int j = 0; j < world_height; j++ ){
	    System.out.println(rowStates[j].toString());
	}
    }

    
    public static void main (String args[]){
	
	int m = 0;
	int n = 0;
	float[] certainties = new float[0];
	int time_horizon = 0;
	int space_horizon = 1;
	int rounds = 0;
	int life_points = 0;
	//float[] altruism = new float[0];   holding constant for now
	float altruism = 0;
	//float[] optimism = new float[0];   starting at 0 for now
	float optimism = 0;
	
	String usage_warning = "usage: [-m rows] [-n columns] [-l lifepoints] [-c certainty array (comma separated, no spaces)] [-s space horizon] [-t time horizon] [-r rounds]";

	String arglength_warning = "certainty array must equal m*n";

	if (args.length != 14){
	    System.err.println(usage_warning);
	    System.exit(1);
	}
	int i;
	int j;

	for ( i = 0; i < 14; i++){

	    if( args[i].equals("-m")){
		i ++;
		m = Integer.parseInt(args[i]);
	    }
	    else if ( args[i].equals("-n")){
		i++;
		n = Integer.parseInt(args[i]);
	    }
	    else if ( args[i].equals("-l")){
		i++;
		life_points = Integer.parseInt(args[i]);
	    }
	    else if (args[i].equals("-c")){
		i++;
		String[] certs = args[i].split(",");
		certainties = new float[certs.length];

		for (j = 0; j < certs.length; j++) {
		    certainties[j] = Float.parseFloat(certs[j]);
		}
		
	    }
	    else if (args[i].equals("-t")){
		i++;
		time_horizon = Integer.parseInt(args[i]);
	    }
	    else if (args[i].equals("-s")){
		i++;
		space_horizon = Integer.parseInt(args[i]);
	    }
	    else if (args[i].equals("-r")){
		i++;
		rounds = Integer.parseInt(args[i]);
	    }
	    else{
		System.err.println(usage_warning);
		System.exit(1);
	    }
	  	    
	}
	if (certainties.length != (m*n)){
	    System.err.println(arglength_warning);
	    System.exit(1);
	}


	// Now that we're done with all that, we can begin our games!
	ArrayList<ArrayList<Player>> players = 
	    new ArrayList<ArrayList<Player>>(n);
	
	// populate the player array with players
	for ( i = 0; i < n; i++){

	    ArrayList<Player> column = new ArrayList<Player>(m);
	    for ( j = 0; j < m; j++ ){
		
		column.add(new Player
		    ( life_points, altruism, certainties[n*i + j], 
		      time_horizon, optimism, new Random()));
	    }

	    players.add(column);
	}

	// build the game board
	Board game = new Board(players, space_horizon);

	for ( i = 0; i < rounds; i++ ){
	    game.round();
	    game.printBoard();
	}

	
	//java Board -m 4 -n 4 -l 10 -c 1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0 -s 1 -t 3 -r 4

    }
}


