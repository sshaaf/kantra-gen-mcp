package dev.shaaf.kantra.rules.gen;

import dev.shaaf.kantra.rules.gen.model.JavaLocation;

public class LocationLookup {

    public static void main(String[] args) {

        System.out.println("=== Location Lookup Examples ===\n");
        System.out.println(getMetadataForLocation());
        System.out.println("=== All Examples Complete ===");
    }


    public static String getMetadataForLocation() {
        StringBuilder sb = new StringBuilder();
        // Iterate over all JavaLocation values
        for (JavaLocation location : JavaLocation.values()) {
            sb.append("Location: ");
            sb.append(location + "\n");
            sb.append(getMethodForLocation(location));
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * Get the method name with parameters in markdown format.
     */
    public static String getMethodForLocation(JavaLocation location) {
        StringBuilder sb = new StringBuilder();
        sb.append("parameters:\n");

        // Common parameters for all rule methods
        sb.append("  - pattern: String (The pattern to match for this location)\n");

        // Location-specific parameters based on JavaReferencedService methods
        switch (location) {
            case IMPORT, METHOD_CALL, CONSTRUCTOR_CALL, INHERITANCE, IMPLEMENTS_TYPE, ENUM, RETURN_TYPE,
                 VARIABLE_DECLARATION, FIELD -> {
                sb.append("  - filepaths: String... (Optional: Filepath patterns to filter matches)\n");
            }
            case ANNOTATION -> {
                sb.append("  - elementsConfigurator: Consumer<AnnotationElementsBuilder> (Optional: Configure annotation elements)\n");
            }
            case TYPE -> {
                sb.append("  - annotationPattern: String (Optional: Annotation pattern to match on the type)\n");
                sb.append("  - elementsConfigurator: Consumer<AnnotationElementsBuilder> (Optional: Configure annotation elements)\n");
                sb.append("  - filepaths: String... (Optional: Filepath patterns to filter matches)\n");
            }
            case CLASS -> {
                sb.append("  - annotationPattern: String (Optional: Annotation pattern to match on the class)\n");
                sb.append("  - filepaths: String... (Optional: Filepath patterns to filter matches)\n");
            }
            case METHOD -> {
                sb.append("  - annotationPattern: String (Optional: Annotation pattern to match on the method)\n");
                sb.append("  - elementsConfigurator: Consumer<AnnotationElementsBuilder> (Optional: Configure annotation elements)\n");
                sb.append("  - filepaths: String... (Optional: Filepath patterns to filter matches)\n");
            }
            case PACKAGE -> {
            }
        }

        return sb.toString();
    }

}