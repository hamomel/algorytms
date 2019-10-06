package karaskal;

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
import java.util.*;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;
import static guru.nidi.graphviz.model.Link.to;

public class App {
    public static void main(String[] args) {

        int[][] array = createRandomGraph(20, 3);
        List<Edge> tree = findMinimalTree(array);
        printGraph(array, tree);
    }

    private static List<Edge> findMinimalTree(int[][] graph) {
        List<Edge> edges = getSortedEdges(graph);
        List<Edge> tree = new ArrayList<>();
        List<Set<Integer>> subtrees = new ArrayList<>();

        for (Edge edge : edges) {
            Set<Integer> tree1 = null;
            Set<Integer> tree2 = null;

            for (Set<Integer> subtree : subtrees) {
                if (subtree.contains(edge.v1)) {
                    tree1 = subtree;
                }

                if (subtree.contains(edge.v2)) {
                    tree2 = subtree;
                }
            }

            if (tree1 == null && tree2 == null){
                Set<Integer> newTree = new HashSet<>();
                newTree.add(edge.v1);
                newTree.add(edge.v2);
                subtrees.add(newTree);
            } else if (tree1 == tree2) {
                continue;
            } else if (tree1 == null) {
                tree2.add(edge.v1);
                tree2.add(edge.v2);
            } else if (tree2 == null) {
                tree1.add(edge.v1);
                tree1.add(edge.v2);
            } else {
                tree1.addAll(tree2);
                subtrees.remove(tree2);
            }

            tree.add(edge);
        }

        return tree;
    }

    private static List<Edge> getSortedEdges(int[][] graph) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            for (int j = i; j < graph[i].length; j++) {
                int weight = graph[i][j];
                if (weight != 0) sortedAdd(new Edge(i, j, weight), edges);
            }
        }

        return edges;
    }

    private static void sortedAdd(Edge edge, List<Edge> list) {
        list.add(edge);

        for (int i = list.size() - 2; i >= 0; i--) {
            Edge element = list.get(i);
            if (element.weight > list.get(i + 1).weight) {
                list.set(i, list.get(i + 1));
                list.set(i + 1, element);
            } else  {
                break;
            }
        }
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

    private static void printGraph(int[][] graph, List<Edge> tree) {
        MutableGraph g = mutGraph("example1").setDirected(true);

        for (int vertex = 0; vertex < graph.length; vertex++) {
            MutableNode node = mutNode(vertex + "");
            for (int edge = vertex; edge < graph.length; edge++) {
                if (graph[vertex][edge] > 0) {
                    Link link = null;

                    for (Edge e : tree) {
                        if ((e.v1 == vertex || e.v2 == vertex) && (e.v1 == edge || e.v2 == edge)) {
                            link = to(mutNode(edge + ""))
                                    .with(Label.of(graph[vertex][edge] + ""))
                                    .with(Arrow.NONE)
                                    .with(Color.RED);

                            break;
                        }
                    }

                    if (link == null) {
                        link = to(mutNode(edge + ""))
                                .with(Label.of(graph[vertex][edge] + ""))
                                .with(Arrow.NONE);
                    }
                    node.addLink(link);
                }
            }

            g.add(node);
        }

        Date now = new Date();
        File file = new File("graphs/" + now + "-text.txt");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            printGraphArray(graph, writer);
            printEdges(tree, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File picture = new File("graphs/" + now + "-pic.png");
        try {
            int size = graph.length * 30;
            Graphviz.fromGraph(g).width(size).render(Format.PNG).toFile(picture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printGraphArray(int[][] graph, BufferedWriter writer) throws IOException {
        for (int i = 0; i < graph.length; i++) {
            int[] line = graph[i];
            writer.write(i + ": ");

            for (int edge : line) {
                if (edge != 0) {

                    writer.write(String.format("%2d ",edge));
                } else {
                    writer.write("-- ");
                }
            }
            writer.newLine();
        }

        writer.newLine();
    }

    private static void printEdges(List<Edge> edges, BufferedWriter writer) throws IOException {
        for (Edge edge : edges) {
            writer.write("[ " + edge.v1 + " : " + edge.v2 + " w: " + edge.weight + " ]");
            writer.newLine();
        }
        writer.newLine();
    }
}


