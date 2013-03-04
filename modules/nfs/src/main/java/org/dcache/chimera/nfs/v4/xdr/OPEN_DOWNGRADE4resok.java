/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;

import java.io.IOException;

import org.dcache.xdr.OncRpcException;
import org.dcache.xdr.XdrAble;
import org.dcache.xdr.XdrDecodingStream;
import org.dcache.xdr.XdrEncodingStream;

public class OPEN_DOWNGRADE4resok implements XdrAble {
    public stateid4 open_stateid;

    public OPEN_DOWNGRADE4resok() {
    }

    public OPEN_DOWNGRADE4resok(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        open_stateid.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        open_stateid = new stateid4(xdr);
    }

}
// End of OPEN_DOWNGRADE4resok.java
