package com.example.android.navigationadvancedsample

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavGraph

fun NavController.findDestinationGraph(@IdRes destinationId: Int): NavGraph? = findParentGraph().find { node ->
    val destGraph = when {
        node is NavGraph -> node.findNode(destinationId)?.let {
            node
        }
        node.id == destinationId -> node.parent
        else -> null
    }
    destGraph != null
} as? NavGraph

fun NavGraph.findParentGraph(): NavGraph {
    var parentGraph = this
    do {
        parentGraph.parent?.let {
            parentGraph = it
        }
    } while (parentGraph.parent != null)

    return parentGraph
}

private fun NavController.findParentGraph() = graph.findParentGraph()