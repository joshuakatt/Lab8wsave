package grading;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import org.junit.jupiter.api.extension.ExtendWith;
import contacts.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * The test class CompleteAddressBookTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */

@ExtendWith(GradingTestWatcher.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@GradeValue(10)
public class CompleteAddressBookTest
{

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    
    AddressBook ab;
    String tempfileName;
    
    @BeforeEach
    public void setUp()
    {
        ab = new AddressBook();
        tempfileName = "temp" + System.currentTimeMillis(); // Unreliable method for temp file name
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
        
        try {
            Path p = Paths.get(tempfileName);
            Files.deleteIfExists(p);
        }
        catch(InvalidPathException | IOException ex){
            // pass
        }
    }

    @Test
    @GradeValue(1)
    @Order(3)
    public void file1(){
        ab.loadFromFile("DataFile");
        assertEquals(8, ab.getNumberOfEntries());
        assertEquals(1, ab.search("helen").length );
        assertEquals(8, ab.search("08459").length );   
    }
    
    @Test
    @GradeValue(1)
    @Order(4)
    public void nosuchfile(){
        ab.loadFromFile("XXXXX");
        assertEquals(0, ab.getNumberOfEntries());
        file1();
    }
    
    @Test
    @GradeValue(1)
    @Order(6)
    public void listFile() throws IOException {
        ab.loadFromFile("DataFile");
        ContactDetails cd = new ContactDetails("hamish", "08765 222222", "123 Street Avenue");
        ab.addDetails(cd);
        ab.listToFile(tempfileName);
        AddressBook book2 = new AddressBook();
        book2.loadFromFile(tempfileName);
        assertEquals(9, book2.getNumberOfEntries());
        assertEquals(1, book2.search("helen").length );
        assertEquals(8, ab.search("08459").length );
        assertEquals(1, ab.search("hamish").length );
    }
    
    @Test
    @Order(5)
    @GradeValue(1)
    public void badDataFile() {
        assertThrows(BadDetailsException.class, 
            () -> ab.loadFromFile("DataBadFile") );
    }
    
    @Test
    @Order(1)
    @GradeValue(1)
    public void nullContactDetails() {
        assertThrows(NullPointerException.class, 
            () -> {
                ContactDetails cd = new ContactDetails(null, "08765 222222", "123 Street Avenue");
            } );
    }
    
    @Test
    @Order(2)
    @GradeValue(1)
    public void badContactDetails() {
        assertThrows(BadDetailsException.class, 
            () -> {
                ContactDetails cd = new ContactDetails("Fr^oud", "08765 222222", "123 Street Avenue");
            } );
    }
    
    @Test
    @GradeValue(1)
    @Order(7)
    public void listToFileBadIO() throws IOException {
        ab.loadFromFile("DataFile");
        ab.listToFile(tempfileName);
        ContactDetails cd = new ContactDetails("hamish", "08765 222222", "123 Street Avenue");
        ab.addDetails(cd);
        ab.listToFile(tempfileName);
        Path p = Paths.get(tempfileName);
        
        // make file unwriteable
        Files.setPosixFilePermissions(p,PosixFilePermissions.fromString("r--r--r--"));
        assertThrows(IOException.class, 
            () -> {
                ab.listToFile(tempfileName);
            } );
        
        Files.setPosixFilePermissions(p,PosixFilePermissions.fromString("rw-rw-rw-"));
    }
    
    @Test
    @GradeValue(1)
    @Order(8)
    public void doubleLoad() throws IOException {
        ab.loadFromFile("DataFile");
        AddressBook b2 = new AddressBook();
        ContactDetails cd = new ContactDetails("samuel", "08765 222222", "123 Street Avenue");
        b2.addDetails(cd);
        b2.listToFile(tempfileName);
        ab.loadFromFile(tempfileName);
        assertEquals(9, ab.search("0").length );
        assertEquals(1, ab.search("samuel").length );
    }
    
    @Test
    @GradeValue(1)
    @Order(9)
    public void listEmpty() throws IOException {
        ab.listToFile(tempfileName);
    }
    
    
    @Test
    @GradeValue(1)
    @Order(9)
    public void loadEmpty() throws IOException {
        ab.listToFile(tempfileName);
        AddressBook b2 = new AddressBook();
        b2.loadFromFile(tempfileName);
    }
}
