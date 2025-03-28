package com.Estoque.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Item {

	private Long id;  
    
	private String name;

    private Integer quantity;
   
    private String firebaseId;

    @Builder
    public Item(String name, Integer quantity) {
        super();
        this.name = name;
        this.quantity = quantity;
    }
    @Builder
    public Item(Long id,String name, Integer quantity) {
        super();
        this.name = name;
        this.quantity = quantity;
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "id:" + id + "      nome:" + name + "      quantidade:" + quantity + "\n\n";
    }
    
    
}
