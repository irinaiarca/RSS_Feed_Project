package util;

import java.util.ArrayList;
import java.util.HashMap;

public class PubSub {
	HashMap<String, ArrayList<EventHandler>> events;
	public PubSub() {
		events = new HashMap<String, ArrayList<EventHandler>>();
		subscribe("log", new EventHandler() {
			
			@Override
			public void exec(Object... args) {
				System.out.print("LOG GROUP:");
				for (Object o: args) {
					System.out.print(" (" + o + ")");
				}
				System.out.println(";");
			}
		});
	}
	
	public int subscribe(String event, EventHandler handler) {
		if (!events.containsKey(event)) events.put(event, new ArrayList<EventHandler>());
		events.get(event).add(handler);
		return events.get(event).indexOf(handler);
	}
	
	public boolean unsubscribe(String event, EventHandler handler) {
		if (events.containsKey(event)) {
			ArrayList<EventHandler> handlers = events.get(event);
			if (handlers.contains(handler)) {
				handlers.remove(handler);
				return true;
			} 
		}
		return false;
	}
	
	public boolean unsubscribe(String event, int index) {
		if (events.containsKey(event)) {
			ArrayList<EventHandler> handlers = events.get(event);
			if (handlers.get(index) != null) {
				handlers.remove(index);
				return true;
			} 
		}
		return false;
	}
	
	public PubSub publish(String event, Object... args) {
		if (events.containsKey(event)) {
			ArrayList<EventHandler> handlers = events.get(event);
			for (EventHandler handler: handlers) {
				handler.exec(args);
			}
		}
		return this;
	}
}