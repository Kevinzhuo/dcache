// generated by GLUE/wsdl2java on Thu Jan 13 10:36:54 CST 2005
package diskCacheV111.srm;

import java.io.Serializable;

import org.dcache.srm.v2_2.TFileLocality;
import org.dcache.srm.v2_2.TRetentionPolicyInfo;

public class FileMetaData implements Serializable
  {
      private static final long serialVersionUID = 8268193706970163884L;
      public String SURL =  RequestStatus.EMPTY;
  public long size;
  public String owner =  RequestStatus.EMPTY;
  public String group =  RequestStatus.EMPTY;
  public int permMode;
  public String checksumType =  RequestStatus.EMPTY;
  public String checksumValue =  RequestStatus.EMPTY;
  public boolean isPinned;
  public boolean isPermanent;
  public boolean isCached;
  public TRetentionPolicyInfo retentionPolicyInfo;
  public TFileLocality locality;

     public FileMetaData() {
    }

    /**
     * copy constructor
     */
    public FileMetaData(FileMetaData fmd) {
        if(fmd == null) {
            return;
        }

        if(fmd.SURL != null) {
            SURL =fmd.SURL;
        }
        size =fmd.size;

        if(fmd.owner != null) {
            owner =fmd.owner;
        }
        if(fmd.group != null) {
            group =fmd.group;
        }
        permMode=fmd.permMode;

        if(fmd.checksumType != null) {
            checksumType=fmd.checksumType;
        }
        if(fmd.checksumValue != null) {
            checksumValue=fmd.checksumValue;
        }

        isPinned =fmd.isPinned;
        isPermanent=fmd.isPermanent;
        isCached=fmd.isCached;

	if (fmd.retentionPolicyInfo!=null) {
	    retentionPolicyInfo=fmd.retentionPolicyInfo;
	}

        locality=fmd.locality;

    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileMetaData         SURL :").
        append(SURL).append('\n');
        sb.append("                     size :")
        .append(size).append('\n');
        sb.append("                     owner :")
        .append(owner).append('\n');
        sb.append("                     group :")
        .append(group).append('\n');
        sb.append("                     permMode :")
        .append(Integer.toString(permMode,8)).append('\n');
        sb.append("                     checksumType :")
        .append(checksumType).append('\n');
        sb.append("                     checksumValue :")
        .append(checksumValue).append('\n');
        sb.append("                     isPinned :")
        .append(isPinned).append('\n');
        sb.append("                     isPermanent :")
        .append(isPermanent).append('\n');
        sb.append("                     isCached :")
        .append(isCached).append('\n');
        return sb.toString();
    }

  }
