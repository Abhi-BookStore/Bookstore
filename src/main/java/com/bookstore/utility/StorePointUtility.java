package com.bookstore.utility;

import com.bookstore.constants.Constants;
import com.bookstore.domain.Book;
import com.bookstore.service.BookService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;

public class StorePointUtility {

    private static final Logger logger = LoggerFactory.getLogger(StorePointUtility.class);

    @Autowired
    private BookService bookService;

    /**
     * This method takes @{book} as a parameter and calculates the points
     *
     * @param book
     * @return
     */
    public static Long calculateAndFetchStorePointForTheBook(Book book){

        if(book == null){
            System.out.println("*******  Book is passed null *********");
            return null;
        }
        logger.info("::::: calculateAndFetchStorePointForTheBook for book  :::::: "+ book.getTitle());

        Double bookPrice = book.getOurPrice();
        logger.info(":::::  bookPrice ::::::   "+ bookPrice);
        Double pointsInDouble = 0.0;

// TODO: Add condition for particular books receiving points on their purchases.
//        if(book.getCategory().equals("management") && book.getOurPrice()> Constants.MINIMUM_BOOK_PRICE_FOR_SP_ELIGIBLE){
        if(book.getOurPrice() >0 ){
            pointsInDouble += bookPrice * 10;
            logger.info(":::::  pointsInDouble ::::::   "+ pointsInDouble);
        }

        return Math.round(pointsInDouble);
    }

    public static Double convertStorePointToCashAmount(Long storePoint){

        return Constants.STORE_POINT_TO_CASH_CONVERTER_MULTIPLIER*storePoint;
    }
}
