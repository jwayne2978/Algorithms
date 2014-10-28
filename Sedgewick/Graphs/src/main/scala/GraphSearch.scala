package sedgewick.graphs

// This enumeration is used to keep track of what point
// we are in a graph search.
object VertexSearchStatus extends Enumeration {
  type VertexSearchStatus = Value
  val Undiscovered = Value // Vertex not discovered; initialize to this
  val Discovered = Value // Vertex discovered, not all neighbors done
  val Finished = Value // Vertex discovered, all neighbors searched
}

object GraphSearch {
  // Visit all vertices connected to v in the graph g
  //  using a depth first search calling the visitor
  // in pre-order

  import VertexSearchStatus._
  import collection.mutable.{Stack => MStack, Queue => MQueue}

  // Note that DFS and BFS are actually for digraphs -- I need
  // to create a version for undirected graphs and use the
  // appropriate one since the rules for back edges are different

  // dfs visitor function from a given vertex
  // Relies on caller to set up visitor on initial call
  private def dfsInner(g: GraphLike, v: Int, visitor: VertexVisitor,
                       visited: Array[VertexSearchStatus]): Unit = {

    visited(v) = Discovered
    visitor.discoverVertex(v, g) // Pre-order
    for (u <- g.adj(v)) {
      if (visited(u) == Undiscovered) {
        visitor.treeEdge(v, u, g)
        dfsInner(g, u, visitor, visited)
      } else if (visited(u) == Discovered)
        visitor.backEdge(v, u, g)
      else
        visitor.crossEdge(v, u, g)
    }
    visited(v) = Finished
  }

  // Visit all vertices connected to a start vertex v using dfs
  def dfsVisitVertex(g: GraphLike, v: Int, visitor: VertexVisitor): Unit = {
    require(g.V > 0, "Empty graph")
    require(v < g.V & v >= 0, s"Invalid start vertex $v")

    val visited = Array.fill(g.V)(Undiscovered)
    visitor.startVertex(v, g)
    dfsInner(g, v, visitor, visited)
  }

  // Visit all vertices in a graph using dfs
  def dfsVisitAll(g: GraphLike, visitor: VertexVisitor): Unit = {
    require(g.V > 0, "Empty graph")
    val visited = Array.fill(g.V)(Undiscovered)
    for (v <- 0 until g.V)
      if (visited(v) == Undiscovered) {
        visitor.startVertex(v, g)
        dfsInner(g, v, visitor, visited)
      }
  }

  // bfs visitor function from a given vertex
  // Relies on caller to set up visited on initial call
  private def bfsInner(g: GraphLike, s: Int,
                       visitor: VertexVisitor,
                       visited: Array[VertexSearchStatus]): Unit = {

    val q = new MQueue[Int]

    visited(s) = Discovered
    visitor.discoverVertex(s, g) // Pre-order
    q += s // Enqueue start vertex or else this exits immediately

    // Main loop
    while (!q.isEmpty) {
      val v = q.dequeue()
      for (u <- g.adj(v)) {
        if (visited(u) == Undiscovered) {
          visitor.treeEdge(v, u, g)
          visited(u) = Discovered
          visitor.discoverVertex(u, g)
          q += u
        }
        else if (visited(u) == Discovered)
          visitor.backEdge(v, u, g)
        else
          visitor.crossEdge(v, u, g)
      }
      visited(v) = Finished
    }
  }

  // Visit all vertices connected to a start vertex v using bfs
  def bfsVisitVertex(g: GraphLike, v: Int, visitor: VertexVisitor): Unit = {
    require(g.V > 0, "Empty graph")
    require(v < g.V & v >= 0, s"Invalid start vertex $v")

    val visited = Array.fill(g.V)(Undiscovered)
    visitor.startVertex(v, g)
    bfsInner(g, v, visitor, visited)
  }

  // Visit all vertices in a graph using bfs
  def bfsVisitAll(g: GraphLike, visitor: VertexVisitor): Unit = {
    require(g.V > 0, "Empty graph")
    val visited = Array.fill(g.V)(Undiscovered)
    for (v <- 0 until g.V)
      if (visited(v) == Undiscovered) {
        visitor.startVertex(v, g)
        bfsInner(g, v, visitor, visited)
      }
  }

  // Get list of vertices connected to specified one
  def connectedToVertex(v: Int, g: GraphLike): List[Int] = {
    val vdet = new VertexVisited(g)

    dfsVisitVertex(g, v, vdet)
    vdet.visitList
  }

  // Mark connected components
  private class ConnectedComponents(g: GraphLike) extends VertexVisitor {
    private[this] var idx: Int = -1;
    private[this] val comps = Array.fill[Int](g.V)(idx)

    override def startVertex(v: Int, g: GraphLike) = idx += 1
    override def discoverVertex(v: Int, g: GraphLike) = comps(v) = idx
    def components: IndexedSeq[Int] = comps.toIndexedSeq
  }

  // Labels all connected components with increasing index
  // Any two vertices with the same value are connected and
  // in the same component
  def findConnectedComponents(g: GraphLike): IndexedSeq[Int] = {
    val vis = new ConnectedComponents(g)
    dfsVisitAll(g, vis)
    vis.components
  }

  // Cycle visitor
  private class CycleDetector extends VertexVisitor {
    private[this] var cycle = false;

    def reset() = cycle = false
    override def backEdge(v: Int, u: Int, g: GraphLike) = cycle = true
    def hasCycle: Boolean = cycle
  }

  // Detects the presence of a cycle using a dfs
  def detectCycle(g: GraphLike): Boolean = {
    val cdet = new CycleDetector
    dfsVisitAll(g, cdet)
    cdet.hasCycle
  }

  // Visitor for finding paths from initial node
  //  to all nodes reachable from that vertex.  Works for
  //  BFS or DFS
  private class Path(g: GraphLike, initVertex: Int) extends VertexVisitor {
    val V = g.V
    val startVertex = initVertex
    val edgeTo = Array.fill[Int](V)(V)
    edgeTo(initVertex) = initVertex

    override def treeEdge(v: Int, u: Int, g: GraphLike) = edgeTo(u) = v
    def hasPathTo(u: Int): Boolean = edgeTo(u) < V
    def pathTo(u: Int): List[Int] = {
      if (u == startVertex) {
        List(u)
      } else if (!hasPathTo(u)) {
        List()
      } else {
        // Use stack to back it out
        val s = new MStack[Int]
        var currVertex = u
        while (currVertex != startVertex) {
          s.push(currVertex)
          currVertex = edgeTo(currVertex)
        }
        s.push(startVertex)  // Path ends on start vertex
        s.toList
      }
    }
  }

  // Find DFS path between v and u, returning an empty
  //  list if there is none
  def findDFSPathBetween(v: Int, u: Int, g: GraphLike): List[Int] = {
    val vis = new Path(g, v)
    dfsVisitVertex(g, v, vis)
    vis.pathTo(u)
  }

  // Find DFS path between v and all reachable vertices as
  // a map
  def findDFSPathsFrom(v: Int, g: GraphLike): Map[Int, List[Int]] = {
    val vis = new Path(g, v)
    dfsVisitVertex(g, v, vis)

    val ret = collection.mutable.Map.empty[Int, List[Int]]
    for (u <- 0 until g.V)
      if (vis.hasPathTo(u)) ret += (u -> vis.pathTo(u))
    ret.toMap
  }

  // Find BFS path between v and u, returning an empty
  //  list if there is none.  This is the shortest
  // path between the two vertices, although it may not
  // be unique
  def findBFSPathBetween(v: Int, u: Int, g: GraphLike): List[Int] = {
    val vis = new Path(g, v)
    bfsVisitVertex(g, v, vis)
    vis.pathTo(u)
  }

  // Find BFS path between v and all reachable vertices as
  // a map
  def findBFSPathsFrom(v: Int, g: GraphLike): Map[Int, List[Int]] = {
    val vis = new Path(g, v)
    bfsVisitVertex(g, v, vis)

    val ret = collection.mutable.Map.empty[Int, List[Int]]
    for (u <- 0 until g.V)
      if (vis.hasPathTo(u)) ret += (u -> vis.pathTo(u))
    ret.toMap
  }
}