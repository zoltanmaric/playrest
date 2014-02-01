package utils;

import java.util.List;
import java.util.TimeZone;

import models.dtos.TourDto;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides static methods for conversion between DTOs and their JSON representations.
 */
public class JsonHelper {	
	static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static TourDto jsonToTour(JsonNode json) throws JsonProcessingException {
		TourDto tourDto;
		if (json.isArray()) {
			if (json.elements().hasNext() == false) {
				throw new JsonParseException("Empty JSON array", null);
			}
			tourDto = MAPPER.treeToValue(json.get(0), TourDto.class);
		}
		else {
			tourDto = MAPPER.treeToValue(json, TourDto.class);
		}
		return tourDto;
	}
	
	public static JsonNode tourToJson(TourDto tourDto, TimeZone tz) {
		JsonNode json = MAPPER.setTimeZone(tz).valueToTree(tourDto);
		return json;
	}
	
	public static JsonNode toursToJson(List<TourDto> tourDtos, TimeZone tz) {
		JsonNode json = MAPPER.setTimeZone(tz).valueToTree(tourDtos);
		return json;
	}

}
