package sedgewick.graphs

import GraphSearch._
import org.scalatest._

// Test of search algorithms on undirected graphs
class GraphSearchTest extends FlatSpec with Matchers {

  // Three graphs to play with
  // 1) a very simple one
  // 2) the more complex one used as a preliminary
  //  example in Sedgewick 4th Ed
  // 3) A small tree (no cycles)
  val edgeList1 = List((0, 1), (3, 4), (3, 5))
  val g1 = Graph(edgeList1)
  val edgeList2 = List((0, 5), (4, 3), (0, 1), (9, 12), (6, 4),
    (5, 4), (0, 2), (11, 12), (9, 10), (0, 6), (7, 8), (9, 11),
    (5, 3))
  val g2 = Graph(edgeList2)
  val g3 = Graph(List((0, 1), (0, 2), (1, 3), (2, 4), (2, 6)))

  "dfsVisitVertex" should "visit the right number of vertices" in {
    // Simple graph
    val v1 = new VisitCount with dfsVisitor

    // Visit 0
    dfsVisitVertex(g1, 0, v1)
    v1.getNVisited should be(2)

    // Visit 1
    v1.resetNVisited()
    dfsVisitVertex(g1, 1, v1)
    v1.getNVisited should be(2)

    // Visit 2
    v1.resetNVisited()
    dfsVisitVertex(g1, 2, v1)
    v1.getNVisited should be(1)

    // Visit 3, 4, 5
    v1.resetNVisited()
    dfsVisitVertex(g1, 3, v1)
    v1.getNVisited should be(3)
    v1.resetNVisited()
    dfsVisitVertex(g1, 4, v1)
    v1.getNVisited should be(3)
    v1.resetNVisited()
    dfsVisitVertex(g1, 5, v1)
    v1.getNVisited should be(3)

    // More complex graph; the example tinyG example in Sedgewick v4
    v1.resetNVisited()
    dfsVisitVertex(g2, 1, v1)
    v1.getNVisited should be (7)
    v1.resetNVisited()
    dfsVisitVertex(g2, 4, v1)
    v1.getNVisited should be (7)
    v1.resetNVisited()
    dfsVisitVertex(g2, 8, v1)
    v1.getNVisited should be (2)
    v1.resetNVisited()
    dfsVisitVertex(g2, 9, v1)
    v1.getNVisited should be (4)
    v1.resetNVisited()
  }

  it should "visit the right vertices" in {
    val vis = new VertexVisited(g2) with dfsVisitor
    dfsVisitVertex(g2, 0, vis)
    vis.getNVisited should be (7)
    vis.allVisited should be (false)
    vis.didVisit(4) should be (true)
    vis.didVisit(9) should be (false)
    vis.visitList shouldEqual List(0, 1, 2, 3, 4, 5, 6)

    vis.reset()
    dfsVisitVertex(g2, 8, vis)
    vis.getNVisited should be (2)
    vis.didVisit(7) should be (true)
    vis.didVisit(8) should be (true)
    vis.didVisit(9) should be (false)
    vis.visitList shouldEqual List(7, 8)

    vis.reset()
    dfsVisitVertex(g2, 9, vis)
    vis.getNVisited should be (4)
    vis.didVisit(7) should be (false)
    vis.didVisit(9) should be (true)
    vis.didVisit(11) should be (true)
    vis.visitList shouldEqual List(9, 10, 11, 12)
  }

  "bfsVisitVertex" should "visit the right number of vertices" in {
    // Simple graph
    val v1 = new VisitCount with bfsVisitor

    bfsVisitVertex(g1, 0, v1)
    v1.getNVisited should be(2)
    v1.resetNVisited()
    bfsVisitVertex(g1, 1, v1)
    v1.getNVisited should be(2)
    v1.resetNVisited()
    bfsVisitVertex(g1, 2, v1)
    v1.getNVisited should be(1)
    v1.resetNVisited()
    bfsVisitVertex(g1, 3, v1)
    v1.getNVisited should be(3)
    v1.resetNVisited()
    bfsVisitVertex(g1, 4, v1)
    v1.getNVisited should be(3)
    v1.resetNVisited()
    bfsVisitVertex(g1, 5, v1)
    v1.getNVisited should be(3)

    // More complex graph; the example tinyG example in Sedgewick v4
    v1.resetNVisited()
    bfsVisitVertex(g2, 1, v1)
    v1.getNVisited should be (7)
    v1.resetNVisited()
    bfsVisitVertex(g2, 4, v1)
    v1.getNVisited should be (7)
    v1.resetNVisited()
    bfsVisitVertex(g2, 8, v1)
    v1.getNVisited should be (2)
    v1.resetNVisited()
    bfsVisitVertex(g2, 9, v1)
    v1.getNVisited should be (4)
    v1.resetNVisited()
  }

  it should "visit the right vertices" in {
    val vis = new VertexVisited(g2) with bfsVisitor
    bfsVisitVertex(g2, 0, vis)
    vis.getNVisited should be (7)
    vis.allVisited should be (false)
    vis.didVisit(4) should be (true)
    vis.didVisit(9) should be (false)
    vis.visitList shouldEqual List(0, 1, 2, 3, 4, 5, 6)

    vis.reset()
    bfsVisitVertex(g2, 8, vis)
    vis.getNVisited should be (2)
    vis.didVisit(7) should be (true)
    vis.didVisit(8) should be (true)
    vis.didVisit(9) should be (false)
    vis.visitList shouldEqual List(7, 8)

    vis.reset()
    bfsVisitVertex(g2, 9, vis)
    vis.getNVisited should be (4)
    vis.didVisit(7) should be (false)
    vis.didVisit(9) should be (true)
    vis.didVisit(11) should be (true)
    vis.visitList shouldEqual List(9, 10, 11, 12)
  }

  "connectedToVertex" should "find the connected vertices" in {
    connectedToVertex(0, g2) shouldEqual List(0, 1, 2, 3, 4, 5 ,6)
    connectedToVertex(3, g2) shouldEqual List(0, 1, 2, 3, 4, 5 ,6)
    connectedToVertex(7, g2) shouldEqual List(7, 8)
    connectedToVertex(10, g2) shouldEqual List(9, 10, 11, 12)
  }

  "findDFSPathBetween" should "find the path between vertices" in {
    findDFSPathBetween(0, 4, g2) shouldEqual List(0, 5, 3, 4)
    findDFSPathBetween(0, 0, g2) shouldEqual List(0)
    findDFSPathBetween(0, 9, g2) shouldEqual List()
  }

  "findDFSPathsFrom" should "find the paths between vertices" in {
    val paths = findDFSPathsFrom(0, g2)
    paths get 4 shouldEqual Some(List(0, 5, 3, 4))
    paths get 0 shouldEqual Some(List(0))
    paths get 9  shouldEqual None
    paths get 3 shouldEqual Some(List(0, 5, 3))
    paths get 5 shouldEqual Some(List(0, 5))
  }

  "findBFSPathBetween" should "find the path between vertices" in {
    findBFSPathBetween(0, 3, g2) shouldEqual List(0, 5, 3)
    findBFSPathBetween(0, 0, g2) shouldEqual List(0)
    findBFSPathBetween(0, 9, g2) shouldEqual List()
  }

  "findBFSPathsFrom" should "find the paths between vertices" in {
    val paths = findBFSPathsFrom(0, g2)
    paths get 0 shouldEqual Some(List(0))
    paths get 9  shouldEqual None
    paths get 3 shouldEqual Some(List(0, 5, 3))
    paths get 5 shouldEqual Some(List(0, 5))
  }

  "findConnectedComponents" should "find the connected components" in {
    findConnectedComponents(g2) shouldEqual
      IndexedSeq(0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 2)
    findConnectedComponents(g3) shouldEqual
      IndexedSeq(0, 0, 0, 0, 0, 1, 0)
  }

  "detectCycle" should "detect cycles" in {
    detectCycle(g1) should be (false)
    detectCycle(g2) should be (true)
    detectCycle(g3) should be (false)
  }
}
