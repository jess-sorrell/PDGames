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
   
    public Board (ArrayList<ArrayList<Player>> world, 
		  int space_horizon){ 

	this.world = world;
	this.space_horizon = space_horizon;
    } 
    
    
    void round (){

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
	}

    }
}


