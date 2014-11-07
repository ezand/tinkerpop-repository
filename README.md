__OMG it's an OGM for TinkerPop 3!__

Gizmo
=====

Gizmo is an Object Graph Modelling (OGM) framework for TinkerPop 3 using annotations to weave your POJOs to 
the TinkerPop 3 functionality.

Usage
=====
```
@Vertex
public class AnimalShelter {
  private String name;
  
  @Relationship(label = "owns", direction = Direction.IN)
  private Person owner;
  
  // Getters and setters
}

@Vertex
public class Animal {
  @Id
  private Long id;
  
  @Relationship(label = "inhabits")
  private AnimalShelter shelter;
  
  // Getters and setters
}

@Vertex
public class Person {
  @Relationship(label = "owns")
  private AnimalShelter shelter;
  
  // Getters and setters
}
```

```
public class AnimalShelterRepository extends GizmoRepository<PetShop, Long> {
  public AnimalShelterRepository(GizmoGraph graph) {
    super(graph);
  }
}
```
