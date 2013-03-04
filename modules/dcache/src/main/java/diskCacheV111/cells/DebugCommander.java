package diskCacheV111.cells ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diskCacheV111.util.PnfsId;
import diskCacheV111.vehicles.GenericStorageInfo;
import diskCacheV111.vehicles.PoolFetchFileMessage;
import diskCacheV111.vehicles.PoolManagerGetReadPoolMessage;
import diskCacheV111.vehicles.PoolManagerGetWritePoolMessage;
import diskCacheV111.vehicles.PoolManagerMessage;

import dmg.cells.nucleus.CellAdapter;
import dmg.cells.nucleus.CellMessage;
import dmg.cells.nucleus.CellNucleus;
import dmg.cells.nucleus.CellPath;
import dmg.util.Args;
import dmg.util.CommandSyntaxException;

import org.dcache.vehicles.FileAttributes;

public class DebugCommander extends CellAdapter {

    private final static Logger _log =
        LoggerFactory.getLogger(DebugCommander.class);

    private final CellNucleus _nucleus ;
    private final Args        _args ;

    public DebugCommander( String name , String  args ){
       super( name , args , true ) ;
       _nucleus = getNucleus() ;
       _args    = getArgs() ;

    }
    @Override
    public void messageArrived( CellMessage msg ){
       Object obj = msg.getMessageObject() ;
       _log.info( "DBC : From     : "+msg.getSourcePath() ) ;
       _log.info( "DBC : Class    : "+obj.getClass().getName() ) ;
       _log.info( "DBC : toString : "+obj.toString() ) ;

    }
    public static final String hh_send_fetch = "<hsm> <pool> <pnfsId> [-path=<poolPath>]" ;
    public String ac_send_fetch_$_3( Args args )throws Exception {
        CellPath path;
        String hsmName  = args.argv(0);
        String poolName = args.argv(1) ;
        String poolPath = args.getOpt("path") ;
        if( poolPath == null ){
           path = new CellPath( poolName ) ;
        }else{
           path = new CellPath( poolPath ) ;
        }
        String   pnfsid = PnfsId.toCompleteId( args.argv(1) ) ;

        FileAttributes fileAttributes = new FileAttributes();
        fileAttributes.setPnfsId(new PnfsId(pnfsid));
        fileAttributes.setStorageInfo(new GenericStorageInfo(hsmName, "any"));
        _nucleus.sendMessage(
           new CellMessage(
               path, new PoolFetchFileMessage( poolName , fileAttributes)));
        return "Stay tuned" ;
    }
    public static final String hh_send_getpool =
       "[-dest=<destCell>] read|write <storageGroup> <pnfsId>" ;
    public String ac_send_getpool_$_3( Args args )throws Exception{
        String dest = args.getOpt("dest") ;
        dest = dest == null ? "PoolManager" : dest ;

        String dir = args.argv(0) ;

        PoolManagerMessage pm;
        switch (dir) {
        case "read":
            pm = new PoolManagerGetReadPoolMessage(
                    args.argv(1),
                    args.argv(2));
            break;
        case "write":
            pm = new PoolManagerGetWritePoolMessage(
                    args.argv(1),
                    args.argv(2));
            break;
        default:
            throw new
                    CommandSyntaxException("read or write");
        }


        _nucleus.sendMessage( new CellMessage(
                                new CellPath( dest ) ,
                                pm )
                            );

        return "Done" ;

    }

}
