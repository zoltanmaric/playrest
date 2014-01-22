package models.dataaccess;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.Sport;
import models.TimestampedPoint;
import models.Tour;
import models.User;
import models.dtos.TimestampedPointDto;
import models.dtos.TourDto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Point;

@RunWith(PowerMockRunner.class)
@PrepareForTest({User.class, Sport.class})
public class TourDataAccessUtilsTest {
	/** A fixed epoch time. Fixed for the sake of reproducibility of tests. */
	private static final long TIME = 1388840323000L;
	
	private static final String USERNAME = "playrest";
	private static final String SPORT_NAME = "hike";
	
	private User testUser;
	private Sport testSport;
	private Tour testTour;
	private TourDto testTourDto;
	
	@Before
	public void before() {
		testUser = new User();
		testUser.setUsername(USERNAME);
		
		testSport = new Sport();
		testSport.setName(SPORT_NAME);
		
		testTour = new Tour();
		testTour.setUser(testUser);
		testTour.setSport(testSport);
		List<TimestampedPoint> expectedPoints = createPoints(testTour);
		testTour.setPoints(expectedPoints);
		
		TimestampedPoint startPoint = Collections.min(expectedPoints);
		testTour.setStartPoint(startPoint);
		
		List<TimestampedPointDto> pointDtos = createPointDtos();
		testTourDto = new TourDto(USERNAME, SPORT_NAME, pointDtos);
	}
	
	@Test
	public void testToEntity() {
		mockStatic(User.class);
		mockStatic(Sport.class);
		
		User expectedUser = testUser;
		expect(User.findByUsername(eq(USERNAME))).andReturn(expectedUser);
		replay(User.class);
		
		Sport expectedSport = testSport;
		expect(Sport.findByName(eq(SPORT_NAME))).andReturn(expectedSport);
		replay(Sport.class);
		
		Tour expectedTour = testTour;
		Tour actualTour = TourDataAccessUtils.toEntity(testTourDto);
		
		Assert.assertEquals(expectedTour, actualTour);
		
		// These are not included in the hash and equals methods of Tour so we
		// check them manually:
		Assert.assertEquals(expectedTour.getPoints(), actualTour.getPoints());
		Assert.assertEquals(
				expectedTour.getStartPoint(), actualTour.getStartPoint());
	}
	
	@Test
	public void testToDto() {
		TourDto expectedTourDto = testTourDto;
		TourDto actualTourDto = TourDataAccessUtils.toDto(testTour);
		Assert.assertEquals(expectedTourDto, actualTourDto);
	}
	
	private static List<TimestampedPointDto> createPointDtos() {
		List<TimestampedPointDto> points = Lists.newArrayListWithCapacity(2);
		
		Date time = new Date(TIME - 10);
		points.add(new TimestampedPointDto(time, 1, 2, 3));

		time = new Date(TIME);
		points.add(new TimestampedPointDto(time, 4, 5, 6));
		
		return points;
	}
	
	private static List<TimestampedPoint> createPoints(Tour tour) {
		List<TimestampedPoint> points = Lists.newArrayListWithCapacity(2);
		
		Date time = new Date(TIME - 10);
		Point point = TourDataAccessUtils.createPoint(1, 2, 3);
		TimestampedPoint tsPoint = new TimestampedPoint(time , point, tour);
		points.add(tsPoint);
		
		time = new Date(TIME);
		point = TourDataAccessUtils.createPoint(4, 5, 6);
		tsPoint = new TimestampedPoint(time , point, tour);
		points.add(tsPoint);
		
		return points;
	}
}
