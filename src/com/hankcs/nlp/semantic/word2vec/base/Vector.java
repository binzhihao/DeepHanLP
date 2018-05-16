package com.hankcs.nlp.semantic.word2vec.base;

import java.util.Arrays;

/**
 * 数学概念中的 N 维向量
 */
public class Vector {
    float[] elementArray;

    public Vector(float[] elementArray) {
        this.elementArray = elementArray;
    }

    public Vector(int size) {
        elementArray = new float[size];
        Arrays.fill(elementArray, 0);
    }

    public int size() {
        return elementArray.length;
    }

    public float dot(Vector other) {
        float ret = 0.0f;
        for (int i = 0; i < size(); ++i) {
            ret += elementArray[i] * other.elementArray[i];
        }
        return ret;
    }

    public float norm() {
        float ret = 0.0f;
        for (int i = 0; i < size(); ++i) {
            ret += elementArray[i] * elementArray[i];
        }
        return (float) Math.sqrt(ret);
    }

    /**
     * 夹角的余弦<br>
     * 认为this和other都是单位向量，所以方法内部没有除以两者的模。
     *
     * @param other
     * @return
     */
    public float cosineForUnitVector(Vector other) {
        return dot(other);
    }

    /**
     * 夹角的余弦<br>
     *
     * @param other
     * @return
     */
    public float cosine(Vector other) {
        return dot(other) / this.norm() / other.norm();
    }

    public Vector minus(Vector other) {
        float[] result = new float[size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = elementArray[i] - other.elementArray[i];
        }
        return new Vector(result);
    }

    public Vector add(Vector other) {
        float[] result = new float[size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = elementArray[i] + other.elementArray[i];
        }
        return new Vector(result);
    }

    public Vector addToSelf(Vector other) {
        for (int i = 0; i < elementArray.length; i++) {
            elementArray[i] = elementArray[i] + other.elementArray[i];
        }
        return this;
    }

    public Vector divideToSelf(int n) {
        for (int i = 0; i < elementArray.length; i++) {
            elementArray[i] = elementArray[i] / n;
        }
        return this;
    }

    public Vector divideToSelf(float f) {
        for (int i = 0; i < elementArray.length; i++) {
            elementArray[i] = elementArray[i] / f;
        }
        return this;
    }

    /**
     * 自身归一化
     *
     * @return
     */
    public Vector normalize() {
        divideToSelf(norm());
        return this;
    }
}
