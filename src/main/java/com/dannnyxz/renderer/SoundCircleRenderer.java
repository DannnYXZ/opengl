package com.dannnyxz.renderer;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import com.dannnyxz.ecs.component.SoundReactiveCircle;
import com.dannnyxz.util.ShaderProgram;
import java.nio.FloatBuffer;
import java.util.List;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class SoundCircleRenderer {

  private ShaderProgram movingRingsShaderProgram;
  private final int quadVaoId;
  private static final int SLICES_MAX = 32; // sphere slice
  private static final int RINGS_PER_SLICE_MAX = 128; // perimeter rings count
  private final FloatBuffer ringThicknessBuffer = MemoryUtil.memAllocFloat(SLICES_MAX);
  private final FloatBuffer sliceRadiusBuffer = MemoryUtil.memAllocFloat(SLICES_MAX);
  private final FloatBuffer ringsTotalBuffer = MemoryUtil.memAllocFloat(SLICES_MAX);
  private final FloatBuffer sliceModelBuffer = MemoryUtil.memAllocFloat(16 * SLICES_MAX);
  private final FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(3 * SLICES_MAX);
  private int ringThicknessVboId;
  private int sliceRadiusVboId;
  private int ringsTotalVboId;
  private int sliceModelMatrixVboId;
  private int colorVboId;

  public SoundCircleRenderer(ShaderProgram movingRingsShaderProgram) {
    this.movingRingsShaderProgram = movingRingsShaderProgram;

    float[] vertices = new float[]{
        -0.5f, -0.5f, 0f,
        0.5f, -0.5f, 0f,
        -0.5f, 0.5f, 0f,
        0.5f, 0.5f, 0f,
    };
    float[] barycentric = new float[]{
        0f, 0f, 0f,
        1f, 0f, 0f,
        0f, 1f, 0f,
        1f, 1f, 0f,
    };
    FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
    verticesBuffer.put(vertices).flip();
    FloatBuffer barycentricBuffer = MemoryUtil.memAllocFloat(barycentric.length);
    barycentricBuffer.put(barycentric).flip();

    quadVaoId = glGenVertexArrays();
    glBindVertexArray(quadVaoId);

    int quadVerticesVboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, quadVerticesVboId);
    glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
    glEnableVertexAttribArray(0);
    glVertexAttribDivisor(0, 0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

    int quadBarycentricVboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, quadBarycentricVboId);
    glBufferData(GL_ARRAY_BUFFER, barycentricBuffer, GL_STATIC_DRAW);
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

    ringThicknessVboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, ringThicknessVboId);
    glBufferData(GL_ARRAY_BUFFER, 4 * SLICES_MAX, GL_STREAM_DRAW);
    glEnableVertexAttribArray(2);
    glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(2, RINGS_PER_SLICE_MAX);

    sliceRadiusVboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, sliceRadiusVboId);
    glBufferData(GL_ARRAY_BUFFER, 4 * SLICES_MAX, GL_STREAM_DRAW);
    glEnableVertexAttribArray(3);
    glVertexAttribPointer(3, 1, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(3, RINGS_PER_SLICE_MAX);

    ringsTotalVboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, ringsTotalVboId);
    glBufferData(GL_ARRAY_BUFFER, 4 * SLICES_MAX, GL_STREAM_DRAW);
    glEnableVertexAttribArray(4);
    glVertexAttribPointer(4, 1, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(4, RINGS_PER_SLICE_MAX);

    sliceModelMatrixVboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, sliceModelMatrixVboId);
    glBufferData(GL_ARRAY_BUFFER, 4 * 16 * SLICES_MAX, GL_STREAM_DRAW);
    glEnableVertexAttribArray(5);
    glEnableVertexAttribArray(6);
    glEnableVertexAttribArray(7);
    glEnableVertexAttribArray(8);
    glVertexAttribPointer(5, 4, GL_FLOAT, false, 64, 0);
    glVertexAttribPointer(6, 4, GL_FLOAT, false, 64, 16);
    glVertexAttribPointer(7, 4, GL_FLOAT, false, 64, 32);
    glVertexAttribPointer(8, 4, GL_FLOAT, false, 64, 48);
    glVertexAttribDivisor(5, RINGS_PER_SLICE_MAX);
    glVertexAttribDivisor(6, RINGS_PER_SLICE_MAX);
    glVertexAttribDivisor(7, RINGS_PER_SLICE_MAX);
    glVertexAttribDivisor(8, RINGS_PER_SLICE_MAX);

    colorVboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
    glBufferData(GL_ARRAY_BUFFER, 3 * 4 * SLICES_MAX, GL_STREAM_DRAW);
    glEnableVertexAttribArray(9);
    glVertexAttribPointer(9, 3, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(9, RINGS_PER_SLICE_MAX);

    glBindBuffer(quadVerticesVboId, 0);
    glBindVertexArray(0);
    MemoryUtil.memFree(verticesBuffer);
    MemoryUtil.memFree(barycentricBuffer);
  }

  public void render(List<SoundReactiveCircle> circles) {
    movingRingsShaderProgram.bind();
    glBindVertexArray(quadVaoId);

    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer fb = new Matrix4f()
          // zNear actually will be -0.01
          .perspective(Math.toRadians(45), 1920f / 1080f, 0.01f, 100f) // TODO: generalize
          .mul(new Matrix4f().translate(0, 0, -1))
          .mul(new Matrix4f()
              .lookAt(new Vector3f(0, 0, 3f), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0)))
          .get(stack.mallocFloat(16));
      glUniformMatrix4fv(
          glGetUniformLocation(movingRingsShaderProgram.getProgramId(), "transform"),
          false, fb
      );
    }

    for (int i = 0; i < circles.size(); i++) {
      var circle = circles.get(i);
      ringThicknessBuffer.put(i, circle.getThickness());
      sliceRadiusBuffer.put(i, getSliceRadius(circle));
      ringsTotalBuffer.put(i, circle.getRingsCount());
      getSliceModelMatrix(circle).get(i * 16, sliceModelBuffer);
      circle.getColor().get(i * 3, colorBuffer);
    }

    glBindBuffer(GL_ARRAY_BUFFER, ringThicknessVboId);
    glBufferSubData(GL_ARRAY_BUFFER, 0, ringThicknessBuffer);

    glBindBuffer(GL_ARRAY_BUFFER, sliceRadiusVboId);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sliceRadiusBuffer);

    glBindBuffer(GL_ARRAY_BUFFER, ringsTotalVboId);
    glBufferSubData(GL_ARRAY_BUFFER, 0, ringsTotalBuffer);

    glBindBuffer(GL_ARRAY_BUFFER, sliceModelMatrixVboId);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sliceModelBuffer);

    glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
    glBufferSubData(GL_ARRAY_BUFFER, 0, colorBuffer);

    // actual drawing
    for (int i = 0, circlesSize = circles.size(); i < circlesSize; i++) {
      SoundReactiveCircle circle = circles.get(i);
      // workaround to preserve gl_InstanceID
      glBindBuffer(GL_ARRAY_BUFFER, ringThicknessVboId);
      glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 4L * i);

      glBindBuffer(GL_ARRAY_BUFFER, sliceRadiusVboId);
      glVertexAttribPointer(3, 1, GL_FLOAT, false, 0, 4L * i);

      glEnableVertexAttribArray(3);
      glVertexAttribPointer(3, 1, GL_FLOAT, false, 0, 4L * i);

      glBindBuffer(GL_ARRAY_BUFFER, ringsTotalVboId);
      glVertexAttribPointer(4, 1, GL_FLOAT, false, 0, 4L * i);

      glEnableVertexAttribArray(4);
      glVertexAttribPointer(4, 1, GL_FLOAT, false, 0, 4L * i);

      glBindBuffer(GL_ARRAY_BUFFER, sliceModelMatrixVboId);
      glVertexAttribPointer(5, 4, GL_FLOAT, false, 64, 64L * i);
      glVertexAttribPointer(6, 4, GL_FLOAT, false, 64, 64L * i + 16);
      glVertexAttribPointer(7, 4, GL_FLOAT, false, 64, 64L * i + 32);
      glVertexAttribPointer(8, 4, GL_FLOAT, false, 64, 64L * i + 48);

      glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
      glVertexAttribPointer(9, 3, GL_FLOAT, false, 0, 4 * 3L * i);

      glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, circle.getRingsCount());
    }
    glBindVertexArray(0);
    movingRingsShaderProgram.unbind();
  }

  private float getSliceRadius(SoundReactiveCircle circle) {
    return (float)
        Math.sqrt(
            pow(circle.getRadius(), 2)
                - pow(circle.getRadius() * abs(circle.getSliceOffset()), 2)
        );
  }

  private Matrix4f getSliceModelMatrix(SoundReactiveCircle circle) {
    return new Matrix4f()
        .rotate(circle.getOrientation())
        .translate(0, circle.getSliceOffset() * circle.getRadius(), 0);
  }
}
