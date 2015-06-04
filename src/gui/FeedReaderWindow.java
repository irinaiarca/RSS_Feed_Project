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
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import util.PubSubHandler;
import model.Friend;
import model.NewsArticle;
import model.User;

public class FeedReaderWindow {

	private EntityManager em;
	private Runner runner;
	
	private Stage stage;
	
	private ListView<String> list;
	private ListView<String> friendList;
	
	private AddArticleWindow addSourceWindow;
	private AddFriendsWindow addFriendsWindow;
	private AddResourceWindow addResourceWindow;
	
	public FeedReaderWindow() {
		this.runner = Runner.getInstance(); em = runner.entityManager;
		
		setupStage();
		hookEvents();
		
		refreshNews();
		refreshFriends();
	}
	
	private void hookEvents() {
		FeedReaderWindow self = this;
		runner.mediator.subscribe("news.refresh", new PubSubHandler() {		
			@Override
			public void exec(Object... args) {
				if (args.length == 1 && args[0] instanceof Integer) self.refreshNews((Integer) args[0]);
				else self.refreshNews();
			}
		});
		runner.mediator.subscribe("friends.refresh", new PubSubHandler() {		
			@Override
			public void exec(Object... args) {
				self.refreshFriends();
			}
		});
		runner.mediator.subscribe("user.login", new PubSubHandler() {
			
			@Override
			public void exec(Object... args) {
				stage.show();
			}
		});
	}
	
	private void setupStage() {
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
        
        Label l = new Label("Welcome, " + runner.loggedUser.getUsername() + "!");
        gridMain.add(l, 0, 1);
        l = new Label("Here are your news!");
        gridMain.add(l, 1, 1);
        
        list = new ListView<>();
  
        list.setPrefWidth(200);
        list.setPrefHeight(400);
                          
        TextArea txt = new TextArea("\n\n\t\t\t\tClick on an item in the list on the left side to read news.");
        txt.setPrefWidth(600);
        txt.setEditable(false);
        txt.setWrapText(true);
        gridMain.add(txt, 1, 2);
        
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	
        	@Override
        	public void handle (MouseEvent event)
        	{
        		txt.setText(list.getSelectionModel().getSelectedItem().toString());
        	}
        });

        TextArea commentSection = new TextArea("Comment section placeholder");
        commentSection.setEditable(false);
        commentSection.setWrapText(true);
        commentSection.setPrefHeight(100);
        gridMain.add(commentSection, 1, 3);
        
        TextArea comment = new TextArea("Comment box placeholder");
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

        VBox hbBtn = new VBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        Button btn = new Button("Look at news from selected friend");
        Button btnmynews = new Button("Look at my news");
        hbBtn.getChildren().addAll(btn, btnmynews);
        gridMain.add(hbBtn, 2, 2);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				String user = friendList.getSelectionModel().getSelectedItem();

				Query q = em.createQuery("SELECT u FROM User u WHERE u.username =\'" + user + "\'");
				
				User u;
				try {
					u = (User) q.getSingleResult();
				} catch (Exception e) {
					u = null;
				}
				
				if (u != null) {
					runner.mediator.publish("news.refresh", u.getIdUser());
				} else runner.mediator.publish("news.refresh");
					
			}
		});
        
        btnmynews.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				runner.mediator.publish("news.refresh");	
			}
		});
        
        friendList = new ListView<>();
        
        friendList.setPrefWidth(150);
        friendList.setPrefHeight(300);
        gridMain.add(friendList, 2, 2);
        
        Button btn4 = new Button("Add RSS resources");
        HBox hbBtn4 = new HBox(10);
        hbBtn4.setAlignment(Pos.CENTER);
        hbBtn4.getChildren().add(btn4);
        gridMain.add(hbBtn4, 2, 5);
        
        btn4.setOnAction(new EventHandler<ActionEvent>() {
        	
        	@Override
        	public void handle(ActionEvent arg0) {
        		if (addResourceWindow == null) addResourceWindow = new AddResourceWindow();
        		runner.mediator.publish("addresourcewindow.open");
        	}
        });
        
        Button btn2 = new Button("Add friends");
        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.CENTER);
        hbBtn2.getChildren().add(btn2);
        gridMain.add(hbBtn2, 2, 4);
        
        btn2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (addFriendsWindow == null) addFriendsWindow = new AddFriendsWindow();
				runner.mediator.publish("addfriendswindow.open");
			}
		});
        
        Button btn3 = new Button("Add news from RSS resource");
        HBox hbBtn3 = new HBox(10);
        hbBtn3.setAlignment(Pos.CENTER);
        hbBtn3.getChildren().add(btn3);
        gridMain.add(hbBtn3, 2, 6);
        
        btn3.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (addSourceWindow == null) addSourceWindow = new AddArticleWindow();
				runner.mediator.publish("addsourceswindow.open");
			}
		});
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				runner.mediator.publish("mainwindow.close");	
			}
		});
		
	}
	
	public void refreshNews() {
		refreshNews(runner.currentId);
	}
	
	@SuppressWarnings("unchecked")
	public void refreshNews(Integer id) {
		
        Query getFeed = em.createQuery("SELECT item FROM NewsArticle item WHERE item.user.idUser = \'" + id + "\'");
    	
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
	}
	
	@SuppressWarnings("unchecked")
	public void refreshFriends() {

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
	}

}
