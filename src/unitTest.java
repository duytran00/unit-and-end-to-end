import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

public class unitTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private final PrinttokensFixed p = new PrinttokensFixed();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void testOpenCharacterStream() throws IOException {
        assertAll(
                () -> {
                    BufferedReader br = p.open_character_stream(null);
                    assertNotNull(br, "expected return for stdin");
                },
                () -> {
                    BufferedReader br = p.open_character_stream("test.txt");
                    assertNotNull(br, "Failed to open 'test.txt'");
                    assertEquals("xor ( a1 123", br.readLine());
                }
        );
    }

    @Test
    public void testOpenTokenStream() throws IOException {
        assertAll(
                () -> {
                    BufferedReader br = p.open_token_stream(null);
                    assertNotNull(br, "expecetd return for null filename");
                },
                () -> {
                    BufferedReader br = p.open_token_stream("");
                    assertNotNull(br, "expecetd return for empty string filename");
                },
                () -> {
                    BufferedReader br = p.open_token_stream("test2.txt");
                    assertNotNull(br, "Failed to open 'test2.txt'");
                    // Reading a line from an empty file should return null.
                    assertNull(br.readLine());
                }
        );
    }

    @Test
    public void testIsSpecSymbol() {
        assertAll(
	        () -> assertTrue(PrinttokensFixed.is_spec_symbol('('), "path 1 failed: '(' expected true"),
	        () -> assertTrue(PrinttokensFixed.is_spec_symbol(')'), "path 2 failed: ')' expected true"),
	        () -> assertTrue(PrinttokensFixed.is_spec_symbol('['), "path 3 failed: '[' expected true"),
	        () -> assertTrue(PrinttokensFixed.is_spec_symbol(']'), "path 4 failed: ']' expected true"),
	        () -> assertFalse(PrinttokensFixed.is_spec_symbol('/'), "path 5 failed: '/' expected false"),
	        () -> assertTrue(PrinttokensFixed.is_spec_symbol('`'), "path 6 failed: '`' expected true"),
	        () -> assertTrue(PrinttokensFixed.is_spec_symbol(','), "path 7 failed: ',' expected true"),
	        () -> assertFalse(PrinttokensFixed.is_spec_symbol('!'), "path 8 failed: '!' expected false")
        );
    }
    
    @Test
    public void testPrintSpecSymbol() {
    	assertAll(
	        () -> {
	            PrinttokensFixed.print_spec_symbol(")");
	            String result = outContent.toString().replaceAll("\\r?\\n", "");
	            assertEquals("rparen.", result, "path 1 failed: ')' expected 'rparen.' ");
	            outContent.reset();
	        },
	        () -> {
	            PrinttokensFixed.print_spec_symbol("(");
	            String result = outContent.toString().replaceAll("\\r?\\n", "");
	            assertEquals("lparen.", result, "path 2 failed: '(' expected 'lparen.'");
	            outContent.reset();
	        },
	        () -> {
	            PrinttokensFixed.print_spec_symbol("[");
	            String result = outContent.toString().replaceAll("\\r?\\n", "");
	            assertEquals("lsquare.", result, "path 3 failed: '[' expected 'lsquare.'");
	            outContent.reset();
	        },
	        () -> {
	            PrinttokensFixed.print_spec_symbol("]");
	            String result = outContent.toString().replaceAll("\\r?\\n", "");
	            assertEquals("rsquare.", result, "path 4 failed: ']' expected 'rsquare.'");
	            outContent.reset();
	        },
	        () -> {
	            PrinttokensFixed.print_spec_symbol("'");
	            String result = outContent.toString().replaceAll("\\r?\\n", "");
	            assertEquals("quote.", result, "path 5 failed: ''' expected 'quote.'");
	            outContent.reset();
	        },
	        () -> {
	            PrinttokensFixed.print_spec_symbol("`");
	            String result = outContent.toString().replaceAll("\\r?\\n", "");
	            assertEquals("bquote.", result, "path 6 failed: '`' expected 'bquote.'");
	            outContent.reset();
	        },
	        () -> {
	            PrinttokensFixed.print_spec_symbol(",");
	            String result = outContent.toString().replaceAll("\\r?\\n", "");
	            assertEquals("comma.", result, "path 7 failed: ',' expected 'comma.'");
	            outContent.reset();
	        }
    	);
    }
    
    @Test
    public void testIsIdentifier() {
    	assertAll(
			() -> assertFalse(PrinttokensFixed.is_identifier("#ac"), "path 1 failed: '#ac' expected false"),
			() -> assertTrue(PrinttokensFixed.is_identifier("a"), "path 2 failed: 'a' expected true"),
			() -> assertFalse(PrinttokensFixed.is_identifier("a#"), "path 3 failed: 'a#' expected false"),
			() -> assertTrue(PrinttokensFixed.is_identifier("aa1"), "path 4 failed: 'aa1' expected true")
        );
    }
    
    @Test
    public void testIsStrConstant() {
        assertAll(
            () -> assertFalse(PrinttokensFixed.is_str_constant("f"), "path 1 failed: 'f' expected false"),
            () -> assertFalse(PrinttokensFixed.is_str_constant("\""), "path 2 failed: '\"' expected false"),
            () -> assertFalse(PrinttokensFixed.is_str_constant("\"f"), "path 3 failed: '\"f' expected false"),
            () -> assertTrue(PrinttokensFixed.is_str_constant("\"f2\""), "path 4 failed: '\"f2\"' expected true")
        );
    }

    @Test
    public void testIsNumConstant() {
        assertAll(
            () -> assertFalse(PrinttokensFixed.is_num_constant("f"), "path 1 failed: 'f' expected false"),
            () -> assertTrue(PrinttokensFixed.is_num_constant("1"), "path 2 failed: '1' expected true"),
            () -> assertFalse(PrinttokensFixed.is_num_constant("1a"), "path 3 failed: '1a' expected false"),
            () -> assertTrue(PrinttokensFixed.is_num_constant("32"), "path 4 failed: '32' expected true")
        );
    }

    @Test
    public void testIsCharConstant() {
        assertAll(
            () -> assertTrue(PrinttokensFixed.is_char_constant("#f"), "path 1 failed: '#f' expected true"),
            () -> assertFalse(PrinttokensFixed.is_char_constant("f"), "path 2 failed: 'f' expected false"),
            () -> assertFalse(PrinttokensFixed.is_char_constant("#ff"), "failed: '#ff' expected false")
        );
    }

    @Test
    public void testIsKeyword() {
        assertAll(
            () -> assertTrue(PrinttokensFixed.is_keyword("=>"), "path 1 failed: '=>' expected true"),
            () -> assertFalse(PrinttokensFixed.is_keyword("not"), "path 2 failed: 'not' expected false")
        );
    }

    @Test
    public void testIsComment() {
        assertAll(
            () -> assertTrue(PrinttokensFixed.is_comment("; is it?"), "path 1 failed: '; is it?' expected true"),
            () -> assertFalse(PrinttokensFixed.is_comment("is it?"), "path 2 failed: 'is it?' expected false")
        );
    }

    @Test
    public void testPrintToken() {
        assertAll(
            () -> {
                p.print_token("(");
                String result = outContent.toString().replaceAll("\\r?\\n", "");
                assertEquals("lparen.", result, "failed for token '(': expected 'lparen.'");
                outContent.reset();
            },
            () -> {
                p.print_token("a1");
                String result = outContent.toString().replaceAll("\\r?\\n", "");
                assertEquals("identifier,\"a1\".", result, "failed for token 'a1': expected 'identifier,\"a1\".'");
                outContent.reset();
            },
            () -> {
                p.print_token("123");
                String result = outContent.toString().replaceAll("\\r?\\n", "");
                assertEquals("numeric,123.", result, "failed for token '123': expected 'numeric,123.'");
                outContent.reset();
            },
            () -> {
                p.print_token("\"asd\"");
                String result = outContent.toString().replaceAll("\\r?\\n", "");
                assertEquals("string,\"asd\".", result, "failed for token '\"asd\"': expected 'string,\"asd\".'");
                outContent.reset();
            },
            () -> {
                p.print_token("#a");
                String result = outContent.toString().replaceAll("\\r?\\n", "");
                assertEquals("character,\"a\".", result, "failed for token '#a': expected 'character,\"a\".'");
                outContent.reset();
            },
            () -> {
                p.print_token(";comment ");
                String result = outContent.toString().replaceAll("\\r?\\n", "");
                assertEquals("comment,\";comment \".", result, "failed for token ';comment ': expected 'comment,\";comment \".'");
                outContent.reset();
            }
        );
    }

    @Test
    public void testTokenType() {
        assertAll(
            () -> assertEquals(1, PrinttokensFixed.token_type("and"), "path 1 failed: 'and' expected 1"),
            () -> assertEquals(2, PrinttokensFixed.token_type("["), "path 2 failed: '[' expected 2"),
            () -> assertEquals(3, PrinttokensFixed.token_type("a2"), "path 3 failed: 'a2' expected 3"),
            () -> assertEquals(41, PrinttokensFixed.token_type("22"), "path 4 failed: '22' expected 41"),
            () -> assertEquals(42, PrinttokensFixed.token_type("\"f\""), "path 5 failed: '\"f\"' expected 42"),
            () -> assertEquals(43, PrinttokensFixed.token_type("#e"), "path 6 failed: '#e' expected 43"),
            () -> assertEquals(5, PrinttokensFixed.token_type(";e"), "path 7 failed: ';e' expected 5"),
            () -> assertEquals(0, PrinttokensFixed.token_type("#"), "path 8 failed: '#' expected 0")
        );
    }

    @Test
    public void testIsTokenEnd() {
        assertAll(
            () -> assertTrue(PrinttokensFixed.is_token_end(0, -1), "path 1 failed: eof should end token"),
            () -> assertTrue(PrinttokensFixed.is_token_end(2, '"'), "path 2 failed: '\"' should end a string token"),
            () -> assertFalse(PrinttokensFixed.is_token_end(2, 'a'), "path 3 failed: 'a' should not end a string token"),
            () -> assertTrue(PrinttokensFixed.is_token_end(1, '\n'), "path 4 failed: newline should end a comment token"),
            () -> assertFalse(PrinttokensFixed.is_token_end(1, 'b'), "path 5 failed: 'b' should not end a comment token"),
            () -> assertTrue(PrinttokensFixed.is_token_end(0, '('), "path 6 failed: '(' is a special symbol and should end a token"),
            () -> assertTrue(PrinttokensFixed.is_token_end(0, ' '), "path 7 failed: space should end a token"),
            () -> assertFalse(PrinttokensFixed.is_token_end(0, 'd'), "path 8 failed: 'd' should not end a regular token")
        );
    }

    @Test
    public void testGetToken() {
        assertAll(
            () -> {
                BufferedReader br = new BufferedReader(new StringReader(""));
                assertNull(p.get_token(br), "path 1 failed: empty input should return null");
            },
            () -> {
                BufferedReader br = new BufferedReader(new StringReader("\n"));
                assertNull(p.get_token(br), "path 2 failed: newline input should return null");
            },
            () -> {
                BufferedReader br = new BufferedReader(new StringReader("("));
                assertEquals("(", p.get_token(br), "path 3 failed: '(' should return '('");
            },
             () -> {
                BufferedReader br = new BufferedReader(new StringReader("a"));
                assertEquals("a", p.get_token(br), "path 6 failed: 'a' should return 'a'");
            },
            () -> {
                BufferedReader br = new BufferedReader(new StringReader(";word"));
                assertEquals(";word", p.get_token(br), "path 7 failed: ';word' should return ';word'");
            },
            () -> {
                BufferedReader br = new BufferedReader(new StringReader("as]"));
                assertEquals("as", p.get_token(br), "path 9 failed: 'as]' should return 'as'");
                assertEquals("]", p.get_token(br), "path 9 followup failed: should read ']' next");
            },
            () -> {
                BufferedReader br = new BufferedReader(new StringReader("\"word\""));
                assertEquals("\"word\"", p.get_token(br), "path 10 failed: '\"word\"' should return '\"word\"'");
            },
            () -> {
                BufferedReader br = new BufferedReader(new StringReader("word!"));
                assertEquals("word!", p.get_token(br), "path 11 failed: 'word!' should return 'word!'");
            },
            () -> {
                BufferedReader br = new BufferedReader(new StringReader("word;"));
                assertEquals("word", p.get_token(br), "Failed to get token before semicolon");
                assertEquals(";", p.get_token(br), "Failed to unget and get semicolon");
            }
        );
    }
}
