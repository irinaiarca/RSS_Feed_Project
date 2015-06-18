package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the category database table.
 * 
 */
@Entity
@NamedQuery(name="Category.findAll", query="SELECT c FROM Category c")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idCategory;

	private String description;

	private String name;

	//bi-directional many-to-many association to Keyword
	@ManyToMany(mappedBy="categories")
	private List<Keyword> keywords;

	//bi-directional many-to-many association to NewsArticle
	@ManyToMany(mappedBy="categories")
	private List<NewsArticle> newsArticles;

	public Category() {
	}

	public int getIdCategory() {
		return this.idCategory;
	}

	public void setIdCategory(int idCategory) {
		this.idCategory = idCategory;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Keyword> getKeywords() {
		return this.keywords;
	}

	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}

	public List<NewsArticle> getNewsArticles() {
		return this.newsArticles;
	}

	public void setNewsArticles(List<NewsArticle> newsArticles) {
		this.newsArticles = newsArticles;
	}

}