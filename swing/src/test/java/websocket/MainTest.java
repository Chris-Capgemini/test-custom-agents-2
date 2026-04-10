package websocket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MessageParser} – the Swing-free JSON-parsing utility.
 *
 * <p>These tests do NOT load {@link Main} and therefore do not trigger the
 * AWT static initialisers, making them safe for headless CI environments.</p>
 */
@DisplayName("MessageParser JSON parsing")
class MainTest {

    // -------------------------------------------------------------------------
    // MessageParser.extract()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("extract: parses target=textarea, returns content value")
    void extractTextareaUsesContentValue() {
        var json = "{\"target\":\"textarea\",\"content\":\"Hello World\"}";
        var msg = MessageParser.extract(json);
        assertEquals("textarea", msg.target());
        assertEquals("Hello World", msg.content());
    }

    @Test
    @DisplayName("extract: parses target=textfield, returns full JSON as content")
    void extractTextfieldReturnsFullJson() {
        var json = "{\"target\":\"textfield\",\"content\":\"ignored\"}";
        var msg = MessageParser.extract(json);
        assertEquals("textfield", msg.target());
        // For non-textarea targets the full JSON string becomes the content
        assertEquals(json, msg.content());
    }

    @Test
    @DisplayName("extract: unknown target returns full JSON as content")
    void extractUnknownTargetReturnsFullJson() {
        var json = "{\"target\":\"unknown\",\"content\":\"data\"}";
        var msg = MessageParser.extract(json);
        assertEquals("unknown", msg.target());
        assertEquals(json, msg.content());
    }

    @Test
    @DisplayName("extract: empty JSON yields empty target and input JSON as content")
    void extractEmptyJson() {
        var json = "{}";
        var msg = MessageParser.extract(json);
        assertEquals("", msg.target());
        assertEquals(json, msg.content());
    }

    // -------------------------------------------------------------------------
    // MessageParser.toSearchResult()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toSearchResult: parses all standard fields")
    void toSearchResultParsesAllFields() {
        var json = """
                {
                  "name":       "Müller",
                  "first":      "Hans",
                  "dob":        "1980-01-15",
                  "zip":        "12345",
                  "ort":        "Berlin",
                  "street":     "Hauptstraße",
                  "hausnr":     "7",
                  "iban":       "DE89370400440532013000",
                  "bic":        "COBADEFFXXX",
                  "valid_from": "2024-01-01"
                }
                """;

        var sr = MessageParser.toSearchResult(json);

        assertEquals("Müller",                 sr.name());
        assertEquals("Hans",                   sr.first());
        assertEquals("1980-01-15",             sr.dob());
        assertEquals("12345",                  sr.zip());
        assertEquals("Berlin",                 sr.ort());
        assertEquals("Hauptstraße",            sr.street());
        assertEquals("7",                      sr.hausnr());
        assertEquals("DE89370400440532013000", sr.iban());
        assertEquals("COBADEFFXXX",            sr.bic());
        assertEquals("2024-01-01",             sr.validFrom());
    }

    @Test
    @DisplayName("toSearchResult: missing fields default to empty string")
    void toSearchResultMissingFieldsDefaultToEmpty() {
        var sr = MessageParser.toSearchResult("{\"name\":\"Only\"}");

        assertEquals("Only", sr.name());
        assertEquals("", sr.first());
        assertEquals("", sr.dob());
        assertEquals("", sr.zip());
        assertEquals("", sr.ort());
        assertEquals("", sr.street());
        assertEquals("", sr.hausnr());
        assertEquals("", sr.iban());
        assertEquals("", sr.bic());
        assertEquals("", sr.validFrom());
    }

    @Test
    @DisplayName("toSearchResult: empty JSON yields all empty strings")
    void toSearchResultEmptyJson() {
        var sr = MessageParser.toSearchResult("{}");
        assertEquals("", sr.name());
        assertEquals("", sr.validFrom());
    }
}
