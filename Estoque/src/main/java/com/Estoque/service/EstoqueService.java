package com.Estoque.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Estoque.api.IdAutoIncrement;
import com.Estoque.entities.Item;
import com.Estoque.exception.ExceptionService;
import com.Estoque.repositories.EstoqueRepository;

@Service
public class EstoqueService {

    private final EstoqueRepository repository;

    @Autowired
    public EstoqueService(EstoqueRepository repository) {
        this.repository = repository;
    }

    // Método para alterar item
    public void add(Item item) throws InterruptedException, ExecutionException {
    	 repository.getMaxId().thenAcceptAsync(maxId -> {
    	        // Incrementa o id com o valor retornado de maxId
    	        IdAutoIncrement.Increment(item, maxId);
    	        // Salva o item no repositório após o incremento
    	        repository.save(item);
    	    }).exceptionally(ex -> {
    	        // Tratar erro aqui, caso ocorra ao buscar o maxId ou ao salvar
    	        System.out.println("Erro ao alterar item: " + ex.getMessage());
    	        return null;
    	    });
    }

    // Método para remover item
    public void remove(Long id) {
    	CompletableFuture<Item> existingItemFuture = repository.findById(id);
    	existingItemFuture.thenAcceptAsync(existingItem -> {
            if (existingItem == null) {
                try {
					throw new ExceptionService("Item não encontrado com id: ", existingItemFuture.get().getId());
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            // Agora temos o firebaseId, vamos buscar o item no Firebase para atualizar com os novos dados
        String firebaseId = existingItem.getFirebaseId();
        repository.deleteById(firebaseId);
        reordenarIds(); // Chama a função para reordenar IDs
    });
    }

    // Buscar todos os itens
    public CompletableFuture<List<Item>> findAll() {
    	return repository.findAll();
    
    }

    // Buscar item por ID
    public CompletableFuture<Item> findByIdFirebase(String id) {
        return repository.findByIdFirebase(id);
    }
    public CompletableFuture<Item> findById(Long id) {
        return repository.findById(id);
    }
    
    // Alterar item
    public void alterItem(Item item) {
        if (item.getId() == null) {
            throw new ExceptionService("ID do item não pode ser nulo.");
        }

        // Buscar o item usando o id manual
        CompletableFuture<Item> existingItemFuture = repository.findById(item.getId());

        existingItemFuture.thenAcceptAsync(existingItem -> {
            if (existingItem == null) {
                throw new ExceptionService("Item não encontrado com id: " + item.getId());
            }

            // Agora temos o firebaseId, vamos buscar o item no Firebase para atualizar com os novos dados
            String firebaseId = existingItem.getFirebaseId();

            // Atualizar os campos do item (nome e quantidade)
            existingItem.setNome(item.getNome());
            existingItem.setQuantidade(item.getQuantidade());

            // Agora, usando o firebaseId, vamos atualizar o item no Firebase
            repository.updateItem(firebaseId, existingItem);

        }).exceptionally(ex -> {
            // Caso haja erro ao procurar o item, exibe uma mensagem de erro
            System.out.println("Erro ao alterar item: " + ex.getMessage());
            return null;
        });
    }

    
    private void reordenarIds() {
        repository.findAll().thenAcceptAsync(itens -> {
            for (int i = 0; i < itens.size(); i++) {
                Item item = itens.get(i);
                item.setId((long) (i + 1)); // Reatribui um ID sequencial
                repository.updateItem(item.getFirebaseId(), item); // Atualiza no Firebase
            }
        }).exceptionally(ex -> {
            System.out.println("Erro ao reordenar IDs: " + ex.getMessage());
            return null;
        });
    }


    
}
