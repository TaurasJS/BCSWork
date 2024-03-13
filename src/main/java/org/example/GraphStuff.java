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



   /* public double Heuristicfunction (OWLOntology adjnode, OWLOntology end,
                                     Graph<OWLOntology, DefaultEdge> graph)
    {
        GraphPath<OWLOntology, DefaultEdge> path = Dijkstrapath(adjnode, end, graph);
        //gets the sum of the weight of the path members
        double fullweight = path.getWeight();
        //gets the ammount of edges in the path
        int length = path.getLength();

        return fullweight/length;
    }*/

    public GraphPath<OWLOntology, DefaultEdge> AStarpath (OWLOntology start, OWLOntology end,
                                                              Graph<OWLOntology, DefaultEdge> graph){
        class averageincompatabilityweight
                implements AStarAdmissibleHeuristic<OWLOntology>
        {
            @Override
            public double getCostEstimate(OWLOntology source, OWLOntology target) {
                GraphPath<OWLOntology, DefaultEdge> path = Dijkstrapath(source, target, graph);
                double fullweight = path.getWeight();
                int length = path.getLength();

                //because edge weight will be how well they fit in % they will be <=1
                //therefore 1-average so the edges with the leas % difference are preferred
                return 1-(fullweight/length);
            }
        }

        AStarAdmissibleHeuristic<OWLOntology> admissibleHeuristic = new averageincompatabilityweight();
        //heuristic not yet implemented, note to myself - ask about it
        //possibly g(n) + h(n)? g(n) start -> node | h(n) node -> goal
        AStarShortestPath<OWLOntology, DefaultEdge> AStarAlg = new AStarShortestPath<>(graph, admissibleHeuristic);


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
