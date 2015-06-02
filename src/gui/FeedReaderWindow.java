package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import model.Friend;
import model.NewsArticle;
import model.User;

public class FeedReaderWindow {

	private EntityManager em;
	private Runner runner;
	
	private Stage stage;
	private LoginWindow lw;
	
	public FeedReaderWindow(EntityManager em, Runner runner, LoginWindow lw) {
		this.em = em; this.runner = runner; this.lw = lw;
		
		setupStage();
		stage.show();
	}
	
	@SuppressWarnings("unchecked")
	void setupStage() {
		if (stage != null) return;
		
		stage = new Stage();
		

		stage.setHeight(700);
		stage.setWidth(1200);
		
		stage.setTitle("Feed reader");
		
		GridPane gridMain = new GridPane();
        gridMain.setAlignment(Pos.CENTER);
        gridMain.setHgap(10);
        gridMain.setVgap(10);
        gridMain.setPadding(new Insets(10, 10, 10, 10));
        
        Scene sc = new Scene(gridMain, 700, 1200);
        stage.setScene(sc);
        
        Label l = new Label("Welcome, " + runner.loggedUser + "!");
        gridMain.add(l, 0, 1);
        l = new Label("Here are your news!");
        gridMain.add(l, 1, 1);
        
        ListView<String> list = new ListView<>();
        
        Query getFeed = em.createQuery("SELECT item FROM NewsArticle item WHERE item.user.idUser = \'" + runner.currentId + "\'");
    	
    	List<NewsArticle> articleList;
    	List<String> aux = new ArrayList<String>();
    	ObservableList<String> items = null;
    	
    	try {
    		articleList = getFeed.getResultList();
    		for(NewsArticle f : articleList)
    		{
    			aux.add(f.getDescription());
    		}
    		items = FXCollections.observableArrayList(aux);
    	} catch (NoResultException ex) {
    		(new AlertWindow()).message("Could not grab news!").show();
    	}
    	
    	if (items != null) 
    		list.setItems(items);
  
        list.setPrefWidth(200);
        list.setPrefHeight(400);
                          
        TextArea txt = new TextArea("\n\n\t\t\t\tClick on an item in the list on the left side to read news.");
        txt.setPrefWidth(600);
        txt.setEditable(false);
        txt.setWrapText(true);
        gridMain.add(txt, 1, 2);

        TextArea commentSection = new TextArea("Comment placeholder");
        commentSection.setEditable(false);
        commentSection.setWrapText(true);
        commentSection.setPrefHeight(100);
        gridMain.add(commentSection, 1, 3);
        
        TextArea comment = new TextArea("Comment placeholder");
        comment.setEditable(true);
        comment.setWrapText(true);
        comment.setPrefHeight(30);
        gridMain.add(comment, 1, 4);
        
        Button btn5 = new Button ("Post comment");
        HBox hBtn5 = new HBox(10);
        hBtn5.setAlignment(Pos.BOTTOM_LEFT);
        hBtn5.getChildren().add(btn5);
        gridMain.add(hBtn5, 1, 5);
        
        gridMain.add(list, 0, 2);
        
        Button btn = new Button("Look at News from friends");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(btn);
        gridMain.add(hbBtn, 2, 2);
        
        ListView<String > friendList = new ListView<>();
        ObservableList<String> obsFriendList = FXCollections.observableArrayList();
        List<Friend> friendQueryResult = new ArrayList<Friend>();
                           
        Query friends = em.createQuery("SELECT f FROM Friend f WHERE f.user.idUser =" + runner.currentId);
        
        friendQueryResult = friends.getResultList();
        for(Friend f : friendQueryResult) {
        	Query test = em.createQuery("SELECT u FROM User u WHERE u.idUser =" + f.getIdFriends());
        	List<User> u = test.getResultList();
        	for(User x : u) {
        		obsFriendList.add(x.getUsername());
        	}
        	
        }

        friendList.setItems(obsFriendList);
        friendList.setPrefWidth(150);
        friendList.setPrefHeight(300);
        gridMain.add(friendList, 3, 2);
        
        Button btn2 = new Button("Add friends");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.CENTER);
        hbBtn2.getChildren().add(btn2);
        gridMain.add(hbBtn2, 2, 3);
        
        Button btn3 = new Button("Add resource");
        HBox hbBtn3 = new HBox(10);
        hbBtn3.setAlignment(Pos.CENTER);
        hbBtn3.getChildren().add(btn3);
        gridMain.add(hbBtn3, 2, 4);
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent arg0) {
				lw.reset();		
			}
		});
		
	}

}