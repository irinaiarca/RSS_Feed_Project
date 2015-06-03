package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.persistence.EntityManager;

import model.NewsArticle;
import rss.model.Feed;
import rss.model.FeedMessage;
import rss.read.FeedParser;

public class AddSourceWindow {
	
	private EntityManager em;
	private Runner runner;
	
	private Stage stage;
    private List<FeedMessage> feedHolder = new ArrayList<FeedMessage>();
    private ListView<String> list;
    private TextField myURL;

	public AddSourceWindow(Runner runner) {
		this.runner = runner; em = runner.entityManager;
		
		setupStage();
        
        stage.show();
	}
	
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
        
        Label help = new Label("Type or paste in the text field below \n an URL thet leads to an RSS feed:");
        gridRSS.add(help, 0, 1);

        myURL = new TextField();
        gridRSS.add(myURL, 0, 2);
        
        list = new ListView<>();
        list.setPrefWidth(350);
        gridRSS.add(list, 0, 3);
        
        Button btn = new Button("Add");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(btn);
        gridRSS.add(hbBtn, 0, 4);

        Button btn2 = new Button("Add selected item to my news list");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.CENTER);
        hbBtn2.getChildren().add(btn2);
        gridRSS.add(hbBtn2, 1, 4);
        
        AddSourceWindow self = this;

        btn2.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle (ActionEvent e) {
        		self.add();
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
	
	private void add() {               		
    		feedHolder.addAll(addFeed(myURL.getText()));
    		ObservableList<String> feedToChooseFrom = FXCollections.observableArrayList();
    		for(FeedMessage m : feedHolder)
    		{
    			feedToChooseFrom.add(m.getTitle());
    		}
    		
    		list.setItems(feedToChooseFrom);
	}
	
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
		
		em.getTransaction().begin();
		
		NewsArticle newArticle  = new NewsArticle();
		newArticle.setUser(runner.loggedUser);
		newArticle.setDescription(selectedArticle.getDescription());
		newArticle.setSource(selectedArticle.getAuthor());
		System.out.println(newArticle.getIdNews());
		System.out.println(newArticle.getUser().getIdUser());

		em.persist(runner.loggedUser);
		em.persist(newArticle);

		em.getTransaction().commit();

       	ObservableList<String> holder = list.getItems();
	 	holder.add(newArticle.getDescription());
	 	list.setItems(holder);
	 	
	 	runner.mediator.publish("news.refresh");
	}
	
	public List<FeedMessage> addFeed(String feedURL)
	{
		
		FeedParser parser = new FeedParser(feedURL);
	    Feed feed = parser.readFeed();
	    System.out.println(feed);
	    List<FeedMessage> returnedList = new ArrayList<FeedMessage>();
	    for (FeedMessage message : feed.getMessages()) 
	    {
	      System.out.println(message);
	      returnedList.add(message);

	    }
	    
	    return returnedList;
	}

}
