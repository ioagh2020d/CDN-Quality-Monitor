package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.CdnWithUrlsDTO;
import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;
import pl.edu.agh.cqm.data.model.Url;

import java.time.Instant;
import java.util.List;

public interface ParameterService {

    void updateCdnsWithUrls(List<CdnWithUrlsDTO> cdns);

    void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate);

    List<String> getActiveUrlAddresses();  // TODO: delete (temporary method)

    List<Url> getActiveUrls();
    List<Url> getActiveUrls(String cdn);
    List<CdnWithUrlsDTO> getActiveCdnsWithUrls();

    int getActiveSamplingRate();
    int getActiveTestIntensity();
    int getPassiveSamplingRate();

    List<ConfigSampleDTO> getParameterHistory(Instant startDate, Instant endDate);
}
