package agentMarketTrafficSimulation.agent;

public enum AgentType {
	WHOLE (-1), ONE (1), TWO (2), THREE (3);
	
	private final int type;
	AgentType(int type){
		this.type = type;
	}
	
	public int type(){
		return type;
	}
}
