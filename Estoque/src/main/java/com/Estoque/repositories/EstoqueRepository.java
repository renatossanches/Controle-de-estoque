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


}
