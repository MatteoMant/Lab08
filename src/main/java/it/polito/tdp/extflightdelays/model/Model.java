package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private Map<Integer, Airport> aereoportiIdMap;
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	
	public Model() {
		this.aereoportiIdMap = new HashMap<Integer, Airport>();
		this.dao = new ExtFlightDelaysDAO();
		this.dao.loadAllAirports(aereoportiIdMap);
	}
	
	public void creaGrafo(int distanzaMedia) {
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, aereoportiIdMap.values());
		
		for (Rotta rotta : dao.getRotte(aereoportiIdMap, distanzaMedia)) {
			DefaultWeightedEdge edge = this.grafo.getEdge(rotta.getPartenza(), rotta.getArrivo());
			
			if (edge == null) {
				Graphs.addEdge(this.grafo, rotta.getPartenza(), rotta.getArrivo(), rotta.getPeso());
			}else {
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = (peso + rotta.getPeso())/2;
				grafo.setEdgeWeight(edge, newPeso);
			}
		}
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Rotta> getRotte(){
		List<Rotta> rotte = new ArrayList<Rotta>();
		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			rotte.add(new Rotta(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
		}
		return rotte;
	}
}
