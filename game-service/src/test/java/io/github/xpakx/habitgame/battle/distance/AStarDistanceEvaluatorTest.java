package io.github.xpakx.habitgame.battle.distance;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.Position;
import io.github.xpakx.habitgame.battle.TerrainType;
import io.github.xpakx.habitgame.expedition.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class AStarDistanceEvaluatorTest {
    private DistanceEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new AStarDistanceEvaluator();
    }

    @Test
    void shouldCalculateDistanceAs0ForSameTargetAndStart() {
        int result = evaluator.shortestPath(new ArrayList<>(), getPosition(1,1), getPosition(1,1), getBattle(5,5));
        assertThat(result, equalTo(0));
    }

    private Battle getBattle(int width, int height) {
        Battle battle = new Battle();
        battle.setWidth(width);
        battle.setHeight(height);
        return battle;
    }

    private Position getPosition(int x, int y) {
        return getPosition(x, y, 1, false);
    }

    private Position getPosition(int x, int y, int weight) {
        return getPosition(x, y, weight, false);
    }

    private Position getPosition(int x, int y, int weight, boolean blocked) {
        Position position = new Position();
        position.setX(x);
        position.setY(y);
        if(weight>1 || blocked) {
            position.setTerrain(getTerrain(weight));
            position.getTerrain().setBlocked(blocked);
        }
        return position;
    }
    private Position getObstacle(int x, int y) {
        return getPosition(x, y, 1, true);
    }

    private TerrainType getTerrain(int weight) {
        TerrainType type = new TerrainType();
        type.setMove(weight);
        return type;
    }

    private Position getShip(int x, int y) {
        Ship ship = new Ship();
        Position position = new Position();
        position.setX(x);
        position.setY(y);
        position.setShip(ship);
        return position;
    }

    @Test
    void shouldCalculateDistanceAs1ForNeighbor() {
        int result = evaluator.shortestPath(new ArrayList<>(), getPosition(1,1), getPosition(2,1), getBattle(5,5));
        assertThat(result, equalTo(1));
    }

    @Test
    void shouldCalculateDistanceAs2ForDiagonalNeighbor() {
        int result = evaluator.shortestPath(new ArrayList<>(), getPosition(1,1), getPosition(2,2), getBattle(5,5));
        assertThat(result, equalTo(2));
    }

    @Test
    void shouldCalculateDistanceAs2WithoutWeights() {
        int result = evaluator.shortestPath(new ArrayList<>(), getPosition(1,1), getPosition(3,1), getBattle(5,5));
        assertThat(result, equalTo(2));
    }

    @Test
    void shouldCalculateDistanceAs3WithIncreasedWeights() {
        List<Position> positions = new ArrayList<>();
        positions.add(getPosition(2,0, 2));
        positions.add(getPosition(2,1, 2));
        positions.add(getPosition(2,2, 2));
        int result = evaluator.shortestPath(positions, getPosition(1,1), getPosition(3,1), getBattle(5,5));
        assertThat(result, equalTo(3));
    }

    @Test
    void shouldNotTakeShortcutByDiagonal() {
        List<Position> positions = new ArrayList<>();
        positions.add(getPosition(2,0, 2));
        positions.add(getPosition(2,1, 2));
        int result = evaluator.shortestPath(positions, getPosition(1,1), getPosition(3,1), getBattle(5,5));
        assertThat(result, equalTo(3));
    }

    @Test
    void shouldOmitShortPathWithCostlyField() {
        List<Position> positions = new ArrayList<>();
        positions.add(getPosition(2,1, 50));
        int result = evaluator.shortestPath(positions, getPosition(1,1), getPosition(3,1), getBattle(5,5));
        assertThat(result, equalTo(4));
    }

    @Test
    void shouldOmitObstacle() {
        List<Position> positions = new ArrayList<>();
        positions.add(getObstacle(2,1));
        int result = evaluator.shortestPath(positions, getPosition(1,1), getPosition(3,1), getBattle(5,5));
        assertThat(result, equalTo(4));
    }

    @Test
    void shouldNotReachFieldSurroundedByObstacles() {
        List<Position> positions = new ArrayList<>();
        positions.add(getObstacle(2,1));
        positions.add(getObstacle(4,1));
        positions.add(getObstacle(3,0));
        positions.add(getObstacle(3,2));
        int result = evaluator.shortestPath(positions, getPosition(1,1), getPosition(3,1), getBattle(5,5));
        assertThat(result, equalTo(-1));
    }

    @Test
    void shouldNotTakeShortcutOutsideTheMap() {
        List<Position> positions = new ArrayList<>();
        positions.add(getPosition(1,0, 50));
        positions.add(getPosition(1,1, 50));
        int result = evaluator.shortestPath(positions, getPosition(0,0), getPosition(2,0), getBattle(5,5));
        assertThat(result, equalTo(6));
    }

    @Test
    void shouldNotReachBlockedField() {
        List<Position> positions = new ArrayList<>();
        positions.add(getObstacle(3,1));
        int result = evaluator.shortestPath(positions, getPosition(1,1), getPosition(3,1), getBattle(5,5));
        assertThat(result, equalTo(-1));
    }

    @Test
    void shouldNotReachFieldOutsideTheBoard() {
        int result = evaluator.shortestPath(new ArrayList<>(), getPosition(1,1), getPosition(5,5), getBattle(5,5));
        assertThat(result, equalTo(-1));
    }

    @Test
    void shouldCalculateDistanceIfThereIsAShipAtStartPosition() {
        List<Position> positions = new ArrayList<>();
        positions.add(getShip(1,1));
        int result = evaluator.shortestPath(positions, getPosition(1,1), getPosition(2,2), getBattle(5,5));
        assertThat(result, equalTo(2));
    }

}