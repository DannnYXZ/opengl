package com.dannnyxz.ecs.system;

import com.dannnyxz.renderer.SoundCircleRenderer;
import com.dannnyxz.ecs.Stage;
import com.dannnyxz.ecs.System;
import com.dannnyxz.ecs.component.SoundReactiveCircle;

public class RenderSystem implements System {

  private final SoundCircleRenderer soundCircleRenderer;

  public RenderSystem(SoundCircleRenderer soundCircleRenderer) {
    this.soundCircleRenderer = soundCircleRenderer;
  }

  @Override
  public void execute(Stage stage) {
    soundCircleRenderer.render(stage.getComponents(SoundReactiveCircle.class));
  }
}
