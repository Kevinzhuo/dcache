/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v3.xdr;

import java.io.IOException;

import org.dcache.chimera.nfs.nfsstat;
import org.dcache.xdr.OncRpcException;
import org.dcache.xdr.XdrAble;
import org.dcache.xdr.XdrDecodingStream;
import org.dcache.xdr.XdrEncodingStream;

public class FSSTAT3res implements XdrAble {
    public int status;
    public FSSTAT3resok resok;
    public FSSTAT3resfail resfail;

    public FSSTAT3res() {
    }

    public FSSTAT3res(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(status);
        switch ( status ) {
        case nfsstat.NFS_OK:
            resok.xdrEncode(xdr);
            break;
        default:
            resfail.xdrEncode(xdr);
            break;
        }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        status = xdr.xdrDecodeInt();
        switch ( status ) {
        case nfsstat.NFS_OK:
            resok = new FSSTAT3resok(xdr);
            break;
        default:
            resfail = new FSSTAT3resfail(xdr);
            break;
        }
    }

}
// End of FSSTAT3res.java
