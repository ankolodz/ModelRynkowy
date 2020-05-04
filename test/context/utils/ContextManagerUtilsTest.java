package context.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import agentMarketTrafficSimulation.context.utils.ContextManagerUtils;
import agentMarketTrafficSimulation.environment.fixedgeography.Junction;

public class ContextManagerUtilsTest {

	@Test
	public void iteratorToListTest() {
		// given
		List<Junction> originalList = Arrays.asList(Mockito.mock(Junction.class), Mockito.mock(Junction.class),
				Mockito.mock(Junction.class));
		Iterator<Junction> iter = originalList.iterator();
		// when
		List<Junction> list = ContextManagerUtils.iteratorToList(iter);
		// then
		Collections.sort(originalList);
		Collections.sort(list);
		assertEquals(originalList, list);
	}
}
