package ru.apolonomelchuk.datastructures

class LinkedStack {
    var head: Node? = null
    var tail: Node? = null
    var current: Node? = null

    inner class Node(var element: Any?){
        var prev: Node? = null
        var next:Node? = null
    }

    fun add(element: Any?){
        var h = this.head
        val newNode = Node(element)
        newNode.next = null
        while (h?.next != null){
            h = h.next
        }
        h?.next = newNode
        newNode.prev = h
        tail = newNode
        current = newNode
    }

    fun getPrevious(): Any? {
        current = current?.prev
        return current?.element
    }

    fun getNext(): Any? {
        current = current?.next
        return current?.element
    }
}