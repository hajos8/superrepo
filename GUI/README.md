# JavaFX GUI Template

Reusable JavaFX template wired to a simple headered CSV loader. Adjust field names, delimiter, and column mappings to your data.

## Structure
- `pom.xml`: JavaFX + Maven setup (javafx-controls/fxml, plugin entrypoint `org.example.template.TemplateApp`).
- `src/main/java/org/example/template/`: `TemplateApp`, `TemplateController`, `CsvReader`, `DataRecord`.
- `src/main/resources/org/example/template/template.fxml`: Table view with filter/search and summary labels.

## Usage
1. Update `DELIMITER`, date format, and column mappings in `CsvReader` to match your CSV.
2. Adjust `DataRecord` fields and FXML table columns to your domain.
3. Run with Maven: `mvn javafx:run`.

## Notes
- Minimal error handling; surface IO issues in UI as needed.
- Filtering uses simple text contains on primary/secondary fields; tweak as needed.
