package me.augusts;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

class Card extends Renderable {
    final Rank rank;
    //Parts of model
    final float[] vertices;
    final short[] indices;
    //Transform
    final Matrix4 transform = new Matrix4();
    //Card Information
    private final Suit suit;
    Vector3 position = new Vector3();
    float angle;

    Card(Suit suit, Rank rank, Sprite back, Sprite front) {
        assert (front.getTexture() == back.getTexture());
        this.suit = suit;
        this.rank = rank;

        //Sets card size
        front.setSize(EgyptianRatScrew.CARD_WIDTH, EgyptianRatScrew.CARD_HEIGHT);
        back.setSize(EgyptianRatScrew.CARD_WIDTH, EgyptianRatScrew.CARD_HEIGHT);
        //Moves origin to center
        front.setPosition(-front.getWidth() * 0.5f, -front.getHeight() * 0.5f);
        back.setPosition(-front.getWidth() * 0.5f, -front.getHeight() * 0.5f);
        //Converts to plane
        vertices = convert(front.getVertices(), back.getVertices());
        indices = new short[]{0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4};
    }

    //Convert from sprite into plane
    private static float[] convert(float[] front, float[] back) {
        return new float[]{
                front[Batch.X2], front[Batch.Y2], 0, 0, 0, 1, front[Batch.U2], front[Batch.V2],
                front[Batch.X1], front[Batch.Y1], 0, 0, 0, 1, front[Batch.U1], front[Batch.V1],
                front[Batch.X4], front[Batch.Y4], 0, 0, 0, 1, front[Batch.U4], front[Batch.V4],
                front[Batch.X3], front[Batch.Y3], 0, 0, 0, 1, front[Batch.U3], front[Batch.V3],

                back[Batch.X1], back[Batch.Y1], 0, 0, 0, -1, back[Batch.U1], back[Batch.V1],
                back[Batch.X2], back[Batch.Y2], 0, 0, 0, -1, back[Batch.U2], back[Batch.V2],
                back[Batch.X3], back[Batch.Y3], 0, 0, 0, -1, back[Batch.U3], back[Batch.V3],
                back[Batch.X4], back[Batch.Y4], 0, 0, 0, -1, back[Batch.U4], back[Batch.V4]
        };
    }

    //Updates transform
    void update() {
        float z = position.z + 0.5f * Math.abs(MathUtils.sinDeg(angle));
        transform.setToRotation(Vector3.Y, angle);
        transform.trn(position.x, position.y, z);
    }

    //Compares two cards
    boolean equals(Card card) {
        return card.rank == this.rank;
    }

    //Checks if two cards are next to each other in a sorted deck
    boolean adjacent(Card card) {
        return card.rank.pointValue + 1 == this.rank.pointValue || card.rank.pointValue - 1 == this.rank.pointValue || (card.rank == Rank.King && this.rank == Rank.Ace) || (card.rank == Rank.Ace && this.rank == Rank.King);
    }
}
