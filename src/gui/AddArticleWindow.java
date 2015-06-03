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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
	
	private void hookEvents() {
		runner.mediator.subscribe("addsourceswindow.open", new util.PubSubHandler() {
			
			@Override
			public void exec(Object... args) {
				stage.show();
			}
		});
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
        
        Label help = new Label("Choose an URL from the list below \n and then click OK:");
        gridRSS.add(help, 0, 1);

        myURL = new ComboBox();
        myURL.setPrefWidth(400);
        myURL.setItems(fillComboBox());
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
        
        AddArticleWindow self = this;

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
    		feedHolder.addAll(addFeed(myURL.getSelectionModel().selectedItemProperty().toString()));
    		ObservableList<String> feedToChooseFrom = FXCollections.observableArrayList();
    		for(FeedMessage m : feedHolder)
    		{
    			feedToChooseFrom.add(m.getTitle());
    		}
    		
    		list.setItems(feedToChooseFrom);
	}
	
	@SuppressWarnings("unchecked")
	private ObservableList<String> fillComboBox()
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

	//	em.persist(runner.loggedUser);
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
