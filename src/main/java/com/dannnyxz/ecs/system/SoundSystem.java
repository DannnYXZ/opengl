package com.dannnyxz.ecs.system;

import static java.lang.Float.max;
import static org.joml.Math.abs;
import static org.joml.Math.ceil;
import static org.joml.Math.floor;

import com.dannnyxz.ecs.Stage;
import com.dannnyxz.ecs.System;
import com.dannnyxz.ecs.component.SoundReactiveCircle;
import com.dannnyxz.util.StopWatch;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import java.io.InputStream;
import org.joml.Vector3f;

public class SoundSystem implements System {

  private final int AUDIO_BUFFER_SIZE = 1024;
  private final float NOISE_THRESHOLD = 0.2f;
  private final FFT inputFft;
  private final AudioInput audioInput;
  private final StopWatch stopWatch;

  public SoundSystem(StopWatch stopWatch) {
    this.stopWatch = stopWatch;
    Minim minim = new Minim(new Object() {
      public String sketchPath(String fileName) {
        return ".";
      }

      public InputStream createInput(String fileName) {
        return null;
      }
    });
    audioInput = minim.getLineIn(Minim.STEREO, AUDIO_BUFFER_SIZE);
    inputFft = new FFT(audioInput.bufferSize(), audioInput.sampleRate());
  }

  @Override
  public void execute(Stage stage) {
    inputFft.forward(audioInput.mix);
    stage.getEntitiesByComponent(SoundReactiveCircle.class).forEach(entity -> {
          var soundCircle = entity.getComponent(SoundReactiveCircle.class);
          updateCircle(soundCircle);
        }
    );
  }

  private float cutNoise(float amplitude, float threshold) {
    return max(0, amplitude - threshold) / (1 - threshold);
  }

  private void updateCircle(SoundReactiveCircle circle) {
    float sliceFreqAmplitude = cutNoise(inputFft.getBand(circle.getBand()), NOISE_THRESHOLD);
    float cvxFreqAmplitude = cutNoise(inputFft.getBand(circle.getBand() - 1), NOISE_THRESHOLD);
    float ccvxFreqAmplitude = cutNoise(inputFft.getBand(circle.getBand() - 2), NOISE_THRESHOLD);
    float cvzFreqAmplitude = cutNoise(inputFft.getBand(circle.getBand() + 1), NOISE_THRESHOLD);
    float ccvzFreqAmplitude = cutNoise(inputFft.getBand(circle.getBand() + 2), NOISE_THRESHOLD);
    float dt = stopWatch.getDeltaSeconds();
    float newTime = circle.getLocalTime() + dt * circle.getTimeSpeed() * sliceFreqAmplitude;
//    float newTime = circle.getLocalTime() + dt * circle.getTimeSpeed();
    float newSliceOffset =
        circle.getSliceOffsetLimit() * (2 * (2 * abs(fract(newTime * .5f) - .5f)) - 1);// * circle
    // .getSliceOffsetLimit();
    circle.setSliceOffset(newSliceOffset);
    circle.setLocalTime(newTime);
    circle.setThickness(
        circle.getMinThickness()
            + sliceFreqAmplitude * (circle.getMaxThickness() - circle.getMinThickness())
    );
    circle.getOrientation().rotateAxis(ccvxFreqAmplitude * circle.getTimeSpeed() * dt,
        new Vector3f(1, 0, 0));
    circle.getOrientation()
        .rotateAxis(-cvxFreqAmplitude * circle.getTimeSpeed() * dt, new Vector3f(1, 0, 0));
    circle.getOrientation()
        .rotateAxis(ccvzFreqAmplitude * circle.getTimeSpeed() * dt, new Vector3f(0, 0, 1));
    circle.getOrientation()
        .rotateAxis(-cvzFreqAmplitude * circle.getTimeSpeed() * dt, new Vector3f(0, 0, 1));
  }

  private float fract(float x) {
    return x - (x >= 0 ? floor(x) : ceil(x));
  }
}
