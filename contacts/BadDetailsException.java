package contacts;


/**
 * Write a description of class BadDetailsException here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class BadDetailsException extends RuntimeException
{
    public BadDetailsException(String errorMessage) {
        super(errorMessage);
    }
}
