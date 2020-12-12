package com.dannnyxz.ecs;

import java.util.Collection;
import java.util.List;

public interface Stage {


  <T extends Component> Collection<Entity> getEntitiesByComponent(Class<T> component);

  <T extends Entity> Collection<Entity> getEntitiesByClass(Class<T> entityClass);

  <T extends Component> List<T> getComponents(Class<T> componentType);

  void addEntity(Entity entity);
}
