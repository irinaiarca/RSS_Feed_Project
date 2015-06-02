package gui;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static javafx.geometry.HPos.RIGHT;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Comment;
import model.Friend;
import model.NewsArticle;
import model.User;
import rss.model.*;
import rss.read.*;
import sun.util.calendar.BaseCalendar.Date;


public class Login extends Application {

	public User loggedUser = new User();
	public static Integer currentID;
	

	EntityManagerFactory entityManagerFactory;
	
	
	/**
	 * This function sets up the database for accessing data. Used only when the database 
	 * is first accessed, at the start of the application (either when a new user is 
	 * created, or when an existing user accesses the application).
	 */
	void setUp()
	{
			entityManagerFactory = Persistence.createEntityManagerFactory("RSS_feed_project");
			entityManager = entityManagerFactory.createEntityManager();
	}	
	
	@PersistenceContext protected EntityManager entityManager;
	
	/**
	 * Created a new User, based on the username and password strings, adds it to the database,
	 *  then returns the object resulted from the creation.
	 * @param userName
	 * @param passWord
	 * @return User
	 */
	
	public User createUser(String userName, String passWord) {
		
		System.out.println("Starting create routine");
		
		setUp();
		System.out.println("Database conenction set up");
		
		entityManager.getTransaction().begin();
		System.out.println("Transaction started");
		
        User newUser  = new User();
        newUser.setUsername(userName);
        newUser.setPassword(passWord);
		System.out.println("User created and updated");
		
        entityManager.persist(newUser);
		System.out.println("User persisted");
		
        entityManager.getTransaction().commit();
        entityManager.close();
		System.out.println("Transaction Closed");
		
		return newUser;
		
    }

	/**
	 * returns all feed messages found in the URL
	 * @param feedURL URL that leads directly to the online RSS or XML file
	 * @return
	 */
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
	
    @Override
    public void start(Stage primaryStage) 
    {
    	
        primaryStage.setTitle("Welcome");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Please log in");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        
        Button btn2 = new Button ("New user");
        HBox hBtn2 = new HBox(10);
        hBtn2.setAlignment(Pos.BOTTOM_LEFT);
        hBtn2.getChildren().add(btn2);
        grid.add(hBtn2, 0, 4);
        
        btn2.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e)
        	{
        		Stage st = new Stage();
        		st.setHeight(250);
        		st.setWidth(300);
        		
        		GridPane gridMain = new GridPane();
                gridMain.setAlignment(Pos.CENTER);
                gridMain.setHgap(10);
                gridMain.setVgap(10);
                gridMain.setPadding(new Insets(10, 10, 10, 10));
                
                Scene sc = new Scene(gridMain, 250, 300);
                st.setScene(sc);
                
                Text scenetitle = new Text("Choose a username and a password");
                scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
                gridMain.add(scenetitle, 0, 0, 2, 1);

                Label newName = new Label("New User Name:");
                gridMain.add(newName, 0, 1);

                TextField newUserTextField = new TextField();
                gridMain.add(newUserTextField, 1, 1);
                
                Label newPass = new Label("New Password:");
                gridMain.add(newPass, 0, 2);

                TextField newPassTextField = new TextField();
                gridMain.add(newPassTextField, 1, 2);
                
                Button btn3 = new Button("Create");
                HBox hbBtn3 = new HBox(10);
                hbBtn3.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn3.getChildren().add(btn3);
                gridMain.add(hbBtn3, 1, 4);
                
                btn3.setOnAction(new EventHandler<ActionEvent>() {
                	
                	@Override
                	public void handle(ActionEvent e)
                	{
                		loggedUser = createUser(newUserTextField.getText(), newPassTextField.getText());
                	}
                });

                st.show();
        		
        	}
        });

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 6);
        grid.setColumnSpan(actiontarget, 2);
        grid.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) 
            {
            	
            	String currentUser = userTextField.getText();
            	String currentPwd = pwBox.getText();
            	         	
            	setUp();
          
            	Query q = entityManager.createQuery("SELECT usr FROM User usr WHERE usr.username=\'" + currentUser + "\' and usr.password=\'" + currentPwd + "\'");
            	
            	User foundUser = null;
            	
            	try
            	{
            		foundUser = (User)q.getSingleResult();
            	}
            	catch (NoResultException ex)
            	{
            		System.out.println("Query error");
            	}
            	
            	if (foundUser != null)
            	{
            		currentID = foundUser.getIdUser();
            		Stage st = new Stage();
            		
            		loggedUser = foundUser;
            		
            		st.setHeight(700);
            		st.setWidth(1200);
            		
            		st.setTitle("Feed reader");
            		
            		GridPane gridMain = new GridPane();
                    gridMain.setAlignment(Pos.CENTER);
                    gridMain.setHgap(10);
                    gridMain.setVgap(10);
                    gridMain.setPadding(new Insets(10, 10, 10, 10));
                    
                    Scene sc = new Scene(gridMain, 700, 1200);
                    st.setScene(sc);
                    
                    Label l = new Label("Welcome, " + currentUser + "!");
                    gridMain.add(l, 0, 1);
                    l = new Label("Here are your news!");
                    gridMain.add(l, 1, 1);
                    
                    ListView<String> list = new ListView<>();
                    
                    Query getFeed = entityManager.createQuery("SELECT item FROM NewsArticle item WHERE item.user.idUser = \'" + loggedUser.getIdUser() + "\'");
                	
                	List<NewsArticle> articleList;
                	List<String> aux = new ArrayList<String>();
                	ObservableList<String> items = null;
                	
                	try
                	{
                		articleList = getFeed.getResultList();
                		for(NewsArticle f : articleList)
                		{
                			aux.add(f.getDescription());
                		}
                		items = FXCollections.observableArrayList(aux);
                	}
                	catch (NoResultException ex)
                	{
                		System.out.println("Query error");
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
                    
                    list.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    	
                    	@Override
                    	public void handle (MouseEvent event)
                    	{
                    		if (txt.getText().equals("\n\n                      Click on an item in the list on the left side to read news."))
                    		{
                    			txt.setText(list.getSelectionModel().getSelectedItem().toString());
                    		}
                    		else
                    		{
                    			txt.appendText("\n\n");
                    			txt.appendText(list.getSelectionModel().getSelectedItem().toString());
                    		}
                    			
                    		
                    	}
                    });
                    
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
                    
                    btn5.setOnAction(new EventHandler<ActionEvent>() {
                    	@Override
                    	public void handle(ActionEvent e)
                    	{
                    		
                    	}
                    });
                    
                    
                    gridMain.add(list, 0, 2);
                    
                    Button btn = new Button("Look at News from friends");
                    HBox hbBtn = new HBox(10);
                    hbBtn.setAlignment(Pos.CENTER);
                    hbBtn.getChildren().add(btn);
                    gridMain.add(hbBtn, 2, 2);
                    
           //       btn.setOnAction(new EventHandler<ActionEvent>() {});
                    
                    ListView<String > friendList = new ListView<>();
                    ObservableList<String> obsFriendList = FXCollections.observableArrayList();
                    List<Friend> friendQueryResult = new ArrayList<Friend>();
                                       
                    Query friends = entityManager.createQuery("SELECT f FROM Friend f WHERE f.user.idUser =" + loggedUser.getIdUser());
                    
                    friendQueryResult = friends.getResultList();
                    for(Friend f : friendQueryResult)
                    {
                    	Query test = entityManager.createQuery("SELECT u FROM User u WHERE u.idUser =" + f.getIdFriends());
                    	List<User> u = test.getResultList();
                    	for(User x : u)
                    	{
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
                    
                    btn2.setOnAction(new EventHandler<ActionEvent>() {
                    	
                    	 @Override
                         public void handle(ActionEvent e) 
                         {
                    		 Stage st = new Stage();
                     		
                    		 st.setHeight(200);
                    		 st.setWidth(350);
                     		
                    		 st.setTitle("Add a new Friend");
                     		
                    		 GridPane gridFriend = new GridPane();
                             gridFriend.setAlignment(Pos.CENTER);
                             gridFriend.setHgap(10);
                             gridFriend.setVgap(10);
                             gridFriend.setPadding(new Insets(10, 10, 10, 10));
                             
                             Scene sc = new Scene(gridFriend, 500, 500);
                             st.setScene(sc);
                             
                             Text txt = new Text("Choose the person you want to add as friend, then click OK.");
                             
                             List<User> queryHolder = new ArrayList<User>();
                             
                             Query getNonFriends = entityManager.createQuery("SELECT u from User u");
                             
                             queryHolder = getNonFriends.getResultList();
                             queryHolder.remove(loggedUser);
                             queryHolder.remove(loggedUser.getFriends());                             
                             
                             ObservableList<String> options = FXCollections.observableArrayList();
                             
                             for(User f : queryHolder)
                             {
                            	 options.add(f.getUsername());
                             }
                             
                             final ComboBox comboBox = new ComboBox(options);
                             comboBox.setPrefWidth(320);
                             
                             Button btn = new Button("OK");
                             HBox hbBtn = new HBox(10);
                             hbBtn.setAlignment(Pos.CENTER);
                             hbBtn.getChildren().add(btn);
                             gridFriend.add(hbBtn, 0, 2);
                             
                             btn.setOnAction(new EventHandler<ActionEvent>() {
                            	 
                            	 @Override
                            	 public void handle(ActionEvent e)
                            	 {
                            		 String selected;
                            		 Friend newFriend = new Friend(); 
                            		 
                            		 newFriend.setUser(loggedUser);
                            	
                            		 selected = comboBox.getSelectionModel().getSelectedItem().toString();
                            		 System.out.println(selected);
                            		 
                            		 Query q = entityManager.createQuery("SELECT u FROM User u WHERE u.username =\'" + selected + "\'");
                            		 User u = (User)q.getSingleResult();
                            		 System.out.println(u.getUsername());
                            		 newFriend.setIdFriends(u.getIdUser());  
                            		 
                            		 entityManager.getTransaction().begin();
                            		 entityManager.persist(newFriend);
                            		 System.out.println("Friend persisted");
                            			
                            		 entityManager.getTransaction().commit();
                            		 entityManager.close();
                            		 System.out.println("Transaction Closed");
                            		 
                            		 //FRIENDLISTVIEW REFRESH
                            		 ObservableList<String> holder = friendList.getItems();
                            		 holder.add(u.getUsername());
                            		 friendList.setItems(holder);
                            
                            		 st.close();
                            	 }
                             });
                             
                             gridFriend.add(txt, 0, 0);
                             gridFriend.add(comboBox, 0, 1);
                            
                             st.show();
                         }
                    });
                    
                    Button btn3 = new Button("Add resource");
                    HBox hbBtn3 = new HBox(10);
                    hbBtn3.setAlignment(Pos.CENTER);
                    hbBtn3.getChildren().add(btn3);
                    gridMain.add(hbBtn3, 2, 4);
                    
                    btn3.setOnAction(new EventHandler<ActionEvent>() {
                    	
                    	@Override
                        public void handle(ActionEvent e) 
                        {
                    		Stage st = new Stage();
                    		
                    		st.setHeight(500);
                    		st.setWidth(500);
                    		
                    		st.setTitle("Add a new RSS link");
                    		
                    		GridPane gridRSS = new GridPane();
                            gridRSS.setAlignment(Pos.CENTER);
                            gridRSS.setHgap(10);
                            gridRSS.setVgap(10);
                            gridRSS.setPadding(new Insets(10, 10, 10, 10));
                            
                            Scene sc = new Scene(gridRSS, 500, 500);
                            st.setScene(sc);
                            
                            Label help = new Label("Type or paste in the text field below \n an URL thet leads to an RSS feed:");
                            gridRSS.add(help, 0, 1);

                            TextField myURL = new TextField();
                            gridRSS.add(myURL, 0, 2);
                            
                            ListView<String> list = new ListView<>();
                            list.setPrefWidth(350);
                            gridRSS.add(list, 0, 3);
                            
                            Button btn = new Button("Add");
                            HBox hbBtn = new HBox(10);
                            hbBtn.setAlignment(Pos.CENTER);
                            hbBtn.getChildren().add(btn);
                            gridRSS.add(hbBtn, 0, 4);
                            
                            List<FeedMessage> feedHolder = new ArrayList<FeedMessage>();
                            
                            btn.setOnAction(new EventHandler<ActionEvent>() {
                            	
                            	@Override
                            	public void handle (ActionEvent e)
                            	{                            		
                            		feedHolder.addAll(addFeed(myURL.getText()));
                            		ObservableList<String> feedToChooseFrom = FXCollections.observableArrayList();
                            		for(FeedMessage m : feedHolder)
                            		{
                            			feedToChooseFrom.add(m.getTitle());
                            		}
                            		
                            		list.setItems(feedToChooseFrom);
                            	}
                            });
                            
                            Button btn2 = new Button("Add selected item to my news list");
                            HBox hbBtn2 = new HBox(10);
                            hbBtn2.setAlignment(Pos.CENTER);
                            hbBtn2.getChildren().add(btn2);
                            gridRSS.add(hbBtn2, 1, 4);
                            
                            btn2.setOnAction(new EventHandler<ActionEvent>() {
                            	@Override
                            	public void handle (ActionEvent e)
                            	{
                            		String selectedTitle = list.getSelectionModel().getSelectedItem();
                            		FeedMessage selectedArticle = new FeedMessage();
                            		for(FeedMessage m : feedHolder)
                            		{
                            			if (m.getTitle().equals(selectedTitle))
                            			{
                            				selectedArticle = m;
                            			}
                            		}
                            		
                            		entityManager.getTransaction().begin();
                            		
                            		NewsArticle newArticle  = new NewsArticle();
                            		newArticle.setUser(loggedUser);
                            		newArticle.setDescription(selectedArticle.getDescription());
                            		newArticle.setSource(selectedArticle.getAuthor());
                            		System.out.println(newArticle.getIdNews());
                            		System.out.println(newArticle.getUser().getIdUser());

                            		entityManager.persist(loggedUser);
                            		entityManager.persist(newArticle);

                            		entityManager.getTransaction().commit();
                            		entityManager.close();

                                   	ObservableList<String> holder = list.getItems();
                          		 	holder.add(newArticle.getDescription());
                          		 	list.setItems(holder);
                            	}
                            
                            });
                            
                            st.show();
                        }
                    });
                    
            		st.show();
            	}
            	else
            	{
                	System.out.println("nu");
                	actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Error!");
            	
            	}
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void startApp(String[] args) {
        launch(args);
    }

}
