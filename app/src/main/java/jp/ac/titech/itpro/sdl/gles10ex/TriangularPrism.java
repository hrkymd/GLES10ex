package jp.ac.titech.itpro.sdl.gles10ex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class TriangularPrism implements SimpleRenderer.Obj{
    private FloatBuffer vbuf;
    private float x, y, z;

    public TriangularPrism(float s, float x, float y, float z) {
        float[] vertices = {
                // bottom
                s, 0, 0,
                0, 0, s,
                0, 0, -s,
                // left
                s, 0, 0,
                0, 0, s,
                s, s, 0,
                0, s, s,
                // right
                s, 0, 0,
                0, 0, -s,
                s, s, 0,
                0, s, -s,

                // back
                0, 0, s,
                0, s, s,
                0, 0, -s,
                0, s, -s,

                // top
                s, s, 0,
                0, s, s,
                0, s, -s

        };
        vbuf = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuf.put(vertices);
        vbuf.position(0);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vbuf);

        // bottom
        gl.glNormal3f(0, -1, 0);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

        // left
        gl.glNormal3f(1, 0, 1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 3, 4);


        // right
        gl.glNormal3f(1, 0, -1);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 7, 4);


        // back
        gl.glNormal3f(-1, 0, 0);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 11, 4);

        // top
        gl.glNormal3f(0, 1, 0);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 15, 3);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }
}
