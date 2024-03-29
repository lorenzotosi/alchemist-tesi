package it.unibo.alchemist.models.myEnums;

/**
 * An enumeration representing different directions.
 * Each direction has associated coordinates (x, y) and neighboring directions.
 */
public enum Directions implements DirectionInfo {
    NORTH {
        @Override
        public Double getX() {
            return 0.0;
        }

        @Override
        public Double getY() {
            return 1.0;
        }

        @Override
        public Directions getMyLeft() {
            return NORTH_WEST;
        }

        @Override
        public Directions getMyRight() {
            return NORTH_EAST;
        }
    },
    NORTH_EAST {
        @Override
        public Double getX() {
            return 1.0;
        }

        @Override
        public Double getY() {
            return 1.0;
        }

        @Override
        public Directions getMyLeft() {
            return NORTH;
        }

        @Override
        public Directions getMyRight() {
            return EAST;
        }
    },
    NORTH_WEST {
        @Override
        public Double getX() {
            return -1.0;
        }

        @Override
        public Double getY() {
            return 1.0;
        }

        @Override
        public Directions getMyLeft() {
            return WEST;
        }

        @Override
        public Directions getMyRight() {
            return NORTH;
        }
    },
    SOUTH {
        @Override
        public Double getX() {
            return 0.0;
        }

        @Override
        public Double getY() {
            return -1.0;
        }

        @Override
        public Directions getMyLeft() {
            return SOUTH_EAST;
        }

        @Override
        public Directions getMyRight() {
            return SOUTH_WEST;
        }
    },
    SOUTH_EAST {
        @Override
        public Double getX() {
            return 1.0;
        }

        @Override
        public Double getY() {
            return -1.0;
        }

        @Override
        public Directions getMyLeft() {
            return EAST;
        }

        @Override
        public Directions getMyRight() {
            return SOUTH;
        }
    },
    SOUTH_WEST {
        @Override
        public Double getX() {
            return -1.0;
        }

        @Override
        public Double getY() {
            return -1.0;
        }

        @Override
        public Directions getMyLeft() {
            return SOUTH;
        }

        @Override
        public Directions getMyRight() {
            return WEST;
        }
    },
    EAST {
        @Override
        public Double getX() {
            return 1.0;
        }

        @Override
        public Double getY() {
            return 0.0;
        }

        @Override
        public Directions getMyLeft() {
            return NORTH_EAST;
        }

        @Override
        public Directions getMyRight() {
            return SOUTH_WEST;
        }
    },
    WEST {
        @Override
        public Double getX() {
            return -1.0;
        }

        @Override
        public Double getY() {
            return 0.0;
        }

        @Override
        public Directions getMyLeft() {
            return SOUTH_WEST;
        }

        @Override
        public Directions getMyRight() {
            return NORTH_WEST;
        }
    },
    DEFAULT {
        @Override
        public Double getX() {
            return 0.0;
        }

        @Override
        public Double getY() {
            return 0.0;
        }

        @Override
        public Directions getMyLeft() {
            return null;
        }

        @Override
        public Directions getMyRight() {
            return null;
        }
    };

    /**
     * Returns the direction based on the given number.
     *
     * @param number the number representing the direction
     * @return the direction corresponding to the number, or null if the number is invalid
     */
    public Directions getDirection(int number) {
        return switch (number) {
            case 0 -> NORTH;
            case 1 -> SOUTH;
            case 2 -> EAST;
            case 3 -> WEST;
            case 4 -> NORTH_EAST;
            case 5 -> NORTH_WEST;
            case 6 -> SOUTH_EAST;
            case 7 -> SOUTH_WEST;
            default -> null;
        };
    }
    @Override
    public abstract Double getX();
    @Override
    public abstract Double getY();
    @Override
    public abstract Directions getMyLeft();
    @Override
    public abstract Directions getMyRight();
}
