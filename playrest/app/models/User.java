package models;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@NamedQuery(
	name = "findUserByUsername",
	query = "FROM User u WHERE lower(u.username) = lower(:user)"
)
@Entity
// This is used because "user" is a keyword, so the generated queries are
// incorrect without declaring schema.
@Table(schema = "public", name = "user")
public class User {
	private static final String USER_PARAM = "user";
	private static final String USER_QUERY = "findUserByUsername";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usr_id")
	private Integer id;
	
	@Required
	@Column(name = "usr_username")
	private String username;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Collection<Tour> tours;
	
	public static User findByUsername(String username) {
		Query q = JPA.em().createNamedQuery(USER_QUERY);
		q.setParameter(USER_PARAM, username);
		
		@SuppressWarnings("unchecked")
		List<User> results = q.getResultList();
		
		User result;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return "User [id=" + id + ", username=" + username + ", tours=" + tours
				+ "]";
	}
}
