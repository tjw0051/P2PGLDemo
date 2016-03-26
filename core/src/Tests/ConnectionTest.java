package Tests;

import P2PGL.DHT.KademliaFacade;
import P2PGL.Profile.*;
import com.badlogic.gdx.math.Vector3;
import com.tw.p2pgldemo.Networking.Connection;
import com.tw.p2pgldemo.Networking.PlayerState;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

/**
 * Created by t_j_w on 17/03/2016.
 */
public class ConnectionTest {

    static Connection connection;
    static P2PGL.Connection server;

    @Test
    public void testConnect() throws Exception {
        if(SetUpConnection() != 0)
            fail();
        Disconnect();
    }

    private int SetUpConnection() {
        try {
            Thread.sleep(1000L);
        } catch(java.lang.InterruptedException ie) {
            fail("Unable to sleep thread");
        }
        connection  = Connection.GetInstance();

        //Set up server
        IProfile profile = new Profile(InetAddress.getLoopbackAddress(), 4000, "server");
        server = new P2PGL.Connection(profile, new KademliaFacade());
        try {
            server.Connect();
        } catch(IOException ioe) {
            fail("Cannot set up server: \n" +
            ioe.getMessage());
            return -1;
        }

        if(connection.Connect("client") == -1) {
            fail("Failed to connect to server.");
            return -1;
        }
        return 0;
    }

    @Test
    public void testDisconnect() {
        SetUpConnection();
        if(Disconnect() != 0)
            fail();
    }

    private int Disconnect() {
        if(connection.Disconnect() != 0) {
            fail("Failed to disconnect client");
            return -1;
        }
        connection = null;
        try {
            server.Disconnect();
            server = null;
        } catch(IOException ioe) {
            fail("Failed to disconnect server");
            return -1;
        }
        return 0;
    }

    @Test
    public void testSaveState() throws Exception {
        if(saveState() != 0)
            fail();
        Disconnect();
    }

    private int saveState() {
        if(SetUpConnection() != 0)
            return -1;
        String world = "a1";
        Vector3 pos = new Vector3(1, 2, 3);
        Vector3 dest = new Vector3(4, 5, 6);
        if(connection.SaveState(world, pos, dest) != 0)
            return -1;
        return 0;
    }

    @Test
    public void testGetState() throws Exception {
        if(saveState() != 0)
            fail();
        PlayerState state = connection.GetState("client");
        if(state == null)
            fail("No player state found");
        if(!state.getWorld().equals("a1"))
            fail("playerState world name is incorrect");
        if(state.getPos().x != 1 || state.getPos().y != 2 || state.getPos().z != 3)
            fail("playerState position is incorrect");
        if(state.getDestination().x != 4 ||
                state.getDestination().y != 5 || state.getDestination().z != 6)
            fail("playerState destination is incorrect");
    }

    @Test
    public void testGetPlayerStates() throws Exception {
    }

    @Test
    public void testGetName() throws Exception {
        if(SetUpConnection() != 0)
            fail("Failed to set up connection");
        if(!connection.GetName().equals("client"))
            fail("Player name is incorrect");
        Disconnect();
    }
}