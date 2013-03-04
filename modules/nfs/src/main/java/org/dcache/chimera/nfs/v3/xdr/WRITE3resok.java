/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v3.xdr;

import java.io.IOException;

import org.dcache.xdr.OncRpcException;
import org.dcache.xdr.XdrAble;
import org.dcache.xdr.XdrDecodingStream;
import org.dcache.xdr.XdrEncodingStream;

public class WRITE3resok implements XdrAble {
    public wcc_data file_wcc;
    public count3 count;
    public int committed;
    public writeverf3 verf;

    public WRITE3resok() {
    }

    public WRITE3resok(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        file_wcc.xdrEncode(xdr);
        count.xdrEncode(xdr);
        xdr.xdrEncodeInt(committed);
        verf.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        file_wcc = new wcc_data(xdr);
        count = new count3(xdr);
        committed = xdr.xdrDecodeInt();
        verf = new writeverf3(xdr);
    }

}
// End of WRITE3resok.java
