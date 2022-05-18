package sis;

import java.io.IOException;
import org.apache.commons.collections15.Factory;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;

/** SIS модель "Восприимчивый" - "Инфецированный" - "Восприимчивый" 
 * @ author Djas 
 * https://www.youtube.com/watch?v=JiqsEcV2_1g&t=4184s*/

public class Main {
	/** Главный метод */
	public static void main(String[] args) {
		Graph<Node,Object> graph = loadGraph("myAs.net"); 
		System.out.println("Граф успешно загружен!");
		System.out.println("Кол-во рёбер E: " + graph.getEdgeCount()); 
		System.out.println("Кол-во вершин V: " + graph.getVertexCount() + "\n");
	
		double[] res = usePro(graph, 0.55, 0.025, 0.05,  0.05, 0.15, 0.06,  10); // заразность от 35% до 5% с шагом 2,5%,  от 5% выздоровление, шаг 15% и до 60%,  100 шагов
		for(int i = 0; i < res.length; i++) { 
			System.out.println("Плотность заражения на шаге " + i + ": " + res[i]);
		}
	}
	
	
	/** Подготовка для работы основной логики (modelling(...))
	 * @param graph - исследуемый граф
	 * @param u_start - начальное значение интенсивности распространения
	 * @param u_step - шаг изменения в эксперименте для интенсивности распространения
	 * @param u_stop - конечное значение интенсивности распространения
	 * @param d - значение вероятности выздоровления вершины
	 * @param d_step - шаг изменения шанса выздороветь
	 * @param d_stop - конечное значение интенсивности выздоровления
	 * @param totalSteps - общее число шагов моделирования
	 * @return dr - плотность распространения */
	public static double[] usePro(Graph<Node,Object> graph, double u_start, double u_step, double u_stop, double d, double d_step, double d_stop, int totalSteps) {
		double dr[] = new double[totalSteps];
		for (double u = u_start; u > u_stop; u -= u_step) { // уменьшает заразность
			dr = modelling(graph, totalSteps, u, d);
			if (d < d_stop) d += d_step;// увеличиваем выздоровление
		}
		return dr;
	}

	
	/**Имитация процесса моделирования
	 * @param graph - исследуемый граф
	 * @param totalSteps - общее число шагов моделирования
	 * @param u - вероятность заразить соседа
	 * @param d - вероятность выздороветь вершины
	 * @return mass - плотность распространения - сколько верщин из всего числа болеет */
	public static double[] modelling(Graph<Node, Object> graph, int totalSteps, double u, double d) {
		double mass[] = new double[totalSteps]; 
		
		for(Node v: graph.getVertices()) { // Присваиваем вершинам состояние (подготовка к шагу 0)
			if(Math.random() <= 0.1) // 10% вершин в начале болеют
				v.setInfeceted(true); 
			else 
				v.setInfeceted(false);
			v.fixNewState(); // Обновляем состояние подготовки к шагу 0
		}
		
		for(int i = 0; i < totalSteps; i++) { // Общее число шагов
			for(Node n1:graph.getVertices()) { // Для каждой вершины...
				if(n1.isInfected()) { // Если заразна, то заражаем соседей
					for(Node n: graph.getNeighbors(n1)) {
						if(Math.random() <= u) 
							n.setInfeceted(true); // Заражаем соседа с веротяностью u
					}
					if (Math.random() <= d) 
						n1.setInfeceted(false); // Выздоравливаем с вероятностью d
				}
			}
			
			int inf = 0; // Кол-во заразившихся на шаге i
			
			for (Node n1:graph.getVertices()) {
				if(n1.isInfected()) 
				{
					inf++; 
				}
				n1.fixNewState(); // Обновить старое состояние вершины (их 2: состояние на этом шаге и на следующем)
			}
			mass[i] = (double) inf / (double) graph.getVertexCount(); // Сколько вершин заразилось из всех
			//System.out.println(i + "//    " + inf);
		}
		
		return mass;
	}

	
	/** Считывание данные из файла, создаем граф и записываем данные в граф
	 * @param fileName - название файла с данными
	 * @return graph - исследуемый граф */
	public static Graph<Node,Object> loadGraph(String fileName) { 
		Graph<Node,Object> graph = new SparseGraph<>(); // Создаем пустой граф-структуру
		
		// Для того, чтобы указать как создавать в программе считанные из файла вершины и ребра (У паттерна "Фабрика" есть методы для создания соответствующих объектов)
		Factory<Node> vertex_factory = new Factory<Node>() { // Создание вершины
			@Override
			public Node create() {
				return new Node();
			}
			
		};
		Factory<Object> edge_factory = new Factory<Object>() { // Создание ребра
			@Override
			public Object create() {
				return new Object();
			}
		};
		
		PajekNetReader<Graph<Node,Object>,Node,Object> pnr = new PajekNetReader<Graph<Node,Object>,Node,Object>(vertex_factory,edge_factory); // Класс для программного создания графа
		try {
			graph = pnr.load(fileName,graph);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graph;
	}
}