package com.dannnyxz.ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralStage implements Stage {

  private final Map<Class<? extends Component>, Collection<Entity>> componentsEntities = new HashMap<>();
  private final Map<Class<? extends Entity>, Collection<Entity>> entities = new HashMap<>();
  private final Map<Class<? extends Component>, Collection<Component>> components = new HashMap<>();

  @Override
  public <T extends Component> Collection<Entity> getEntitiesByComponent(Class<T> component) {
    return componentsEntities.get(component);
  }

  @Override
  public <T extends Entity> Collection<Entity> getEntitiesByClass(Class<T> entityClass) {
    return entities.get(entityClass);
  }

  @Override
  public <T extends Component> List<T> getComponents(Class<T> componentType) {
    var componentsOfType = components.get(componentType);
    return componentsOfType == null
        ? new ArrayList<>()
        : componentsOfType
            .stream() // TODO: optimize
            .map(component -> (T) component)
            .collect(Collectors.toList());
  }

  // to perform fast queries by component
  @Override
  public void addEntity(Entity entity) {
    entity.components().values().forEach(component -> {
      components.putIfAbsent(component.getClass(), new ArrayList<>());
      components.get(component.getClass()).add(component);
      componentsEntities.putIfAbsent(component.getClass(), new ArrayList<>());
      componentsEntities.get(component.getClass()).add(entity);
    });
  }
}
