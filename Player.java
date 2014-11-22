//The Player class defines players of iterated prisoner's dilemma
//games. 
//
//@author Jessica Sorrell
//@version 25-Oct-2014
// 
import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.*;
import java.util.Collections;

public class Player
{

    // Hidden data members
    private Random prng;
    private int memory_span;
    private float misanthropy;
    private float certainty;
    private float optimism;

    private final int cooperate = 1;
    private final int defect = -1;

    private final float PROB_MUTATION = (float)0.2;

    private ArrayList<Float> memories;

    // Public data members
    int total_life;
    int life_points;
    
    // Default Constructor
    public Player (){
    }

    // Constructor
    public Player (int life_points, float misanthropy, float certainty, 
		   int memory_span, float optimism, Random prng){
	this.total_life = life_points;
	this.life_points = life_points;
	this.misanthropy = misanthropy;
	this.certainty = certainty;
	this.memory_span = memory_span;
	this.prng = prng;
	this.optimism = optimism;

	this.memories = new ArrayList<Float>(memory_span);
	for (int i = 0; i < memory_span; i++){
	    memories.add(optimism);
	}
    }  

    /**
     * setLP sets this Player's life points to the given value
     *
     * @param  life_points  the total number of life points
     **/
    void setLP (int life_points){
	this.life_points = life_points;
    }


    /**
     * getLP returns this player's current life points
     *
     * @return  life_points  this player's current life points
     **/
    int getLP (){
	return life_points;
    }


    /**
     * increaseLP adds the given number of life points to the 
     * player's total life points.
     *
     * @param  add_life  the number of life points to add
     **/
    void increaseLP (int add_life){
	this.life_points += add_life;
    }

    /**
     * setMemory sets this Player's memory span to the given int.
     * A player's memory represents the player's time horizon, how
     * many rounds of play the player remembers.
     *
     * calling setMemory will wipe reset the current memory array
     * to all 0's.
     *
     * @param  memory_span  the player's time horizon
     **/
    void setMemory (int memory_span){
	this.memory_span = memory_span;
	this.memories = new ArrayList<Float>(memory_span);

	for (int i = 0; i < memory_span; i++){
	    memories.add((float)0.0);
	}
    }


    /**
     * getMemory returns this Player's memory span
     *
     * @return  memory_span  This Player's memory span
     **/
    int getMemory (){
	return memory_span;
    }

    /**
     * getMemories returns the ArrayList of this Player's memories
     *
     * @return  memories  This Player's memories
     **/
    ArrayList<Float> getMemories(){
	return memories;
    }

    /**
     * pushMemory adds a new value to the Player's memory array. If
     * the player's memory is already full, this will push out the 
     * oldest memory. Just like in real life.
     *
     * @param  mem_val  The value representing the memory
     **/
    void pushMemory (float mem_val){
		
	Collections.rotate(memories, -1);
	memories.set( memory_span - 1, mem_val);
    }

    /**
     * setPRNG sets the player's PRNG to the given PRNG
     *
     * @param  prng  This player's new PRNG
     *
     **/
    void setPRNG (Random prng){
	this.prng = prng;
    }


    /**
     * setMisanthropy sets this player's level of misanthropy. 
     * Misanthropy is defined as the a in 
     * f(x) = 1/(1 + e^(a - bx))
     *
     * @param  misanthropy  This player's new level of misanthropy
     **/
    void setMisanthropy (float misanthropy){
	this.misanthropy = misanthropy;
    }


    /**
     * getMisanthropy returns this player's level of misanthropy.
     * Misanthropy is defined as the a in 
     * f(x) = 1/(1 + e^(a - bx))
     *
     * @return  misanthropy  This player's misanthropy
     **/
    float getMisanthropy (){
	return misanthropy;
    }


    /**
     * setCertainty sets this player's level of certainty.
     * Certainty is defined as the b in 
     * f(x) = 1/(1 + e^(a - bx))
     *
     * @param  certainty  This player's new level of certainty
     **/
    void setCertainty (float certainty){
	this.certainty = certainty;
    }


    /**
     * getCertainty returns this player's level of certainty.
     * Certainty is defined as the b in 
     * f(x) = 1/(1 + e^(a - bx))
     *
     * @return  certainty  This player's certainty
     **/
    float getCertainty (){
	return certainty;
    }


    /**
     * getDecision returns this player's decision function 
     * evaluated at the given point. This is a "memoryless"
     * decision in that this Player is not basing its decision off
     * of its own memories.
     *
     * @param  conditions  the float at which to evaluate dec func
     * 
     * @return decision  return cooperate or defect
     **/
    int getDecision (float conditions){

	// evaluate this player's decision function at the given point
	float threshold = 
	    (float)(1 / (1 + exp(misanthropy  - conditions*certainty)));

	// run player's random number generator
	float mood = prng.nextFloat();

	// compare mood to threshold. cooperate or defect accordingly
	return (mood <= threshold) ? cooperate : defect;
    }    


    /**
     * getDecision returns this player's decision function 
     * evaluated at a point representing what it remembers about
     * its environment. 
     *
     * @return decision  return cooperate or defect
     **/
    int getDecision (){

	float conditions = 0;

	// take a weighted average of memories. recent memories
	// are weighted more heavily.
	for (int i = 0; i < memory_span; i++){
	    conditions += (i+1)*memories.get(i);
	}
	conditions /= (memory_span*(memory_span + 1)/2);

	// evaluate this player's decision function at the given point
	float threshold = 
	    (float)(1.0/(1.0 + exp(misanthropy - conditions*certainty)));

	// run player's random number generator
	float mood = prng.nextFloat();

	// compare mood to threshold. cooperate or defect accordingly
	return (mood <= threshold) ? cooperate : defect;
    }



    /**
     * amnesia wipes memories and sets them all to the specified 
     * value
     *
     * @param  optimism  baseline memories
     **/
    void amnesia (float optimism){

	for (int i = 0; i < memory_span; i++){
	    memories.set (i, optimism);
	}
    }



    /**
     * birth creates a new Player with the same relevant parameters
     * as this Player, with the probability of some small mutation.
     *
     * @return  offspring  born into a cruel game where the only
     * way to win is not to play, our young heroine steps onto the
     * battlefield. Perhaps she will be the one to change things.
     * Perhaps she will be the one to end this twisted game.
     **/
    Player birth (){
	
	float mutation = prng.nextFloat();
     	
	if (mutation < PROB_MUTATION){

	    float mutated_certainty = 
		max(0, this.certainty +((float)(prng.nextFloat() - .5)/10));
	    
	    
	    return new Player(  this.total_life, this.misanthropy,
				mutated_certainty, this.memory_span, 
				this.optimism, new Random());
	    

	}
	return new Player( this.total_life, this.misanthropy,
			   this.certainty, this.memory_span, 
			   this.optimism, new Random());
    }
    
    public static void main (String args[]){

	int mem = 4;
	Random prn = new Random();
	float misanthropy = 0;
	float certainty = 1;
	int life = 10;
	float optimism = (float)0.0;


	Player player1 = 
	    new Player (life, misanthropy, certainty, mem, optimism, prn);

	
	System.out.printf( "Life points: 10 = %d \n", player1.getLP() );
	System.out.printf( "Misanthropy: 0 = %.2f \n", 
			   player1.getMisanthropy());
	System.out.printf( "Certainty: 1 = %.2f \n", 
			    player1.getCertainty());
	System.out.printf( "Mem: 4 = %d \n", player1.getMemory() );



	System.out.printf( "Decision: Random = %d \n", 
			    player1.getDecision((float)0.0));
	System.out.printf( "Decision: Defect = %d \n", 
			    player1.getDecision((float)-4.0));
	System.out.printf( "Decision: Cooperate = %d \n",
			    player1.getDecision( (float)4.0));
	
	System.out.printf( "Decision: based on neut memories = %d \n",
			    player1.getDecision());
	
	float mem_reset = 4;
	player1.amnesia(mem_reset);
	
	System.out.printf( "Decision: based on safe memories = %d \n",
			    player1.getDecision());

	mem_reset = -4;
	player1.amnesia(mem_reset);
	System.out.printf( "Decision: unsafe memories = %d \n", 
			    player1.getDecision());


	System.out.printf( "Memories: \n");
	for (int i = 0; i < player1.getMemory(); i++ ){
	    System.out.printf( "%d : %.2f \n", i, player1.getMemories().get(i));
	}

	player1.pushMemory((float)3.33);
	System.out.printf( "Memories: \n");
	for (int i = 0; i < player1.getMemory(); i++ ){
	    System.out.printf( "%d : %.2f \n", i, player1.getMemories().get(i));
	}

	player1.pushMemory((float)2.22);
	System.out.printf( "Memories: \n");
	for (int i = 0; i < player1.getMemory(); i++ ){
	    System.out.printf( "%d : %.2f \n", i, player1.getMemories().get(i));
	}

	player1.pushMemory((float)1.11);
	System.out.printf( "Memories: \n");
	for (int i = 0; i < player1.getMemory(); i++ ){
	    System.out.printf( "%d : %.2f \n", i, player1.getMemories().get(i));
	}
    }
}
