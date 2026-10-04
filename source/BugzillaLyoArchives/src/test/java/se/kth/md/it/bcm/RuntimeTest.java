package se.kth.md.it.bcm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import se.kth.md.it.bcm.bugzilla.BugzillaArchive;

class RuntimeTest {
  @Test
  void logbackIsTheOnlySlf4jProvider() throws Exception {
    assertInstanceOf(LoggerContext.class, LoggerFactory.getILoggerFactory());
    var providers =
        java.util.Collections.list(
            getClass()
                .getClassLoader()
                .getResources("META-INF/services/org.slf4j.spi.SLF4JServiceProvider"));
    assertEquals(1, providers.size(), "Multiple logging providers conflict at runtime");
  }

  @Test
  void bundledArchiveLoadsAndFiltersByProductAndComponent() {
    var archive = BugzillaArchive.getInstance();
    assertTrue(archive.getProducts().contains("Lyo"));
    assertTrue(archive.getComponents("Lyo").size() > 0);
    var component = archive.getComponents("Lyo").getFirst();
    var bugs = archive.getBugsByProductAndComponent("Lyo", component);
    assertTrue(bugs.size() > 0);
    for (var bug : bugs) {
      assertEquals("Lyo", bug.getProduct());
      assertEquals(component, bug.getComponent());
      assertNotNull(archive.getBug(bug.getID()));
      assertNotNull(bug.getSummary());
    }
  }
}
