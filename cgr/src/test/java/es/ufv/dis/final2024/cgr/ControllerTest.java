package es.ufv.dis.final2024.cgr;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.UUID;

public class ControllerTest {

    @InjectMocks
    private Controller controller;

    @Mock
    private DataHandling dataHandling;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateUserSuccess() {
        DataEntry newUser = new DataEntry(UUID.randomUUID(), "MS001", "2021", "EST001", 100.0, 1.5, 98.5, 101.5, "A");
        ArrayList<DataEntry> userList = new ArrayList<>();
        userList.add(newUser);

        when(dataHandling.addUser(any(DataEntry.class))).thenReturn(userList);

        ResponseEntity<DataEntry> response = controller.create(newUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newUser.getMscode(), response.getBody().getMscode());
    }

    @Test
    public void testCreateUserConflict() {
        DataEntry newUser = new DataEntry(UUID.randomUUID(), "MS001", "2021", "EST001", 100.0, 1.5, 98.5, 101.5, "A");

        when(dataHandling.addUser(any(DataEntry.class))).thenReturn(null);  // Simula un conflicto

        ResponseEntity<DataEntry> response = controller.create(newUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }
}

