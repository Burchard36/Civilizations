package xyz.oli.wrapper;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class PathVector implements Cloneable {
    
    private double x;
    private double y;
    private double z;

    public PathVector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public PathVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NonNull
    public PathVector setX(double x) {
        this.x = x;
        return this;
    }

    @NonNull
    public PathVector setY(double y) {
        this.y = y;
        return this;
    }

    @NonNull
    public PathVector setZ(double z) {
        this.z = z;
        return this;
    }

    /**
     * Subtracts one vector from another
     * @param otherVector {@link PathVector} to vector to subtract from the current Vector
     * @return The same {@link PathVector}
     */
    @NonNull
    public PathVector subtract(PathVector otherVector) {
        this.x -= otherVector.x;
        this.y -= otherVector.y;
        this.z -= otherVector.z;
        return this;
    }

    /**
     * Multiplies itself by a scalar constant
     * @param value The constant to multiply by
     * @return The same {@link PathVector}
     */
    @NonNull
    public PathVector multiply(double value) {
        this.x *= value;
        this.y *= value;
        this.z *= value;
        return this;
    }

    @Override
    public PathVector clone() {
        final PathVector clone;
        try {
            clone = (PathVector) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Superclass messed up", ex);
        }
        clone.x = this.x;
        clone.y = this.y;
        clone.z = this.z;
        return clone;
    }

    /**
     * Normalises the {@link PathVector} (Divides the components by its magnitude)
     * @return The same {@link PathVector}
     */
    @NonNull
    public PathVector normalize() {
        double length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    private double square(double value){
        return value * value;
    }

    public double length() {
        return Math.sqrt(this.square(this.x) + this.square(this.y) + this.square(this.z));
    }

    /**
     * Gets the distance between this vector and another vector
     * @param otherVector The other vector
     * @return The distance
     */
    public double distance(PathVector otherVector) {
        return Math.sqrt(this.square(this.x - otherVector.x) + this.square(this.y - otherVector.y) + this.square(this.z - otherVector.z));
    }

    /**
     * Divide the vector by a scalar constant
     * @param value The constant to divide by
     * @return The same {@link PathVector}
     */
    public PathVector divide(double value) {
        this.x /= value;
        this.y /= value;
        this.z /= value;
        return this;
    }

    /**
     * Calculates the dot product of two vectors
     * @param otherVector The other vector
     * @return The dot product
     */
    public double dot(PathVector otherVector) {
        return this.x * otherVector.x + this.y * otherVector.y + this.z * otherVector.z;
    }

    /**
     * Adds two vectors together
     * @param otherVector The other vector
     * @return The same {@link PathVector}
     */
    public PathVector add(PathVector otherVector) {
        this.x += otherVector.x;
        this.y += otherVector.y;
        this.z += otherVector.z;
        return this;
    }

    /**
     * Finds the distance between the line BC and the point A
     * @param A The point
     * @param B The first point of the line
     * @param C The second point of the line
     * @return The distance
     */
    public static double computeDistance(PathVector A, PathVector B, PathVector C) {
        PathVector d = (C.subtract(B)).divide(C.distance(B));
        PathVector v = A.subtract(B);
        double t = v.dot(d);
        PathVector P = B.add(d.multiply(t));
        return P.distance(A);
    }

    /**
     * Calculates the cross product of two vectors
     * @param o The other vector
     * @return The cross product vector
     */
    public PathVector getCrossProduct(PathVector o) {
        double x = this.y * o.getZ() - o.getY() * this.z;
        double y = this.z * o.getX() - o.getZ() * this.x;
        double z = this.x * o.getY() - o.getX() * this.y;
        return new PathVector(x, y, z);
    }

}
