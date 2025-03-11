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
    
	private String nome;

    private Integer quantidade;
   
    private String firebaseId;

    @Builder
    public Item(String nome, Integer quantidade) {
        super();
        this.nome = nome;
        this.quantidade = quantidade;
    }
    @Builder
    public Item(Long id,String nome, Integer quantidade) {
        super();
        this.nome = nome;
        this.quantidade = quantidade;
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "id:" + id + "      nome:" + nome + "      quantidade:" + quantidade + "\n\n";
    }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public String getFirebaseId() {
		return firebaseId;
	}
	public void setFirebaseId(String firebaseId) {
		this.firebaseId = firebaseId;
	}
    
    
}
