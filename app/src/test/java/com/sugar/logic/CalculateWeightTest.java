package com.sugar.logic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculateWeightTest {

    @Test
    public void testCalculate() throws Exception {
       assertEquals(1, new CalculateWeight().calculate(100, 10, 10), 0f);
       assertEquals(20.4f, new CalculateWeight().calculate(100, 51, 40), 0f);
    }

}