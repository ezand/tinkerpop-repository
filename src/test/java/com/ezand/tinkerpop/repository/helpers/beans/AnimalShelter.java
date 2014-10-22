package com.ezand.tinkerpop.repository.helpers.beans;

import static com.tinkerpop.gremlin.structure.Direction.IN;

import java.util.Set;

import lombok.Value;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.ezand.tinkerpop.repository.structure.Relationship;

@Value
public class AnimalShelter implements GraphElement<Long> {
    Long id;
    String name;

    @Relationship(label = "inhabits", direction = IN)
    Set<Animal> animals;
}
