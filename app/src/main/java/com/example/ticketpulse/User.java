package com.example.ticketpulse;

public class User {
    public String email, preference, ticket;

    public User(){

    }


    public User(String email, String preference, String ticket) {
        this.email = email;
        this.preference = preference;
        this.ticket = ticket;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
