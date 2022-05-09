package com.example.ymdbanking.model;

/**
 * Class User for all users that extend from this class
 * e.g - Admin,Clerk,Profile
 */
public class User
{
	public enum USER_TYPE
	{
		ADMIN(1),
		CLERK(2),
		PROFILE(3);

		int value;

		USER_TYPE(int i)
		{
			value = i;
		}
		public int getValue() {return value;}
	}

	private String email;
	private String fullName;
	private String id;
	private String password;
	private String phone;
	private String username;
	private int typeID;

	public User()
	{
		//Empty constructor
	}

//	public User(String username, String password)
//	{
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.country = country;
//		this.username = username;
//		this.password = password;
//	}

	public User(String email,String fullName,String id,String password,String phone,String username)
	{
		this.email = email;
		this.fullName = fullName;
		this.id = id;
		this.password = password;
		this.phone = phone;
		this.username = username;
	}

//	public String getFirstName() {return firstName;}
//	public void setFirstName(String firstName) {this.firstName = firstName;}
//	public String getLastName() {return lastName;}
//	public void setLastName(String lastName) {this.lastName = lastName;}
//	public String getCountry() {return country;}
//	public void setCountry(String country) {this.country = country;}
	public String getUsername() {return username;}
	public void setUsername(String username) {this.username = username;}
	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	public String getFullName() {return fullName;}
	public void setFullName(String fullName) {this.fullName = fullName;}
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}
	public String getPhone() {return phone;}
	public void setPhone(String phone) {this.phone = phone;}
	public int getTypeID() {return typeID;}
	public void setTypeID(int typeID) {this.typeID = typeID;}

	@Override
	public String toString()
	{
		return getUsername() + " - " + getId();
	}
}
