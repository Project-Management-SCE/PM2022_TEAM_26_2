package com.example.ymdbanking.model;

import java.util.ArrayList;

/**
 * Class for Admin user
 */
public class Admin extends User
{
	private String firstName;
	private String lastName;
	private String country;
	//private String username;
	//private String password;
	private ArrayList<User> users;
	//private long dbId;

	public Admin()
	{
		super();
		//Empty constructor
	}
	public Admin(String email,String fullName,String id,String password,String phone,String username,
	             ArrayList<User> users)
	{
		super(email,fullName,id,password,phone,username);
		this.users = users;
	}
//	public String getCountry() {return country;}
//	public void setCountry(String country) {this.country = country;}
//	public String getFirstName() {return firstName;}
//	public void setFirstName(String firstName) {this.firstName = firstName;}
//	public String getLastName() {return lastName;}
//	public void setLastName(String lastName) {this.lastName = lastName;}
//	public String getUsername() {return username;}
//	public void setUsername(String username) {this.username = username;}
//	public String getPassword() {return password;}
//	public void setPassword(String password) {this.password = password;}
	public ArrayList<User> getUsers() {return users;}
	public void setUsers(ArrayList<User> users) {this.users = users;}
//	public long getDbId() {return dbId;}
//	public void setDbId(long dbId) {this.dbId = dbId;}
//
//	@Override
//	public String toString()
//	{
//		return getFirstName() + " " + getLastName();
//	}
}
