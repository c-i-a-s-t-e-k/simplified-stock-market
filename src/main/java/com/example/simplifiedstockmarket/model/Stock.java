package com.example.simplifiedstockmarket.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Objects;

@Data
@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    @NotNull
    private String name;

    @Min(0)
    private int quantity_in_bank;

    public boolean equals(Object other){
        if (!(other instanceof Stock otherStock)) return false;
        return otherStock.name.equals(this.name);
    }
    public int hashCode(){
        return Objects.hash(name);
    }
}
