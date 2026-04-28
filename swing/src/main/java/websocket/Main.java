package websocket;

import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import javax.swing.*;

/**
 * WebSocket Swing client.
 *
 * <p>Connects to a local WebSocket server and populates a Swing form with
 * data received as JSON messages.</p>
 *
 * <p>Modernisation highlights (Java 21):</p>
 * <ul>
 *   <li>{@link Message} and {@link SearchResult} are Java records.</li>
 *   <li>JSON parsing lives in {@link MessageParser} (testable without AWT).</li>
 *   <li>Enhanced switch expression used in {@code onMessage}.</li>
 *   <li>Jakarta WebSocket API (javax.* namespace removed).</li>
 *   <li>Static UI fields declared {@code final}.</li>
 * </ul>
 */
public class Main {

    // CountDownLatch keeps the main thread alive until the server closes the connection.
    private static CountDownLatch latch;

    private static final JFrame     frame             = new JFrame("Allegro");
    private static final JTextArea  textArea          = new JTextArea();
    private static final JTextField tf_name           = new JTextField();
    private static final JTextField tf_first          = new JTextField();
    private static final JTextField tf_dob            = new JTextField();
    private static final JTextField tf_zip            = new JTextField();
    private static final JTextField tf_ort            = new JTextField();
    private static final JTextField tf_street         = new JTextField();
    private static final JTextField tf_hausnr         = new JTextField();
    private static final JTextField tf_ze_iban        = new JTextField();
    private static final JTextField tf_ze_bic         = new JTextField();
    private static final JTextField tf_ze_valid_from  = new JTextField();

    private static final JRadioButton rb_female  = new JRadioButton("Weiblich");
    private static final JRadioButton rb_male    = new JRadioButton("Männlich");
    private static final JRadioButton rb_diverse = new JRadioButton("Divers");
    private static final ButtonGroup  bg_gender  = new ButtonGroup();

    // -------------------------------------------------------------------------
    // Records – replace old hand-rolled mutable data classes
    // -------------------------------------------------------------------------

    /**
     * Parsed WebSocket message with a routing {@code target} and a
     * {@code content} payload.
     */
    public record Message(String target, String content) {}

    /**
     * Immutable projection of the search-result JSON payload.
     * Constructed by {@link MessageParser#toSearchResult(String)}.
     */
    public record SearchResult(
            String name,
            String first,
            String dob,
            String zip,
            String ort,
            String street,
            String hausnr,
            String iban,
            String bic,
            String validFrom
    ) {}

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    public static void main(String[] args) throws IOException, DeploymentException {
        initUI();

        latch = new CountDownLatch(1);

        var uri = "ws://localhost:1337/";
        System.out.println("Connecting to " + uri);

        // Constructor blocks until latch is released by onClose
        new WebsocketClientEndpoint(URI.create(uri));
    }

    // -------------------------------------------------------------------------
    // UI
    // -------------------------------------------------------------------------

    private static void initUI() {
        var panel = new JPanel(new GridBagLayout());
        var c = new GridBagConstraints();
        c.ipady = 4;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.FIRST_LINE_END;

        // Row 0: Vorname | first-name | Name | last-name | Geburtsdatum | dob
        addLabelAndField(panel, c, 0, 0, "Vorname",      tf_first, 1);
        addLabelAndField(panel, c, 2, 0, "Name",         tf_name,  3);
        addLabelAndField(panel, c, 4, 0, "Geburtsdatum", tf_dob,   5);

        // Row 1: Geschlecht radio-button group
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

        c.gridx = 1; c.gridy = 1; c.weightx = 1; c.gridwidth = 5;
        c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE;
        panel.add(genderPanel, c);
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_END;

        // Row 2: Strasse | PLZ | Ort
        addLabelAndField(panel, c, 0, 2, "Strasse", tf_street, 1);
        addLabelAndField(panel, c, 2, 2, "PLZ",     tf_zip,    3);
        addLabelAndField(panel, c, 4, 2, "Ort",     tf_ort,    5);

        // Row 3: IBAN | BIC | Gültig ab
        addLabelAndField(panel, c, 0, 3, "IBAN",      tf_ze_iban,       1);
        addLabelAndField(panel, c, 2, 3, "BIC",       tf_ze_bic,        3);
        addLabelAndField(panel, c, 4, 3, "Gültig ab", tf_ze_valid_from, 5);

        // Row 4: RT text area
        c.gridx = 0; c.gridy = 4; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("RT"), c);

        textArea.setPreferredSize(new Dimension(200, 400));
        textArea.setBorder(BorderFactory.createEtchedBorder());
        c.gridx = 1; c.gridy = 4; c.gridwidth = 6; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textArea, c);

        // Row 5: Anordnen button
        var button = new JButton("Anordnen");
        button.addActionListener(e -> System.out.println("Button clicked!"));
        c.gridx = 1; c.gridy = 5; c.weightx = 0;
        c.fill = GridBagConstraints.NONE; c.gridwidth = 1;
        panel.add(button, c);

        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 650);
        frame.setVisible(true);
    }

    /** Helper to add a label + text-field pair at specific grid coordinates. */
    private static void addLabelAndField(JPanel panel, GridBagConstraints c,
                                         int labelCol, int row, String labelText,
                                         JTextField field, int fieldCol) {
        c.gridx = labelCol; c.gridy = row; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(labelText), c);
        c.gridx = fieldCol; c.gridy = row; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);
    }

    // -------------------------------------------------------------------------
    // WebSocket endpoint (inner static class)
    // -------------------------------------------------------------------------

    @jakarta.websocket.ClientEndpoint
    public static class WebsocketClientEndpoint {

        private Session userSession;

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
            System.out.println("Opening WebSocket connection");
            this.userSession = userSession;
        }

        @OnClose
        public void onClose(Session userSession, CloseReason reason) {
            System.out.println("Closing WebSocket connection: " + reason);
            this.userSession = null;
            latch.countDown();
        }

        /**
         * Handles incoming JSON messages and routes them to the appropriate
         * UI component using an enhanced switch expression.
         *
         * <p>Parsing is delegated to {@link MessageParser} which has no
         * Swing/AWT dependencies and is therefore unit-testable.</p>
         *
         * @param json raw JSON message text
         */
        @OnMessage
        public void onMessage(String json) {
            var message = MessageParser.extract(json);
            switch (message.target()) {
                case "textarea" -> textArea.setText(message.content());
                case "textfield" -> {
                    var sr = MessageParser.toSearchResult(message.content());
                    tf_name.setText(sr.name());
                    tf_first.setText(sr.first());
                    tf_dob.setText(sr.dob());
                    tf_zip.setText(sr.zip());
                    tf_ort.setText(sr.ort());
                    tf_street.setText(sr.street());
                    tf_hausnr.setText(sr.hausnr());
                    tf_ze_iban.setText(sr.iban());
                    tf_ze_bic.setText(sr.bic());
                    tf_ze_valid_from.setText(sr.validFrom());
                }
                default -> System.out.println("Unknown message target: " + message.target());
            }
        }

        public void sendMessage(String message) {
            this.userSession.getAsyncRemote().sendText(message);
        }
    }
}
