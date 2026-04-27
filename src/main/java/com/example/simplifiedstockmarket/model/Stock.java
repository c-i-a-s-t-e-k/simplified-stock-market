package com.example.simplifiedstockmarket.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

@Getter
@Setter
@Entity
public class Stock {
    @Id
    private String id;

    @Column(unique = true)
    @NotNull
    private String name;

    @Min(0)
    private int quantity_in_bank;
}
