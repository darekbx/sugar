package com.sugar.logic;

public class CalculateWeight {

    public float calculate(float weight, float sugar, float portionweight) {
        return portionweight * sugar / weight;
    }
}
