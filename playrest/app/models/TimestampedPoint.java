package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name = "timestamped_point")
public class TimestampedPoint implements Comparable<TimestampedPoint> {
	public TimestampedPoint() {
		
	}
	
	public TimestampedPoint(Date time, Point point, Tour tour) {
		this.time = time;
		this.point = point;
		this.tour = tour;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tsp_id")
	private Integer id;
	
	@Required
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "tsp_time")
	private Date time;
	
	@Required
	@Type(type = "org.hibernate.spatial.GeometryType")
	@Column(name = "tsp_point")
	private Geometry point;
	
	@Required
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tsp_tur_id")
	private Tour tour;
	
	public void save() {
		JPA.em().persist(this);
	}

	/**
	 * Compares this object to another {@link TimestampedPoint} by their
	 * time fields.
	 * @throws NullPointerException if {@code o} is {@code null}.
	 */
	@Override
	public int compareTo(TimestampedPoint o) {
		int result = this.time.compareTo(o.getTime());
		return result;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Geometry getPoint() {
		return point;
	}

	public void setPoint(Geometry point) {
		this.point = point;
	}

	public Tour getTourId() {
		return tour;
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		TimestampedPoint other = (TimestampedPoint) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimestampedPoint [id=" + id + ", time=" + time + ", point="
				+ point + ", tour=" + tour + "]";
	}
}
