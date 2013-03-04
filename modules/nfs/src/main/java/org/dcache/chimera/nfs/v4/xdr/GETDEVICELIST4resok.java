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

public class GETDEVICELIST4resok implements XdrAble {
    public nfs_cookie4 gdlr_cookie;
    public verifier4 gdlr_cookieverf;
    public deviceid4 [] gdlr_deviceid_list;
    public boolean gdlr_eof;

    public GETDEVICELIST4resok() {
    }

    public GETDEVICELIST4resok(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        gdlr_cookie.xdrEncode(xdr);
        gdlr_cookieverf.xdrEncode(xdr);
        { int $size = gdlr_deviceid_list.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { gdlr_deviceid_list[$idx].xdrEncode(xdr); } }
        xdr.xdrEncodeBoolean(gdlr_eof);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        gdlr_cookie = new nfs_cookie4(xdr);
        gdlr_cookieverf = new verifier4(xdr);
        { int $size = xdr.xdrDecodeInt(); gdlr_deviceid_list = new deviceid4[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { gdlr_deviceid_list[$idx] = new deviceid4(xdr); } }
        gdlr_eof = xdr.xdrDecodeBoolean();
    }

}
// End of GETDEVICELIST4resok.java
