package model.cards;

import model.Management;
import model.Resource;
import model.players.Player;

public class YearOfPlentyCard implements ProgressCard {

    private Management management;

    public YearOfPlentyCard(Management management) {
        this.management = management;
    }

    /**
     * Play a yearOfPlenty card and update the new values in the management
     *
     * @param player    player who plays the card
     * @param resource1 selected resource number one
     * @param resource2 selected resource number two
     */
    public void playCard(Player player, Resource resource1, Resource resource2) {
        if (management.getExistingRessource(resource1) > 0) {
            player.setSpecificResource(resource1, 1);
            management.setSpecificResource(resource1, -1);
        }
        if (management.getExistingRessource(resource2) > 0) {
            player.setSpecificResource(resource2, 1);
            management.setSpecificResource(resource2, -1);
        }
    }

}


