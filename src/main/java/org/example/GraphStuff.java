package org.example;

import com.google.common.collect.Multimap;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.jgrapht.*;
import org.jgrapht.alg.connectivity.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.graph.*;
import org.semanticweb.owlapi.model.OWLOntology;
import org.w3c.dom.Node;

import java.util.*;

public class GraphStuff {


    //start - client or other Otnology
    //End - Ontology node that has desired information
    // graph - graph of ontologies with for now unweighted edges
    public GraphPath<OWLOntology, DefaultEdge> Dijkstrapath (OWLOntology start, OWLOntology end,
                                                         Graph<OWLOntology, DefaultEdge> graph){
        DijkstraShortestPath<OWLOntology, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);

        return dijkstraAlg.getPath(start, end);
    }

    public GraphPath<OWLOntology, DefaultEdge> AStarpath (OWLOntology start, OWLOntology end,
                                                              Graph<OWLOntology, DefaultEdge> graph){
        //heuristic not yet implemented, note to myself - ask about it
        //possibly g(n) + h(n)? g(n) start -> node | h(n) node -> goal
        AStarShortestPath<OWLOntology, DefaultEdge> AStarAlg = new AStarShortestPath<>(graph,);


        return AStarAlg.getPath(start, end);
    }

    //returns an unweighted graph from a multimap of Ontologies
    public DirectedMultigraph<OWLOntology, DefaultEdge> genGraph (Multimap<OWLOntology, OWLOntology> ontomap){

        DirectedMultigraph<OWLOntology, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);

        Set<OWLOntology> keyset = ontomap.keySet();

        //kont - key ontology (from domain)
        //ont - range ontology
        for (OWLOntology kont : keyset) {
            if (!graph.vertexSet().contains(kont)) {
                graph.addVertex(kont);
            }
            Collection<OWLOntology> values = ontomap.get(kont);
            for (OWLOntology ont : values) {
                //adds if vertex does not exist for where edge goes to
                if (!graph.vertexSet().contains(ont)) {
                    graph.addVertex(ont);
                }
                graph.addEdge(kont, ont);
            }
        }
        return graph;
    }

}
