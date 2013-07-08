package com.example.geoev;

public class Person {
	private String id;
	private String name;
	
	//Constructor
	public Person(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	//Constructor
	public Person(Person p){
		this.id = p.getId();
		this.name = p.getName();
	}
	
	//Standard Constructors
	public Person() {}
	
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
