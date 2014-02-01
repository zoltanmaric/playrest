package utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import models.dtos.TimestampedPointDto;
import models.dtos.TourDto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class JsonHelperTest {
	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("CET");
	
	private static final String INPUT_JSON = "{ " +
		   "\"creator\": \"zoltan\", " +
		   "\"sport\": \"hike\", " +
		   "\"geometry\": [ " +
		        "{ " +
		            "\"time\": \"2009-07-10 14:56:10 +0200\", " +
		            "\"x\": 10.275514, " +
		            "\"y\": 47.514749, " +
		            "\"z\": 756.587 " +
		        "}, " +
		        "{ " +
		            "\"time\": \"2009-07-10 14:56:19 +0200\", " + 
		            "\"x\": 10.275563, " +
		            "\"y\": 47.514797, " +
		            "\"z\": 757.417 " +
		        "} " +
		    "] " +
		"}";
	
	private TourDto tourDto;
	private JsonNode json;
	
	@Before
	public void before()
			throws ParseException, JsonProcessingException, IOException {
		tourDto = createTourDto();
		json = JsonHelper.MAPPER.setTimeZone(TIME_ZONE).readTree(INPUT_JSON);
	}
	
	@Test
	public void testJsonToTour() throws JsonProcessingException, IOException {
		TourDto actual = JsonHelper.jsonToTour(json);		
		TourDto expected = tourDto;		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testTourToJson() {
		JsonNode expected = json;
		JsonNode actual = JsonHelper.tourToJson(tourDto, TIME_ZONE);
		Assert.assertEquals(expected, actual);
	}
	
	private static TourDto createTourDto() throws ParseException {
		List<TimestampedPointDto> points = Lists.newArrayListWithCapacity(2);
		
		Date time = DF.parse("2009-07-10 14:56:10 +0200");
		points.add(new TimestampedPointDto(time, 10.275514, 47.514749, 756.587));
		
		time = DF.parse("2009-07-10 14:56:19 +0200");
		points.add(new TimestampedPointDto(time, 10.275563, 47.514797, 757.417));
		
		TourDto tourDto = new TourDto("zoltan", "hike", points);
		return tourDto;
	}

}
