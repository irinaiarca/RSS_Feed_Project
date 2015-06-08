package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sun.org.apache.xpath.internal.axes.SelfIteratorNoPredicate;

import model.NewsArticle;
import model.Resource;
import rss.model.Feed;
import rss.model.FeedMessage;
import rss.read.FeedParser;

public class AddArticleWindow {
	
	private EntityManager em;
	private Runner runner;
	
	private Stage stage;
    private List<FeedMessage> feedHolder = new ArrayList<FeedMessage>();
    private ListView<String> list;
    private ComboBox myURL;

	public AddArticleWindow() {
		runner = Runner.getInstance(); em = runner.entityManager;
		
		setupStage();
		hookEvents();
	}
	
	/**
	 * An auxiliary function that hooks certain methods to global events.
	 */
	private void hookEvents() {
		AddArticleWindow self = this;
		runner.mediator.subscribe(new String[]{"resources.add", "addsourceswindow.open"}, new util.PubSubHandler() {
			
			@Override
			public void exec(Object... args) {
				self.fillComboBox();
			}
		});
		runner.mediator.subscribe("addsourceswindow.open", new util.PubSubHandler() {
			
			@Override
			public void exec(Object... args) {
				stage.show();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Verifies if the stage already exists and, if it doesn't, it creates it. If it already exists, nothing happens.
	 */
	private void setupStage() {
		if (stage != null) return;
		
		stage = new Stage();
		
		stage.setHeight(500);
		stage.setWidth(500);
		
		stage.setTitle("Add a new RSS link");
		
		GridPane gridRSS = new GridPane();
        gridRSS.setAlignment(Pos.CENTER);
        gridRSS.setHgap(10);
        gridRSS.setVgap(10);
        gridRSS.setPadding(new Insets(10, 10, 10, 10));
        
        Scene sc = new Scene(gridRSS, 500, 500);
        stage.setScene(sc);
        
        Label help = new Label("Choose an URL from the list below \n and then click OK:");
        gridRSS.add(help, 0, 1);
        
        AddArticleWindow self = this;

        myURL = new ComboBox();
        myURL.setPrefWidth(400);
        
        myURL.valueProperty().addListener(new ChangeListener<String>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void changed(ObservableValue arg0, String arg1, String arg2) {
				self.getFeedsFromResource();
			}
		});
        
        gridRSS.add(myURL, 0, 2);
        
        list = new ListView<>();
        list.setPrefWidth(350);
        gridRSS.add(list, 0, 3);
        
        Button btn = new Button("Add selected item to my news list");
        Button btn2 = new Button("Refresh");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().addAll(btn, btn2);
        gridRSS.add(hbBtn, 0, 4);

        btn2.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle (ActionEvent e) {
        		self.getFeedsFromResource();
        	}
        	
        });
        btn.setOnAction(new EventHandler<ActionEvent>() {
        	
        	@Override
        	public void handle (ActionEvent e)
        	{                            		
        		self.addToNewsList();
        	}
        	
        });
        
	}
	
	/**
	 * Grabs the news feed from the selected resource (URL).
	 */
	private void getFeedsFromResource() {               		
    		feedHolder.addAll(addFeed(myURL.getSelectionModel().getSelectedItem().toString()));
    		ObservableList<String> feedToChooseFrom = FXCollections.observableArrayList();
    		for(FeedMessage m : feedHolder)
    		{
    			feedToChooseFrom.add(m.getTitle());
    		}
    		
    		list.setItems(feedToChooseFrom);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Retrieves the list of URLs that the user has saved.
	 * @return fill List that contains all URL strings.
	 */
	private ObservableList<String> getResources()
	{
		Query q = em.createQuery("SELECT r from Resource r WHERE r.user.idUser=" + runner.loggedUser.getIdUser());
		List<Resource> result = new ArrayList<Resource>();
		result.addAll(q.getResultList());
		List<String> links = new ArrayList<String>();
		for(Resource r : result)
		{
			links.add(r.getUrl());
		}
		ObservableList<String> fill = FXCollections.observableArrayList(links);
		return fill;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Populates the ComboBox with the retrieved URLs.
	 */
	private void fillComboBox() {
        myURL.setItems(getResources());
	}
	
	/**
	 * Saves the selected article to the database and adds it to the user's article list. 
	 */
	private void addToNewsList() {
		String selectedTitle = list.getSelectionModel().getSelectedItem();
		FeedMessage selectedArticle = new FeedMessage();
		for(FeedMessage m : feedHolder)
		{
			if (m.getTitle().equals(selectedTitle))
			{
				selectedArticle = m;
			}
		}
		
		NewsArticle newArticle  = new NewsArticle();
		newArticle.setUser(runner.loggedUser);
		
		newArticle.setDescription(selectedArticle.getDescription());
		newArticle.setSource(selectedArticle.getAuthor());
		
		System.out.println(newArticle.getUser().getIdUser());
		System.out.println(newArticle.getIdNews());

		em.getTransaction().begin();
		em.persist(newArticle);
		em.getTransaction().commit();
	 	
	 	runner.mediator.publish("news.refresh");
	}
	/**
	 * Parses the feed found in the URL
	 * @param feedURL string that contains the adress to the RSS feed.
	 * @return returnedList List of parsed articles.
	 */
	public List<FeedMessage> addFeed(String feedURL)
	{
		
		System.out.println(feedURL);
		FeedParser parser = new FeedParser(feedURL);
	    Feed feed = parser.readFeed();
	    System.out.println(feed);
	    List<FeedMessage> returnedList = new ArrayList<FeedMessage>();
	    for (FeedMessage message : feed.getMessages()) 
	    {
	      returnedList.add(message);

	    }
	    
	    return returnedList;
	}

}
