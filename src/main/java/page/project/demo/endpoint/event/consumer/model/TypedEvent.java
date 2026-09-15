package page.project.demo.endpoint.event.consumer.model;

import page.project.demo.PojaGenerated;
import page.project.demo.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
