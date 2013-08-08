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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.protocol.tcp.sampler.ReadException;
import org.apache.jmeter.protocol.tcp.sampler.TCPClient;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * A {@link TCPClient} which works with spring security's
 * <code>org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer</code>
 * . <br />
 * Reads data until the sequence \r\n is reached. or until the end of the stream
 * is reached.
 *
 * @author <a href='mailto:andrew.clemons@gmail.com'>Andrew Clemons</a>
 */
public class CrLfTcpClient implements TCPClient {

    private static final Logger LOG = LoggingManager.getLoggerForClass();

    private final String charset = JMeterUtils.getPropDefault("tcp.charset",
	    Charset.defaultCharset().name());

    /**
     */
    public CrLfTcpClient() {
	super();

	final String configuredCharset = JMeterUtils.getProperty("tcp.charset");

	if (StringUtils.isEmpty(configuredCharset)) {
	    LOG.info("Using platform default charset:" + this.charset);
	} else {
	    LOG.info("Using charset:" + configuredCharset);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final OutputStream os, final String s) throws IOException {
	os.write(s.getBytes(this.charset));

	os.write('\r');
	os.write('\n');

	os.flush();

	if (LOG.isDebugEnabled()) {
	    LOG.debug("Wrote: " + s);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final OutputStream os, final InputStream is)
	    throws IOException {
	byte buff[] = new byte[512];
	while (is.read(buff) > 0) {
	    os.write(buff);
	}

	os.write('\r');
	os.write('\n');

	os.flush();
    }

    /**
     * Reads data until the defined EOL byte is reached. If there is no EOL byte
     * defined, then reads until the end of the stream is reached.
     */
    @Override
    public String read(final InputStream is) throws ReadException {

	final ByteArrayOutputStream w = new ByteArrayOutputStream();

	try {

	    int count = 0;
	    int read = 0;
	    int last = -1;

	    while (true) {
		read = is.read();

		if (count > 0 && read == '\n' && last == '\r') {
		    break;
		}

		w.write(read);

		count++;

		last = read;

	    }

	    w.flush();

	    if (LOG.isDebugEnabled()) {
		LOG.debug("Read: " + w.size() + "\n" + w.toString());
	    }

	    // cut off last trailing \r
	    byte[] assembledData = new byte[count - 1];
	    System.arraycopy(w.toByteArray(), 0, assembledData, 0, count - 1);

	    return new String(assembledData, this.charset);
	} catch (final IOException e) {
	    throw new ReadException("", e, w.toString());
	}
    }

    @Override
    public String getCharset() {
	return this.charset;
    }

    @Override
    public byte getEolByte() {
	throw new UnsupportedOperationException(
		"Method not supported for CrLf terminated data.");
    }

    @Override
    public void setEolByte(int bite) {
	throw new UnsupportedOperationException(
		"Method not supported for CrLf terminated data.");
    }

    @Override
    public void setupTest() {
	// nothing to do
    }

    @Override
    public void teardownTest() {
	// nothing to do
    }
}