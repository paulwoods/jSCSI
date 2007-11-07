
package org.jscsi.scsi.transport;

import java.nio.ByteBuffer;

import org.jscsi.scsi.target.Target;
import org.jscsi.scsi.tasks.Status;

/**
 * The SCSI Target Transport Port provides an interface to the Service Delivery Subsystem services
 * required by SCSI Target Devices.
 * <p>
 * The target port is responsible for enqueuing incoming commands onto the Task Router of the
 * appropriate SCSI target. As the Task Router does not distinguish between command and data
 * transfer services the transport port is responsible for writing all expected incoming data to a
 * byte buffer and enqueuing that buffer along with the incoming command. The transport port must
 * then process returning status, sense data, and input data buffers which are sent directly from
 * Logical Units.
 */
public interface TargetTransportPort
{

   /**
    * Registers a SCSI Target. The target name is used by the transport services to advertise the
    * target. Incoming commands are enqueued onto the target's Task Router.
    * 
    * @param target
    *           The target to register.
    */
   void registerTarget(Target target);

   /**
    * Removes a SCSI Target. After removal no further commands will be sent to the target.
    * 
    * @param targetName
    *           The name of the target to remove.
    * @throws Exception
    *            If a target with the given name has not be registered.
    */
   void removeTarget(String targetName) throws Exception;

   /**
    * Performs Receive Data-Out operation. If successful all expected data will have been read to
    * the output buffer. If failed a partial transfer may have occurred.
    * <p>
    * This method is called by task implementations for commands such as WRITE, MODE SENSE, and
    * REPORT LUNS.
    * 
    * @param nexus Generally either an I_T_L nexus or an I_T_L_Q nexus
    * @param commandReferenceNumber The command reference number associated with the nexus.
    * @param output The data output buffer which data will be written to.
    * @return True if all expected data has been written; False if no data or partial data
    *    has been written.
    */
   boolean readData(Nexus nexus, long commandReferenceNumber, ByteBuffer output)
         throws InterruptedException;

   /**
    * Performs a Send Data-In operation. If successful all expected data will have been written
    * from the input buffer. If failed a partial transfer may have occurred.
    * <p>
    * This method is called by task implementations for commands such as READ and MODE SELECT.
    * 
    * @param nexus Generally either an I_T_L nexus or an I_T_L_Q nexus.
    * @param commandReferenceNumber The command reference number associated with the nexus.
    * @param input The data input buffer which data will be read from.
    * @return True if all expected data has been written; False if no data or partial data has
    *    been written.
    */
   boolean writeData(Nexus nexus, long commandReferenceNumber, ByteBuffer input)
         throws InterruptedException;

   /**
    * Instructs the transport layer to terminate data transfer for the indicated nexus. The
    * transport layer shall throw an {@link InterruptedException} from any in-progress
    * {@link #writeData(Nexus, long, ByteBuffer)} or {@link #readData(Nexus, long, ByteBuffer)}
    * operation.
    * <p>
    * Interrupting a thread performing a read or write operation shall have the same effect.
    * <p>
    * This method does nothing if there are no in-progress data transfers for the indicated nexus.
    * 
    * @param nexus Generally either an I_T_L nexus or an I_T_L_Q nexus.
    */
   void terminateDataTransfer(Nexus nexus, long commandReferenceNumber);

   /**
    * Enqueues return data to send to the initiator indicated by the given Nexus. Used by both Task
    * Routers and Logical Units, depending on the original command.
    * 
    * @param nexus The nexus of the original SCSI request.
    * @param commandReferenceNumber The command reference number associated with the nexus.
    * @param status The final status of the command.
    * @param senseData Autosense data; <code>null</code> if the status is not 
    *    {@link Status#CHECK_CONDITION}.
    */
   void writeResponse(Nexus nexus, long commandReferenceNumber, Status status, ByteBuffer senseData);
}
