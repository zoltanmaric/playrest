package models.dtos;

import java.util.Date;

import models.TimestampedPoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A JSON-annotated DTO representation of the {@link TimestampedPoint} entity.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimestampedPointDto {
	
	public final Date time;
	public final double x;
	public final double y;
	public final double z;
	
	@JsonCreator
	public TimestampedPointDto(
			@JsonProperty("time")
			@JsonFormat(
				shape = JsonFormat.Shape.STRING,
				pattern="yyyy-MM-dd HH:mm:ss Z"
			)
			Date timestamp,
			@JsonProperty("x") double x,
			@JsonProperty("y") double y,
			@JsonProperty("z") double z
			) {
		this.time = timestamp;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((time == null) ? 0 : time.hashCode());
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		TimestampedPointDto other = (TimestampedPointDto) obj;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimestampedPointDto [timestamp=" + time +
				", x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
