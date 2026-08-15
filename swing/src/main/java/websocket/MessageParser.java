package websocket;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;

import java.io.StringReader;
import java.util.HashMap;

/**
 * Pure JSON-parsing utilities for WebSocket message handling.
 *
 * <p>Keeping these methods in a Swing-free class allows them to be unit-tested
 * in a headless environment without triggering the AWT/Swing static
 * initialisers in {@link Main}.</p>
 */
public final class MessageParser {

    /** Shared, thread-safe Jakarta JSON parser factory. */
    private static final jakarta.json.stream.JsonParserFactory PARSER_FACTORY =
            Json.createParserFactory(null);

    private MessageParser() {}

    /**
     * Parses a JSON string into a {@link Main.Message}.
     *
     * <p>When {@code target == "textarea"} the extracted {@code content} value
     * is used verbatim; for all other targets the full {@code json} string is
     * returned as the content so that downstream handlers can re-parse it.</p>
     *
     * @param json raw JSON text
     * @return a non-null {@link Main.Message}
     */
    public static Main.Message extract(String json) {
        String target = "";
        String rawContent = json; // default: route full JSON for non-textarea targets

        try (var parser = PARSER_FACTORY.createParser(new StringReader(json))) {
            String currentKey = null;
            while (parser.hasNext()) {
                var event = parser.next();
                if (event == JsonParser.Event.KEY_NAME) {
                    currentKey = parser.getString();
                } else if (event == JsonParser.Event.VALUE_STRING && currentKey != null) {
                    switch (currentKey) {
                        case "target"  -> target     = parser.getString();
                        case "content" -> rawContent = parser.getString();
                    }
                    currentKey = null;
                }
            }
        }

        var content = "textarea".equals(target) ? rawContent : json;
        return new Main.Message(target, content);
    }

    /**
     * Parses a JSON string into a {@link Main.SearchResult} record.
     *
     * <p>Key-tracking replaces the previous per-field boolean-flag approach,
     * reducing the parsing code from ~80 lines to ~20.</p>
     *
     * @param json raw JSON text
     * @return a non-null {@link Main.SearchResult} (missing fields default to {@code ""})
     */
    public static Main.SearchResult toSearchResult(String json) {
        var fields = new HashMap<String, String>();

        try (var parser = PARSER_FACTORY.createParser(new StringReader(json))) {
            String currentKey = null;
            while (parser.hasNext()) {
                var event = parser.next();
                if (event == JsonParser.Event.KEY_NAME) {
                    currentKey = parser.getString();
                } else if (event == JsonParser.Event.VALUE_STRING && currentKey != null) {
                    fields.put(currentKey, parser.getString());
                    currentKey = null;
                }
            }
        }

        return new Main.SearchResult(
                fields.getOrDefault("name",       ""),
                fields.getOrDefault("first",      ""),
                fields.getOrDefault("dob",        ""),
                fields.getOrDefault("zip",        ""),
                fields.getOrDefault("ort",        ""),
                fields.getOrDefault("street",     ""),
                fields.getOrDefault("hausnr",     ""),
                fields.getOrDefault("iban",       ""),
                fields.getOrDefault("bic",        ""),
                fields.getOrDefault("valid_from", "")
        );
    }
}
