package io.swapastack.gomoku.shared;

/**
 * This is the class for the test message.
 * The current implementations sends the TestMessage encoded
 * as JSON message to the WebSocket server.
 *
 * The JSON representation of this class could look like this:
 * <pre>
 * {@code
 * {
 *     "username": "Mario",
 *     "greeting": "Hello, it is me."
 * }
 * }
 * </pre>
 *
 * You can use GSON to convert between:
 * class instance <=> JSON string
 * see: https://github.com/google/gson/blob/master/UserGuide.md#TOC-Serializing-and-Deserializing-Generic-Types
 *
 * note: if you are reading this comment directly in the
 * source file, ignore the java doc related parts (pre, @code).
 *
 */
public class TestMessage {
    public String username;
    public String greeting;

    public TestMessage(String username, String greeting) {
        this.username = username;
        this.greeting = greeting;
    }
}
