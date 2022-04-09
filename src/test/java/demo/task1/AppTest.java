package demo.task1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import task1.App;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Nested
    @DisplayName("Group Max")
    class Max{
        Double first = 1.2;
        Double second = 1.4;
        App appTest;
        @BeforeEach
        void setUp(){
            appTest = new App();
        }

        @Test
        @DisplayName("When comparing is first is bigger than second, second will be chosen")
        public void testMax() {
            assertEquals(appTest.max(first, second), second);
        }

        @Test
        @DisplayName("When comparing is second is bigger than first, second will be chosen")
        public void testMax2() {
            assertEquals(appTest.max(second, first), second);
        }
    }


    @Test
    @DisplayName("Test if output from method is equal to string")
    public void main() {
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        App appTest = new App();
        String [] som = {"Some text"};
        appTest.main(som);
        assertEquals("start\r\n", outputStreamCaptor.toString());
    }
}

