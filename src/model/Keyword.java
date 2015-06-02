package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the keywords database table.
 * 
 */
@Entity
@Table(name="keywords")
@NamedQuery(name="Keyword.findAll", query="SELECT k FROM Keyword k")
public class Keyword implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idKeyWords;

	private String name;

	//bi-directional many-to-many association to Category
	@ManyToMany
	@JoinTable(
		name="keyword_category"
		, joinColumns={
			@JoinColumn(name="idKeyWords")
			}
		, inverseJoinColumns={
			@JoinColumn(name="idCategory")
			}
		)
	private List<Category> categories;

	public Keyword() {
	}

	public int getIdKeyWords() {
		return this.idKeyWords;
	}

	public void setIdKeyWords(int idKeyWords) {
		this.idKeyWords = idKeyWords;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Category> getCategories() {
		return this.categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

}