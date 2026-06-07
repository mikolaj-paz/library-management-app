package com.example.library.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.catalog.domain.book.Book;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.reader.Reader;
import com.example.library.lending.domain.reservation.Reservation;
import com.example.library.sharedkernel.aggregate.AggregateRoot;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

  private final JavaClasses productionClasses =
      new ClassFileImporter().importPackages("com.example.library");

  @Test
  void should_keep_domain_layers_free_from_framework_dependencies() {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("org.springframework..", "java.sql..", "jakarta..")
        .check(productionClasses);
  }

  @Test
  void should_prevent_application_layers_from_depending_on_infrastructure() {
    noClasses()
        .that()
        .resideInAPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..infrastructure..")
        .check(productionClasses);
  }

  @Test
  void should_keep_lending_and_catalog_domain_models_isolated() {
    noClasses()
        .that()
        .resideInAPackage("com.example.library.lending.domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("com.example.library.catalog.domain..")
        .check(productionClasses);

    noClasses()
        .that()
        .resideInAPackage("com.example.library.catalog.domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("com.example.library.lending.domain..")
        .check(productionClasses);
  }

  @Test
  void should_model_domain_entities_as_aggregate_roots() {
    assertThat(Book.class).isAssignableTo(AggregateRoot.class);
    assertThat(com.example.library.catalog.domain.copy.BookCopy.class)
        .isAssignableTo(AggregateRoot.class);
    assertThat(com.example.library.lending.domain.copy.BookCopy.class)
        .isAssignableTo(AggregateRoot.class);
    assertThat(Loan.class).isAssignableTo(AggregateRoot.class);
    assertThat(Reader.class).isAssignableTo(AggregateRoot.class);
    assertThat(Reservation.class).isAssignableTo(AggregateRoot.class);
  }
}
