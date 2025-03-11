package com.Estoque.api;

import org.springframework.stereotype.Component;

import com.Estoque.entities.Item;

@Component
public class IdAutoIncrement {

	public static void Increment(Item item, Long maxId) {
		item.setId(maxId + 1L);
	}
}
