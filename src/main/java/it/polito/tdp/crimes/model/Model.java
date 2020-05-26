package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private EventsDao dao;
	private Graph<String, DefaultWeightedEdge> grafo;
	List<String> best= new ArrayList<>();
	public Model() {
		dao= new EventsDao();
		best= new ArrayList<>();
	}
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	public List<Integer> getMesi(){
		return dao.getMesi();
	}
	
	public void creaGrafo(String categoria, Integer mese) {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		List<Adiacenza> adiacenze = this.dao.getAdiacenze(categoria, mese);
		for(Adiacenza a: adiacenze) {
			if(!this.grafo.containsVertex(a.getV1())) {
				this.grafo.addVertex(a.getV1());
			}
			if(!this.grafo.containsVertex(a.getV2())) {
				this.grafo.addVertex(a.getV2());
			}
			
			if(this.grafo.getEdge(a.getV1(), a.getV2())==null) {
				Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(),a.getPeso());
			}
		}
	}
	
	public List<Adiacenza> getArchi(){
		double pesoMedio = 0;
		for( DefaultWeightedEdge e: this.grafo.edgeSet()) {
			pesoMedio+= this.grafo.getEdgeWeight(e);
		}
		pesoMedio= pesoMedio/this.grafo.edgeSet().size();
		
		List<Adiacenza> result= new ArrayList<>();
		result.add(new Adiacenza(null,null, pesoMedio));
		for( DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)> pesoMedio) {
				result.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
			}
		}
		return result;
	}
	
	public List<String> trovaPercorso( String sorgente, String arrivo){
		List<String> parziale= new ArrayList<>();
		parziale.add(sorgente);
		this.cerca(parziale,arrivo);
		return this.best;
	}
	private void cerca(List<String> parziale, String arrivo ) {
		if(parziale.get(parziale.size()-1).equals(arrivo)) {
			if(parziale.size()>this.best.size()) {
				this.best= new ArrayList<>(parziale);
			}
			return;
		}
		
		for(String s: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(s)) {
				parziale.add(s);
				cerca(parziale,arrivo);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
}
