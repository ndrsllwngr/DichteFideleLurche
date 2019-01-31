package model.board;

public class Sea extends Tile {

    private PortType portType = null;

    public Sea() {

    }

    /**
     * Return the type of this port
     *
     * @return porttype
     */
    public PortType getPort() {
        return portType;
    }

    /**
     * Set a type of port
     *
     * @param portType "GENERIC", "BRICK", "LUMBER", "ORE", "GRAIN" or "WOOL"
     */
    public void setPort(PortType portType) {
        if (this.portType == null) {
            this.portType = portType;
        }
    }
}


