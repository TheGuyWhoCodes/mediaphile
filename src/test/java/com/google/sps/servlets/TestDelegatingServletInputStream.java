package com.google.sps.servlets;

import junit.framework.Assert;
import org.apache.commons.lang3.NotImplementedException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper function to help test null data
 */
public class TestDelegatingServletInputStream extends ServletInputStream {

    private final InputStream sourceStream;

    /**
     * Create a DelegatingServletInputStream for the given source stream.
     * @param sourceStream the source stream (never <code>null</code>)
     */
    public TestDelegatingServletInputStream(InputStream sourceStream) {
        Assert.assertNotNull("Source InputStream must not be null", sourceStream);
        this.sourceStream = sourceStream;
    }

    /**
     * Return the underlying source stream (never <code>null</code>).
     */
    public final InputStream getSourceStream() {
        return this.sourceStream;
    }


    public int read() throws IOException {
        return this.sourceStream.read();
    }

    public void close() throws IOException {
        super.close();
        this.sourceStream.close();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new NotImplementedException("Not needed");
    }
} 