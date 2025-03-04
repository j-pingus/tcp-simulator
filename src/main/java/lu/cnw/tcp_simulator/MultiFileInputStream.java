package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class MultiFileInputStream extends InputStream {
    private final Iterator<File> fileIterator;
    private InputStream currentStream;

    public MultiFileInputStream(List<File> files) throws IOException {
        this.fileIterator = files.iterator();
        openNextFile();
    }

    @Override
    public int read() throws IOException {
        if (currentStream == null) {
            return -1; // No more files
        }

        int data = currentStream.read();
        if (data == -1) { // End of current file
            currentStream.close();
            openNextFile();
            return read(); // Recursive call to continue reading
        }
        return data;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (currentStream == null) {
            return -1;
        }

        int bytesRead = currentStream.read(b, off, len);
        if (bytesRead == -1) {
            currentStream.close();
            openNextFile();
            return read(b, off, len);
        }
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        if (currentStream != null) {
            currentStream.close();
        }
    }

    private void openNextFile() throws IOException {
        if (fileIterator.hasNext()) {
            var nextFile = fileIterator.next();
            if (nextFile.exists()) {
                currentStream = new FileInputStream(nextFile);
            }
        } else {
            currentStream = null; // No more files
        }
    }
}
