package com.Estoque.entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class LogsDTO {

    private final StringProperty email;
    private final StringProperty data;
    private final StringProperty mensagem;

	
	public LogsDTO(String email, String data, String mensagem) {
		this.email = new SimpleStringProperty(email);
		this.data = new SimpleStringProperty(data);
		this.mensagem = new SimpleStringProperty(mensagem);
	}
	 public StringProperty email() {
	        return email;
	    }

	    public StringProperty data() {
	        return data;
	    }

	    public StringProperty mensagem() {
	        return mensagem;
	    }
	
}
