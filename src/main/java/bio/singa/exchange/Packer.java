package bio.singa.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import bio.singa.exchange.trajectories.TrajectoryDataset;
import bio.singa.exchange.units.UnitJacksonModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author cl
 */
public class Packer {

    public static void main(String[] args) throws IOException {

        Path trajectoryPath = Paths.get("/home/leberech/git/model-data/raw_data/camp_sink_development/restricted/observations/2019-05-28T13-53-08Z/trajectory.json");
        
        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
        mapper.registerModule(new UnitJacksonModule());
        TrajectoryDataset trajectoryDataset = mapper.readValue(String.join("", Files.readAllLines(trajectoryPath)), TrajectoryDataset.class);


        byte[] bytes = mapper.writeValueAsBytes(trajectoryDataset);
        Files.write(Paths.get("/home/leberech/git/model-data/raw_data/camp_sink_development/restricted/observations/2019-05-28T13-53-08Z/trajectory.msg"), bytes);

    }

}
