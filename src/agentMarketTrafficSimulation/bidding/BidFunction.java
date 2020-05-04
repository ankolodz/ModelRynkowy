package agentMarketTrafficSimulation.bidding;

public class BidFunction {

	/*
	 * function argument x=c_edge 
	 * E other const parameters of function = T+L+W
	 * cost - cost parameter [0.7 , 0.2, 0.3] depends on agent type
	 * minCost
	 * maxCost
	 * f(x) = a*x + b;
	 */
	
	private double E=-1;
	private double cost=1;
	private double minCost=0.1;
	private double maxCost=1;
	private double a;
	private double b;
	
	
	public BidFunction(double E, double cost, double minCost, double maxCost){
		this.E =E;
		this.cost = cost;
		this.minCost = minCost;
		this.maxCost = maxCost;
		calculate();
	}
	
	public BidFunction(){
	}
	
	private void calculate(){
		double d;
		
		this.a = (cost)/(maxCost - minCost);
		if(a<0) System.err.println("Error in BidFunction");		
		d = (-1)*(cost * minCost)/(maxCost - minCost);
		this.b = E + d;
	}
	
	public double getA(){
		return this.a;
	}
	
	public double getB(){
		return this.b;
	}
	
	public void setE(double E){
		this.E = E;
		calculate();
	}
	public void setMaxCost(double maxCost){
		this.maxCost = maxCost;
		calculate();
	}
	
	public void setMinCost(double minCost){
		this.minCost = minCost;
		calculate();
	}
	
	public void setCost(double cost){
		this.cost = cost;
		calculate();
	}
	/*
	 * all the equations in documentation
	 */	
	public double getFunctionValue(double x){	
		double result;
		//linear function supposed to be decreasing
		result = a*x + b;
		return result;
	}
	
	public double getZeroCrossing(){
		double result = 0;
		if(a<=0 || b>=0){
			result = Double.MAX_VALUE;
		}
		else{
			result = (-1)*b/a;
			if(result<0){
				System.err.println("Error with zerocrossing calculation in BiDFunction");
				
			}
		}
		
		return result;
	}
	
	
}
