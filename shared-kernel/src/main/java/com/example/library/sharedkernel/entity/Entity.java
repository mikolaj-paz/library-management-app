package com.example.library.sharedkernel.entity;

import java.util.Objects;

public abstract class Entity<ID> {

  private final ID id;

  protected Entity(ID id) {
    Objects.requireNonNull(id, "Entity ID must not be null");
    this.id = id;
  }

  public ID id() {
    return id;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    Entity<?> entity = (Entity<?>) object;
    return id.equals(entity.id());
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
