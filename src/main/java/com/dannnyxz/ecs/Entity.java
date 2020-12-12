package com.dannnyxz.ecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(fluent = true)
@Data
public class Entity {
  private Integer id;
  private Stage stage;
  private Collection<Entity> children = new ArrayList<>();
  private Map<Class<? extends Component>, Component> components = new HashMap<>();

  public Entity(Component... components) {
    Arrays.stream(components).forEach(this::addComponent);
  }

  public <T extends Component> Entity addComponent(T component) {
    components.put(component.getClass(), component);
    return this;
  }

  public <T extends Component> T getComponent(Class<T> componentType) {
    return (T) components.get(componentType);
  }
}
