package it.polito.tdp.yelp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	
	Graph<Business, DefaultWeightedEdge> grafo;
	List<Business> vertici;
	Map<String,Business> idMap;
	
	public List<String> getAllCities() {
		YelpDao dao = new YelpDao();
		return dao.getAllCities();
	}
	
	public void creaGrafo(int anno,String citta) {
		//Secondo punto:creare il grafo
		grafo = new SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		YelpDao dao = new YelpDao();
		//Aggiungo i vertici al grafo:
		vertici = dao.getAllBusinesswithcondition(anno, citta);
		idMap= new HashMap<String,Business>();
		for(Business b : vertici) {
			idMap.put(b.getBusinessId(), b);
		}
		Graphs.addAllVertices(grafo, vertici);
		
		//Terzo punto: Calcolare i cammini del grafo:
		List<Archi> archi = dao.getAllArchi(anno, citta);
		for(Archi a : archi) {
			
			Graphs.addEdge(grafo, idMap.get(a.getBusiness_id_1()), idMap.get(a.getBusiness_id_2()),a.getPeso());
			
		}
	}
	
	public int getVertex() {
		return grafo.vertexSet().size();
	}
	
	public int getEdges() {
		return grafo.edgeSet().size();
	}
	
	public Business getBestB() {
		
		double max_peso = 0.0;
		Business result = null;
		
		for(Business b : grafo.vertexSet()) {
			double val = 0.0;
			
			for(DefaultWeightedEdge e : grafo.incomingEdgesOf(b)) {
				val += grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(b)) {
				val -= grafo.getEdgeWeight(e);
			}
			
			if(val > max_peso) {
				max_peso = val;
				result = b;
			}
		}
		
		
		return result;
	}
}
