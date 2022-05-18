package sis;

import java.io.IOException;
import org.apache.commons.collections15.Factory;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;

/** SIS ������ "�������������" - "��������������" - "�������������" 
 * @ author Djas 
 * https://www.youtube.com/watch?v=JiqsEcV2_1g&t=4184s*/

public class Main {
	/** ������� ����� */
	public static void main(String[] args) {
		Graph<Node,Object> graph = loadGraph("myAs.net"); 
		System.out.println("���� ������� ��������!");
		System.out.println("���-�� ���� E: " + graph.getEdgeCount()); 
		System.out.println("���-�� ������ V: " + graph.getVertexCount() + "\n");
	
		double[] res = usePro(graph, 0.55, 0.025, 0.05,  0.05, 0.15, 0.06,  10); // ���������� �� 35% �� 5% � ����� 2,5%,  �� 5% �������������, ��� 15% � �� 60%,  100 �����
		for(int i = 0; i < res.length; i++) { 
			System.out.println("��������� ��������� �� ���� " + i + ": " + res[i]);
		}
	}
	
	
	/** ���������� ��� ������ �������� ������ (modelling(...))
	 * @param graph - ����������� ����
	 * @param u_start - ��������� �������� ������������� ���������������
	 * @param u_step - ��� ��������� � ������������ ��� ������������� ���������������
	 * @param u_stop - �������� �������� ������������� ���������������
	 * @param d - �������� ����������� ������������� �������
	 * @param d_step - ��� ��������� ����� �����������
	 * @param d_stop - �������� �������� ������������� �������������
	 * @param totalSteps - ����� ����� ����� �������������
	 * @return dr - ��������� ��������������� */
	public static double[] usePro(Graph<Node,Object> graph, double u_start, double u_step, double u_stop, double d, double d_step, double d_stop, int totalSteps) {
		double dr[] = new double[totalSteps];
		for (double u = u_start; u > u_stop; u -= u_step) { // ��������� ����������
			dr = modelling(graph, totalSteps, u, d);
			if (d < d_stop) d += d_step;// ����������� �������������
		}
		return dr;
	}

	
	/**�������� �������� �������������
	 * @param graph - ����������� ����
	 * @param totalSteps - ����� ����� ����� �������������
	 * @param u - ����������� �������� ������
	 * @param d - ����������� ����������� �������
	 * @return mass - ��������� ��������������� - ������� ������ �� ����� ����� ������ */
	public static double[] modelling(Graph<Node, Object> graph, int totalSteps, double u, double d) {
		double mass[] = new double[totalSteps]; 
		
		for(Node v: graph.getVertices()) { // ����������� �������� ��������� (���������� � ���� 0)
			if(Math.random() <= 0.1) // 10% ������ � ������ ������
				v.setInfeceted(true); 
			else 
				v.setInfeceted(false);
			v.fixNewState(); // ��������� ��������� ���������� � ���� 0
		}
		
		for(int i = 0; i < totalSteps; i++) { // ����� ����� �����
			for(Node n1:graph.getVertices()) { // ��� ������ �������...
				if(n1.isInfected()) { // ���� �������, �� �������� �������
					for(Node n: graph.getNeighbors(n1)) {
						if(Math.random() <= u) 
							n.setInfeceted(true); // �������� ������ � ������������ u
					}
					if (Math.random() <= d) 
						n1.setInfeceted(false); // �������������� � ������������ d
				}
			}
			
			int inf = 0; // ���-�� ������������ �� ���� i
			
			for (Node n1:graph.getVertices()) {
				if(n1.isInfected()) 
				{
					inf++; 
				}
				n1.fixNewState(); // �������� ������ ��������� ������� (�� 2: ��������� �� ���� ���� � �� ���������)
			}
			mass[i] = (double) inf / (double) graph.getVertexCount(); // ������� ������ ���������� �� ����
			//System.out.println(i + "//    " + inf);
		}
		
		return mass;
	}

	
	/** ���������� ������ �� �����, ������� ���� � ���������� ������ � ����
	 * @param fileName - �������� ����� � �������
	 * @return graph - ����������� ���� */
	public static Graph<Node,Object> loadGraph(String fileName) { 
		Graph<Node,Object> graph = new SparseGraph<>(); // ������� ������ ����-���������
		
		// ��� ����, ����� ������� ��� ��������� � ��������� ��������� �� ����� ������� � ����� (� �������� "�������" ���� ������ ��� �������� ��������������� ��������)
		Factory<Node> vertex_factory = new Factory<Node>() { // �������� �������
			@Override
			public Node create() {
				return new Node();
			}
			
		};
		Factory<Object> edge_factory = new Factory<Object>() { // �������� �����
			@Override
			public Object create() {
				return new Object();
			}
		};
		
		PajekNetReader<Graph<Node,Object>,Node,Object> pnr = new PajekNetReader<Graph<Node,Object>,Node,Object>(vertex_factory,edge_factory); // ����� ��� ������������ �������� �����
		try {
			graph = pnr.load(fileName,graph);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graph;
	}
}