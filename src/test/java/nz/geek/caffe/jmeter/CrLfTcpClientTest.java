/**
 * Copyright 2013 Andrew Clemons <andrew.clemons@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.geek.caffe.jmeter;

import java.io.ByteArrayInputStream;

import nz.geek.caffe.jmeter.CrLfTcpClient;

import org.apache.jmeter.protocol.tcp.sampler.TCPClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link TCPClient}.
 *
 * @author <a href='mailto:andrew.clemons@gmail.com'>Andrew Clemons</a>
 */
public class CrLfTcpClientTest {

    /**
     * setup.
     */
    @Before
    public void setup() {
	// JMeterUtils.setProperty("tcp.charset", "UTF-8");
    }

    /**
     * Tests an empty payload
     *
     * @throws Exception
     */
    @Test
    public void testEmpty() throws Exception {
	byte[] buffer = "\r\n".getBytes("UTF-8");

	final ByteArrayInputStream inputStream = new ByteArrayInputStream(
		buffer);

	final String value = new CrLfTcpClient().read(inputStream);

	Assert.assertEquals("", value);
    }

    /**
     * Test single character payload.
     *
     * @throws Exception
     */
    @Test
    public void testSingleChar() throws Exception {

	byte[] buffer = "1\r\n".getBytes("UTF-8");

	final ByteArrayInputStream inputStream = new ByteArrayInputStream(
		buffer);

	final String value = new CrLfTcpClient().read(inputStream);

	Assert.assertEquals("1", value);
    }

    /**
     * Test payload with a carriage return.
     *
     * @throws Exception
     */
    @Test
    public void testCarriageReturn() throws Exception {
	byte[] buffer = "1\r\r\n".getBytes("UTF-8");

	final ByteArrayInputStream inputStream = new ByteArrayInputStream(
		buffer);

	final String value = new CrLfTcpClient().read(inputStream);

	Assert.assertEquals("1\r", value);
    }

    /**
     * Test simple scenario with text.
     *
     * @throws Exception
     */
    @Test
    public void testSimple() throws Exception {

	byte[] buffer = "test\r\n".getBytes("UTF-8");

	final ByteArrayInputStream inputStream = new ByteArrayInputStream(
		buffer);

	final String value = new CrLfTcpClient().read(inputStream);

	Assert.assertEquals("test", value);
    }

    /**
     * Test when the payload starts with CR.
     *
     * @throws Exception
     */
    @Test
    public void testLeadingCr() throws Exception {

	byte[] buffer = "\rtest\r\n".getBytes("UTF-8");

	final ByteArrayInputStream inputStream = new ByteArrayInputStream(
		buffer);

	final String value = new CrLfTcpClient().read(inputStream);

	Assert.assertEquals("\rtest", value);
    }

    /**
     * Test when the payload contains an embedded Cr.
     *
     * @throws Exception
     */
    @Test
    public void testEmbeddedCRLF() throws Exception {

	byte[] buffer = "te\rst\r\n".getBytes("UTF-8");

	final ByteArrayInputStream inputStream = new ByteArrayInputStream(
		buffer);

	final String value = new CrLfTcpClient().read(inputStream);

	Assert.assertEquals("te\rst", value);
    }
}
