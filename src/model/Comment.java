package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the comment database table.
 * 
 */
@Entity
@NamedQuery(name="Comment.findAll", query="SELECT c FROM Comment c")
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idComment;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private int idFriend;

	private String text;

	//bi-directional many-to-one association to NewsArticle
	@ManyToOne
	@JoinColumn(name="idNews")
	private NewsArticle newsArticle;

	//bi-directional many-to-one association to Friend
	@OneToMany(mappedBy="comment")
	private List<Friend> friends;

	public Comment() {
	}

	public int getIdComment() {
		return this.idComment;
	}

	public void setIdComment(int idComment) {
		this.idComment = idComment;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getIdFriend() {
		return this.idFriend;
	}

	public void setIdFriend(int idFriend) {
		this.idFriend = idFriend;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public NewsArticle getNewsArticle() {
		return this.newsArticle;
	}

	public void setNewsArticle(NewsArticle newsArticle) {
		this.newsArticle = newsArticle;
	}

	public List<Friend> getFriends() {
		return this.friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}

	public Friend addFriend(Friend friend) {
		getFriends().add(friend);
		friend.setComment(this);

		return friend;
	}

	public Friend removeFriend(Friend friend) {
		getFriends().remove(friend);
		friend.setComment(null);

		return friend;
	}

}