package controllers;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.Collections;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Results;
import play.mvc.Results.Status;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Controller.class, Application.class})
public class ApplicationTest {
	private Request mockRequest;
	@Before
	public void before() {
		mockStatic(Controller.class);
		mockRequest = EasyMock.createMock(Request.class);
		expect(Controller.request()).andReturn(mockRequest);
		replayAll(Controller.class);
	}
	
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
		Map<String, String[]> qMap = ImmutableMap.of("startlat",
				new String[]{"45.3"}, "startlon", new String[]{"15.3"});

		Status result = (Status) Application.handleCriteria(qMap);
		int expected = Results.badRequest()
				.getWrappedSimpleResult().header().status();
		int actual = result.getWrappedSimpleResult().header().status();
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testGetToursByCriteriaPosition() {
		Map<String, String[]> qMap = ImmutableMap.of("startlat",
				new String[]{"45.3"}, "startlon", new String[]{"15.3"},
				"radius", new String[]{"15000"});
		expect(mockRequest.queryString()).andReturn(qMap);
		mockStaticPartial(Application.class, "getToursByStartPoint");
		expect(Application.getToursByStartPoint(
				eq(45.3),
				eq(15.3),
				eq(Optional.<Double>absent()),
				eq(15000.0),
				eq(Optional.<String>absent()
						))).andReturn(Results.ok());
		replayAll(Application.class);
		replayAll(mockRequest);
		Application.getToursByCriteria();
	}
	
	@Test
	public void testGetToursByCriteriaUsername() {
		Map<String, String[]> qMap = ImmutableMap.of("username",
				new String[]{"user"});
		expect(mockRequest.queryString()).andReturn(qMap);
		mockStaticPartial(Application.class, "getToursByUsername");
		expect(Application.getToursByUsername(eq("user"))).andReturn(Results.ok());
		replayAll(Application.class);
		replayAll(mockRequest);
		Application.getToursByCriteria();
	}
	
	@Test
	public void testGetToursByCriteriaSport() {
		Map<String, String[]> qMap = ImmutableMap.of("sport",
				new String[]{"hiking"});
		expect(mockRequest.queryString()).andReturn(qMap);
		mockStaticPartial(Application.class, "getToursBySport");
		expect(Application.getToursBySport(eq("hiking"))).andReturn(Results.ok());
		replayAll(Application.class);
		replayAll(mockRequest);
		Application.getToursByCriteria();
	}
}
