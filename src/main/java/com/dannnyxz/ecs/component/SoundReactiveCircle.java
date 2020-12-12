package com.dannnyxz.ecs.component;

import com.dannnyxz.ecs.Component;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * This circle is a intersection of a sphere and a plane orthogonal to circle's diameter.
 */
@Data
@AllArgsConstructor
public class SoundReactiveCircle implements Component {

  // from -1 to 1, 0 - central slice
  private float sliceOffset;
  private float sliceOffsetLimit;
  private float radius;
  private float timeSpeed;
  // time goes only when FFT amplitude goes
  private float localTime;
  private float minThickness;
  private float maxThickness;
  private float thickness;
  private int ringsCount;
  private int band;
  private Vector2i frequencyRange;
  private Quaternionf orientation;
  private Vector3f position;
  private Vector3f color;
}
