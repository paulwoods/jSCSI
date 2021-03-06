/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Konstanz nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jscsi.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jscsi.core.utils.WiresharkMessageParser;
import org.jscsi.parser.AdditionalHeaderSegment.AdditionalHeaderSegmentType;
import org.jscsi.parser.exception.InternetSCSIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing the correctness of the AdditionalHeaderSegment.
 * 
 * @author Volker Wildi
 */
public class AdditionalHeaderSegmentTest extends ProtocolDataUnitTest {

    // --------------------------------------------------------------------------
    // --------------------------------------------------------------------------

    /** Specific field of the TEST_CASE_1. */
    private static final String TEST_CASE_1_SPECIFIC_FIELD = "00 CA 25 26 C4";

    /**
     * Valid Test Case with the following expected values. <blockquote>
     * AHSLength = <code>0x0005</code><br/>
     * AHSType =
     * <code>AdditionalHeaderSegmentType.EXPECTED_BIDIRECTIONAL_READ_DATA_LENGTH</code>
     * <br/>
     * Expected Read-Data Length = <code>0xCA2526C4</code><br/>
     * </blockquote>
     */
    private static final String TEST_CASE_1 = "00 05 02" + " "
            + TEST_CASE_1_SPECIFIC_FIELD;

    private AdditionalHeaderSegment additionalHeaderSegment;

    // --------------------------------------------------------------------------
    // --------------------------------------------------------------------------

    @Before
    public final void setUp() {

        additionalHeaderSegment = new AdditionalHeaderSegment();
    }

    @After
    public final void tearDown() {

        additionalHeaderSegment = null;
    }

    // --------------------------------------------------------------------------
    // --------------------------------------------------------------------------

    /**
     * This test case validates the parsing process.
     * 
     * @throws IOException
     *             This exception should be never thrown.
     * @throws InternetSCSIException
     *             This exception should be never thrown.
     */
    @Test
    public final void testDeserialize1() throws InternetSCSIException {

        additionalHeaderSegment.deserialize(
                WiresharkMessageParser.parseToByteBuffer(TEST_CASE_1), 0);

        ByteBuffer expectedReadDataLength = WiresharkMessageParser
                .parseToByteBuffer(TEST_CASE_1_SPECIFIC_FIELD);

        assertEquals((short) 0x0005, additionalHeaderSegment.getLength());
        assertEquals(
                AdditionalHeaderSegmentType.EXPECTED_BIDIRECTIONAL_READ_DATA_LENGTH,
                additionalHeaderSegment.getType());
        assertTrue(expectedReadDataLength.equals(additionalHeaderSegment
                .getSpecificField()));

    }

    /**
     * This test case validates the serialization process.
     * 
     * @throws InternetSCSIException
     * @throws InternetSCSIException
     *             This exception should be never thrown.
     * @throws IOException
     *             This exception should be never thrown.
     */
    @Test
    public final void testSerialize1() throws InternetSCSIException {

        ByteBuffer expectedResult = WiresharkMessageParser
                .parseToByteBuffer(TEST_CASE_1);
        additionalHeaderSegment.deserialize(expectedResult, 0);
        ByteBuffer testSerialize = ByteBuffer.allocate(8);
        assertEquals(8, additionalHeaderSegment.serialize(testSerialize, 0));
        assertTrue(expectedResult.equals(testSerialize));
    }

    // --------------------------------------------------------------------------
    // --------------------------------------------------------------------------
    // --------------------------------------------------------------------------
    // --------------------------------------------------------------------------

}
