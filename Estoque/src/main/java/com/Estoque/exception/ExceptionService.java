package com.Estoque.exception;

public class ExceptionService extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ExceptionService(String message) {
		super(message);
	}
	public ExceptionService(String message, Long id) {
		super(message+ id);
	}
}
