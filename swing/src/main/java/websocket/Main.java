package websocket;

import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.stream.JsonParserFactory;
import javax.swing.*;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class Main {

    /**
     * A WebSocket message with a target UI element name and payload content.
     * Using a record gives us an immutable value object with auto-generated
     * accessors, equals, hashCode, and toString.
     */
    record Message(String target, String content) {}

    /**
     * Data transfer object for a search result received over WebSocket.
     * All fields are stored as camelCase to follow Java naming conventions.
     */
    record SearchResult(
            String name,
            String first,
            String dob,
            String zip,
            String ort,
            String street,
            String hausnr,
            String zeIban,
            String zeBic,
            String zeValidFrom
    ) {}

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    // CountDownLatch keeps the main thread alive until the WebSocket is closed
    private static CountDownLatch latch;

    // UI components
    private static final JFrame    frame           = new JFrame("Allegro");
    private static final JTextArea textArea        = new JTextArea();
    private static final JTextField tf_name        = new JTextField();
    private static final JTextField tf_first       = new JTextField();
    private static final JTextField tf_dob         = new JTextField();
    private static final JTextField tf_zip         = new JTextField();
    private static final JTextField tf_ort         = new JTextField();
    private static final JTextField tf_street      = new JTextField();
    private static final JTextField tf_hausnr      = new JTextField();
    private static final JTextField tf_ze_iban     = new JTextField();
    private static final JTextField tf_ze_bic      = new JTextField();
    private static final JTextField tf_ze_valid_from = new JTextField();

    private static final JRadioButton rb_female  = new JRadioButton("Weiblich");
    private static final JRadioButton rb_male    = new JRadioButton("Männlich");
    private static final JRadioButton rb_diverse = new JRadioButton("Divers");
    private static final ButtonGroup  bg_gender  = new ButtonGroup();

    private static final JsonParserFactory jsonParserFactory = Json.createParserFactory(null);

    public static void main(String[] args) throws IOException, DeploymentException {
        initUI();

        latch = new CountDownLatch(1);

        String uri = "ws://localhost:1337/";
        System.out.println("Connecting to " + uri);

        // Open WebSocket – the constructor blocks until the connection closes
        new WebsocketClientEndpoint(URI.create(uri));
    }

    private static void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());

        var c = new GridBagConstraints();
        c.ipady = 4;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.FIRST_LINE_END;

        c.gridx = 0; c.gridy = 0; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Vorname"), c);

        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_first, c);

        c.gridx = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Name"), c);

        c.gridx = 3; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_name, c);

        c.gridx = 4; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Geburtsdatum"), c);

        c.gridx = 5; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_dob, c);

        bg_gender.add(rb_female);
        bg_gender.add(rb_male);
        bg_gender.add(rb_diverse);
        rb_female.setSelected(true);

        c.gridx = 0; c.gridy = 1; c.weightx = 0; c.fill = GridBagConstraints.CENTER;
        panel.add(new JLabel("Geschlecht"), c);

        var genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.add(rb_female);
        genderPanel.add(rb_male);
        genderPanel.add(rb_diverse);

        c.gridx = 1; c.weightx = 1; c.gridwidth = 5; c.anchor = GridBagConstraints.WEST;
        panel.add(genderPanel, c);

        // Reset grid layout
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_END;

        c.gridx = 0; c.gridy = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Strasse"), c);

        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_street, c);

        c.gridx = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("PLZ"), c);

        c.gridx = 3; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_zip, c);

        c.gridx = 4; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Ort"), c);

        c.gridx = 5; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_ort, c);

        c.gridx = 0; c.gridy = 3; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("IBAN"), c);

        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_ze_iban, c);

        c.gridx = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("BIC"), c);

        c.gridx = 3; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_ze_bic, c);

        c.gridx = 4; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Gültig ab"), c);

        c.gridx = 5; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(tf_ze_valid_from, c);

        c.gridx = 0; c.gridy = 4; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("RT"), c);

        c.gridx = 1; c.gridy = 4; c.gridwidth = 6; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        textArea.setPreferredSize(new Dimension(200, 400));
        textArea.setBorder(BorderFactory.createEtchedBorder());
        panel.add(textArea, c); // single add with GridBagConstraints

        c.gridx = 1; c.gridy = 5; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        var button = new JButton("Anordnen");
        button.addActionListener(event -> System.out.println("Button clicked!"));
        panel.add(button, c);

        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 650);
        frame.setVisible(true);
    }

    // -------------------------------------------------------------------------
    // WebSocket endpoint (inner class)
    // -------------------------------------------------------------------------

    @javax.websocket.ClientEndpoint
    public static class WebsocketClientEndpoint {

        Session userSession = null;

        public WebsocketClientEndpoint(URI endpointURI) {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(this, endpointURI);
                latch.await();
            } catch (DeploymentException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @OnOpen
        public void onOpen(Session userSession) {
            System.out.println("opening websocket");
            this.userSession = userSession;
        }

        @OnClose
        public void onClose(Session userSession, CloseReason reason) {
            System.out.println("closing websocket");
            this.userSession = null;
            latch.countDown();
        }

        /**
         * Dispatches an incoming JSON message to the appropriate UI component.
         * Uses a modern switch statement with arrow labels (Java 14+).
         */
        @OnMessage
        public void onMessage(String json) {
            var message = extract(json);
            switch (message.target()) {
                case "textarea" -> textArea.setText(message.content());
                case "textfield" -> {
                    var sr = toSearchResult(message.content());
                    tf_name.setText(sr.name());
                    tf_first.setText(sr.first());
                    tf_dob.setText(sr.dob());
                    tf_zip.setText(sr.zip());
                    tf_ort.setText(sr.ort());
                    tf_street.setText(sr.street());
                    tf_hausnr.setText(sr.hausnr());
                    tf_ze_iban.setText(sr.zeIban());
                    tf_ze_bic.setText(sr.zeBic());
                    tf_ze_valid_from.setText(sr.zeValidFrom());
                }
                default -> LOGGER.log(Level.WARNING, "Unknown WebSocket message target: ''{0}''", message.target());
            }
        }

        public void sendMessage(String message) {
            this.userSession.getAsyncRemote().sendText(message);
        }
    }

    // -------------------------------------------------------------------------
    // JSON helpers
    // -------------------------------------------------------------------------

    /**
     * Extracts a {@link Message} from a JSON string of the form
     * {@code {"target":"…","content":"…"}}.
     * When target is "textfield" the full JSON string is used as the content
     * (it will be re-parsed by {@link #toSearchResult}).
     */
    static Message extract(String json) {
        var values = parseKeyValuePairs(json);
        String target  = values.getOrDefault("target",  "");
        // For "textarea" the JSON content field holds the text;
        // for "textfield" the whole JSON is forwarded to toSearchResult.
        String content = "textarea".equals(target)
                ? values.getOrDefault("content", "")
                : json;
        return new Message(target, content);
    }

    /**
     * Parses a flat JSON object from {@code json} and maps it to a
     * {@link SearchResult} record.
     */
    static SearchResult toSearchResult(String json) {
        var values = parseKeyValuePairs(json);
        return new SearchResult(
                values.getOrDefault("name",       ""),
                values.getOrDefault("first",      ""),
                values.getOrDefault("dob",        ""),
                values.getOrDefault("zip",        ""),
                values.getOrDefault("ort",        ""),
                values.getOrDefault("street",     ""),
                values.getOrDefault("hausnr",     ""),
                values.getOrDefault("iban",       ""),
                values.getOrDefault("bic",        ""),
                values.getOrDefault("valid_from", "")
        );
    }

    /**
     * Parses a flat JSON object into a {@code Map<String,String>} using the
     * streaming JSON-P parser.  Only top-level string values are collected.
     */
    private static Map<String, String> parseKeyValuePairs(String json) {
        Map<String, String> result = new HashMap<>();
        try (JsonParser parser = jsonParserFactory.createParser(new StringReader(json))) {
            String currentKey = null;
            while (parser.hasNext()) {
                Event e = parser.next();
                if (e == Event.KEY_NAME) {
                    currentKey = parser.getString();
                } else if (e == Event.VALUE_STRING && currentKey != null) {
                    result.put(currentKey, parser.getString());
                    currentKey = null;
                }
            }
        }
        return result;
    }
}
