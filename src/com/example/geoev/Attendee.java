package com.example.geoev;

public class Attendee {
	private String id;
	private String name;
	
	//Constructor
	public Attendee(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	//Constructor
	public Attendee(Attendee p){
		this.id = p.getId();
		this.name = p.getName();
	}
	
	//Standard Constructors
	public Attendee() {}
	
	//getters and setters
	
	public String getId() { 
		return id;
	}
	public void setId(String id) {
		this.id = id;
		}
	
	public String getName() {
		return name;
		}
		
	public void setName(String name) {
		this.name = name;
		}
	
}
