package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.ThroughputSample;

public interface CentralApiService {

    void sendSample(RTTSample rttSample);

    void sendSample(ThroughputSample throughputSample);
}
