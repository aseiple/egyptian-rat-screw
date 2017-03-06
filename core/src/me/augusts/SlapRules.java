package me.augusts;

class SlapRules {
    private static boolean isDouble() {
        return EgyptianRatScrew.CenterDeck.centerCards.get(0).equals(EgyptianRatScrew.CenterDeck.centerCards.get(1));
    }

    private static boolean isSandwich() {
        return EgyptianRatScrew.CenterDeck.centerCards.get(0).equals(EgyptianRatScrew.CenterDeck.centerCards.get(2));
    }

    private static boolean isTopBottom() {
        return EgyptianRatScrew.CenterDeck.centerCards.get(0).equals(EgyptianRatScrew.CenterDeck.centerCards.get(EgyptianRatScrew.CenterDeck.centerCards.size()-1));
    }

    private static boolean isTens() {
        if (EgyptianRatScrew.CenterDeck.centerCards.get(0).rank.pointValue + EgyptianRatScrew.CenterDeck.centerCards.get(1).rank.pointValue == 10) {
            return true;
        }
        if (EgyptianRatScrew.CenterDeck.centerCards.size() > 2) {
            if (EgyptianRatScrew.CenterDeck.centerCards.get(1).rank.pointValue > 10) {
                return EgyptianRatScrew.CenterDeck.centerCards.get(0).rank.pointValue + EgyptianRatScrew.CenterDeck.centerCards.get(2).rank.pointValue == 10;
            }
        }
        return false;
    }

    private static boolean isFourInARow() {
        return EgyptianRatScrew.CenterDeck.centerCards.get(0).adjacent(EgyptianRatScrew.CenterDeck.centerCards.get(1)) &&
                EgyptianRatScrew.CenterDeck.centerCards.get(1).adjacent(EgyptianRatScrew.CenterDeck.centerCards.get(2)) &&
                EgyptianRatScrew.CenterDeck.centerCards.get(2).adjacent(EgyptianRatScrew.CenterDeck.centerCards.get(3));
    }

    private static boolean isMarriage() {
        return (EgyptianRatScrew.CenterDeck.centerCards.get(0).rank == Rank.King && EgyptianRatScrew.CenterDeck.centerCards.get(1).rank == Rank.Queen) ||
                (EgyptianRatScrew.CenterDeck.centerCards.get(0).rank == Rank.Queen && EgyptianRatScrew.CenterDeck.centerCards.get(1).rank == Rank.King);
    }

    static boolean isValidSlap() {
        switch (EgyptianRatScrew.CenterDeck.centerCards.size()) {
            default:
            case 4:
                if (isFourInARow()) {
                    System.out.println("Four in a Row");
                    return true;
                }
            case 3:
                if (isSandwich()) {
                    System.out.println("Sandwich");
                    return true;
                }
            case 2:
                if (isDouble()) {
                    System.out.println("Double");
                    return true;
                }
                else if (isTopBottom()) {
                    System.out.println("Top and Bottom");
                    return true;
                }
                else if (isTens()) {
                    System.out.println("Tens");
                    return true;
                }
                else if (isMarriage()) {
                    System.out.println("Marriage");
                    return true;
                }
                else {
                    return false;
                }
            case 1:
            case 0:
                return false;
        }
    }
}
