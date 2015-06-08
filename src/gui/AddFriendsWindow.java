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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import model.Friend;
import model.User;

public class AddFriendsWindow implements EventHandler<ActionEvent> {

	private EntityManager em;
	private Runner runner;
	
	private Stage stage;
	
	@SuppressWarnings("rawtypes")
	private final ComboBox comboBox = new ComboBox();

	public AddFriendsWindow() {
		runner = Runner.getInstance(); em = runner.entityManager;
		
		setupStage();
		hookEvents();
		refreshUsers();
	}
	
	/**
	 * An auxiliary function that hooks certain methods to global events.
	 */
	private void hookEvents() {
		AddFriendsWindow self = this;
		runner.mediator.subscribe("addfriendswindow.open", new util.PubSubHandler() {
			
			@Override
			public void exec(Object... args) {
		        stage.show();
			}
		});
		runner.mediator.subscribe("addfriendswindow.refresh", new util.PubSubHandler() {
			
			@Override
			public void exec(Object... args) {
		        self.refreshUsers();
			}
		});
	}
	
	/**
	 * Verifies if the stage already exists and, if it doesn't, it creates it. If it already exists, nothing happens.
	 */
	private void setupStage() {
		stage = new Stage();
 		
		stage.setHeight(200);
		stage.setWidth(350);
		
		stage.setTitle("Add a new Friend");
		
		GridPane gridFriend = new GridPane();
        gridFriend.setAlignment(Pos.CENTER);
        gridFriend.setHgap(10);
        gridFriend.setVgap(10);
        gridFriend.setPadding(new Insets(10, 10, 10, 10));
        
        Scene sc = new Scene(gridFriend, 500, 500);
        stage.setScene(sc);
        
        Text txt = new Text("Choose the person you want to add as friend, then click OK.");
        
        comboBox.setPrefWidth(320);
        
        Button btn = new Button("OK");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(btn);
        gridFriend.add(hbBtn, 0, 2);
        
        gridFriend.add(txt, 0, 0);
        gridFriend.add(comboBox, 0, 1);
        
        btn.setOnAction(this);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Populates list with users that can be followed (aren't already friends).
	 */
	public void refreshUsers() {
		
        List<User> queryHolder = new ArrayList<User>();
        
        Query getNonFriends = em.createQuery("SELECT u from User u");
        
        queryHolder = getNonFriends.getResultList();
        queryHolder.remove(runner.loggedUser);
        queryHolder.remove(runner.loggedUser.getFriends());                             
        
        ObservableList<String> options = FXCollections.observableArrayList();
        
        for(User f : queryHolder)
        {
       	 options.add(f.getUsername());
        }
        
        comboBox.setItems(options);
	}

	@Override
	/**
	 * Adds the selected friend to the database and the user's friends list.
	 */
	public void handle(ActionEvent arg0) {
		 String selected;
		 Friend newFriend = new Friend(); 
		 
		 newFriend.setUser(runner.loggedUser);
	
		 selected = comboBox.getSelectionModel().getSelectedItem().toString();
		 System.out.println(selected);
		 
		 Query q = em.createQuery("SELECT u FROM User u WHERE u.username =\'" + selected + "\'");
		 User u = (User) q.getSingleResult();
		 newFriend.setIdFriends(u.getIdUser());  
		 
		 em.getTransaction().begin();
		 em.persist(newFriend);
		 System.out.println("Friend persisted: " + newFriend.getUser().getIdUser() + " friend to " + newFriend.getIdFriends() );
			
		 em.getTransaction().commit();
		 System.out.println("Transaction Closed");
		 
		 runner.mediator.publish("friends.refresh");		
	}

}
