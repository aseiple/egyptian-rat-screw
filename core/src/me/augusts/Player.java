package me.augusts;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

class Player {
    //Creates player hand
    ArrayList<Card> playerDeck;

    Player() {
        playerDeck = new ArrayList<Card>();
    }

    //Deals a card to the center pile
    void dealToCenter() {
        if (playerDeck.size() != 0) {
            Card card = playerDeck.remove(0);
            //Sets transform
            card.angle = 0;
            card.position = new Vector3(0, 0, EgyptianRatScrew.CenterDeck.centerCards.size() + 1);
            card.update();
            //Adds to center
            EgyptianRatScrew.CenterDeck.addCard(card);
        }
    }
}
