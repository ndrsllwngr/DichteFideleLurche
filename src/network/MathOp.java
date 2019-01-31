package network;

public enum MathOp {
    ADD {
        /**
         * Add new value to old value
         *
         * @param o old int value
         * @param n new int value
         * @return sum of o and n (int)
         */
        @Override
        public int apply(int o, int n) {
            return o + n;
        }
    },
    SUBTRACT {
        /**
         * Subtract new value from old value
         * @param o old int value
         * @param n new int value
         * @return result of o-n (int)
         */
        @Override
        public int apply(int o, int n) {
            return o - n;
        }
    },
    SET {
        /**
         * Set old value to new value
         * @param o old int value
         * @param n new int value
         * @return n (new int value)
         */
        @Override
        public int apply(int o, int n) {
            return n;
        }
    };

    /**
     * Abstract method apply
     *
     * @param o old int value
     * @param n new int value
     * @return calculated int value
     */
    public abstract int apply(int o, int n);
}
