package com.example.library.sharedkernel.primitives;

import java.util.Optional;

public interface Repository<T extends AggregateRoot<ID>, ID> {
  Optional<T> findById(ID id);

  void save(T aggregate);
}
