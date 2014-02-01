package controllers;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import play.mvc.Results;
import play.mvc.Results.Status;

import com.google.common.collect.ImmutableMap;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {
	
	@Test
	public void testHandleCriteriaNoQueryParams() throws Exception {
		Status result = (Status) Application.handleCriteria(
				Collections.<String, String[]>emptyMap());
		int expected = Results.badRequest()
				.getWrappedSimpleResult().header().status();
		int actual = result.getWrappedSimpleResult().header().status();
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testHandleCriteriaNoRadius() {
		Map<String, String[]> qMap = ImmutableMap.of(
				"startlat", new String[]{"45.3"}, "startlon", new String[]{"15.3"});

		Status result = (Status) Application.handleCriteria(qMap);
		int expected = Results.badRequest()
				.getWrappedSimpleResult().header().status();
		int actual = result.getWrappedSimpleResult().header().status();
		Assert.assertEquals(expected, actual);
	}
}
