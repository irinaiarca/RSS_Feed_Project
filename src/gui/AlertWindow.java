package gui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AlertWindow {
	
	Stage stage;
	Text msg;
	
	public AlertWindow() {
		
		setupStage();
	}
	
	/**
	 * Verifies if the stage already exists and, if it doesn't, it creates it. If it already exists, nothing happens.
	 */
	void setupStage() {
		if (stage != null) return;
		
		stage = new Stage();

		GridPane gridMain = new GridPane();
        gridMain.setAlignment(Pos.CENTER);
        gridMain.setHgap(10);
        gridMain.setVgap(10);
        gridMain.setPadding(new Insets(10, 10, 10, 10));
		
		Scene scene = new Scene(gridMain, 200, 100);

        msg = new Text();
        msg.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        gridMain.add(msg, 0, 0, 2, 1);

		stage.setScene(scene);
	}
	
	AlertWindow handle(EventHandler<WindowEvent> handler) {
		stage.setOnCloseRequest(handler);
		return this;
	}
	
	AlertWindow message(String message) {
		msg.setText(message);
		return this;
	}
	
	AlertWindow show() {
		stage.show();		
		return this;
	}

}
