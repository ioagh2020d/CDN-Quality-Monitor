package pl.edu.agh.cqm.service.impl;

import kong.unirest.UnirestInstance;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.dto.SubmitSamplesDTO;
import pl.edu.agh.cqm.data.dto.SubmittedSampleWrapperDTO;
import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.Sample;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.service.CentralApiService;

import java.util.List;

@Service
@AllArgsConstructor
public class CentralApiServiceImpl implements CentralApiService {

    private CqmConfiguration cqmConfiguration;
    private UnirestInstance unirestInstance;
    private final Logger logger = LogManager.getLogger(CentralApiService.class);

    public void sendSample(RTTSample sample) {
        sendSample(sample, "rtt");
    }

    public void sendSample(ThroughputSample sample) {
        sendSample(sample, "throughput");
    }

    private void sendSample(Sample sample, String parameter) {
        if (!cqmConfiguration.getCentralServer().isBlank()) {
            try {
                unirestInstance.post(cqmConfiguration.getCentralServer() + "/api/remotes/" + parameter)
                    .header("Content-Type", "application/json")
                    .body(new SubmitSamplesDTO<>(List.of(
                        new SubmittedSampleWrapperDTO<>(
                            sample.toDTO(),
                            sample.getUrl().getCdn().getName(),
                            sample.getUrl().getAddress()
                        )
                    )))
                    .asJson();
            } catch (Exception exception) {
                logger.warn(exception.getMessage());
            }
        }
    }
}
