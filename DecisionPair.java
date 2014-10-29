public class DecisionPair {


    public int decision1;
    public int decision2;
   
   
    public DecisionPair( int decision1, int decision2 ){
	this.decision1 = decision1;
	this.decision2 = decision2;
    } 

    public void setDecision( int decision1, int decision2 ){
	this.decision1 = decision1;
	this.decision2 = decision2;
    }
    
    public int[] getPayoffs(){
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
}
