package it.unibo.alchemist.models.layer;

import it.unibo.alchemist.model.Layer;
import it.unibo.alchemist.model.Position2D;

import java.awt.*;


/**
 * Represents a pheromone layer in a grid-based model.
 * The layer allows depositing pheromone values at specific positions.
 *
 * @param <P> The type of position.
 */
public interface PheromoneLayer<P extends Position2D<P>> extends Layer<Double, P> {
    /**
     * Adds the specified pheromone value to the map at the given position.
     *
     * @param p The patch position
     * @param value The pheromone value associated to the grid position
     */
    void deposit(P p, Double value);

    /**
     * Evaporates the specified pheromone value at the given position.
     *
     * @param p The patch position
     * @param value The pheromone value associated to the grid position
     */
    void evaporate(P p, Double value);

    /**
     * Returns the bounds of the layer as a Rectangle object.
     *
     * @return the bounds of the layer as a Rectangle object
     */
    Rectangle getLayerBounds();
}
