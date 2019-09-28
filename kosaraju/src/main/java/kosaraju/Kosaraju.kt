package main.java.kosaraju

object Kosaraju {

    private lateinit var graph: Array<Array<Int>>
    private lateinit var visited: Array<Boolean>
    private lateinit var path: Array<Int>
    private var current = 0

    fun find(graph: Array<Array<Int>>): Array<Int> {
        this.graph = graph
        visited = Array(graph.size) { false }
        path = Array(graph.size) { 0 }
        current = graph.size - 1

        for (vertex in 0 until graph.size) {
            if (visited[vertex]) continue
            visited[vertex] = true
            DfsStraight(vertex)
        }

        visited = Array(graph.size) { false }

        val result = Array(graph.size) { 0 }
        var componentIndex = 0

        for (vertex in path) {
            if (visited[vertex]) continue
            visited[vertex] = true

            DfsReversed(vertex, result, componentIndex)

            result[vertex] = componentIndex
            componentIndex++
        }

        return result
    }

    private fun DfsStraight(vertex: Int) {
        for (edge in graph[vertex]) {
            if (visited[edge]) continue
            visited[edge] = true
            DfsStraight(edge)
        }

        path[current] = vertex
        current--
    }

    private fun DfsReversed(vertex: Int, connected: Array<Int>, component: Int) {
        for (index in 0 until graph.size) {
            if (index == vertex) continue
            if (visited[index]) continue

            if (graph[index].contains(vertex)) {
                visited[index] = true
                DfsReversed(index, connected, component)
            }
        }

        connected[vertex] = component
    }
}