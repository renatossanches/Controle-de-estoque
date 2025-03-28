package com.Estoque.entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class LogsDTO {

    private final StringProperty email;
    private final StringProperty date;
    private final StringProperty message;

	
	public LogsDTO(String email, String date, String message) {
		this.email = new SimpleStringProperty(email);
		this.date = new SimpleStringProperty(date);
		this.message = new SimpleStringProperty(message);
	}
	 public StringProperty email() {
	        return email;
	    }

	    public StringProperty date() {
	        return date;
	    }

	    public StringProperty message() {
	        return message;
	    }
	
}
