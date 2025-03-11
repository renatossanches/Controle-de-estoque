package com.Estoque.exception;

public class ExceptionService extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ExceptionService(String msg) {
		super(msg);
	}
	public ExceptionService(String msg, Long id) {
		super(msg+ id);
	}
}
