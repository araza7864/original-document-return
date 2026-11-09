package edu.gmu.cs321;

import java.util.List;

public class Reviewer extends User {

    private ReviewScreen reviewScreen; //as 1-1 association

    public Reviewer(String name, ReviewScreen screen) {
        super(name);
        this.reviewScreen = screen;
    }

    public ReviewScreen getReviewScreen() {
        return reviewScreen;
    }
}