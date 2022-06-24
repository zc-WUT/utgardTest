package tcb;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.jinterop.dcom.core.*;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import java.io.IOException;
import java.util.logging.Level;

public class JIntegra  {

    private static final String WMI_DEFAULT_NAMESPACE = "ROOT\\CIMV2";

    private static JISession configAndConnectDCom(String domain, String user, String pass ) throws Exception
    {
        JISystem.getLogger().setLevel( Level.OFF );
        try
        {
            JISystem.setInBuiltLogHandler( false );
        }
        catch ( IOException ignored )
        {
        }

        JISystem.setAutoRegisteration( true );
        JISession dcomSession = JISession.createSession( domain, user, pass );
        dcomSession.useSessionSecurity( true );
        return dcomSession;
    }


    private static IJIDispatch getWmiLocator(String host, JISession dcomSession ) throws Exception
    {

        JIComServer wbemLocatorComObj = new JIComServer(JIProgId.valueOf("WbemScripting.SWbemLocator"), host, dcomSession);
        return (IJIDispatch) JIObjectFactory.narrowObject( wbemLocatorComObj.createInstance().queryInterface( IJIDispatch.IID ) );
    }


    private static IJIDispatch toIDispatch( JIVariant comObjectAsVariant ) throws JIException
    {
        return (IJIDispatch) JIObjectFactory.narrowObject( comObjectAsVariant.getObjectAsComObject() );
    }


    public static void main( String[] args )
    {

        String domain = "syntbots.com";

        //     String host = "syncgdc3244";
        String host = "10.128.75.231";
        String user = "OPCServer";
        String pass = "root";

        JISession dcomSession = null;


        try
        {
            dcomSession = configAndConnectDCom( domain, user, pass );
            IJIDispatch wbemLocator = getWmiLocator( host, dcomSession );

            JIVariant results[] =
                    wbemLocator.callMethodA( "ConnectServer", new Object[] { new JIString( host ), new JIString( WMI_DEFAULT_NAMESPACE ),
                            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(), new Integer( 0 ),
                            JIVariant.OPTIONAL_PARAM() } );


            IJIDispatch wbemServices = toIDispatch( results[ 0 ] );

            final String QUERY_FOR_ALL_LOG_EVENTS = "SELECT * FROM Win32_OperatingSystem";
            final int RETURN_IMMEDIATE = 16;
            final int FORWARD_ONLY = 32;

            JIVariant[] eventSourceSet =
                    wbemServices.callMethodA( "ExecNotificationQuery", new Object[] { new JIString( QUERY_FOR_ALL_LOG_EVENTS ), new JIString( "WQL" ),
                            new JIVariant( new Integer( RETURN_IMMEDIATE + FORWARD_ONLY ) ) } );
            IJIDispatch wbemEventSource = (IJIDispatch) JIObjectFactory.narrowObject( ( eventSourceSet[ 0 ] ).getObjectAsComObject() );


            while ( true )
            {

                JIVariant eventAsVariant = (JIVariant) ( wbemEventSource.callMethodA( "NextEvent", new Object[] { JIVariant.OPTIONAL_PARAM() } ) )[ 0 ];
                IJIDispatch wbemEvent = toIDispatch( eventAsVariant );



                JIVariant objTextAsVariant = (JIVariant) ( wbemEvent.callMethodA( "GetObjectText_", new Object[] { new Integer( 1 ) } ) )[ 0 ];
                String asText = objTextAsVariant.getObjectAsString().getString();
                System.out.println( asText );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( null != dcomSession )
            {
                try
                {
                    JISession.destroySession( dcomSession );
                }
                catch ( Exception ex )
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}
