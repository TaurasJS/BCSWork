package org.example;

//import org.apache.commons.lang3.builder.ToStringExclude;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFaDocumentFormat;
import org.semanticweb.owlapi.formats.RioRDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RioTurtleDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.Arrays;

public class OntologyHelper {
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLDataFactory df =  OWLManager.getOWLDataFactory();

    public IRI convertStringToIRI(String ns){
        return IRI.create(ns);
    }

    public OWLOntology createOntology(String iri) throws OWLOntologyCreationException{
        return createOntology(convertStringToIRI(iri));
    }

    public OWLOntology createOntology(IRI iri) throws OWLOntologyCreationException {
        return m.createOntology(iri);
    }

    public void writeOntology(OWLOntology o, OWLOntologyDocumentTarget documentTarget)
            throws OWLOntologyStorageException {
        m.saveOntology(o, documentTarget);
    }

    public OWLOntology readOntology(OWLOntologyDocumentSource source)
            throws OWLOntologyCreationException {
        return m.loadOntologyFromOntologyDocument(source);
    }

    public OWLClass createClass(String iri) {
        return createClass(convertStringToIRI(iri));
    }

    public OWLClass createClass(IRI iri) {
        return df.getOWLClass(iri);
    }

    public OWLAxiomChange createSubclass(OWLOntology o, OWLClass subclass, OWLClass superclass) {
        return new AddAxiom(o,df.getOWLSubClassOfAxiom(subclass, superclass));
    }

    public void applyChange(OWLAxiomChange ... axiom) {
        applyChanges(axiom);
    }

    private void applyChanges(OWLAxiomChange ... axioms) {
        m.applyChanges(Arrays.asList(axioms));
    }

    public OWLIndividual createIndividual(String iri) {
        return createIndividual(convertStringToIRI(iri));
    }
    private OWLIndividual createIndividual(IRI iri) {
        return df.getOWLNamedIndividual(iri);
    }

    public OWLAxiomChange associateIndividualWithClass(OWLOntology o,
                                                       OWLClass clazz,
                                                       OWLIndividual individual) {
        return new AddAxiom(o, df.getOWLClassAssertionAxiom(clazz, individual));
    }

    /**
     * With ontology o, property in refHolder points to a refTo.
     *
     * @param o The ontology reference
     * @param property the data property reference
     * @param refHolder the container of the property
     * @param refTo the class the property points to
     * @return a patch to the ontology
     */
    public OWLAxiomChange associateObjectPropertyWithClass(OWLOntology o,
                                                           OWLObjectProperty property,
                                                           OWLClass refHolder,
                                                           OWLClass refTo) {
        OWLClassExpression hasSomeRefTo=df.getOWLObjectSomeValuesFrom(property, refTo);
        OWLSubClassOfAxiom ax=df.getOWLSubClassOfAxiom(refHolder, hasSomeRefTo);
        return new AddAxiom(o, ax);
    }

    /**
     * With ontology o, an object of class a cannot be simultaneously an object of class b.
     * This is not implied to be an inverse relationship; saying that a cannot be a b does not
     * mean that b cannot be an a.
     * @param o the ontology reference
     * @param a the source of the disjunction
     * @param b the object of the disjunction
     * @return a patch to the ontology
     */
    public OWLAxiomChange addDisjointClass(OWLOntology o, OWLClass a, OWLClass b) {
        OWLDisjointClassesAxiom expression=df.getOWLDisjointClassesAxiom(a, b);
        return new AddAxiom(o, expression);
    }

    public OWLDataProperty createDataProperty(String iri) {
        return createDataProperty(convertStringToIRI(iri));
    }

    public OWLDataProperty createDataProperty(IRI iri) {
        return df.getOWLDataProperty(iri);
    }

    public OWLAxiomChange addDataToIndividual(OWLOntology o, OWLIndividual individual,
                                              OWLDataProperty property, String value) {
        OWLLiteral literal = df.getOWLLiteral(value, OWL2Datatype.XSD_STRING);
        return new AddAxiom(o, df.getOWLDataPropertyAssertionAxiom(property, individual, literal));
    }

    public OWLAxiomChange addDataToIndividual(OWLOntology o, OWLIndividual individual,
                                              OWLDataProperty property, boolean value) {
        OWLLiteral literal = df.getOWLLiteral(value);
        return new AddAxiom(o, df.getOWLDataPropertyAssertionAxiom(property, individual, literal));
    }

    public OWLAxiomChange addDataToIndividual(OWLOntology o, OWLIndividual individual,
                                              OWLDataProperty property, int value) {
        OWLLiteral literal = df.getOWLLiteral(value);
        return new AddAxiom(o, df.getOWLDataPropertyAssertionAxiom(property, individual, literal));
    }



}
