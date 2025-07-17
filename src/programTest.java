import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class programTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    public void testCase1() {
        provideInput("(");
        PrinttokensFixed.main(new String[]{});
        String expected = "lparen.\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void testCase2() {
        provideInput("xor ] a1 123 \"asd\" #a ;comment 123a");
        PrinttokensFixed.main(new String[]{});
        String expected = "keyword,\"xor\".\n" +
                          "rsquare.\n" +
                          "identifier,\"a1\".\n" +
                          "numeric,123.\n" +
                          "string,\"asd\".\n" +
                          "character,\"a\".\n" +
                          "comment,\";comment 123a\".\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void testCase3() {
        provideInput("`var 456 \"test\"");
        PrinttokensFixed.main(new String[]{});
        String expected = "bquote.\n" +
                          "identifier,\"var\".\n" +
                          "numeric,456.\n" +
                          "string,\"test\".\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void testCase4() {
        provideInput("'lambda 789 #t");
        PrinttokensFixed.main(new String[]{});
        String expected = "quote.\n" +
                          "keyword,\"lambda\".\n" +
                          "numeric,789.\n" +
                          "character,\"t\".\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void testCase5() {
        String[] args = {"test2.txt"};
        PrinttokensFixed.main(args);
        assertEquals("", outContent.toString());
    }

    @Test
    public void testCase6() {
        provideInput("af");
        PrinttokensFixed.main(new String[]{});
        String expected = "identifier,\"af\".\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void testCase7() {
        provideInput("g4");
        PrinttokensFixed.main(new String[]{});
        String expected = "identifier,\"g4\".\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void testCase8() {
        // Input: "hello"
        provideInput("\"hello\"");
        PrinttokensFixed.main(new String[]{});
        String expected = "string,\"hello\".\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void testCase9() {
        provideInput(",");
        PrinttokensFixed.main(new String[]{});
        String expected = "comma.\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    public void testCase10() {
        provideInput("");
        PrinttokensFixed.main(new String[]{});
        assertEquals("", outContent.toString());
    }

    @Test
    public void testCase11() {
        String[] args = {"test.txt", "test2.txt"};
        PrinttokensFixed.main(args);
        String expected = "Error! Please give the token stream\n";
        assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"));
    }
    
    @Test
    public void testCoverageCases() {
        assertAll(
                () -> {
                    provideInput(") [");
                    PrinttokensFixed.main(new String[]{});
                    String expected = "rparen.\n" + "lsquare.\n";
                    assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"),
                            "Failed to cover special symbols ')' and '['.");
                    outContent.reset();
                },
                () -> {
                    provideInput("\"unclosed");
                    PrinttokensFixed.main(new String[]{});
                    String expected = "error,\"\\\"unclosed\".\n";
                    assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"),
                            "Failed to cover unterminated string error.");
                    outContent.reset();
                },
                () -> {
                    provideInput("ident#ifier 123x");
                    PrinttokensFixed.main(new String[]{});
                    String expected = "error,\"ident#ifier\".\n" + "error,\"123x\".\n";
                    assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"),
                            "Failed to cover invalid identifier and numeric errors.");
                    outContent.reset();
                },
                () -> {
                    provideInput("!");
                    PrinttokensFixed.main(new String[]{});
                    String expected = "error,\"!\".\n";
                    assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"),
                            "Failed to cover general error token.");
                    outContent.reset();
                },
                () -> {
                    provideInput("abc;comment");
                    PrinttokensFixed.main(new String[]{});
                    String expected = "identifier,\"abc\".\n" + "comment,\";comment\".\n";
                    assertEquals(expected, outContent.toString().replaceAll("\\r\\n", "\n"),
                            "Failed to cover token followed by semicolon.");
                    outContent.reset();
                }
        );
    }
}
