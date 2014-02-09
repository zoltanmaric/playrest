package controllers;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import play.mvc.Controller;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Controller.class, Application.class })
public class ApplicationTest {
	// private Request mockRequest;
	// @Before
	// public void before() {
	// mockStatic(Controller.class);
	// mockRequest = EasyMock.createMock(Request.class);
	// expect(Controller.request()).andReturn(mockRequest);
	// replayAll(Controller.class);
	// }
	//
	// @Test
	// public void testHandleCriteriaNoQueryParams() throws Exception {
	// Status result = (Status) Application.handleCriteria(
	// Collections.<String, String[]>emptyMap());
	// int expected = Results.badRequest()
	// .getWrappedSimpleResult().header().status();
	// int actual = result.getWrappedSimpleResult().header().status();
	// Assert.assertEquals(expected, actual);
	// }
	//
	// @Test
	// public void testHandleCriteriaNoRadius() {
	// Map<String, String[]> qMap = ImmutableMap.of("startlat",
	// new String[]{"45.3"}, "startlon", new String[]{"15.3"});
	//
	// Status result = (Status) Application.handleCriteria(qMap);
	// int expected = Results.badRequest()
	// .getWrappedSimpleResult().header().status();
	// int actual = result.getWrappedSimpleResult().header().status();
	// Assert.assertEquals(expected, actual);
	// }
	//
	// @Test
	// public void testGetToursByCriteriaPosition() {
	// Map<String, String[]> qMap = ImmutableMap.of("startlat",
	// new String[]{"45.3"}, "startlon", new String[]{"15.3"},
	// "radius", new String[]{"15000"});
	// expect(mockRequest.queryString()).andReturn(qMap);
	// mockStaticPartial(Application.class, "getToursByStartPoint");
	// expect(Application.getToursByStartPoint(
	// eq(45.3),
	// eq(15.3),
	// eq(Optional.<Double>absent()),
	// eq(15000.0),
	// eq(Optional.<String>absent()
	// ))).andReturn(Results.ok());
	// replayAll(Application.class);
	// replayAll(mockRequest);
	// Application.getToursByCriteria();
	// }
	//
	// @Test
	// public void testGetToursByCriteriaUsername() {
	// Map<String, String[]> qMap = ImmutableMap.of("username",
	// new String[]{"user"});
	// expect(mockRequest.queryString()).andReturn(qMap);
	// mockStaticPartial(Application.class, "getToursByUsername");
	// expect(Application.getToursByUsername(eq("user"))).andReturn(Results.ok());
	// replayAll(Application.class);
	// replayAll(mockRequest);
	// Application.getToursByCriteria();
	// }
	//
	// @Test
	// public void testGetToursByCriteriaSport() {
	// Map<String, String[]> qMap = ImmutableMap.of("sport",
	// new String[]{"hiking"});
	// expect(mockRequest.queryString()).andReturn(qMap);
	// mockStaticPartial(Application.class, "getToursBySport");
	// expect(Application.getToursBySport(eq("hiking"))).andReturn(Results.ok());
	// replayAll(Application.class);
	// replayAll(mockRequest);
	// Application.getToursByCriteria();
	// }
}
