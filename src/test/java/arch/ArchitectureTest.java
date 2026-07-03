package arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.Architectures;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "org.bazar.bazarstorage",
        importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {
    public static final String FW_LAYER = "FW";
    public static final String ADAPTER_LAYER = "ADAPTER";
    public static final String APP_API_LAYER = "API";
    public static final String APP_IMPL_LAYER = "IMPL";
    public static final String DOMAIN_LAYER = "DOMAIN";

    @ArchTest
    void adapterLayerTest(JavaClasses classes) {
        getLayers()
                .whereLayer(ADAPTER_LAYER)
                .mayOnlyBeAccessedByLayers(FW_LAYER)
                .check(classes);
    }

    @ArchTest
    void appApiLayerTest(JavaClasses classes) {
        getLayers().whereLayer(APP_API_LAYER).mayOnlyAccessLayers(DOMAIN_LAYER).check(classes);
    }

    @ArchTest
    void appImplLayerTest(JavaClasses classes) {
        getLayers()
                .whereLayer(APP_IMPL_LAYER)
                .mayOnlyBeAccessedByLayers(FW_LAYER)
                .check(classes);
    }

    @ArchTest
    void domainLayerTest(JavaClasses classes) {
        getLayers().whereLayer(DOMAIN_LAYER).mayNotAccessAnyLayer().check(classes);
    }

    @ArchTest
    void fwTest(JavaClasses classes) {
        getLayers()
                .whereLayer(FW_LAYER)
                .mayNotBeAccessedByAnyLayer()
                .check(classes);
    }

    @ArchTest
    void dtoDependencyTest(JavaClasses classes) {
        noClasses()
                .that()
                .haveNameMatching(".*Dto")
                .should()
                .resideOutsideOfPackage("..adapter..")
                .check(classes);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Architectures.LayeredArchitecture getLayers() {
        return Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer(ADAPTER_LAYER)
                .definedBy("..adapter..")
                .layer(APP_API_LAYER)
                .definedBy("..app.api..")
                .layer(APP_IMPL_LAYER)
                .definedBy("..app.impl..")
                .layer(DOMAIN_LAYER)
                .definedBy("..domain..")
                .layer(FW_LAYER)
                .definedBy("..fw..");
    }
}
