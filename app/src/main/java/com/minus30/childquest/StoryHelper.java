package com.minus30.childquest;

public class StoryHelper {
    static String NumberToPriceText(int price){
        String textPrice;
        if( price == -1 ) {
            textPrice = "Куплено";
        } else if( price != 0 ){
            textPrice = price + " ₽";
        } else {
            textPrice = "Бесплатно";
        }
        return textPrice;
    }
}
