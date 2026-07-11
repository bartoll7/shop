package com.example.shop.shared;

/**
 * A functional interface representing a specification that can be used to determine if an object satisfies certain criteria.
 * @param <T> The type of object to be evaluated by the specification.
 */
@FunctionalInterface
public interface Specification<T> {
    boolean isSatisfiedBy(T candidate);

    /**
     * Combines this specification with another specification using a logical AND operation.
     * @param other The other specification to combine with.
     * @return A new specification that is satisfied if both this and the other specification are satisfied.
     */
    default Specification<T> and(Specification<T> other) {
        return candidate -> this.isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
    }

    /**
     * Combines this specification with another specification using a logical OR operation.
     * @param other The other specification to combine with.
     * @return A new specification that is satisfied if either this or the other specification is satisfied.
     */
    default Specification<T> or(Specification<T> other) {
        return candidate -> this.isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
    }

    /**
     * Negates this specification, creating a new specification that is satisfied if this specification is not satisfied.
     * @return A new specification that is satisfied if this specification is not satisfied.
     */
    default Specification<T> not() {
        return candidate -> !this.isSatisfiedBy(candidate);
    }
}
