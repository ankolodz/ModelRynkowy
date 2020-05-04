package agentMarketTrafficSimulation.agent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.worldwind.render.BasicWWTexture;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.render.WWTexture;
import repast.simphony.visualization.gis3D.style.DefaultMarkStyle;

public class DefaultAgentStyle extends DefaultMarkStyle<DefaultAgent> {
	
	private Map<Integer, Color> colorMap;
	private Map<Integer, String> patternMap;
	
	public DefaultAgentStyle() {
		super();
		this.colorMap = new HashMap<>();
		colorMap.put(0, Color.BLUE);
		colorMap.put(1, Color.GREEN);
		colorMap.put(2, Color.RED);
		colorMap.put(3, Color.YELLOW);
		this.patternMap = new HashMap<>();
		patternMap.put(1, PatternFactory.PATTERN_SQUARE);
		patternMap.put(2, PatternFactory.PATTERN_CIRCLE);
		patternMap.put(3, PatternFactory.PATTERN_TRIANGLE_UP);
	}
	

	@Override
	public WWTexture getTexture(DefaultAgent object, WWTexture texture) {
		if (object.isEnded()) {
			return new BasicWWTexture(new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB));
		}else if (texture != null) {
			return texture;
		}else {
			int agentType = object.getAgentStyleIndex();
			BufferedImage image = PatternFactory.createPattern(patternMap.get(agentType / 10), 
					new Dimension(10, 10), 0.7f,  colorMap.get(agentType % 10));
			
			texture = new BasicWWTexture(image);
			return texture;
		}
	}


	@Override
	public String getLabel(DefaultAgent obj) {
		return "Agent: " + obj.getId();
	}


//	@Override
//	public Font getLabelFont(DefaultAgent obj) {
//		return new Font("Symbol", Font.PLAIN, 8);
//	}
	
	
	
}
