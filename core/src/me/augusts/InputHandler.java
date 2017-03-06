package me.augusts;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor {
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(!EgyptianRatScrew.gameover) {
            //Player 2 Deal
            if (keycode == Input.Keys.K && EgyptianRatScrew.player2Status == Status.Deal) {
                EgyptianRatScrew.player2Status = Status.Waiting;
                EgyptianRatScrew.player1Status = Status.Deal;
                EgyptianRatScrew.player2.dealToCenter();
                return true;
            }
            //Player 1 Deal
            else if (keycode == Input.Keys.A && EgyptianRatScrew.player1Status == Status.Deal) {
                EgyptianRatScrew.player2Status = Status.Deal;
                EgyptianRatScrew.player1Status = Status.Waiting;
                EgyptianRatScrew.player1.dealToCenter();
                return true;
            }
            //Player 1 Slap
            else if (keycode == Input.Keys.S) {
                System.out.print("Player 1 Slap: ");
                if (SlapRules.isValidSlap()) {
                    EgyptianRatScrew.CenterDeck.giveDeckToPlayer(EgyptianRatScrew.player1);
                } else {
                    System.out.println("Invalid giving two cards to the center");
                    EgyptianRatScrew.CenterDeck.takeTwoFromPlayer(EgyptianRatScrew.player1);
                }
            }
            //Player 2 Slap
            else if (keycode == Input.Keys.L) {
                System.out.print("Player 2 Slap: ");
                if (SlapRules.isValidSlap()) {
                    EgyptianRatScrew.CenterDeck.giveDeckToPlayer(EgyptianRatScrew.player2);
                } else {
                    System.out.println("Invalid giving two cards to the center");
                    EgyptianRatScrew.CenterDeck.takeTwoFromPlayer(EgyptianRatScrew.player2);
                }
            }
        }
        else {
            if(keycode == Input.Keys.SPACE) {
                EgyptianRatScrew.gameover = false;
                EgyptianRatScrew.dealCards();
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
