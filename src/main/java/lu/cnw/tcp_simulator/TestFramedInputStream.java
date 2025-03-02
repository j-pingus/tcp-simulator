package lu.cnw.tcp_simulator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TestFramedInputStream {
    public static void main(String[] args) throws IOException {
        var frames = FrameUtils.loadFrames(new File("CIW-Session2/index.log"));
        var inputStream = new FramedInputStream(
                new MultiFileInputStream(
                        frames.stream()
                                .filter(f->f.event==Event.FRAME)
                                .map(f->new File(f.filename)).toList()
                )
        );
        String frame ;
        ObjectMapper mapper = new ObjectMapper();
        while((frame=inputStream.readFrame())!=null){
            //Reading json as tree, just to confirm it is well formed
            var tree = mapper.readTree(frame);
            System.out.println(tree);
        }
    }
}
