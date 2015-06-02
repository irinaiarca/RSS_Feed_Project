package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the friends database table.
 * 
 */
@Entity
@Table(name="friends")
@NamedQuery(name="Friend.findAll", query="SELECT f FROM Friend f")
public class Friend implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idFriends;

	//bi-directional many-to-one association to Comment
	@ManyToOne
	@JoinColumn(name="idUsersFriend")
	private Comment comment;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="id_current_User")
	private User user;

	public Friend() {
	}

	public int getIdFriends() {
		return this.idFriends;
	}

	public void setIdFriends(int idFriends) {
		this.idFriends = idFriends;
	}

	public Comment getComment() {
		return this.comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}