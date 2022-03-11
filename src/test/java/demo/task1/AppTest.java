package demo.task1;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {


    @Test
    public void testMax() {
        App appTest = new App();
        Double first = 1.2;
        Double second = 1.4;

        assertEquals(appTest.max(first, second), second);
    }

    @Test
    public void testMax2() {
        App appTest = new App();
        Double first = 1.2;
        Double second = 1.4;

        assertEquals(appTest.max(second, first), second);
    }

    @Test
    public void main() {
        final PrintStream standardOut = System.out;
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        App appTest = new App();
        String [] som = {"yep"};
        appTest.main(som);
        assertEquals("start\r\n", outputStreamCaptor.toString());
    }
}

