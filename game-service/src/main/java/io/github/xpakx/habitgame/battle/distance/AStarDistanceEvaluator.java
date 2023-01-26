package io.github.xpakx.habitgame.battle.distance;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.Position;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

@Service
public class AStarDistanceEvaluator implements DistanceEvaluator {
    @Override
    public int shortestPath(List<Position> positions, Position startPosition, Position targetPosition, Battle battle) {
        if(battle.getWidth() == 1 && battle.getHeight() == 1) {
            return 0;
        }
        boolean[][] check = new boolean[battle.getWidth()][battle.getHeight()];
        Board board = new Board(positions, battle, startPosition);
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a.expected));
        queue.add(new Node(startPosition, targetPosition, 0));
        while(!queue.isEmpty()) {
            Node next = queue.remove();
            if(check[next.getX()][next.getY()]) {
                continue;
            }
            check[next.getX()][next.getY()] = true;
            if(board.isBlocked(next.position)) {
                continue;
            }
            if(next.samePosition(targetPosition)) {
                return next.dist;
            }
            addNeighboursToQueue(targetPosition, board, queue, next, battle);
        }
        return -1;
    }

    private void addNeighboursToQueue(Position targetPosition, Board board, PriorityQueue<Node> queue, Node next, Battle battle) {
        if(next.getX()-1>=0) {
            Position position = next.step(-1, 0);
            queue.add(new Node(position, targetPosition, next.dist + board.getCost(position)));
        }
        if(next.getX()+1 < battle.getWidth()) {
            Position position = next.step(1, 0);
            queue.add(new Node(position, targetPosition, next.dist + board.getCost(position)));
        }
        if(next.getY()-1>=0) {
            Position position = next.step(0, -1);
            queue.add(new Node(position, targetPosition, next.dist + board.getCost(position)));
        }
        if(next.getY()+1 < battle.getHeight()) {
            Position position = next.step(0, 1);
            queue.add(new Node(position, targetPosition, next.dist + board.getCost(position)));
        }
    }

    private static class Node {
        int dist;
        int expected;
        Position position;
        public Node(Position position, Position target, int dist) {
            this.dist = dist;
            this.position = new Position();
            this.position.setX(position.getX());
            this.position.setY(position.getY());
            this.expected = target.getX() - this.position.getX() + target.getY() - this.position.getY() + dist;
        }

        public boolean samePosition(Position position) {
            return Objects.equals(this.position.getX(), position.getX()) && Objects.equals(this.position.getY(), position.getY());
        }

        public Position step(int x, int y) {
            Position newPosition = new Position();
            newPosition.setX(position.getX()+x);
            newPosition.setY(position.getY()+y);
            return newPosition;
        }

        public int getX() {
            return position.getX();
        }

        public int getY() {
            return position.getY();
        }
    }

    private static class Board {
        boolean[][] blocked;
        int[][] cost;

        public Board(List<Position> positions, Battle battle, Position startPosition) {
            blocked = new boolean[battle.getWidth()][battle.getHeight()];
            cost = new int[battle.getWidth()][battle.getHeight()];
            for(int i=0; i<battle.getWidth(); i++){
                for(int j=0; j<battle.getHeight(); j++){
                    blocked[i][j] = false;
                    cost[i][j] = 1;
                }
            }
            for(Position pos : positions) {
                blocked[pos.getX()][pos.getY()] = isNonMovingShipOnPosition(startPosition, pos) || isBlockingTerrainOnPosition(pos);
                if(isTerrainOnPosition(pos)) {
                    cost[pos.getX()][pos.getY()] = pos.getTerrain().getMove();
                }
            }
        }

        private boolean isNonMovingShipOnPosition(Position startPosition, Position pos) {
            return isShipOnPosition(pos) && isNotStartingPosition(startPosition, pos);
        }

        private boolean isNotStartingPosition(Position startPosition, Position pos) {
            return !Objects.equals(pos.getX(), startPosition.getX()) && !Objects.equals(pos.getY(), startPosition.getY());
        }

        private boolean isBlockingTerrainOnPosition(Position pos) {
            return isTerrainOnPosition(pos) && pos.getTerrain().isBlocked();
        }

        private boolean isBlocked(Position position) {
            return blocked[position.getX()][position.getY()];
        }

        private int getCost(Position position) {
            return cost[position.getX()][position.getY()];
        }

        private boolean isShipOnPosition(Position pos) {
            return pos.getShip() != null;
        }

        private boolean isTerrainOnPosition(Position pos) {
            return pos.getTerrain() != null;
        }

    }
}
