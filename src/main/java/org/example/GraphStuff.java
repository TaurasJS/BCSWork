package org.example;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
/*
import it.giacomobergami.microservices.Microservice;
import it.giacomobergami.microservices.MicroserviceFunction;
import it.giacomobergami.simpleschema.IConcept;
import it.giacomobergami.simpleschema.NativeTypes;
import it.giacomobergami.simpleschema.Ontology;
import it.giacomobergami.simpleschema.OntologyLoader;
import it.giacomobergami.simpleschema.alignments.Alignment;
*/
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.jgrapht.*;
import org.jgrapht.alg.connectivity.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.graph.*;
import org.mapdb.Fun;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

public class GraphStuff {

    /*          COMMENTED OUT CODE IS FROM WHEN USING SIMPLE-ONTOLOGY-ALIGNMENT AS CODEBASE


    //will be used to change from OWLOntology to Ontology in simple-ontology-alignment.
    public Ontology changeOntoType (OWLOntology owlOntology) throws  OWLOntologyStorageException{
        OntologyHelper oh = new OntologyHelper();
        File file = new File("ontology.txt");
        IRI documentIRI = IRI.create(file);

        StringDocumentTarget sdt = new StringDocumentTarget();
        oh.m.saveOntology(owlOntology, documentIRI);

        OntologyLoader.loadOntology("Ontology", "ontology.txt");
        Ontology ontology = OntologyLoader.getLoadedOntology("Ontology");

        return ontology;
    }

    public double alignmentscore (OWLOntology owlonto1, OWLOntology owlonto2) throws  OWLOntologyStorageException{
        Ontology onto1 = changeOntoType(owlonto1);
        Ontology onto2 = changeOntoType(owlonto2);

        Alignment x = new Alignment(onto1, onto2);
        double totalcost = 0;
        for(var xp : x.getConceptCorrespondences())
        {
            totalcost += xp.getScore();
        }
        return totalcost;
    }


    public Multimap<OWLOntology, Fun.Tuple2<OWLOntology, Double>> genmap (List<OWLOntology> ontologies) throws  OWLOntologyStorageException{
        Multimap <OWLOntology, Fun.Tuple2<OWLOntology, Double>> map = ArrayListMultimap.create();
        double value = 0;

        // -1 the last ontology is supposed to be the target one
        for(int i = 0; i < ontologies.size()-1; i++)
        {
            for(int j = 1; j < ontologies.size(); j++)
            {
                // if not the same ontology
                if(i != j)
                {
                    value = alignmentscore(ontologies.get(i), ontologies.get(j));
                    map.put(ontologies.get(i), new Fun.Tuple2<>(ontologies.get(j), value));
                }
            }
        }

        return map;
    }
    */


    //start - client or other Otnology
    //End - Ontology node that has desired information
    // graph - graph of ontologies with for now unweighted edges
    public GraphPath<OWLOntology, DefaultWeightedEdge> Dijkstrapath (OWLOntology start, OWLOntology end,
                                                                     Graph<OWLOntology, DefaultWeightedEdge> graph){
        DijkstraShortestPath<OWLOntology, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
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

    public GraphPath<OWLOntology, DefaultWeightedEdge> AStarpath (OWLOntology start, OWLOntology end, Graph<OWLOntology,
            DefaultWeightedEdge> graph){
        class averageincompatabilityweight
                implements AStarAdmissibleHeuristic<OWLOntology>
        {
            @Override
            public double getCostEstimate(OWLOntology source, OWLOntology target) {
                GraphPath<OWLOntology, DefaultWeightedEdge> path = Dijkstrapath(source, target, graph);
                double fullweight = path.getWeight();
                int length = path.getLength();
                return 1-(fullweight/length);
            }
        }
        AStarAdmissibleHeuristic<OWLOntology> admissibleHeuristic = new averageincompatabilityweight();
        AStarShortestPath<OWLOntology, DefaultWeightedEdge> AStarAlg = new AStarShortestPath<>(graph, admissibleHeuristic);
        return AStarAlg.getPath(start, end);
    }


    //returns a weighted graph from a multimap of Ontologies and tuples(Ontology/weight)
    public DirectedWeightedMultigraph<OWLOntology, DefaultWeightedEdge> genGraph (Multimap<OWLOntology,
            Fun.Tuple2<OWLOntology, Double>> ontomap){

        DirectedWeightedMultigraph<OWLOntology, DefaultWeightedEdge> graph =
                new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);

        Set<OWLOntology> keyset = ontomap.keySet();

        //kont - key ontology (from domain)
        //ont - range ontology
        for (OWLOntology kont : keyset) {
            if (!graph.vertexSet().contains(kont)) {
                graph.addVertex(kont);
            }
            Collection<Fun.Tuple2<OWLOntology, Double>> values = ontomap.get(kont);
            for (Fun.Tuple2<OWLOntology, Double> t2 : values) {
                //adds if vertex does not exist for where edge goes to
                if (!graph.vertexSet().contains(t2.a)) {
                    graph.addVertex(t2.a);
                }
                //adds edge
                DefaultWeightedEdge edge = graph.addEdge(kont, t2.a);
                graph.setEdgeWeight(edge, t2.b); //adds weight to that edge from the tuple
            }
        }
        return graph;
    }

    /*
    public List<NativeTypes> getoutputTypes (Microservice microservice){
        List<NativeTypes> typesList = new ArrayList<NativeTypes>();

        for(MicroserviceFunction function : microservice.ls)
        {

            typesList.add(function.microserviceResult.getType());
        }

        return typesList;
    }

    public List<NativeTypes> getinputTypes (Microservice microservice){
        List<NativeTypes> typesList = new ArrayList<NativeTypes>();

        HashMap<String, IConcept> entrymap = new HashMap<String, IConcept>();
        for(MicroserviceFunction function : microservice.ls)
        {
            entrymap = function.input_field_function;
            for (Map.Entry<String, IConcept> entry : entrymap.entrySet()){
                typesList.add(entry.getValue().getType());
            }
        }

        return typesList;
    }

    public Boolean checkmicros (Microservice microservice1, Microservice microservice2)
    {
        return getoutputTypes(microservice2).containsAll(getinputTypes(microservice1));
    }

    public DirectedMultigraph<Microservice, DefaultEdge> genGraphmicro (Graph<OWLOntology,
            DefaultWeightedEdge> ontograph, List<Microservice> microserviceList){

        DirectedMultigraph<Microservice, DefaultEdge> micrograph =
                new DirectedMultigraph<>(DefaultEdge.class);

        for(int i = 0; i < (microserviceList.size()-1); i++)
        {
            // for all servicei ∈ Oi and servicej ∈ Oj do
            if(!micrograph.vertexSet().contains(microserviceList.get(i)))
            {
                micrograph.addVertex(microserviceList.get(i));
            }

            for(int j = 0; j < microserviceList.size(); j++)
            {
                if(j != i) {
                    if (ontograph.containsEdge(microserviceList.get(i).microserviceOntology,
                            microserviceList.get(j).microserviceOntology)) {

                        if (!micrograph.vertexSet().contains(microserviceList.get(j))) {
                            micrograph.addVertex(microserviceList.get(j));
                        }
                        //if output(service i) ⊑ input(service j)
                        if (checkmicros(microserviceList.get(i), microserviceList.get(j))) {
                            DefaultEdge edge = micrograph.addEdge(microserviceList.get(i), microserviceList.get(j));
                        }
                    }
                }
            }
        }


        return micrograph;
    }

    public List<Microservice> micropath (GraphPath<OWLOntology, DefaultWeightedEdge> ontopath,
                                         Graph<Microservice, DefaultEdge> micrograph){
        List<Microservice> microservicepath = new ArrayList<>();
        List<OWLOntology> ontologyList = ontopath.getVertexList();
        //pabaikti veliau. jei yra edgas micrographe ir yra ontopathe idet i patha, jei me pathas neimanomas.

        return microservicepath;
    }*/

}
