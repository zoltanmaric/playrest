package models;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@NamedQueries({
	@NamedQuery(
		name = "findSportByName",
		query = "FROM Sport s WHERE lower(s.name) = lower(:sport)"
	)
})
@Entity
public class Sport {
	private static final String SPORT_PARAM = "sport";
	private static final String SPORT_QUERY = "findSportByName";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "spt_id")
	private Integer id;
	
	@Required
	@Column(name = "spt_name")
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sport")
	private Collection<Tour> tours;
	
	/**
	 * Returns a Sport entity searched by its name.
	 * @param name
	 * @return The queried Sport entity or {@code null} if not found.
	 */
	public static Sport findByName(String name) {
		Query q = JPA.em().createNamedQuery(SPORT_QUERY);
		q.setParameter(SPORT_PARAM, name);
		
		@SuppressWarnings("unchecked")
		List<Sport> results = q.getResultList();
		
		Sport result;
		if (results.isEmpty() == false) {
			result = results.get(0);
		}
		else {
			result = null;
		}
		
		return result;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Tour> getTours() {
		return tours;
	}

	public void setTours(Collection<Tour> tours) {
		this.tours = tours;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Sport other = (Sport) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Sport [id=" + id + ", name=" + name + ", tours=" + tours + "]";
	}
}
