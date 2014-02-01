package models.dtos;

import java.util.List;

import models.Tour;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * A JSON-annotated DTO representation of the {@link Tour} entity.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourDto {
	@JsonProperty("creator")
	public final String username;
	@JsonProperty("sport")
	public final String sportName;
	@JsonProperty("geometry")
	public final List<TimestampedPointDto> points;
	
	@JsonCreator
	public TourDto(
			@JsonProperty("creator") String username,
			@JsonProperty("sport") String sportName,
			@JsonProperty("geometry") List<TimestampedPointDto> points
			) {
		this.username = username;
		this.sportName = sportName;
		this.points = ImmutableList.<TimestampedPointDto>builder()
				.addAll(points).build();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime * result
				+ ((sportName == null) ? 0 : sportName.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TourDto other = (TourDto) obj;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		if (sportName == null) {
			if (other.sportName != null)
				return false;
		} else if (!sportName.equals(other.sportName))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TourDto [username=" + username + ", sportName=" + sportName
				+ ", points=" + points + "]";
	}
}
