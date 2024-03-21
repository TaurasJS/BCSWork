package org.example;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.mapdb.Fun;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.example.GraphStuff.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        OntologyHelper oh = new OntologyHelper();
        OWLOntology o = oh.createOntology("http://Testing.com/Facebook.owl");
        OWLClass Account = oh.createClass("http://Testing.com/Facebook.owl#Account");
        OWLClass Friend = oh.createClass("http://Testing.com/Facebook.owl#Friend");


        OWLDataProperty Name = oh.createDataProperty("http://Testing.com/Facebook.owl#Name");
        OWLDataProperty Surname = oh.createDataProperty("http://Testing.com/Facebook.owl#Surname");
        OWLDataProperty DOB = oh.createDataProperty("http://Testing.com/Facebook.owl#DOB");
        OWLDataProperty Twitter = oh.createDataProperty("http://Testing.com/Facebook.owl#Twitter");

        OWLIndividual JohnSmith = oh.createIndividual("http://Testing.com/Facebook.owl#JohnSmith");
        OWLIndividual JaneSmith = oh.createIndividual("http://Testing.com/Facebook.owl#JaneSmith");

        // twitter ontology stuff
        OWLOntology x = oh.createOntology("http://Testing.com/x.owl");
        OWLClass Accountx = oh.createClass("http://Testing.com/x.owl#Account");
        OWLClass Follower = oh.createClass("http://Testing.com/x.owl#Follower");
        OWLClass Following = oh.createClass("http://Testing.com/x.owl#Following");

        OWLIndividual JohnSmithx = oh.createIndividual("http://Testing.com/x.owl#JohnSmith");
        OWLIndividual JaneSmithx = oh.createIndividual("http://Testing.com/x.owl#JaneSmith");

        OWLDataProperty Nickname = oh.createDataProperty("http://Testing.com/x.owl#Nickname");

        oh.applyChange(
                oh.associateIndividualWithClass(x, Accountx, JohnSmithx),
                oh.associateIndividualWithClass(x, Follower, JaneSmithx),
                oh.associateIndividualWithClass(x, Following, JaneSmithx),
                oh.addDataToIndividual(x, JohnSmithx, Nickname, "JSmith")
        );

        //FOAF client knows fb ontology
        OWLOntology client = oh.createOntology("http://Testing.com/client.owl");
        OWLClass Knows = oh.createClass("http://Testing.com/client.owl#Knows");
        OWLIndividual fb = oh.createIndividual("http://Testing.com/client.owl#fb");
        OWLDataProperty fblink = oh.createDataProperty("http://Testing.com/client.owl#fblink");
        oh.applyChange(
                oh.associateIndividualWithClass(client, Knows, fb),
                oh.addDataToIndividual(client, fb, fblink, "http://Testing.com/Facebook.owl")
        );





        OWLAxiomChange axiom = oh.createSubclass(o, Friend, Account);
        OWLAxiomChange axiom2 = oh.createSubclass(o, Follower, Accountx);
        OWLAxiomChange axiom3 = oh.createSubclass(o, Following, Accountx);

        oh.applyChange(axiom, axiom2, axiom3);
        oh.applyChange(
                oh.associateIndividualWithClass(o, Account, JohnSmith),
                oh.associateIndividualWithClass(o, Friend, JaneSmith),
                oh.addDataToIndividual(o, JohnSmith, Name, "John"),
                oh.addDataToIndividual(o, JohnSmith, Surname, "Smith"),
                oh.addDataToIndividual(o, JohnSmith, DOB, "2000/01/23"),
                oh.addDataToIndividual(o, JohnSmith, Twitter, "http://Testing.com/x.owl/JohnSmith")
        );

       /* File file = new File("ontos/Facebook.owl");
        File filex = new File("ontos/x.owl");
        File fileclient = new File("ontos/client.owl");
        IRI documentIRI = IRI.create(fileclient);
        StringDocumentTarget sdt = new StringDocumentTarget();
        oh.writeOntology(client, sdt);
        System.out.println(sdt.toString());
        oh.m.saveOntology(client, documentIRI);*/

        //Testing of graph creation and pathfinding algorithms

        OWLOntology Client = oh.createOntology("http://Testing.com/Client.owl");
        OWLOntology Target = oh.createOntology("http://Testing.com/Target.owl");
        OWLOntology A = oh.createOntology("http://Testing.com/A.owl");
        OWLOntology B = oh.createOntology("http://Testing.com/B.owl");
        OWLOntology C = oh.createOntology("http://Testing.com/C.owl");
        OWLOntology D = oh.createOntology("http://Testing.com/D.owl");
        Multimap<OWLOntology, Fun.Tuple2<OWLOntology, Double>> ontomap = ArrayListMultimap.create();

        //example of this mapping is in progress report presentation
        ontomap.put(Client, new Fun.Tuple2<>(A, 0.2));
        ontomap.put(Client, new Fun.Tuple2<>(B, 0.2));
        ontomap.put(A, new Fun.Tuple2<>(C, 0.3));
        ontomap.put(A, new Fun.Tuple2<>(D, 0.4));
        ontomap.put(B, new Fun.Tuple2<>(C, 0.15));
        ontomap.put(B, new Fun.Tuple2<>(D, 0.75));
        ontomap.put(C, new Fun.Tuple2<>(Target, 0.1));
        ontomap.put(D, new Fun.Tuple2<>(Target, 0.3));

        GraphStuff gstuff = new GraphStuff();
        DirectedWeightedMultigraph<OWLOntology, DefaultWeightedEdge> graph =  gstuff.genGraph(ontomap);
        GraphPath<OWLOntology, DefaultWeightedEdge> path = gstuff.AStarpath(Client, Target, graph);
        System.out.println(path);





    }
}