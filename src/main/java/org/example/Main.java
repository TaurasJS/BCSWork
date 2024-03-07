package org.example;

import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
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

        File file = new File("ontos/Facebook.owl");
        File filex = new File("ontos/x.owl");
        File fileclient = new File("ontos/client.owl");
        IRI documentIRI = IRI.create(fileclient);
        StringDocumentTarget sdt = new StringDocumentTarget();
        oh.writeOntology(client, sdt);
        System.out.println(sdt.toString());
        oh.m.saveOntology(client, documentIRI);
    }
}