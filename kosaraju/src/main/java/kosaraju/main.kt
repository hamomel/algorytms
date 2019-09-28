import main.java.kosaraju.Kosaraju
import kotlin.random.Random

fun main() {
    val graph = createRandomGraph(1000)

    val componints = Kosaraju.find(graph)

    val componentsMap = HashMap<Int, MutableList<Int>>()
    componints.forEachIndexed { index, i ->
        val cell = componentsMap[i] ?: mutableListOf()
        cell.add(index)
        componentsMap[i] = cell
    }

    println(graph.joinToString("\n") { it.joinToString()})

    for (item in componentsMap) {
        println("[${item.key}] -> [${item.value.joinToString()}]")
    }
}

fun createRandomGraph(size: Int): Array<Array<Int>> {
    val graph = Array(size) { emptyArray<Int>() }
    for (vertex in 0 until size) {
        val edges = Array(Random.nextInt(size / 100)) { 0 }
        for (index in 0 until edges.size) {
            edges[index] = Random.nextInt(size)
        }
        graph[vertex] = edges
    }

    return graph
}