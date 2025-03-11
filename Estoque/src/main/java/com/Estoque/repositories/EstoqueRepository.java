package com.Estoque.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.Estoque.entities.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@Repository
public class EstoqueRepository {

    private final DatabaseReference databaseReference;

    @Autowired
    public EstoqueRepository(FirebaseDatabase firebaseDatabase) {
        this.databaseReference = firebaseDatabase.getReference("itens");
    }

    // Método para alterar item
    public void save(Item item) {
        databaseReference.push().setValueAsync(item);
    }

    // Buscar todos os itens de forma síncrona
    public CompletableFuture<List<Item>> findAll() {
        CompletableFuture<List<Item>> future = new CompletableFuture<>();
        List<Item> itemList = new ArrayList<>();
        
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {            	    
            	    if (dataSnapshot.exists()) {
            	        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            	            // Recuperando o item a partir do snapshot
            	            Item item = snapshot.getValue(Item.class);
            	            
            	            // Verifique se o item foi mapeado corretamente
            	            if (item != null) {
            	                // Atribuindo a chave única do Firebase (snapshot.getKey()) ao campo firebaseId
            	                item.setFirebaseId(snapshot.getKey());  // Atribui a chave única ao firebaseId
            	                
            	                // Adiciona o item na lista
            	                itemList.add(item);
            	            }
            	        }
            	        // Completa o futuro com a lista de itens
            	        future.complete(itemList);  
            	    } else {
            	        future.complete(itemList);  // Completa com uma lista vazia, se nenhum dado foi encontrado
            	    }
            	}
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new Exception("Erro ao carregar os itens"));  // Se der erro
            }
        });
        return future;
    }

    // Buscar um item pelo ID de forma síncrona
    public CompletableFuture<Item> findByIdFirebase(String string) {
        final Item[] item = new Item[1];
        CompletableFuture<Item> future = new CompletableFuture<>();

        databaseReference.child(String.valueOf(string)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item[0] = dataSnapshot.getValue(Item.class);
                future.complete(item[0]);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Tratar erro aqui, se necessário
            }
        });
        return future;  // Retorna o item (ainda não sincronizado)
    }
    // Buscar um item pelo ID de forma síncrona
    public CompletableFuture<Item> findById(Long id) {
        CompletableFuture<Item> future = new CompletableFuture<>();

        databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Item item = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Aqui, você está pegando o item com o id manual fornecido
                    item = snapshot.getValue(Item.class);
                    // Aqui você também pega o firebaseId do item
                    if (item != null) {
                        item.setFirebaseId(snapshot.getKey()); // Armazena o firebaseId
                    }
                }
                future.complete(item); // Completa o futuro com o item encontrado
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;  // Retorna o item (ainda não sincronizado)
    }
    
    // Método para deletar item
    public void deleteById(String id) {
        databaseReference.child(String.valueOf(id)).removeValueAsync();
    }
    
    public CompletableFuture<Long> getMaxId() {
        CompletableFuture<Long> future = new CompletableFuture<>();
        List<Item> itemList = new ArrayList<>();
        
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long maxId = 0;
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null && item.getId() > maxId) {
                        maxId = item.getId();
                    }
                }
                
                // Completa o future com o maior ID encontrado
                future.complete(maxId);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new Exception("Erro ao obter o maior ID"));
            }
        });
        return future;
    }
 // Atualiza o item no Firebase baseado no firebaseId
    public void updateItem(String firebaseId, Item item) {
        databaseReference.child(firebaseId).setValueAsync(item);
    }

    public void addRealTimeListener(ValueEventListener listener) {
        databaseReference.addValueEventListener(listener);
    }

}
