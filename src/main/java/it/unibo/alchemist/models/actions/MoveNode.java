package it.unibo.alchemist.models.actions;

import it.unibo.alchemist.model.*;
import it.unibo.alchemist.model.actions.AbstractAction;
import it.unibo.alchemist.models.layers.PheromoneLayerImpl;
import it.unibo.alchemist.models.myEnums.Directions;
import it.unibo.alchemist.models.nodeProperty.NodeWithDirection;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;


public class MoveNode<P extends Position<P> & Position2D<P>> extends AbstractAction<Double> {
    private final Node<Double> node;
    private final Environment<Double, P> environment;
    private final PheromoneLayerImpl<P> pheromoneLayer;
    private final Double sniffDistance;
    private final Double wiggleAngle;
    private final Double wiggleBias;
    private final Double sniffAngle;
    private final Double sniffThreshold;
    private final Molecule molecule;
    private final Map<P, Double> pheromoneMap;



    public MoveNode(final Node<Double> node, final Environment<Double, P> environment, final double distance,
                    final Molecule molecule, final Double wiggleAngle, final Double wiggleBias,
                    final Double sniffAngle, final Double sniffThreshold) {
        super(node);
        this.node = node;
        this.environment = environment;
        this.sniffDistance = distance;
        this.molecule = molecule;
        this.wiggleAngle = wiggleAngle;
        this.wiggleBias = wiggleBias;
        this.sniffAngle = sniffAngle;
        this.sniffThreshold = sniffThreshold;
        this.pheromoneLayer = (PheromoneLayerImpl<P>) environment.getLayer(molecule).get();
        this.pheromoneMap = pheromoneLayer.getMap();
    }

    @Override
    public Action<Double> cloneAction(final Node<Double> node, final Reaction<Double> reaction) {
        return new MoveNode<>(node, environment, sniffDistance, molecule, wiggleAngle, wiggleBias, sniffAngle, sniffThreshold);
    }

    @Override
    public void execute() {
        var currentPosition = environment.getPosition(node);
        var pos = pheromoneLayer.adaptPosition(currentPosition);
        var possibleDirections = getNeighborhood(pos).stream()
                .filter(x -> pheromoneMap.containsKey(x) && pheromoneMap.get(x)>sniffThreshold)
                .toList();

        /*if (possibleDirections.isEmpty()){
            var newdir = Directions.DEFAULT.getDirection(new Random().nextInt(8));
            updateNodeDirection(node, newdir);
            environment.moveNodeToPosition(node, environment.makePosition(
                    (newdir.getX() * sniffDistance) + currentPosition.getX(),
                    (newdir.getY() * sniffDistance) + currentPosition.getY()));
        } else {
            var newPosition = findBestPosition(possibleDirections, currentPosition, getCurrentNodeDirection(node));
            environment.moveNodeToPosition(node, newPosition);
        }*/
        Optional<P> maxPosition = possibleDirections.stream().filter(pheromoneMap::containsKey)
                .max(Comparator.comparingDouble(pheromoneMap::get));

        if (maxPosition.isPresent()) {
            var xx = maxPosition.get().getX() - pos.getX();
            var yy = maxPosition.get().getY() - pos.getY();
            //var pp = environment.makePosition(pos.getX()+xx, pos.getY()+yy);
            var pp2 = environment.makePosition(currentPosition.getX()+xx, currentPosition.getY()+yy);
            maxPosition.ifPresent(p -> environment.moveNodeToPosition(node, pp2));
        } else {
            var newDirection = Directions.DEFAULT.getDirection(new Random().nextInt(8));
            updateNodeDirection(node, newDirection);
            environment.moveNodeToPosition(node, environment.makePosition(
                    (newDirection.getX() * sniffDistance) + currentPosition.getX(),
                    (newDirection.getY() * sniffDistance) + currentPosition.getY()));
        }
        //maxPosition.ifPresent(p -> environment.moveNodeToPosition(node, p));

    }

    private P findBestPosition(final List<P> neighborhoodPositions, final P nodePosition, final Directions direction){
        var directionValue = environment.makePosition((direction.getX()*sniffDistance)+nodePosition.getX(),
                (direction.getY()*sniffDistance) + nodePosition.getY());

        var possiblePositions = getPositionsInAngle(neighborhoodPositions, directionValue, nodePosition);
        Optional<P> maxPosition = possiblePositions.stream().filter(pheromoneMap::containsKey)
                .max(Comparator.comparingDouble(pheromoneMap::get));
        if (maxPosition.isPresent()){
            return environment.makePosition(maxPosition.get().getX(), maxPosition.get().getY());
        }
        var newdir = Directions.DEFAULT.getDirection(new Random().nextInt(8));
        updateNodeDirection(node, newdir);
        return environment.makePosition((newdir.getX()*sniffDistance)+nodePosition.getX(),
                (newdir.getY()*sniffDistance) + nodePosition.getY());
    }

    @Override
    public Context getContext() {
        return Context.NEIGHBORHOOD; // it is local because it changes only the local molecule
    }

    private List<P> getNeighborhood(final P position) {
        var nodes = environment.getNodes().stream().map(environment::getPosition).toList();
        final var x = position.getX();
        final var y = position.getY();
        final double[] xs = DoubleStream.of(x - sniffDistance, x, x + sniffDistance).toArray();
        final double[] ys = DoubleStream.of(y - sniffDistance, y, y + sniffDistance).toArray();
        return Arrays.stream(xs).boxed()
                .flatMap(x1 -> Arrays.stream(ys).boxed().map(y1 -> environment.makePosition(x1, y1)))
                .filter(p -> !p.equals(position))
                .filter(p -> !nodes.contains(p))
                .collect(Collectors.toList());
    }

    private Directions getCurrentNodeDirection(final Node<Double> node){
        var nodeProperty = (NodeWithDirection<Double>) node.getProperties().get(node.getProperties().size()-1);
        return nodeProperty.getDirection();
    }

    /**
     *
     * @param allPositions
     * @param forwardPosition davanti
     * @param center il mio nodo
     * @return
     */
    private List<P> getPositionsInAngle(List<P> allPositions, P forwardPosition, P center) {
        List<P> positionsInAngle = new ArrayList<>();
        for (P other : allPositions) {
            if (!forwardPosition.equals(other)) {
                double a = calculateAngle(center.getX(), center.getY(), forwardPosition.getX(), forwardPosition.getY(), other.getX(), other.getY());
                if (Math.abs(a) <= wiggleAngle || Math.abs(a) >= 360 - wiggleAngle) {
                    positionsInAngle.add(other);
                }
            }
        }
        return positionsInAngle;
    }

    private double calculateAngle(double centerX, double centerY, double x1, double y1, double x2, double y2) {
        double angle1 = Math.atan2(y1 - centerY, x1 - centerX);
        double angle2 = Math.atan2(y2 - centerY, x2 - centerX);

        double angle = angle2 - angle1;

        if (angle < -Math.PI) {
            angle += 2 * Math.PI;
        } else if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }

        return Math.toDegrees(angle);
    }

    private void updateNodeDirection(final Node<Double> node, final Directions directions){
        NodeWithDirection<Double> pNodeWithDirection = (NodeWithDirection<Double>) node.getProperties().get(1);
        pNodeWithDirection.setDirection(directions, node);
    }
}
