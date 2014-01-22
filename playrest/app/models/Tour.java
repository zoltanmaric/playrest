package models;

import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

@NamedNativeQueries({
	@NamedNativeQuery(
			name = "findTourByRadius",
			query = "SELECT tour.* FROM tour " +
				"JOIN timestamped_point ON tur_start_tsp_id = tsp_id " +
				"WHERE ST_Distance_Sphere(:startpoint, tsp_point) < :radius " +
				"ORDER BY tur_id",
			resultClass = Tour.class
	),
	@NamedNativeQuery(
			name = "findTourByRadiusAndSport",
			query = "SELECT tour.* FROM tour " +
				"JOIN timestamped_point ON tur_start_tsp_id = tsp_id " +
				"JOIN sport ON tur_spt_id = spt_id " +
				"WHERE ST_Distance_Sphere(:startpoint, tsp_point) < :radius " +
				"AND spt_name = :sport " +
				"ORDER BY tur_id",
			resultClass = Tour.class
	)
})
@NamedQueries({
	@NamedQuery(
		name = "findTourByUsername",
		query = "FROM Tour t WHERE t.user.username = :user"
	),
	@NamedQuery(
		name = "findTourBySport",
		query = "FROM Tour t WHERE t.sport.name = :sport"
	)
})
@Entity
public class Tour {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tur_id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tur_usr_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tur_spt_id", nullable = false)
	private Sport sport;
	
	@OneToOne(
			fetch = FetchType.LAZY
	)
	@JoinColumn(
			name = "tur_start_tsp_id",
			nullable = true
	)
	private TimestampedPoint startPoint;
	
	@OneToMany(
		fetch = FetchType.LAZY,
		mappedBy = "tour",
		cascade = CascadeType.ALL
	)
	@OrderBy("tsp_time")
	private List<TimestampedPoint> points;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Sport getSport() {
		return sport;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}

	public Collection<TimestampedPoint> getPoints() {
		return points;
	}

	public void setPoints(List<TimestampedPoint> points) {
		this.points = points;
	}

	public TimestampedPoint getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(TimestampedPoint startPoint) {
		this.startPoint = startPoint;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((sport == null) ? 0 : sport.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/**
	 * Does not include timestamped points or the first point to prevent
	 * stack overflow due to a cyclic dependency.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tour other = (Tour) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (sport == null) {
			if (other.sport != null)
				return false;
		} else if (!sport.equals(other.sport))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tour [id=" + id + ", user=" + user + ", sport=" + sport + "]";
	}
}
