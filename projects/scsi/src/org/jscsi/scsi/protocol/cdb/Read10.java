
package org.jscsi.scsi.protocol.cdb;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jscsi.scsi.protocol.util.ByteBufferInputStream;

public class Read10 extends AbstractTransferCDB
{
   public static final int OPERATION_CODE = 0x28;

   private boolean DPO;
   private boolean FUA;
   private boolean FUA_NV;

   private int groupNumber;

   public Read10()
   {
      super(OPERATION_CODE);
   }

   protected Read10(int operationCode)
   {
      super(operationCode);
   }

   public Read10(
         int groupNumber,
         boolean dpo,
         boolean fua,
         boolean fua_nv,
         boolean linked,
         boolean normalACA,
         long logicalBlockAddress,
         long transferLength)
   {
      this(OPERATION_CODE, groupNumber, dpo, fua, fua_nv, linked, normalACA, logicalBlockAddress,
            transferLength);
   }

   protected Read10(
         int operationCode,
         int groupNumber,
         boolean dpo,
         boolean fua,
         boolean fua_nv,
         boolean linked,
         boolean normalACA,
         long logicalBlockAddress,
         long transferLength)
   {
      super(operationCode, linked, normalACA, logicalBlockAddress, transferLength);
      this.DPO = dpo;
      this.FUA = fua;
      this.FUA_NV = fua_nv;

      if (transferLength > 65536)
      {
         throw new IllegalArgumentException("Transfer length out of bounds for command type");
      }
      if (logicalBlockAddress > 4294967296L)
      {
         throw new IllegalArgumentException("Logical Block Address out of bounds for command type");
      }

      this.groupNumber = groupNumber;
   }

   public void decode(byte[] header, ByteBuffer input) throws IOException
   {
      DataInputStream in = new DataInputStream(new ByteBufferInputStream(input));

      int operationCode = in.readUnsignedByte();
      this.decodeByte1(in.readUnsignedByte());

      setLogicalBlockAddress(in.readInt());

      this.groupNumber = in.readUnsignedByte() & 0x1F;
      setTransferLength(in.readUnsignedShort());
      super.setControl(in.readUnsignedByte());

      if (operationCode != OPERATION_CODE)
      {
         throw new IOException("Invalid operation code: " + Integer.toHexString(operationCode));
      }
   }

   public byte[] encode()
   {
      ByteArrayOutputStream cdb = new ByteArrayOutputStream(this.size());
      DataOutputStream out = new DataOutputStream(cdb);

      try
      {
         out.writeByte(OPERATION_CODE);

         out.writeByte(this.encodeByte1());

         out.writeInt((int)getLogicalBlockAddress());
         out.writeByte(this.groupNumber & 0x1F);
         out.writeShort((int) getTransferLength());
         out.writeByte(super.getControl());

         return cdb.toByteArray();
      }
      catch (IOException e)
      {
         throw new RuntimeException("Unable to encode CDB.");
      }
   }

   protected void decodeByte1(int unsignedByte) throws IllegalArgumentException
   {
      if (((unsignedByte >>> 5) & 0x07) != 0)
      {
         throw new IllegalArgumentException("Read protection information is not supported");
      }

      this.DPO = ((unsignedByte >>> 4) & 0x01) == 1;
      this.FUA = ((unsignedByte >>> 3) & 0x01) == 1;
      this.FUA_NV = ((unsignedByte >>> 1) & 0x01) == 1;
   }

   protected int encodeByte1()
   {
      int b = 0;
      if (DPO)
      {
         b |= 0x10;
      }
      if (FUA)
      {
         b |= 0x08;
      }
      if (FUA_NV)
      {
         b |= 0x02;
      }
      return b;
   }

   public int size()
   {
      return 10;
   }

   public boolean isDPO()
   {
      return this.DPO;
   }

   public void setDPO(boolean dpo)
   {
      this.DPO = dpo;
   }

   public boolean isFUA()
   {
      return this.FUA;
   }

   public void setFUA(boolean fua)
   {
      this.FUA = fua;
   }

   public boolean isFUA_NV()
   {
      return this.FUA_NV;
   }

   public void setFUA_NV(boolean fua_nv)
   {
      this.FUA_NV = fua_nv;
   }

   public int getGroupNumber()
   {
      return this.groupNumber;
   }

   public void setGroupNumber(int groupNumber)
   {
      this.groupNumber = groupNumber;
   }
}
