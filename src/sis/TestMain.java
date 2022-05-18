package sis;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public class TestMain {

	@Test
	public void test() {
		// ѕроверить соответствие типов данных класса (loadGraph return graph)
		Graph<Node,Object> graph = new SparseGraph<>();
		Graph<Node,Object> graph1 = Main.loadGraph("myAs.net");
		
		// SparseGraph
		assertEquals(graph.getClass().getSimpleName(), graph1.getClass().getSimpleName());
		
	}

}
