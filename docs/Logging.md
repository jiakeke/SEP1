# Logging Utility

## Overview

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YourClass {

    private static final Logger logger = LoggerFactory.getLogger(YourClass.class);

    public void someMethod() {
        logger.trace("Trace log message");
        logger.debug("Debug log message");
        logger.info("Info log message");
        logger.warn("Warning log message");
        logger.error("Error log message");

        try {
            // Some code that may throw an exception
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            logger.error("Something exception happen: {}", e.getMessage());
            logger.error("Full exception stack:", e); // Log the full stack trace
        }
    }

    public static void main(String[] args) {
        // ...
        YourClass instance = new YourClass();
        instance.someMethod();
    }
}

```
