package models.dataaccess;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import models.Sport;
import models.TimestampedPoint;
import models.Tour;
import models.User;
import models.dtos.TimestampedPointDto;
import models.dtos.TourDto;
import play.Logger;
import play.Logger.ALogger;
import play.db.jpa.JPA;
import play.libs.F.Function0;
import play.libs.F.Promise;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Provides static data access methods for handling {@link Tour} objects.
 */
public class TourDataAccessUtils {
	private static final ALogger LOG = Logger.of("application");

	private static final GeometryFactory GEO_FACTORY = new GeometryFactory();
	private static final int SRID = 4326;

	private static final String USER_PARAM = "user";
	private static final String SPORT_PARAM = "sport";

	private static final String START_POINT_PARAM = "startpoint";
	private static final String RADIUS_PARAM = "radius";

	// Named queries defined on the Tour entity.
	private static final String USER_QUERY = "findTourByUsername";
	private static final String SPORT_QUERY = "findTourBySport";
	private static final String RADIUS_QUERY = "findTourByRadius";
	private static final String RADIUS_SPORT_QUERY = "findTourByRadiusAndSport";

	/**
	 * Creates a new tour database entry.
	 * 
	 * @return The database ID of the new tour entry.
	 * @throws IllegalArgumentException
	 *             if the user or the sport declared in the given DTO were not
	 *             found.
	 */
	public static Promise<Integer> create(final TourDto tourDto) {
		return Promise.promise(new Function0<Integer>() {
			@Override
			public Integer apply() throws Throwable {
				Tour tour = toEntity(tourDto);
				JPA.em().persist(tour);
				return tour.getId();
			}
		});

	}

	/**
	 * @return A single {@link TourDto} with the provided ID or {@code null} if
	 *         not found.
	 */
	public static Promise<TourDto> findById(final int id) {
		return Promise.promise(new Function0<TourDto>(){
			@Override
			public TourDto apply() throws Throwable {
				Tour tour = JPA.em().find(Tour.class, id);
				TourDto tourDto;
				if (tour != null) {
					tourDto = toDto(tour);
				} else {
					tourDto = null;
				}
				return tourDto;
			}
		});

	}

	/**
	 * @return A list of tours belonging to the user with the specified
	 *         {@code username}. If none are found, an empty list is returned.
	 */
	public static Promise<List<TourDto>> findByUsername(final String username) {
		
		
		return Promise.promise(new Function0<List<TourDto>>(){

			@Override
			public List<TourDto> apply() throws Throwable {
				LOG.debug("Fetching tours for username: " + username);
				Query q = JPA.em().createNamedQuery(USER_QUERY);
				q.setParameter(USER_PARAM, username);

				long start = System.currentTimeMillis();
				@SuppressWarnings("unchecked")
				List<Tour> tours = q.getResultList();
				long duration = System.currentTimeMillis() - start;
				LOG.debug("Fetched " + tours.size() + " tours in " + duration + " ms.");

				List<TourDto> tourDtos = toDtos(tours);
				return tourDtos;
			}
			
		});
		
	}

	/**
	 * @return A list of tours created for the specified {@code sport}. If none
	 *         are found, an empty list is returned.
	 */
	public static Promise<List<TourDto>> findBySportName(final String sport) {
		
		return Promise.promise(new Function0<List<TourDto>>() {

			@Override
			public List<TourDto> apply() throws Throwable {
				LOG.debug("Fetching tours for sport: " + sport);
				Query q = JPA.em().createNamedQuery(SPORT_QUERY);
				q.setParameter(SPORT_PARAM, sport);

				long start = System.currentTimeMillis();
				@SuppressWarnings("unchecked")
				List<Tour> tours = q.getResultList();
				long duration = System.currentTimeMillis() - start;
				LOG.debug("Fetched " + tours.size() + " tours in " + duration + " ms.");

				List<TourDto> tourDtos = toDtos(tours);
				return tourDtos;
			}
		});
		
	}

	/**
	 * Retrieves tour with start points within {@code radius} metres from (
	 * {@code lat, lon, alt}).
	 * 
	 * @param lat
	 *            The WGS84 latitude in degrees.
	 * @param lon
	 *            The WGS84 longitude in degrees.
	 * @param alt
	 *            An optional altitude above sea level in metres.
	 * @param radius
	 *            The radius in metres.
	 * @param sport
	 *            The optional additional sport filter.
	 * 
	 * @return A list of tours satisfying the provided criteria. If none are
	 *         found, an empty list is returned.
	 */
	public static Promise<List<TourDto>> findByStartPoint(final double lat, final double lon,
			final Optional<Double> alt, final double radius, final Optional<String> sport) {
		
		return Promise.promise(new Function0<List<TourDto>>() {

			@Override
			public List<TourDto> apply() throws Throwable {
				LOG.debug("Fetching tours for criteria: lat=" + lat + " lon=" + lon
						+ " alt=" + alt.or(0d) + " radius=" + radius + " "
						+ sport.or(""));

				Point startpoint = createPoint(lon, lat, alt.or(0d));

				Query q;
				if (sport.isPresent()) {
					q = JPA.em().createNamedQuery(RADIUS_SPORT_QUERY);
					q.setParameter(SPORT_PARAM, sport.get());
				} else {
					q = JPA.em().createNamedQuery(RADIUS_QUERY);
				}
				q.setParameter(START_POINT_PARAM, startpoint);
				q.setParameter(RADIUS_PARAM, radius);

				long start = System.currentTimeMillis();
				@SuppressWarnings("unchecked")
				List<Tour> results = q.getResultList();
				long duration = System.currentTimeMillis() - start;
				LOG.debug("Fetched " + results.size() + " tours in " + duration
						+ " ms.");

				List<TourDto> tourDtos = toDtos(results);
				return tourDtos;
			}
		});
		
		
	}

	/**
	 * Converts the given DTO to an entity, fetching username and sport.
	 * 
	 * @throws IllegalArgumentException
	 *             if the user or the sport declared in the given DTO were not
	 *             found.
	 */
	static Tour toEntity(TourDto tourDto) {
		Tour tour = new Tour();
		User user = User.findByUsername(tourDto.username);
		checkArgument(user != null, "User %s not found", tourDto.username);
		Sport sport = Sport.findByName(tourDto.sportName);
		checkArgument(sport != null, "Sport %s not found", tourDto.sportName);

		List<TimestampedPoint> tsPoints = Lists
				.newArrayListWithExpectedSize(tourDto.points.size());
		for (TimestampedPointDto pointDto : tourDto.points) {
			Point point = createPoint(pointDto.x, pointDto.y, pointDto.z);
			Date time = pointDto.time;
			TimestampedPoint tsPoint = new TimestampedPoint(time, point, tour);
			tsPoints.add(tsPoint);
		}

		tour.setPoints(tsPoints);
		tour.setSport(sport);
		tour.setUser(user);

		// Get the first timestamped point ordered by timestamp.
		TimestampedPoint startPoint = Collections.min(tour.getPoints());
		tour.setStartPoint(startPoint);

		return tour;
	}

	static TourDto toDto(Tour tour) {
		String sportName = tour.getSport().getName();
		String username = tour.getUser().getUsername();

		List<TimestampedPointDto> points = Lists
				.newArrayListWithExpectedSize(tour.getPoints().size());
		for (TimestampedPoint tsPoint : tour.getPoints()) {
			double x = tsPoint.getPoint().getCoordinate().x;
			double y = tsPoint.getPoint().getCoordinate().y;
			double z = tsPoint.getPoint().getCoordinate().z;

			Date time = tsPoint.getTime();
			TimestampedPointDto tsPointDto = new TimestampedPointDto(time, x,
					y, z);
			points.add(tsPointDto);
		}

		TourDto tourDto = new TourDto(username, sportName, points);
		return tourDto;
	}

	static List<TourDto> toDtos(Collection<Tour> tours) {
		List<TourDto> dtos = Lists.newArrayListWithExpectedSize(tours.size());
		for (Tour tour : tours) {
			TourDto dto = toDto(tour);
			dtos.add(dto);
		}
		return dtos;
	}

	static Point createPoint(double x, double y, double z) {
		Point point = GEO_FACTORY.createPoint(new Coordinate(x, y, z));
		point.setSRID(SRID);
		return point;
	}

}
