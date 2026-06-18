package com.example.library.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.catalog.domain.book.Book;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.reader.Reader;
import com.example.library.lending.domain.reservation.Reservation;
import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.users.domain.reader.ReaderAccount;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

  private final JavaClasses productionClasses =
      new ClassFileImporter().importPackages("com.example.library");

  private static final String[] DOMAIN_PACKAGES = {
    "com.example.library.catalog.domain..",
    "com.example.library.lending.domain..",
    "com.example.library.notifications.domain..",
    "com.example.library.users.domain.."
  };

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
  void should_keep_application_layers_free_from_framework_dependencies() {
    noClasses()
        .that()
        .resideInAPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage("org.springframework..", "java.sql..", "jakarta..")
        .check(productionClasses);
  }

  @Test
  void should_prevent_inner_layers_from_depending_on_infrastructure() {
    noClasses()
        .that()
        .resideInAnyPackage("..domain..", "..application..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..infrastructure..")
        .check(productionClasses);
  }

  @Test
  void should_model_application_ports_as_interfaces() {
    classes()
        .that()
        .resideInAPackage("..application.port..")
        .should()
        .beInterfaces()
        .check(productionClasses);
  }

  @Test
  void should_keep_web_adapters_from_calling_application_services_directly() {
    noClasses()
        .that()
        .resideInAPackage("..infrastructure.in.web..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..application.service..")
        .check(productionClasses);
  }

  @Test
  void should_keep_input_and_output_adapters_separated() {
    noClasses()
        .that()
        .resideInAPackage("..infrastructure.in..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..infrastructure.out..")
        .check(productionClasses);

    noClasses()
        .that()
        .resideInAPackage("..infrastructure.out..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..infrastructure.in..")
        .check(productionClasses);
  }

  @Test
  void should_keep_domain_models_of_bounded_contexts_isolated() {
    assertDomainIsIndependentFromOtherDomains("com.example.library.catalog.domain..");
    assertDomainIsIndependentFromOtherDomains("com.example.library.lending.domain..");
    assertDomainIsIndependentFromOtherDomains("com.example.library.notifications.domain..");
    assertDomainIsIndependentFromOtherDomains("com.example.library.users.domain..");
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
    assertThat(ReaderAccount.class).isAssignableTo(AggregateRoot.class);
  }

  private void assertDomainIsIndependentFromOtherDomains(String domainPackage) {
    noClasses()
        .that()
        .resideInAPackage(domainPackage)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(otherDomainPackages(domainPackage))
        .check(productionClasses);
  }

  private String[] otherDomainPackages(String domainPackage) {
    return java.util.Arrays.stream(DOMAIN_PACKAGES)
        .filter(packageName -> !packageName.equals(domainPackage))
        .toArray(String[]::new);
  }
}
