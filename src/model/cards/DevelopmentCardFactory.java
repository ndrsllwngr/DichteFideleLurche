package model.cards;

import model.Management;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DevelopmentCardFactory {

    private ArrayList<DevelopmentCard> developmentCards = new ArrayList<>();

    /**
     * Create a ArrayList of developmentsCards and sort them randomly
     *
     * @param management model management
     */
    public DevelopmentCardFactory(Management management) {
        ArrayList<DevelopmentCard> temp = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            temp.add(new MonopolyCard(management));
        }
        for (int j = 0; j < 2; j++) {
            temp.add(new RoadBuildingCard());
        }
        for (int k = 0; k < 2; k++) {
            temp.add(new YearOfPlentyCard(management));
        }
        for (int l = 0; l < 14; l++) {
            temp.add(new KnightCard());
        }
        VictoryPointCard chapel = new VictoryPointCard(VictoryPointCardType.CHAPEL);
        VictoryPointCard library = new VictoryPointCard(VictoryPointCardType.LIBRARY);
        VictoryPointCard market = new VictoryPointCard(VictoryPointCardType.MARKET);
        VictoryPointCard palace = new VictoryPointCard(VictoryPointCardType.PALACE);
        VictoryPointCard university = new VictoryPointCard(VictoryPointCardType.UNIVERSITY);
        temp.add(chapel);
        temp.add(library);
        temp.add(market);
        temp.add(palace);
        temp.add(university);

        for (int i = 0; i <= 24; i++) {
            int r = ThreadLocalRandom.current().nextInt(0, temp.size());
            developmentCards.add(i, temp.get(r));
            temp.remove(r);
        }
    }

    /**
     * Take last developmentCard of ArrayList developmentCards
     *
     * @return null if stack is empty
     */
    public DevelopmentCard takeDevelopCard() {
        if (developmentCards.size() > 0) {
            DevelopmentCard selectedCard = developmentCards.get(developmentCards.size() - 1);
            developmentCards.remove(developmentCards.size() - 1);
            return selectedCard;
        } else {
            return null;
        }
    }

    /**
     * Get the number of developmentcards in the Development-card-factory
     *
     * @return number of developmentcards
     */
    public int getSizeDevelopmentCards() {
        return developmentCards.size();
    }
}




