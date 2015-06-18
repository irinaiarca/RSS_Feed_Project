package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the news_articles database table.
 * 
 */
@Entity
@Table(name="news_articles")
@NamedQuery(name="NewsArticle.findAll", query="SELECT n FROM NewsArticle n")
public class NewsArticle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idNews;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private String description;

	private String source;
	private String sourceAddress;
	private String title;

	//bi-directional many-to-one association to Comment
	@OneToMany(mappedBy="newsArticle")
	private List<Comment> comments;

	//bi-directional many-to-many association to Category
	@ManyToMany
	@JoinTable(
		name="news_category"
		, joinColumns={
			@JoinColumn(name="idNews_nc")
			}
		, inverseJoinColumns={
			@JoinColumn(name="idCategory_nc")
			}
		)
	private List<Category> categories;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userID")
	private User user;

	public NewsArticle() {
	}

	public int getIdNews() {
		return this.idNews;
	}

	public void setIdNews(int idNews) {
		this.idNews = idNews;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceAddress() {
		return this.sourceAddress;
	}

	public void setSourceAddress(String source) {
		this.sourceAddress = source;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Comment> getComments() {
		return this.comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Comment addComment(Comment comment) {
		getComments().add(comment);
		comment.setNewsArticle(this);

		return comment;
	}

	public Comment removeComment(Comment comment) {
		getComments().remove(comment);
		comment.setNewsArticle(null);

		return comment;
	}

	public List<Category> getCategories() {
		return this.categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}