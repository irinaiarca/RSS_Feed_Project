package util;

import java.util.ArrayList;
import java.util.HashMap;

public class PubSub {
	HashMap<String, ArrayList<PubSubHandler>> events;
	public PubSub() {
		events = new HashMap<String, ArrayList<PubSubHandler>>();
		subscribe("log", new PubSubHandler() {
			
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
	
	public int subscribe(String event, PubSubHandler handler) {
		if (!events.containsKey(event)) events.put(event, new ArrayList<PubSubHandler>());
		events.get(event).add(handler);
		return events.get(event).indexOf(handler);
	}
	
	public int[] subscribe(String[] evs, PubSubHandler handler) {
		int[] s = new int[evs.length];
		for (int i = 0; i < evs.length; i++) {
			s[i] = subscribe(evs[i], handler); 
		}
		return s;
	}

	public ArrayList<Integer> subscribe(ArrayList<String> evs, PubSubHandler handler) {
		ArrayList<Integer> s = new ArrayList<Integer>();
		for (String event: evs) {
			s.add(subscribe(event, handler)); 
		}
		return s;
	}
	
	public boolean unsubscribe(String event, PubSubHandler handler) {
		if (events.containsKey(event)) {
			ArrayList<PubSubHandler> handlers = events.get(event);
			if (handlers.contains(handler)) {
				handlers.remove(handler);
				return true;
			} 
		}
		return false;
	}
	
	public boolean unsubscribe(String event, int index) {
		if (events.containsKey(event)) {
			ArrayList<PubSubHandler> handlers = events.get(event);
			if (handlers.get(index) != null) {
				handlers.remove(index);
				return true;
			} 
		}
		return false;
	}
	
	public PubSub publish(String event, Object... args) {
		if (events.containsKey(event)) {
			ArrayList<PubSubHandler> handlers = events.get(event);
			for (PubSubHandler handler: handlers) {
				handler.exec(args);
			}
		}
		return this;
	}
}