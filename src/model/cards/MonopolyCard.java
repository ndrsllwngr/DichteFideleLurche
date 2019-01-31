package model.cards;

import model.Management;
import model.Resource;
import model.players.Player;

public class MonopolyCard implements ProgressCard {

    private Management management;


    public MonopolyCard(Management management) {
        this.management = management;
    }

    /**
     * Play a monopoly card
     *
     * @param player   player who plays the card
     * @param resource resource the player want
     */
    public void playCard(Player player, Resource resource) {
        int number = 0;
        for (Player otherPlayer : management.getAllPlayersId().values()) {
            int tmp = otherPlayer.getSpecificResource(resource);
            otherPlayer.setSpecificResource(resource, -tmp);
            number = number + tmp;
        }
        player.setSpecificResource(resource, number);
    }

}


