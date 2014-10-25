//The Player class defines players of iterated prisoner's dilemma
//games. 
//
//@author Jessica Sorrell
//@version 25-Oct-2014
// 
import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.*;

public class Player
{

    // Hidden data members
    private Random prng;
    private int memory_span;
    private float altruism;
    private float certainty;

    private final int cooperate = 1;
    private final int defect = -1;

    private ArrayList<Float> memories;

    // Public data members
    int life_points;
    
    // Default Constructor
    public Player (){
    }

    // Constructor
    public Player (int life_points, float altruism, float certainty, 
		   int memory_span, float optimism, Random prng){
	
	this.life_points = life_points;
	this.altruism = altruism;
	this.certainty = certainty;
	this.memory_span = memory_span;
	this.prng = prng;

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
     * decreaseLP subtracts the given number of life points from the 
     * player's  total life points.
     *
     * @param  lose_life  the number of life points to subtract
     **/
    void decreaseLP (int lose_life){
	this.life_points -= lose_life;
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
     * @return  memory_span  This player's memory span
     **/
    int getMemory (){
	return memory_span;
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
     * setAltruism sets this player's level of altruism. 
     * Altruism is defined as the a in 
     * f(x) = 1/(1 + e^(a - bx))
     *
     * @param  altruism  This player's new level of altruism
     **/
    void setAltruism (float altruism){
	this.altruism = altruism;
    }


    /**
     * getAltruism returns this player's level of altruism.
     * Altruism is defined as the a in 
     * f(x) = 1/(1 + e^(a - bx))
     *
     * @return  altruism  This player's altruism
     **/
    float getAltruism (){
	return altruism;
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
	    (float)(1 / (1 + exp(altruism  - conditions*certainty)));

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
	    (float)(1.0 / (1.0 + exp(altruism  - conditions*certainty)));

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


    public static void main (String args[]){

	int mem = 4;
	Random prn = new Random();
	float altruism = 0;
	float certainty = 1;
	int life = 10;
	float optimism = (float)0.0;


	Player player1 = 
	    new Player (life, altruism, certainty, mem, optimism, prn);

	
	System.out.printf( "Life points: 10 = %d \n", player1.getLP() );
	System.out.printf( "Altruism: 0 = %.2f \n", 
			   player1.getAltruism());
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
	
			
    }
}
