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
import java.util.Collections;

public class Board {


    // hidden data members
    private Random prng = new Random();     
    private ArrayList<ArrayList<Player>> world;      //the game grid

    // These arrays hold the players' decision to defect or 
    // cooperate each round
    // There are separate arrays for games played between players
    // on vertical axes and games player on horizontal axis.
    // This is for simplicity's sake for the time being, but should
    // be changed.
    private ArrayList<ArrayList<DecisionPair>> vert_decisions;
    private ArrayList<ArrayList<DecisionPair>> horiz_decisions;

    private float[][] push_memories; //memory values
    private int space_horizon;     //will implement later
    // currently players can only see North, South, East, and West
   
    
    public Board (ArrayList<ArrayList<Player>> world, 
		  int space_horizon){ 
	int width = world.size();
	int height = world.get(0).size();
	this.world = world;
	this.space_horizon = space_horizon;

	push_memories = new float[width][height];

	//create and initialize decision grids
	vert_decisions = new ArrayList<ArrayList<DecisionPair>>();
	for (int i = 0; i < width; i++ ){

	    vert_decisions.add(new ArrayList<DecisionPair>(height));

	    for (int j = 0; j < height; j++ ){
		vert_decisions.get(i).add(new DecisionPair(0,0));
	    }
	}

	//create and initialize decision grids
	horiz_decisions = new ArrayList<ArrayList<DecisionPair>>();
	for (int i = 0; i < width; i++ ){

	    horiz_decisions.add(new ArrayList<DecisionPair>(height));

	    for (int j = 0; j < height; j++ ){
		horiz_decisions.get(i).add(new DecisionPair(0,0));
	    }
	}
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


	// Play PD Games and save the outcomes
	for (int i = 0; i < world_width; i ++ ){

	    int east = (i+1) % world_width;
	    int west = (i-1 + world_width) % world_width;
	    
	    ArrayList<Player> column = world.get(i);

	    ArrayList<Player> left_column = 
		world.get(west);

	    ArrayList<Player> right_column = 
		world.get(east);

	    for (int j = 0; j < world_height; j++ ){

		int north = (j-1 + world_height) % world_height;
		
		Player player = column.get(j);
		Player p_north = column.get(north);
		Player p_east = right_column.get(j);

		DecisionPair vertOutcome = 
		    new DecisionPair(player.getDecision(),
				     p_north.getDecision());

		DecisionPair horizOutcome = 
		    new DecisionPair(player.getDecision(),
				     p_east.getDecision());

		vert_decisions.get(i).set(j, vertOutcome);
		horiz_decisions.get(i).set(j, horizOutcome);
		//	System.out.printf("Decisions for player %d, %d in game to its north: (%d, %d)\n", j, i, vertOutcome.getPayoffs()[0], vertOutcome.getPayoffs()[1]);
		//	System.out.printf("Decisions for player %d, %d in game to its east: (%d, %d) \n", j, i, horizOutcome.getPayoffs()[0], horizOutcome.getPayoffs()[1]);

	    }		
	}
	
	// traverse the board again and deal damage, push new
	// memories
	for ( int i = 0; i < world_width; i++ ){

	    ArrayList<Player> column = world.get(i);

	    for (int j = 0; j < world_height; j++ ){

		Player player = column.get(j);

		int south = (j+1) % world_height;
		int west = (i-1 + world_width) % world_width;

		int loss_of_life = 
		    vert_decisions.get(i).get(j).getPayoffs()[0] +
		    vert_decisions.get(i).get(south).getPayoffs()[1] +
		    horiz_decisions.get(i).get(j).getPayoffs()[0] + 
		    horiz_decisions.get(west).get(j).getPayoffs()[1];
		//	System.out.printf("Player %d, %d loses %d life \n",
		//				  j, i, loss_of_life);

		player.increaseLP(loss_of_life);
		
		float new_memory = (float)
		    (vert_decisions.get(i).get(j).decision1 +
		     vert_decisions.get(i).get(south).decision2 +
		     horiz_decisions.get(i).get(j).decision1 + 
		     horiz_decisions.get(west).get(j).decision2);

		player.pushMemory(new_memory);
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
		    
		    //	    System.out.printf("Player %d,%d has died \n", j, i);
		    float chooseParent = prng.nextFloat();
		    Player parent;

		    int east = (i+1) % world_width;
		    int west = (i-1 + world_width) % world_width;
		    int north = (j-1 + world_height) % world_height;
		    int south = (j+1) % world_height;

		    // if random number is < .25, choose player
		    // to the north
		    if (chooseParent < .25 ) {
			parent = world.get(i).get(north);
		    }
		    // if .25 < x < .5, choose player to the east
		    else if (chooseParent < .5 ){
			parent = world.get(east).get(j);
		    }
		    // if .5 < x < .75, choose player to the south
		    else if (chooseParent < .75 ){
			parent = world.get(i).get(south);
		    }
		    // if .75 < x < 1, choose player to the west
		    else {
			parent = world.get(west).get(j);
		    }
		
		    a_whole_new_world.get(i).set(j, parent.birth());
		    //		    System.out.printf("New player at %d,%d has certainty %.3f \n", j, i, a_whole_new_world.get(i).get(j).getCertainty());
		}
	    }
	}
	this.world = a_whole_new_world;
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
	    rowStates[j].append(": ");
	 
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


    /**
     * summaryStats prints everything you need, nothing you don't
     **/
    public void printSummaryStats(){

	int world_width = world.size();
	int world_height = world.get(0).size();

	float mean_certainty;
	float max_certainty;
	float min_certainty;
	float median_certainty = 0;
	float sum = 0;

	ArrayList<Float> certs = 
	    new ArrayList<Float>(world_width * world_height);

	for (int i = 0; i < world_width; i ++ ){
	    
	    ArrayList<Player> column = world.get(i);
	    
	    for (int j = 0; j < world_height; j++ ){
		// add this certainty to the list
		certs.add(column.get(j).getCertainty());
		// add this certainty to the sum
		sum += column.get(j).getCertainty();
	    }
	}
	// calculate average certainty
	mean_certainty = sum/(world_width * world_height);
	
	// sort the certainties and calculate stats
	Collections.sort(certs);
	max_certainty = certs.get(world_width * world_height - 1);
	min_certainty = certs.get(0);
	median_certainty = 
	    certs.get((int)((world_width * world_height - 1 )/2));

	System.out.printf("World is %d x %d \n",
			  world_height, world_width);
	System.out.printf("Median b = \t %.2f \n", median_certainty);
	System.out.printf("Mean b = \t %.2f \n", mean_certainty);
	System.out.printf("Max b = \t %.2f \n", max_certainty);
	System.out.printf("Min b = \t %.2f \n", min_certainty);

	// if median is at least 5% greater than the mean, print
	// skew left message.
	if ((median_certainty - mean_certainty) > 
	    (0.05*mean_certainty)) {
	    System.out.println("Median > mean. Could be skewed left.");
	}
	// if median as at least 5% less than the mean, print skew
	// right message.
	else if ((mean_certainty - median_certainty) > 
		 (0.05*mean_certainty)) {
	    System.out.println("Median < mean. Could be skewed right.");
	}
    }

    /**
     * getSummaryStats returns everything you need, nothing you don't
     *
     * @return  stats  float array where
     * stats[0] = mean certainty
     * stats[1] = median certainty
     * stats[2] = max certainty
     * stats[3] = min certainty
     * stats[4] = empty
     * stats[5] = empty
     * stats[6] = number of players
     **/
    public float[] getSummaryStats(){

	int world_width = world.size();
	int world_height = world.get(0).size();

	float[] stats = new float[7];

	float mean_certainty;
	float max_certainty;
	float min_certainty;
	float median_certainty = 0;
	float sum = 0;

	ArrayList<Float> certs = 
	    new ArrayList<Float>(world_width * world_height);

	for (int i = 0; i < world_width; i ++ ){
	    
	    ArrayList<Player> column = world.get(i);
	    
	    for (int j = 0; j < world_height; j++ ){
		// add this certainty to the list
		certs.add(column.get(j).getCertainty());
		// add this certainty to the sum
		sum += column.get(j).getCertainty();
	    }
	}
	// calculate average certainty
	stats[0] = sum/(world_width * world_height);
	
	// sort the certainties and calculate stats
	Collections.sort(certs);
	stats[2] = certs.get(world_width * world_height - 1);
	stats[3] = certs.get(0);
	stats[1] = 
	    certs.get((int)((world_width * world_height - 1 )/2));
	stats[6] = (float)world_height * world_width;

	return stats;
    }

    /**
     * percentLessThan takes a float x and returns the percentage of
     * the population with certainties less than that value
     *
     * @param  x  the certainty value of interest
     *
     * @return  the pop % with certainties less than x
     **/
    float percentLessThan ( float x ){
	int world_width = world.size();
	int world_height = world.get(0).size();

	float popPercentage = 0;;    //the percentage to return

	for (int i = 0; i < world_width; i ++ ){
	    
	    ArrayList<Player> column = world.get(i);
	    
	    for (int j = 0; j < world_height; j++ ){

		if ( column.get(j).getCertainty() < x ){
		    popPercentage += 1;
		}
	    }
	    
	}
	return (popPercentage/(world_width * world_height));
    }
    /**
     * percentGreaterEqualThan returns the percentage of the population
     * with a certainty greater than x
     *
     * @param  x  the certainty value of interest
     *
     * @return  the pop % with certainties less than x
     **/
    float percentGreaterEqualThan (float x ){
	return ((float)1.0 - percentLessThan(x));
    }


    void markPlayersGreaterThan( float cert ){

	int world_width = world.size();
	int world_height = world.get(0).size();

	for (int i = 0; i < world_height; i ++ ){
	    
	    for (int j = 0; j < world_width; j ++ ){
		System.out.printf("__");
	    }

	    System.out.printf("_\n");
	    System.out.printf("|");
	    for (int j = 0; j < world_width; j++ ){
		if (world.get(j).get(i).getCertainty() > cert ){
		    System.out.printf("o|");
		}
		else{
		    System.out.printf(" |");
		}
	    }
	    System.out.printf("\n");
	}
	System.out.println();
	System.out.println("****************************************");
	System.out.println("****************************************");
	System.out.println();
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
	float altruism = -3;
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
	    System.out.printf("Round %d: \n", i);
	    game.round();
	    game.printBoard();
	}

	
	//java Board -m 4 -n 4 -l 10 -c 1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0 -s 1 -t 3 -r 4

    }
}


