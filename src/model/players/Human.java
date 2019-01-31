package model.players;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Human extends Player {

    private static final Logger LOGGER = LogManager.getLogger(Human.class.getName());

    public Human(int id, String name, PColor color) {
        LOGGER.traceEntry(this.toString());
        this.setId(id);
        this.setName(name);
        this.setColor(color);
        printPlayer();
    }

}
