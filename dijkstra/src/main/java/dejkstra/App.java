package dejkstra;

import guru.nidi.graphviz.attribute.Arrow;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;
import static guru.nidi.graphviz.model.Link.to;

public class App {

    public static void main(String[] args) {
        int from = 0;
        int[][] graph = createRandomGraph(20, 2);
        long[] path = findPath(graph, from);
        Date now = new Date();

        File dir = new File("graphs/" + now );
        dir.mkdir();
        printGraphArray(graph, path, dir);
        printGraph(graph, dir);
        printPath(path, from, dir);
    }

    private static long[] findPath(int[][] graph, int from) {
        long[] distances = new long[graph.length];
        BitSet processed = new BitSet(graph.length);

        for (int i = 0; i < distances.length; i++) {
            distances[i] = i == from
                    ? createDistanceNote(from, 0)
                    : createDistanceNote(from, Integer.MIN_VALUE); // let's consider Integer.MIN_VALUE as infinity
        }

        while (true) {
            int vertex = findClosest(distances, processed);
            if (vertex == Integer.MIN_VALUE) break;

            for (int i = 0; i < graph[vertex].length; i++) {
                int edge = graph[vertex][i];
                if (edge == 0 || processed.get(i)) continue;
                int distance = getDistance(distances[i]);
                int newDistance = getDistance(distances[vertex]) + edge;
                if (distance == Integer.MIN_VALUE || newDistance < distance) {
                    distances[i] = createDistanceNote(vertex, newDistance);
                }
            }

            processed.set(vertex);
        }

        return distances;
    }

    private static int findClosest(long[] distances, BitSet processed) {
        int vertex = Integer.MIN_VALUE;
        int distance = Integer.MAX_VALUE;

        for (int i = 0; i < distances.length; i++) {
            if (processed.get(i)) continue;

            int d = getDistance(distances[i]);
            if (d > Integer.MIN_VALUE && d <= distance) {
                distance = d;
                vertex = i;
            }
        }

        return vertex;
    }

    private static long createDistanceNote(int vertex, int distance) {
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putInt(0, vertex);
        b.putInt(4, distance);
        return b.getLong();
    }

    private static int getVertex(long distanceNotice) {
        return (int) (distanceNotice >> 32);
    }

    private static int getDistance(long distanceNote) {
        return (int) distanceNote;
    }

    private static int[][] createRandomGraph(int size, int factor) {
        int[][] graph = new int[size][];
        for (int i = 0; i < size; i++) {
            graph[i] = new int[size];
        }

        Random random = new Random();

        for (int i = 0; i < size / 2 + 1; i++) {
            int quantity = random.nextInt(size / factor);

            for (int j = 0; j < quantity; j++) {
                int element = random.nextInt(size);
                if (element == i || graph[element] != null && graph[element][i] != 0) continue;
                int weight = random.nextInt(size * 2);
                graph[i][element] = weight;
                graph[element][i] = weight;

            }
        }

        return graph;
    }

    private static void printGraph(int[][] graph,  File dir) {
        MutableGraph g = mutGraph("graph").setDirected(true);
        for (int vertex = 0; vertex < graph.length; vertex++) {
            MutableNode node = mutNode(vertex + "");
            for (int edge = vertex; edge < graph.length; edge++) {
                if (graph[vertex][edge] > 0) {
                    Link link = to(mutNode(edge + ""))
                            .with(Label.of(graph[vertex][edge] + ""))
                            .with(Arrow.NONE);
                    node.addLink(link);
                }

                g.add(node);
            }

            int size = graph.length * 50;
            File picture = new File(dir, "graph.png");
            try {
                Graphviz.fromGraph(g).width(size).render(Format.PNG).toFile(picture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printPath(long[] path, int fromVertex, File dir) {
        MutableGraph g = mutGraph("path").setDirected(true);
        g.add(mutNode("paths from " + fromVertex));

        for (int i = 0; i < path.length; i++) {
            if (i == fromVertex) continue;
            int current = i;
            MutableNode node = mutNode(
                    (getDistance(path[i]) == Integer.MIN_VALUE
                            ? "\u221E"
                            : getDistance(path[i])) +
                            " to " + i
            );
            g.add(node);

            while (current != fromVertex) {
                current = getVertex(path[current]);
                if (getDistance(path[i]) == Integer.MIN_VALUE) break;
                MutableNode newNode = mutNode(current + " to " + i);
                Link link = to(newNode)
                        .with(Label.of(getDistance(path[i]) + ""))
                        .with(Arrow.NONE);
                node.addLink(link);
                node = newNode;
            }
        }

        int size = g.graphs().size() * 30;
        File picture = new File(dir, "path.png");
        try {
            Graphviz.fromGraph(g).width(size).render(Format.PNG).toFile(picture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printGraphArray(int[][] graph, long[] path, File dir) {
        File file = new File(dir,"text.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            for (int i = 0; i < graph.length; i++) {
                int[] line = graph[i];
                writer.write(i + ": ");

                for (int edge : line) {
                    if (edge != 0) {

                        writer.write(String.format("%2d ", edge));
                    } else {
                        writer.write("-- ");
                    }
                }
                writer.newLine();
            }

            writer.newLine();

            for (long point : path) {
                writer.write("v: " +
                        getVertex(point) +
                        " d: " +
                        (getDistance(point) == Integer.MIN_VALUE ? "\u221E" : getDistance(point)));
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
