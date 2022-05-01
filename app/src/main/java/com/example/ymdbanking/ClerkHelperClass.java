package com.example.ymdbanking;

import java.util.ArrayList;

public class ClerkHelperClass {

    private String username;
    private final String id = "1";
    private String email;
    private String password;
    private String phone;
    private ArrayList<UserHelperClass>users;


    public ClerkHelperClass() {}

    public ClerkHelperClass(String username, String email, String password,String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.users = new ArrayList<>();
    }

    //Users list functions
    public UserHelperClass getUser(String id)
    {
        for(int i = 0;i<users.size();i++)
        {
            if(users.get(i).id.equals(id))
                return users.get(i);
        }
        return new UserHelperClass();
    }

    public void addUser(UserHelperClass user)
    {
        this.users.add(user);
    }




    //getters and setters

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<UserHelperClass> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserHelperClass> users) {
        this.users = users;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
