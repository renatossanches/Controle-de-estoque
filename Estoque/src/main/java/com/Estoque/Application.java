package com.Estoque;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan("com.Estoque")
@ComponentScan("com.Estoque")
@SpringBootApplication(scanBasePackages = "com.Estoque")
public class Application {

	public static void main(String[] args) {
		javafx.application.Application.launch(EstoqueApp.class, args);

	}
}