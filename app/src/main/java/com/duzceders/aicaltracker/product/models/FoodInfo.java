package com.duzceders.aicaltracker.product.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FoodInfo implements Serializable {
    private String foodName;
    private int calories;
    private int protein;
    private int fat;
    private int carbs;
    private String recommendations;
}

