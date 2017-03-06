package me.augusts;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

class Player {
    ArrayList<Card> playerDeck;

    Player() {
        playerDeck = new ArrayList<Card>();
    }

    void dealToCenter() {
        if(playerDeck.size() != 0) {
            Card card = playerDeck.remove(0);
            card.angle = 0;
            card.position = new Vector3(0, 0, EgyptianRatScrew.CenterDeck.centerCards.size() + 1);
            card.update();
            EgyptianRatScrew.CenterDeck.addCard(card);
        }
    }
}
