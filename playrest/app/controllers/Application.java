package controllers;

import static com.google.common.collect.Sets.newHashSet;
import static play.libs.Json.toJson;
import static utils.JsonHelper.jsonToTour;
import static utils.JsonHelper.tourToJson;
import static utils.JsonHelper.toursToJson;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import models.dataaccess.TourDataAccessUtils;
import models.dtos.TourDto;
import play.Logger.ALogger;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * Handles all HTTP requests for the application, performs DTO-JSON conversion
 * and delegates work to the data layer.
 */
public class Application extends Controller {
	private static final ALogger LOG = play.Logger.of("application");
	
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

	private static final String Q_USER = "username";
	private static final String Q_SPORT = "sport";
	private static final String Q_START_LAT = "startlat";
	private static final String Q_START_LON = "startlon";
	private static final String Q_START_ALT = "startalt";
	private static final String Q_RADIUS = "radius";
	
	/**
	 * Creates a new tour based on a JSON received in the body of a POST
	 * request.<br>
	 * The maximum size of the received content is 1 megabyte.
	 * 
	 * @return <ul>
	 * <li><b>{@code 201 (CREATED)}</b> if the tour was successfully created.
	 * The body of the request will contain the ID of the created tour which can
	 * later be used to fetch the particular tour.</li>
	 * <li><b>{@code 400 (BAD REQUEST)}</b> if there was an error interpreting
	 * the data or if the content exceeds the maximum length (1 M).</li>
	 * </ul>
	 */
	@Transactional
	@BodyParser.Of(value = Json.class, maxLength = 1024 * 1024)
	public static Result createTour() {
		if (request().body().isMaxSizeExceeded()) {
			return badRequest(toJson("Request body too large. (> 1 M)"));
		}
		
		JsonNode node = request().body().asJson();
		TourDto tourDto;
		try {
			tourDto = jsonToTour(node);
			int id = TourDataAccessUtils.create(tourDto);
			return created(toJson(id));
		}
		catch (JsonProcessingException e) {
			LOG.info("Error parsing JSON request.", e);
			String message = Strings.nullToEmpty(
					Throwables.getRootCause(e).getMessage());
			return badRequest(toJson(message));
		}
		catch (IllegalArgumentException e) {
			LOG.info("Error creating tour.", e);
			String message = Strings.nullToEmpty(
					Throwables.getRootCause(e).getMessage());
			return badRequest(toJson(message));
		}
		catch (RuntimeException e) {
			LOG.error("While creating tour.", e);
			String message = Strings.nullToEmpty(
					Throwables.getRootCause(e).getMessage());
			return internalServerError(toJson(message));
		}
	}
	
	/**
	 * Fetches a single tour by its ID.
	 * @param id The ID of the tour to fetch.
	 * @return <ul>
	 * <li><b>{@code200 (OK)}</b> if the tour was found. The body of the
	 * response will contain a JSON representation of the selected tour.</li>
	 * <li><b>{@code404 (NOT FOUND)}</b> if a tour with the provided ID was not
	 * found.
	 * </li>
	 * </ul>
	 */
	@Transactional(readOnly = true)
	public static Result getTour(int id) {
		TourDto tourDto = TourDataAccessUtils.findById(id);
		LOG.debug("tour: " + tourDto);
		Result result;
		if (tourDto != null) {
			JsonNode json = tourToJson(tourDto, TIME_ZONE);
			result = ok(json);
		}
		else {
			result = notFound();
		}
		
		return result;
	}
	
	/**
	 * Retrieves a JSON array of tours queried by a set of criteria returned
	 * from the URL query parameters. <br>
	 * The following query parameters are interpreted:
	 * <ul>
	 * <li><b>{@code username}</b> (string): The username of the queried user.
	 * </li>
	 * <li><b>{@code sport}</b> (string): The name of the queried sport.</li>
	 * <li><b>{@code startlat}</b> (double):
	 * The start point WGS84 latitude in degrees.</li>
	 * <li><b>{@code startlon}</b> (double):
	 * The start point WGS84 longitude in degrees.</li>
	 * <li><b>{@code startalt}</b> (double): The start point altitude above sea
	 * level in metres. (optional)</li>
	 * <li><b>{@code radius}</b> (double): The radius in metres with reference
	 * to the start point coordinates.</li>
	 * </ul>
	 * 
	 * @return <ul>
	 * <li><b>{@code 200 (OK)}</b> with a JSON array of tours in the body. If no
	 * tours match the given criteria, an empty JSON array is returned
	 * ({@code []}).</li>
	 * <li><b>{@code 400 (BAD REQUEST)}</b> if the query parameters didn't
	 * contain all the mandatory parameters defined in any of the three criteria
	 * sets described below.
	 * </li>
	 * </ul>
	 * Valid criteria sets:
	 * <ul>
	 * <li><b>{@code startlat, startlon, [startalt], radius, [sport]}: </b>
	 * Retrieves an array of tours with their starting point within
	 * {@code radius} metres from ({@code startlat, startlon, startalt}).
	 * If {@code sport} is provided, the tours are additionally filtered by
	 * the given sport name.</li>
	 * <li><b>{@code username}: </b> Retrieves an array of tours created by the
	 * user with the given username.</li>
	 * <li><b>{@code sport}: </b> Retrieves an array of tours created for the
	 * given sport.</li>
	 * </ul>
	 */
	@Transactional(readOnly = true)
	public static Result getToursByCriteria() {
		Map<String, String[]> qMap = request().queryString();
		Result result = handleCriteria(qMap);
		return result;
	}
	
	static Result handleCriteria(Map<String, String[]> qMap) {
		Result result;
		Collection<String> keys = qMap.keySet();
		if (keys.containsAll(newHashSet(Q_START_LAT, Q_START_LON, Q_RADIUS))) {
			double lat = Double.parseDouble(qMap.get(Q_START_LAT)[0]);
			double lon = Double.parseDouble(qMap.get(Q_START_LON)[0]);
			double radius = Double.parseDouble(qMap.get(Q_RADIUS)[0]);
			
			String[] altParam = qMap.get(Q_START_ALT);
			Optional<Double> alt;
			if (altParam != null) {
				alt = Optional.of(Double.parseDouble(altParam[0]));
			}
			else {
				alt = Optional.absent();
			}
			
			String[] sportParam = qMap.get(Q_SPORT);
			Optional<String> sport;
			if (sportParam != null) {
				sport = Optional.of(sportParam[0]);
			}
			else {
				sport = Optional.absent();
			}
			
			
			result = getToursByStartPoint(lat, lon, alt, radius, sport);
		}
		else if (keys.contains(Q_USER)){
			String username = qMap.get(Q_USER)[0];
			result = getToursByUsername(username);
		}
		else if (keys.contains(Q_SPORT)) {
			String sport = qMap.get(Q_SPORT)[0];
			result = getToursBySport(sport);
		}
		else {
			result = badRequest(
					toJson("Illegal set of query parameters received."));
		}
		
		return result;
	}
	
	static Result getToursByUsername(String username) {
		List<TourDto> tours = TourDataAccessUtils.findByUsername(username);
		JsonNode json = toursToJson(tours, TIME_ZONE);
		Result result = ok(json);
		return result;
	}
	
	static Result getToursBySport(String sport) {
		List<TourDto> tours = TourDataAccessUtils.findBySportName(sport);
		JsonNode json = toursToJson(tours, TIME_ZONE);
		Result result = ok(json);
		return result;
	}
	
	static Result getToursByStartPoint(
			double lat,
			double lon,
			Optional<Double> alt,
			Double radius,
			Optional<String> sport
			) {
		List<TourDto> tours =
			TourDataAccessUtils.findByStartPoint(lat, lon, alt, radius, sport);
		JsonNode json = toursToJson(tours, TIME_ZONE);
		Result result = ok(json);
		return result;
	}

}
