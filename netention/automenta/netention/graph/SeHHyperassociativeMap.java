/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.graph;

/******************************************************************************
 *                                                                             *
 *  Copyright: (c) Syncleus, Inc.                                              *
 *                                                                             *
 *  You may redistribute and modify this source code under the terms and       *
 *  conditions of the Open Source Community License - Type C version 1.0       *
 *  or any later version as published by Syncleus, Inc. at www.syncleus.com.   *
 *  There should be a copy of the license included with this file. If a copy   *
 *  of the license is not included you are granted no right to distribute or   *
 *  otherwise use this file except through a legal and valid license. You      *
 *  should also contact Syncleus, Inc. at the information below if you cannot  *
 *  find a license:                                                            *
 *                                                                             *
 *  Syncleus, Inc.                                                             *
 *  2604 South 12th Street                                                     *
 *  Philadelphia, PA 19148                                                     *
 *                                                                             *
 ******************************************************************************/
import automenta.spacegraph.math.linalg.Vec3f;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import com.syncleus.dann.*;
import com.syncleus.dann.graph.*;
import com.syncleus.dann.graph.drawing.GraphDrawer;
import com.syncleus.dann.graph.drawing.hyperassociativemap.HyperassociativeMap;
import com.syncleus.dann.graph.topological.Topography;
import com.syncleus.dann.math.Vector;
import org.apache.log4j.Logger;

/**
 * TODO verify that anchor functionality works
 * @param <G>
 * @param <N>
 */
public class SeHHyperassociativeMap<G extends Graph<N, ?>, N> implements GraphDrawer<G, N> {

    private static final double REPULSIVE_WEAKNESS = 2.0;
    private static final double ATTRACTION_STRENGTH = 4.0;
    private static final double DEFAULT_LEARNING_RATE = 0.4;
    private static final double DEFAULT_MAX_MOVEMENT = 0.0;
    private static final double DEFAULT_TOTAL_MOVEMENT = 0.0;
    private static final double DEFAULT_ACCEPTABLE_DISTANCE_FACTOR = 0.75;
    //private static final double EQUILIBRIUM_DISTANCE = 1.0;
    private static final double EQUILIBRIUM_ALIGNMENT_FACTOR = 0.005;
    private static final double LEARNING_RATE_INCREASE_FACTOR = 0.9;
    private static final double LEARNING_RATE_PROCESSING_ADJUSTMENT = 1.01;
    private final G graph;
    private final int dimensions;
    private final ExecutorService threadExecutor;
    private static final Logger LOGGER = Logger.getLogger(HyperassociativeMap.class);
    private Map<N, Vector> coordinates = Collections.synchronizedMap(new HashMap<N, Vector>());
    private static final Random RANDOM = new Random();
    private final boolean useWeights;
    private double equilibriumDistance;
    private double learningRate = DEFAULT_LEARNING_RATE;
    private double maxMovement = DEFAULT_MAX_MOVEMENT;
    private double totalMovement = DEFAULT_TOTAL_MOVEMENT;
    private double acceptableDistanceFactor = DEFAULT_ACCEPTABLE_DISTANCE_FACTOR;
    private Map<N, Vector> anchors = new WeakHashMap();

    public void anchor(N n, Vec3f vf) {
        Vector v = new Vector(vf.x(), vf.y(), vf.z());
        anchors.put(n, v);
    }

    public void unAnchor(N n) {
        anchors.remove(n);
    }

    /**
     * TODO use a parameterized randomization function
     * @return
     */
    private Vector newRandomPosition() {
        Vector v = new Vector(getDimensions());
        for (int i = 1; i <= getDimensions(); i++) {
            v.setCoordinate(Math.random(), i);
        }
        return v;
    }

    private class Align implements Callable<Vector> {

        private final N node;
        private final List<N> nodesToProcess;

        public Align(final N backingNode, List<N> nodesToProcess) {
            this.node = backingNode;
            this.nodesToProcess = nodesToProcess;
        }

        @Override
        public Vector call() {
            return align(this.node, nodesToProcess);
        }
    }

    public SeHHyperassociativeMap(final G ourGraph, final int ourDimensions, final double ourEquilibriumDistance, final boolean shouldUseWeights, final ExecutorService ourThreadExecutor) {
        if (ourGraph == null) {
            throw new IllegalArgumentException("Graph can not be null");
        }
        if (ourDimensions <= 0) {
            throw new IllegalArgumentException("ourDimensions must be 1 or more");
        }

        this.graph = ourGraph;
        this.dimensions = ourDimensions;
        this.threadExecutor = ourThreadExecutor;
        this.equilibriumDistance = ourEquilibriumDistance;
        this.useWeights = shouldUseWeights;

        //refresh all nodes
        for (final N node : this.graph.getNodes()) {
            this.coordinates.put(node, randomCoordinates(this.dimensions));
        }
    }

    public SeHHyperassociativeMap(final G ourGraph, final int ourDimensions, final double ourEquilibriumDistance, final boolean shouldUseWeights) {
        this(ourGraph, ourDimensions, ourEquilibriumDistance, shouldUseWeights, null);
    }

    @Override
    public G getGraph() {
        return this.graph;
    }

    public double getEquilibriumDistance() {
        return this.equilibriumDistance;
    }

    public void setEquilibriumDistance(final double newEquilbirumDistance) {
        this.equilibriumDistance = newEquilbirumDistance;
    }

    public void resetLearning() {
        this.learningRate = DEFAULT_LEARNING_RATE;
        this.maxMovement = DEFAULT_TOTAL_MOVEMENT;
        this.totalMovement = DEFAULT_TOTAL_MOVEMENT;
        this.acceptableDistanceFactor = DEFAULT_ACCEPTABLE_DISTANCE_FACTOR;
    }

    @Override
    public void reset() {
        this.resetLearning();
        //randomize all nodes
        for (final N node : this.coordinates.keySet()) {
            this.coordinates.put(node, randomCoordinates(this.dimensions));
        }
    }

    @Override
    public boolean isAlignable() {
        return true;
    }

    @Override
    public boolean isAligned() {
        if (this.isAlignable()) {
            return ((this.maxMovement < EQUILIBRIUM_ALIGNMENT_FACTOR * this.equilibriumDistance) && (this.maxMovement > DEFAULT_MAX_MOVEMENT));
        } else {
            return false;
        }
    }

    private double getAverageMovement() {
        return this.totalMovement / ((double) Topography.getOrder(this.graph));
    }

    @Override
    public void align() {
        if (graph.getNodes() == null) {
            return;
        }
        List<N> nodesToProcess = new LinkedList(graph.getNodes());
        if (nodesToProcess.isEmpty()) {
            return;
        }

        //refresh all nodes
        if (!this.coordinates.keySet().equals(this.graph.getNodes())) {
            final Map<N, Vector> newCoordinates = new HashMap<N, Vector>();

            for (final N node : nodesToProcess) {
                if (this.coordinates.containsKey(node)) {
                    newCoordinates.put(node, this.coordinates.get(node));
                } else {
                    newCoordinates.put(node, randomCoordinates(this.dimensions));
                }
            }
            this.coordinates = Collections.synchronizedMap(newCoordinates);
        }

        this.totalMovement = DEFAULT_TOTAL_MOVEMENT;
        this.maxMovement = DEFAULT_MAX_MOVEMENT;
        Vector center;
        if (this.threadExecutor != null) {
            //align all nodes in parallel
            final List<Future<Vector>> futures = this.submitFutureAligns(nodesToProcess);

            //wait for all nodes to finish aligning and calculate new sum of all the points
            try {
                center = this.waitAndProcessFutures(futures);
            } catch (InterruptedException caught) {
                LOGGER.warn("waitAndProcessFutures was unexpectidy interupted", caught);
                throw new UnexpectedInterruptedException("Unexpected interuption. Get should block indefinately", caught);
            }
        } else {
            center = this.processLocally(nodesToProcess);
        }

        LOGGER.debug("maxMove: " + this.maxMovement + ", Average Move: " + this.getAverageMovement());

        //divide each coordinate of the sum of all the points by the number of
        //nodes in order to calculate the average point, or center of all the
        //points
        for (int dimensionIndex = 1; dimensionIndex <= this.dimensions; dimensionIndex++) {
            center = center.setCoordinate(center.getCoordinate(dimensionIndex) / ((double) this.graph.getNodes().size()), dimensionIndex);
        }

        this.recenterNodes(center);

        for (N node : anchors.keySet()) {
            //TODO make a Vector.set(Vector v) method
            getCoordinates().get(node).setCoordinate(anchors.get(node).getCoordinate(1), 1);
            if (getDimensions() > 1) {
                getCoordinates().get(node).setCoordinate(anchors.get(node).getCoordinate(2), 2);
            }
            if (getDimensions() > 2) {
                getCoordinates().get(node).setCoordinate(anchors.get(node).getCoordinate(3), 3);
            }
        }

    }

    @Override
    public int getDimensions() {
        return this.dimensions;
    }

    @Override
    public Map<N, Vector> getCoordinates() {
        return Collections.unmodifiableMap(this.coordinates);
    }

    private void recenterNodes(final Vector center) {
        if ((graph.getNodes() == null) || (this.coordinates == null)) {
            return;
        }

        for (final N node : new LinkedList<N>(this.graph.getNodes())) {
            //TODO this is hackish
            try {
                this.coordinates.put(node, this.coordinates.get(node).calculateRelativeTo(center));
            } catch (NullPointerException e) {
            }
        }
    }

    public boolean isUsingWeights() {
        return this.useWeights;
    }

    Map<N, Double> getNeighbors(final N nodeToQuery) {
        final Map<N, Double> neighbors = new HashMap<N, Double>();
        for (final Edge<N> neighborEdge : this.graph.getAdjacentEdges(nodeToQuery)) {
            final Double currentWeight = ((neighborEdge instanceof Weighted) && this.useWeights ? ((Weighted) neighborEdge).getWeight() : this.equilibriumDistance);
            for (final N neighbor : neighborEdge.getNodes()) {
                if (!neighbor.equals(nodeToQuery)) {
                    neighbors.put(neighbor, currentWeight);
                }
            }
        }
        return neighbors;
    }

    private Vector align(final N nodeToAlign, List<N> nodesToProcess) {
        //calculate equilibrium with neighbors
        final Vector location = this.coordinates.get(nodeToAlign);
        final Map<N, Double> neighbors = this.getNeighbors(nodeToAlign);

        {
            Vector compositeVector = new Vector(location.getDimensions());
            for (final Entry<N, Double> neighborEntry : neighbors.entrySet()) {
                final N neighbor = neighborEntry.getKey();
                final double associationEquilibriumDistance = neighborEntry.getValue();

                Vector neighborVector;
                //TODO this is hackish..
                try {
                    neighborVector = this.coordinates.get(neighbor).calculateRelativeTo(location);
                } catch (NullPointerException e) {
                    continue;
                }

                if (Math.abs(neighborVector.getDistance()) > associationEquilibriumDistance) {
                    double newDistance = Math.pow(Math.abs(neighborVector.getDistance()) - associationEquilibriumDistance, ATTRACTION_STRENGTH);
                    if (Math.abs(newDistance) > Math.abs(Math.abs(neighborVector.getDistance()) - associationEquilibriumDistance)) {
                        newDistance = Math.copySign(Math.abs(Math.abs(neighborVector.getDistance()) - associationEquilibriumDistance), newDistance);
                    }
                    newDistance *= this.learningRate;
                    neighborVector = neighborVector.setDistance(Math.signum(neighborVector.getDistance()) * newDistance);
                } else {
                    double newDistance = -equilibriumDistance * atanh((associationEquilibriumDistance - Math.abs(neighborVector.getDistance())) / associationEquilibriumDistance);
                    if (Math.abs(newDistance) > Math.abs(associationEquilibriumDistance - Math.abs(neighborVector.getDistance()))) {
                        newDistance = -equilibriumDistance * (associationEquilibriumDistance - Math.abs(neighborVector.getDistance()));
                    }
                    newDistance *= this.learningRate;
                    neighborVector = neighborVector.setDistance(Math.signum(neighborVector.getDistance()) * newDistance);
                }
                compositeVector = compositeVector.add(neighborVector);
            }
            //calculate repulsion with all non-neighbors
            for (final N node : nodesToProcess) {
                //TODO this is hackish
                try {
                    if ((!neighbors.containsKey(node)) && (node != nodeToAlign) && (!this.graph.getAdjacentNodes(node).contains(nodeToAlign))) {
                        Vector nodeVector;
                        if (coordinates.get(node) == null) {
                            nodeVector = this.newRandomPosition();
                        } else {
                            nodeVector = this.coordinates.get(node).calculateRelativeTo(location);
                        }
                        double newDistance = -equilibriumDistance / Math.pow(nodeVector.getDistance(), REPULSIVE_WEAKNESS);
                        if (Math.abs(newDistance) > Math.abs(this.equilibriumDistance)) {
                            newDistance = Math.copySign(this.equilibriumDistance, newDistance);
                        }
                        newDistance *= this.learningRate;
                        nodeVector = nodeVector.setDistance(newDistance);
                        compositeVector = compositeVector.add(nodeVector);
                    }
                } catch (NullPointerException e) {
                }
            }

            Vector newLocation = location.add(compositeVector);
            final Vector oldLocation = this.coordinates.get(nodeToAlign);
            double moveDistance = Math.abs(newLocation.calculateRelativeTo(oldLocation).getDistance());
            if (moveDistance > this.equilibriumDistance * this.acceptableDistanceFactor) {
                final double newLearningRate = ((this.equilibriumDistance * this.acceptableDistanceFactor) / moveDistance);
                if (newLearningRate < this.learningRate) {
                    this.learningRate = newLearningRate;
                    LOGGER.debug("learning rate: " + this.learningRate);
                } else {
                    this.learningRate *= LEARNING_RATE_INCREASE_FACTOR;
                    LOGGER.debug("learning rate: " + this.learningRate);
                }

                newLocation = oldLocation;
                moveDistance = DEFAULT_TOTAL_MOVEMENT;
            }

            if (moveDistance > this.maxMovement) {
                this.maxMovement = moveDistance;
            }
            this.totalMovement += moveDistance;

            if (!anchors.containsKey(nodeToAlign)) {
                this.coordinates.put(nodeToAlign, newLocation);
            }


            return newLocation;
        }
    }

    /**
     * Obtains a Vector with RANDOM coordinates for the specified number of
     * dimensions.
     *
     * @param dimensions Number of dimensions for the RANDOM Vector
     * @return New RANDOM Vector
     * @since 1.0
     */
    public static Vector randomCoordinates(final int dimensions) {
        final double[] randomCoords = new double[dimensions];
        for (int randomCoordsIndex = 0; randomCoordsIndex < dimensions; randomCoordsIndex++) {
            randomCoords[randomCoordsIndex] = (RANDOM.nextDouble() * 2.0) - 1.0;
        }

        return new Vector(randomCoords);
    }

    private static double atanh(final double value) {
        final double oneHalf = 0.5; //for checkstyle.
        return oneHalf * Math.log(Math.abs((value + 1.0) / (1.0 - value)));
    }

    private List<Future<Vector>> submitFutureAligns(final List<N> nodesToProcess) {
        final ArrayList<Future<Vector>> futures = new ArrayList<Future<Vector>>();
        for (final N node : this.graph.getNodes()) {
            futures.add(this.threadExecutor.submit(new Align(node, nodesToProcess)));
        }
        return futures;
    }

    private Vector processLocally(List<N> nodesToProcess) {
        Vector pointSum = new Vector(this.dimensions);
        //List<N> nodesToProcess = new LinkedList(graph.getNodes())

        for (final N node : nodesToProcess) {
            Vector newPoint = this.align(node, nodesToProcess);
            for (int dimensionIndex = 1; dimensionIndex <= this.dimensions; dimensionIndex++) {
                pointSum = pointSum.setCoordinate(pointSum.getCoordinate(dimensionIndex) + newPoint.getCoordinate(dimensionIndex), dimensionIndex);
            }
        }
        if (this.learningRate * LEARNING_RATE_PROCESSING_ADJUSTMENT < DEFAULT_LEARNING_RATE) {
            final double acceptableDistanceAdjustment = 0.1;
            if (this.getAverageMovement() < (this.equilibriumDistance * this.acceptableDistanceFactor * acceptableDistanceAdjustment)) {
                this.acceptableDistanceFactor *= LEARNING_RATE_INCREASE_FACTOR;
            }
            this.learningRate *= LEARNING_RATE_PROCESSING_ADJUSTMENT;
            LOGGER.debug("learning rate: " + this.learningRate + ", acceptableDistanceFactor: " + this.acceptableDistanceFactor);
        }




        return pointSum;
    }

    private Vector waitAndProcessFutures(final List<Future<Vector>> futures) throws InterruptedException {
        //wait for all nodes to finish aligning and calculate new center point
        Vector pointSum = new Vector(this.dimensions);


        try {
            for (final Future<Vector> future : futures) {
                final Vector newPoint = future.get();


                for (int dimensionIndex = 1; dimensionIndex
                        <= this.dimensions; dimensionIndex++) {
                    pointSum = pointSum.setCoordinate(pointSum.getCoordinate(dimensionIndex) + newPoint.getCoordinate(dimensionIndex), dimensionIndex);


                }
            }
        } catch (ExecutionException caught) {
            LOGGER.error("Align had an unexpected problem executing.", caught);


            throw new UnexpectedDannError("Unexpected execution exception. Get should block indefinitely", caught);


        }
        if (this.learningRate * LEARNING_RATE_PROCESSING_ADJUSTMENT < DEFAULT_LEARNING_RATE) {
            final double acceptableDistanceAdjustment = 0.1;


            if (this.getAverageMovement() < (this.equilibriumDistance * this.acceptableDistanceFactor * acceptableDistanceAdjustment)) {
                this.acceptableDistanceFactor = this.maxMovement * 2.0;


            }
            this.learningRate *= LEARNING_RATE_PROCESSING_ADJUSTMENT;
            LOGGER.debug("learning rate: " + this.learningRate + ", acceptableDistanceFactor: " + this.acceptableDistanceFactor);


        }
        return pointSum;


    }

    public void randomize(double uniformDimensionLength) {
        List<N> l = new LinkedList<N>(getCoordinates().keySet());


        for (N n : l) {
            Vector v = (Vector) getCoordinates().get(n);


            for (int i = 1; i
                    <= v.getDimensions(); i++) {
                double c = (Math.random() * 2.0 - 1.0) * uniformDimensionLength;
                v.setCoordinate(c, i);

            }
        }
    }
}
