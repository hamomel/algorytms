package demukron;

import java.util.*;

public class Main {

    public static void main(String[] args) {
	    int[][] graph = createAcyclicGraph(10);

        for (int i = 0; i < graph.length; i++) {
            System.out.print(i + " ");
            for (int j : graph[i]) {
                System.out.print("[ " + j + " ]");
            }
            System.out.println();
        }

	    System.out.println(demukron(graph));
	    int[] sorted = terjan(graph);

	    for (int vertex : sorted) {
	        System.out.print("[ " + vertex + " ]");
        }

        System.out.println();
    }

    private static VectorArray<VectorArray<Integer>> demukron(int[][] graph) {
        int[] sums = new int[graph.length];

        for (int[] edges : graph) {
            for (int edge : edges) {
                sums[edge]++;
            }
        }

        VectorArray<VectorArray<Integer>> result = new VectorArray<VectorArray<Integer>>(10);

        while (true) {
            VectorArray<Integer> level = new VectorArray<Integer>(10);

            for (int i = 0; i < sums.length; i++) {
                if (sums[i] == 0) {
                    level.add(i);
                }
            }

            if (level.size() == 0){
                break;
            } else {
                for (int i = 0; i < level.size(); i++) {
                    Integer vertex = level.get(i);
                    for (int edge : graph[vertex]) {
                        sums[edge]--;
                    }
                    sums[vertex] = -1;
                }
                result.add(level);
            }
        }

        return result;
    }

    private static int[] terjan(int[][] graph) {
        int[] array = new int[graph.length];
        VectorArray<Integer> stack = new VectorArray<Integer>(graph.length);

        for (int i = 0; i < graph.length; i++) {
            if (array[i] != 0) continue;
            stack.add(Dfs(i, graph, array, stack));
            array[i] = 2;
        }

        int place = array.length - 1;
        for (int i = 0; i < stack.size(); i++) {
            array[place] = stack.get(i);
            place--;
        }

        return array;
    }

    private static int Dfs(int vertex, int[][] graph, int[] colored, VectorArray<Integer> stack) {
        for (int edge : graph[vertex]) {
            if (colored[edge] == 2) continue;
            if (colored[edge] == 1) throw new IllegalStateException("there is a cycle in the graph");
            colored[edge] = 1;
            stack.add(Dfs(edge, graph, colored, stack));
        }

        colored[vertex] = 2;
        return vertex;
    }

    private static int[][] createAcyclicGraph(int size) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        for (int i = 0; i < size; i++) {
            result.add(new ArrayList<Integer>());
        }

        List<Integer> roots = new ArrayList<Integer>();
        List<Integer> notUsed = new ArrayList<Integer>();
        for (Integer i = 0; i < size; i++) {
            notUsed.add(i);
        }
        Random random = new Random();

        for (int i = 0; i < size / 10; i++) {
            roots.add(random.nextInt(size));
        }

        notUsed.removeAll(roots);

        while (notUsed.size() > 0) {
            Set<Integer> nextLevel = new HashSet<Integer>();
            for (int i = 0; i < size / 5; i++) {
                int nextIndex = random.nextInt(notUsed.size());
                nextLevel.add(notUsed.get(nextIndex));
                notUsed.remove(nextIndex);
                if (notUsed.size() == 0) break;
            }

            for (Integer edge : nextLevel) {
                int vertex = random.nextInt(roots.size());
                Integer root = roots.get(vertex);
                result.get(root).add(edge);
            }

            roots.addAll(nextLevel);
        }

        int[][] res = new int[result.size()][];
        for (int i = 0; i < result.size(); i++) {
            List<Integer> line = result.get(i);
            int[] simpleLine = new int[line.size()];
            for (int j = 0; j < line.size(); j++) {
                simpleLine[j] = line.get(j);
            }
            res[i] = simpleLine;
        }

        return res;
    }
}
