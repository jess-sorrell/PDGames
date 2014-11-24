// PDTrials runs a series of iterated prisoner's dilemma games on a 
// a Board.
// Relevant outcomes of the games are printed to std out.
//
// @author Jessica Sorrell
// @version 29-Oct-2014

import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Iterator;

public class PDTrials {

    Random prng = new Random();
    int num_trials;
    
    

    // These should probably be held constant for each set of 
    // trials, at least for the time being
    int space_horizon = 1;
    int time_horizon = 3;
    int life_points = 50;
    int num_rounds = 2000;
    float deviant_ratio = (float).25;
    
    // Let's start things off sort of neutral
    float misanthropy = (float)0.0;
    float optimism = (float)0.0;
    
    // Collect information on large Boards
    // m rows, n columns
    int m = 100;
    int n = 100;
    float[] certainties = new float[m*n];
    
    int t; //index for looping through trials
    
    
    // results collects the end state statistics
    float[][] results;
    float[][] starts;    
    
    
    public PDTrials(int num_trials){

	this.num_trials = num_trials;
	results = new float[num_trials][7];
	starts = new float[num_trials][7];

    }

    void uniform (){

	for ( t = 0; t < num_trials; t++ ){

	    // uniformly random certainties between 1.0 and 2.0
	    int i; 
	    int j;	
	    for ( i = 0; i < certainties.length; i++ ){
		certainties[i] = prng.nextFloat() + (float)1.0;
	    } 
	    	
	    // Now that we're done with all that, we can begin our 
	    //games!
	    ArrayList<ArrayList<Player>> players = 
		new ArrayList<ArrayList<Player>>(n);
	    
	    // populate the player array with players
	    for ( i = 0; i < n; i++){
		
		ArrayList<Player> column = new ArrayList<Player>(m);
		for ( j = 0; j < m; j++ ){
		    
		    column.add(new Player
			       ( life_points,
				 certainties[m*i + j]* misanthropy, 
				 certainties[m*i + j], 
				 time_horizon, optimism, 
				 new Random()));
		}
		
		players.add(column);
	    }
	
	    // build the game board
	    Board game = new Board(players, space_horizon);
	    starts[t] = game.getSummaryStats();
	    starts[t][4] = game.percentGreaterEqualThan(starts[t][1]);
	    //	    System.out.println("Start.");
	    //game.printSummaryStats();

   
	    // play the game
	    for (i = 0; i < num_rounds; i++ ){
		game.round();
		// if( i % 25 == 0){
		//     game.markPlayersGreaterThan
		// 	((float)(starts[t][1] + 0.025));
		// 	}
	    }

	    // print the end state of the game and store these stats
	    // in array
	    //	    game.printSummaryStats();
	    results[t] = game.getSummaryStats();
	    results[t][4] = game.percentGreaterEqualThan(starts[t][1]);
	    results[t][5] = game.percentGreaterEqualThan(starts[t][0]);
	    
	}


	// Stats on starting stats? What madness is this?!?!
	float median_avg_start, mean_avg_start, max_avg_start, 
	    min_avg_start, mean_proportion_start;
	median_avg_start = mean_avg_start = 
	    max_avg_start = min_avg_start = mean_proportion_start = 0;
	
	for (int i = 0; i < starts.length; i++ ){
	    
	    mean_avg_start += starts[i][0];
	    median_avg_start += starts[i][1];
	    max_avg_start += starts[i][2];
	    min_avg_start += starts[i][3];
	    mean_proportion_start += starts[i][4];
	    
	}
	
	mean_avg_start /= starts.length;
	median_avg_start /= starts.length;
	max_avg_start /= starts.length;
	min_avg_start /= starts.length;
	mean_proportion_start /= starts.length;
	
	// Print summary of games
	
	System.out.println("**************************************");
	System.out.println("**************************************");
	
	System.out.printf("Certainties ranging from .5 to 1.5, uniformly distributed \n");

	
	System.out.printf("Average startstate median:\t %.2f \n",
			  median_avg_start);
	System.out.printf("Average startstate mean:\t %.2f \n",
			  mean_avg_start);
	System.out.printf("Average startstate max:\t \t %.2f \n",
			  max_avg_start);
	System.out.printf("Average startstate min:\t \t %.2f \n", 
			  min_avg_start);
	System.out.printf("Starting proportion greater than starting avg: %.3f \n", mean_proportion_start);
	

	// Stats on stats? What madness is this?!?!
	float median_avg, mean_avg, max_avg, min_avg, 
	    median_proportion, avg_proportion;
	median_avg = mean_avg = max_avg = min_avg = 
	    median_proportion = avg_proportion = 0;


	for (int i = 0; i < results.length; i++ ){
	    
	    mean_avg += results[i][0];
	    median_avg += results[i][1];
	    max_avg += results[i][2];
	    min_avg += results[i][3];
	    median_proportion += results[i][4];
	    avg_proportion += results[i][5];
	    
	}

	mean_avg /= results.length;
	median_avg /= results.length;
	max_avg /= results.length;
	min_avg /= results.length;
	median_proportion /= results.length;
	avg_proportion /=results.length;

	// Print summary of games

	System.out.println("**************************************");
	System.out.println("**************************************");

	System.out.printf("Certainties ranging from .5 to 1.5, uniformly distributed \n");

	
	System.out.printf("Average endstate median:\t %.2f \n",
			  median_avg);
	System.out.printf("Average endstate mean:\t \t %.2f \n",
			  mean_avg);
	System.out.printf("Average endstate max:\t \t %.2f \n", 
			  max_avg);
	System.out.printf("Average endstate min:\t \t %.2f \n",
			  min_avg);

	System.out.printf("Final proportion greater than starting median:\t %.3f \n", median_proportion);
	System.out.printf("Final proportion greater than starting average: %.3f \n", avg_proportion);
	
	// if median is at least 5% greater than the mean, print
	// skew left message.
	if ((median_avg - mean_avg) > 
	    (0.05*mean_avg)) {
	    System.out.println("Median > mean. Could be skewed left.");
	}
	// if median as at least 5% less than the mean, print skew
	// right message.
	else if ((mean_avg - median_avg) > 
		 (0.05*mean_avg)) {
	    System.out.println("Median < mean. Could be skewed right.");
	}

	System.out.println("**************************************");
	System.out.println("**************************************");
	System.out.println("");
	System.out.println("");
    }

    void largeB(){
	// Mostly uniformly random certainties between 1.0 and 2.0
	// with a few larger values sprinkled in for fun!
	// The number of deviants is proportional to grid size
	for ( t = 0; t < num_trials; t++ ){

	    int i; 
	    int j;	
	    for ( i = 0; i < certainties.length; i++ ){
	    	certainties[i] = prng.nextFloat() + (float)1.0;
	    } 
	    
	    // now add the deviants
	    int num_deviants = (int)(deviant_ratio*m*n);	
	    
	    for(i = 0; i < num_deviants; i++ ){
		int index = prng.nextInt(m*n);
		certainties[index] = prng.nextFloat() + (float)2.5;
		
	    }
	    
	    //    for ( i = 0; i < certainties.length; i++ ){
	    //		System.out.println(certainties[i]);
	    //  } 
	    

	    // Now that we're done with all that, we can begin our 
	    //games!
	    ArrayList<ArrayList<Player>> players = 
		new ArrayList<ArrayList<Player>>(n);
	    
	    // populate the player array with players
	    for ( i = 0; i < n; i++){
		
		ArrayList<Player> column = new ArrayList<Player>(m);
		for ( j = 0; j < m; j++ ){
		    
		    column.add(new Player
			       ( life_points, 
				 certainties[m*i + j]* misanthropy,
				 certainties[m*i + j], 
				 time_horizon, optimism, 
				 new Random()));
		}
		
		players.add(column);
	    }
	    
	    // build the game board
	    Board game = new Board(players, space_horizon);
	    //    System.out.println("Start");
	    //	    game.printSummaryStats();
	    starts[t] = game.getSummaryStats();
	    starts[t][4] = game.percentGreaterEqualThan(starts[t][1]);


	    // play the game
	    for (i = 0; i < num_rounds; i++ ){
		game.round();
		// if( i % 25 == 0){
		//     game.markPlayersGreaterThan
		// 	((float)(starts[t][1] + 0.025));
		// 	}
	    }

	    // print the end state of the game and store these stats
	    // in array
	    //  System.out.println("Finish");
	    //	    game.printSummaryStats();
	    results[t] = game.getSummaryStats();
	    results[t][4] = game.percentGreaterEqualThan(starts[t][1]);
	    results[t][5] = game.percentGreaterEqualThan(starts[t][0]);
	}


	// Stats on starting stats? What madness is this?!?!
	float median_avg_start, mean_avg_start, max_avg_start, 
	    min_avg_start, mean_proportion_start;
	median_avg_start = mean_avg_start = 
	    max_avg_start = min_avg_start = mean_proportion_start = 0;
	
	for (int i = 0; i < starts.length; i++ ){
	    
	    mean_avg_start += starts[i][0];
	    median_avg_start += starts[i][1];
	    max_avg_start += starts[i][2];
	    min_avg_start += starts[i][3];
	    mean_proportion_start += starts[i][4];
	    
	}
	
	mean_avg_start /= starts.length;
	median_avg_start /= starts.length;
	max_avg_start /= starts.length;
	min_avg_start /= starts.length;
	mean_proportion_start /= starts.length;
	
	
	// Print summary of games
	
	System.out.println("**************************************");
	System.out.println("**************************************");
	
	System.out.printf("%.3f very large certainty values\n", 
			  deviant_ratio);

	
	System.out.printf("Average startstate median:\t %.2f \n",
			  median_avg_start);
	System.out.printf("Average startstate mean:\t %.2f \n",
			  mean_avg_start);
	System.out.printf("Average startstate max:\t \t %.2f \n",
			  max_avg_start);
	System.out.printf("Average startstate min:\t \t %.2f \n", 
			  min_avg_start);
	System.out.printf("Starting proportion greater than starting avg: %.3f \n", mean_proportion_start);
	

	// Stats on stats? What madness is this?!?!
	float median_avg, mean_avg, max_avg, min_avg, 
	    median_proportion, avg_proportion;
	median_avg = mean_avg = max_avg = min_avg = 
	    median_proportion = avg_proportion = 0;

	for (int i = 0; i < results.length; i++ ){
	    
	    mean_avg += results[i][0];
	    median_avg += results[i][1];
	    max_avg += results[i][2];
	    min_avg += results[i][3];
	    median_proportion += results[i][4];
	    avg_proportion += results[i][5];
	    
	}

	mean_avg /= results.length;
	median_avg /= results.length;
	max_avg /= results.length;
	min_avg /= results.length;
	median_proportion /= results.length;
	avg_proportion /=results.length;


	// Print summary of games

	System.out.println("**************************************");
	System.out.println("**************************************");

	System.out.printf("%.3f very large certainty values \n",
			  deviant_ratio);

	
	System.out.printf("Average endstate median:\t %.2f \n",median_avg);
	System.out.printf("Average endstate mean:\t \t %.2f \n",mean_avg);
	System.out.printf("Average endstate max:\t \t %.2f \n", max_avg);
	System.out.printf("Average endstate min:\t \t %.2f \n", min_avg);
	System.out.printf("Final proportion greater than starting median:\t %.3f \n", median_proportion);
	System.out.printf("Final proportion greater than starting average: %.3f \n", avg_proportion);
	
	// if median is at least 5% greater than the mean, print
	// skew left message.
	if ((median_avg - mean_avg) > 
	    (0.05*mean_avg)) {
	    System.out.println("Median > mean. Could be skewed left.");
	}
	// if median as at least 5% less than the mean, print skew
	// right message.
	else if ((mean_avg - median_avg) > 
		 (0.05*mean_avg)) {
	    System.out.println("Median < mean. Could be skewed right.");
	}

	System.out.println("**************************************");
	System.out.println("**************************************");
	System.out.println("");
	System.out.println("");
    }

    void smallB(){
	// Mostly uniformly random certainties between 0.5 and 1.5
	// with a few smaller values sprinkled in for fun!
	// The number of deviants is proportional to grid size
	for ( t = 0; t < num_trials; t++ ){

	    int i; 
	    int j;	
	    for ( i = 0; i < certainties.length; i++ ){
		certainties[i] = prng.nextFloat() + (float)1.0;
	
	    } 
	    
	    // now add the deviants
	    int num_deviants = (int)(deviant_ratio*m*n);	    
	    for(i = 0; i < num_deviants; i++ ){
		int index = prng.nextInt(m*n);
		certainties[index] = prng.nextFloat();
	    }


	    // Now that we're done with all that, we can begin our 
	    //games!
	    ArrayList<ArrayList<Player>> players = 
		new ArrayList<ArrayList<Player>>(n);
	    
	    // populate the player array with players
	    for ( i = 0; i < n; i++){
		
		ArrayList<Player> column = new ArrayList<Player>(m);
		for ( j = 0; j < m; j++ ){
		    
		    column.add(new Player
			       ( life_points, 
				 certainties[m*i + j]*misanthropy, 
				 certainties[m*i + j], 
				 time_horizon, optimism, 
				 new Random()));
		}
		
		players.add(column);
	    }
	    
	    // build the game board
	    Board game = new Board(players, space_horizon);
	    //	    System.out.println("Start");
	    //    game.printSummaryStats();
	    starts[t] = game.getSummaryStats();
	    starts[t][4] = game.percentGreaterEqualThan(starts[t][1]);

	    // play the game
	    for (i = 0; i < num_rounds; i++ ){
		game.round();
		// if( i % 25 == 0){
		//     game.markPlayersGreaterThan
		// 	((float)(starts[t][1] + 0.025));
		// 	}
	    }

	    // print the end state of the game and store these stats
	    // in array
	    //  System.out.println("Finish");
	    //	    game.printSummaryStats();
	    results[t] = game.getSummaryStats();
	    results[t][4] = game.percentGreaterEqualThan(starts[t][1]);
	    results[t][5] = game.percentGreaterEqualThan(starts[t][0]);
	}




	// Stats on starting stats? What madness is this?!?!
	float median_avg_start, mean_avg_start, max_avg_start, 
	    min_avg_start, mean_proportion_start;
	median_avg_start = mean_avg_start = 
	    max_avg_start = min_avg_start = mean_proportion_start = 0;
	
	for (int i = 0; i < starts.length; i++ ){
	    
	    mean_avg_start += starts[i][0];
	    median_avg_start += starts[i][1];
	    max_avg_start += starts[i][2];
	    min_avg_start += starts[i][3];
	    mean_proportion_start += starts[i][4];
	    
	}
	
	mean_avg_start /= starts.length;
	median_avg_start /= starts.length;
	max_avg_start /= starts.length;
	min_avg_start /= starts.length;
	mean_proportion_start /= starts.length;
	
	
	// Print summary of games
	
	System.out.println("**************************************");
	System.out.println("**************************************");
	
	System.out.printf("%.3f very small certainty values \n",
			  deviant_ratio);
	System.out.printf("Average startstate median:\t %.2f \n",
			  median_avg_start);
	System.out.printf("Average startstate mean:\t %.2f \n",
			  mean_avg_start);
	System.out.printf("Average startstate max:\t \t %.2f \n",
			  max_avg_start);
	System.out.printf("Average startstate min:\t \t %.2f \n", 
			  min_avg_start);
	System.out.printf("Starting proportion greater than starting avg: %.3f \n", mean_proportion_start);
	

	// Stats on stats? What madness is this?!?!
	float median_avg, mean_avg, max_avg, min_avg, 
	    median_proportion, avg_proportion;
	median_avg = mean_avg = max_avg = min_avg = 
	    median_proportion =  avg_proportion = 0;

	for (int i = 0; i < results.length; i++ ){
	    
	    mean_avg += results[i][0];
	    median_avg += results[i][1];
	    max_avg += results[i][2];
	    min_avg += results[i][3];
	    median_proportion += results[i][4];
	    avg_proportion += results[i][5];
	    
	}

	mean_avg /= results.length;
	median_avg /= results.length;
	max_avg /= results.length;
	min_avg /= results.length;
	avg_proportion /= results.length;
	median_proportion /= results.length;

	// Print summary of games

	System.out.println("**************************************");
	System.out.println("**************************************");

	System.out.printf("%.3f very small certainty values \n",
			  deviant_ratio);

	
	System.out.printf("Average endstate median:\t %.2f \n",median_avg);
	System.out.printf("Average endstate mean:\t \t %.2f \n",mean_avg);
	System.out.printf("Average endstate max:\t \t %.2f \n", max_avg);
	System.out.printf("Average endstate min:\t \t %.2f \n", min_avg);
	System.out.printf("Final proportion greater than starting median:\t %.3f \n", median_proportion);
	System.out.printf("Final proportion greater than starting average: %.3f \n", avg_proportion);

	// if median is at least 5% greater than the mean, print
	// skew left message.
	if ((median_avg - mean_avg) > 
	    (0.05*mean_avg)) {
	    System.out.println("Median > mean. Could be skewed left.");
	}
	// if median as at least 5% less than the mean, print skew
	// right message.
	else if ((mean_avg - median_avg) > 
		 (0.05*mean_avg)) {
	    System.out.println("Median < mean. Could be skewed right.");
	}

	System.out.println("**************************************");
	System.out.println("**************************************");
	System.out.println("");
	System.out.println("");
    

    }

    void diverse(){

	// Mostly uniformly random certainties between 1.0 and 2.0
	// with a few larger and a few smaller values sprinkled in 
	// for fun!
	// The number of deviants is proportional to grid size
	for ( t = 0; t < num_trials; t++ ){
	    
	    int i; 
	    int j;	
	    for ( i = 0; i < certainties.length; i++ ){
		certainties[i] = prng.nextFloat() + (float)1.0;
	    } 
	    
	    // now add the deviants
	    int num_deviants = (int)(2*deviant_ratio*m*n);	    
	    Set<Integer> deviant_set = new LinkedHashSet<Integer>();

	    // pick indices to which small or large certainties will
	    // be assigned
	    while( deviant_set.size() < num_deviants ){
		
		deviant_set.add( prng.nextInt(m*n) );
	    }

	    Iterator<Integer> itr = deviant_set.iterator();
	    // assign deviant certainties
	    while (itr.hasNext()){

		// large certainty
		certainties[itr.next()] = 
		    prng.nextFloat() + (float)2.5;
		
		// smaller certainty
		certainties[itr.next()] = 
		    prng.nextFloat();
		
	    }


	    // Now that we're done with all that, we can begin our 
	    //games!
	    ArrayList<ArrayList<Player>> players = 
		new ArrayList<ArrayList<Player>>(n);
	    
	    // populate the player array with players
	    for ( i = 0; i < n; i++){
		
		ArrayList<Player> column = new ArrayList<Player>(m);
		for ( j = 0; j < m; j++ ){
		    
		    column.add(new Player
			       ( life_points, 
				 certainties[m*i + j]*misanthropy,  
				 certainties[m*i + j], 
				 time_horizon, optimism, 
				 new Random()));
		}
		
		players.add(column);
	    }
	    
	    // build the game board
	    Board game = new Board(players, space_horizon);
	    //	    System.out.println("Start");
	    //   game.printSummaryStats();
	    starts[t] = game.getSummaryStats();
	    starts[t][4] = game.percentGreaterEqualThan(starts[t][1]);

	    // play the game
	    for (i = 0; i < num_rounds; i++ ){
		game.round();
		// if( i % 25 == 0){
		//     game.markPlayersGreaterThan
		// 	((float)(starts[t][1] + 0.025));
		// 	}
	    }

	    // print the end state of the game and store these stats
	    // in array
	    //    System.out.println("Finish.");
	    //	    game.printSummaryStats();
	    results[t] = game.getSummaryStats();
	    results[t][4] = game.percentGreaterEqualThan(starts[t][1]);
	    results[t][5] = game.percentGreaterEqualThan(starts[t][0]);
	}

	// Stats on starting stats? What madness is this?!?!
	float median_avg_start, mean_avg_start, max_avg_start, 
	    min_avg_start, mean_proportion_start;
	median_avg_start = mean_avg_start = 
	    max_avg_start = min_avg_start = mean_proportion_start = 0;
	
	for (int i = 0; i < starts.length; i++ ){
	    
	    mean_avg_start += starts[i][0];
	    median_avg_start += starts[i][1];
	    max_avg_start += starts[i][2];
	    min_avg_start += starts[i][3];
	    mean_proportion_start += starts[i][4];
	    
	}
	
	mean_avg_start /= starts.length;
	median_avg_start /= starts.length;
	max_avg_start /= starts.length;
	min_avg_start /= starts.length;
	mean_proportion_start /= starts.length;
	
	// Print summary of games
	
	System.out.println("**************************************");
	System.out.println("**************************************");
	
	System.out.printf("Some very large certainty values, some small.\n");
	
	System.out.printf("Average startstate median:\t %.2f \n",
			  median_avg_start);
	System.out.printf("Average startstate mean:\t %.2f \n",
			  mean_avg_start);
	System.out.printf("Average startstate max:\t \t %.2f \n",
			  max_avg_start);
	System.out.printf("Average startstate min:\t \t %.2f \n", 
			  min_avg_start);
	System.out.printf("Starting proportion greater than starting avg: %.3f \n", mean_proportion_start);
	

	// Stats on stats? What madness is this?!?!
	float median_avg, mean_avg, max_avg, min_avg, 
	    median_proportion, avg_proportion;
	median_avg = mean_avg = max_avg = min_avg = 
	    median_proportion =  avg_proportion = 0;

	for (int i = 0; i < results.length; i++ ){
	    
	    mean_avg += results[i][0];
	    median_avg += results[i][1];
	    max_avg += results[i][2];
	    min_avg += results[i][3];
	    median_proportion += results[i][4];
	    avg_proportion += results[i][5];
	    
	}

	mean_avg /= results.length;
	median_avg /= results.length;
	max_avg /= results.length;
	min_avg /= results.length;
	avg_proportion /= results.length;
	median_proportion /= results.length;


	// Print summary of games

	System.out.println("**************************************");
	System.out.println("**************************************");

	System.out.printf("Some very large certainty values, some small. \n");

	
	System.out.printf("Average endstate median: \t %.2f \n",median_avg);
	System.out.printf("Average endstate mean: \t \t %.2f \n",mean_avg);
	System.out.printf("Average endstate max:\t \t %.2f \n", max_avg);
	System.out.printf("Average endstate min:\t \t %.2f \n", min_avg);

	System.out.printf("Final proportion greater than starting median:\t %.3f \n", median_proportion);
	System.out.printf("Final proportion greater than starting average: %.3f \n", avg_proportion);
	
	// if median is at least 5% greater than the mean, print
	// skew left message.
	if ((median_avg - mean_avg) > 
	    (0.05*mean_avg)) {
	    System.out.println("Median > mean. Could be skewed left.");
	}
	// if median as at least 5% less than the mean, print skew
	// right message.
	else if ((mean_avg - median_avg) > 
		 (0.05*mean_avg)) {
	    System.out.println("Median < mean. Could be skewed right.");
	}

	System.out.println("**************************************");
	System.out.println("**************************************");
	System.out.println("");
	System.out.println("");

    }

    void bimodal(){

	// All certainties are very large or very small
	for ( t = 0; t < num_trials; t++ ){
	    
	    int i; 
	    int j;	
	    //for ( i = 0; i < certainties.length; i++ ){
	    //		certainties[i] = prng.nextFloat() + (float)1.0;
	    // } 
	    
	    // now add the deviants
	    int num_deviants = m*n;	    
	    Set<Integer> deviant_set = new LinkedHashSet<Integer>();

	    // pick indices to which small or large certainties will
	    // be assigned
	    while( deviant_set.size() < num_deviants ){
		
		deviant_set.add( prng.nextInt(m*n) );
	    }

	    Iterator<Integer> itr = deviant_set.iterator();
	    // assign deviant certainties
	    while (itr.hasNext()){

		// large certainty
		certainties[itr.next()] = 
		    prng.nextFloat() + (float)2.5;
		
		// smaller certainty
		certainties[itr.next()] = 
		    prng.nextFloat();
		
	    }


	    // Now that we're done with all that, we can begin our 
	    //games!
	    ArrayList<ArrayList<Player>> players = 
		new ArrayList<ArrayList<Player>>(n);
	    
	    // populate the player array with players
	    for ( i = 0; i < n; i++){
		
		ArrayList<Player> column = new ArrayList<Player>(m);
		for ( j = 0; j < m; j++ ){
		    
		    column.add(new Player
			       ( life_points, 
				 certainties[m*i + j]*misanthropy,  
				 certainties[m*i + j], 
				 time_horizon, optimism, 
				 new Random()));
		}
		
		players.add(column);
	    }
	    
	    // build the game board
	    Board game = new Board(players, space_horizon);
	    //	    System.out.println("Start");
	    //   game.printSummaryStats();
	    starts[t] = game.getSummaryStats();
	    starts[t][4] = game.percentGreaterEqualThan(starts[t][1]);

	    // play the game
	    for (i = 0; i < num_rounds; i++ ){
		game.round();
		// if( i % 25 == 0){
		//     game.markPlayersGreaterThan
		// 	((float)(starts[t][1] + 0.025));
		// }
	    }

	    // print the end state of the game and store these stats
	    // in array
	    //    System.out.println("Finish.");
	    //	    game.printSummaryStats();
	    results[t] = game.getSummaryStats();
	    results[t][4] = game.percentGreaterEqualThan(starts[t][1]);
	    results[t][5] = game.percentGreaterEqualThan(starts[t][0]);
	}

	// Stats on starting stats? What madness is this?!?!
	float median_avg_start, mean_avg_start, max_avg_start, 
	    min_avg_start, mean_proportion_start;
	median_avg_start = mean_avg_start = 
	    max_avg_start = min_avg_start = mean_proportion_start = 0;
	
	for (int i = 0; i < starts.length; i++ ){
	    
	    mean_avg_start += starts[i][0];
	    median_avg_start += starts[i][1];
	    max_avg_start += starts[i][2];
	    min_avg_start += starts[i][3];
	    mean_proportion_start += starts[i][4];
	    
	}
	
	mean_avg_start /= starts.length;
	median_avg_start /= starts.length;
	max_avg_start /= starts.length;
	min_avg_start /= starts.length;
	mean_proportion_start /= starts.length;
	
	
	// Print summary of games
	
	System.out.println("**************************************");
	System.out.println("**************************************");
	
	System.out.printf("All very large or small.\n");
	
	System.out.printf("Average startstate median:\t %.2f \n",
			  median_avg_start);
	System.out.printf("Average startstate mean:\t %.2f \n",
			  mean_avg_start);
	System.out.printf("Average startstate max:\t \t %.2f \n",
			  max_avg_start);
	System.out.printf("Average startstate min:\t \t %.2f \n", 
			  min_avg_start);
	System.out.printf("Starting proportion greater than starting avg: %.3f \n", mean_proportion_start);
	

	// Stats on stats? What madness is this?!?!
	float median_avg, mean_avg, max_avg, min_avg, 
	    median_proportion, avg_proportion;
	median_avg = mean_avg = max_avg = min_avg = 
	    median_proportion = avg_proportion = 0;
	
	for (int i = 0; i < results.length; i++ ){
	    
	    mean_avg += results[i][0];
	    median_avg += results[i][1];
	    max_avg += results[i][2];
	    min_avg += results[i][3];
	    median_proportion += results[i][4];
	    avg_proportion += results[i][5];
	    
	}

	mean_avg /= results.length;
	median_avg /= results.length;
	max_avg /= results.length;
	min_avg /= results.length;
	avg_proportion /= results.length;
	median_proportion /= results.length;


	// Print summary of games

	System.out.println("**************************************");
	System.out.println("**************************************");

	System.out.printf("All very small or very large \n");
	
	System.out.printf("Average endstate median: \t %.2f \n",median_avg);
	System.out.printf("Average endstate mean: \t \t %.2f \n",mean_avg);
	System.out.printf("Average endstate max:\t \t %.2f \n", max_avg);
	System.out.printf("Average endstate min:\t \t %.2f \n", min_avg);

	System.out.printf("Final proportion greater than starting median:\t %.3f \n", median_proportion);
	System.out.printf("Final proportion greater than starting average: %.3f \n", avg_proportion);
	
	// if median is at least 5% greater than the mean, print
	// skew left message.
	if ((median_avg - mean_avg) > 
	    (0.05*mean_avg)) {
	    System.out.println("Median > mean. Could be skewed left.");
	}
	// if median as at least 5% less than the mean, print skew
	// right message.
	else if ((mean_avg - median_avg) > 
		 (0.05*mean_avg)) {
	    System.out.println("Median < mean. Could be skewed right.");
	}

	System.out.println("**************************************");
	System.out.println("**************************************");
	System.out.println("");
	System.out.println("");

    }


    public static void main (String[] args){
	
	// only command line argument is the number of trials for each
	// test
	int num_trials = Integer.parseInt(args[0]);
	
	PDTrials trials = new PDTrials(num_trials);
	
	trials.uniform();
	trials.largeB();
	trials.smallB();
	trials.diverse();
	trials.bimodal();
}
    

}
