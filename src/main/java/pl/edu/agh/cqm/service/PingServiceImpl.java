package pl.edu.agh.cqm.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.repository.RTTSampleRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Builder
public class PingServiceImpl implements PingService {

    private final RTTSampleRepository rttSampleRepository;
    private final CqmConfiguration cqmConfiguration;
    private final Logger logger = LogManager.getLogger(PingServiceImpl.class);

    @Override
    public void doMeasurement() {
        for (String domain : cqmConfiguration.getCdns()) {
            try {
                rttSampleRepository.save(ping(domain));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private RTTSample ping(String host) throws IOException {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getDecimalSeparator();

        String command = String.join(" ",
                "ping", "-c", cqmConfiguration.getActiveTestsIntensity() + "", "-i 0" + sep + "2", host);
        logger.info("Starting active sampling with command \"" + command + "\"");
        BufferedReader inputStream = runSystemCommand(command);

        // reading output stream of the command
        List<String> lines = inputStream.lines().collect(Collectors.toList());
        return RTTSample.builder()
                .id(0)
                .packetLoss(getValFromString(lines.get(lines.size() - 2), "(\\d+)% packet loss"))
                .min(getValFromString(lines.get(lines.size() - 1), " ((\\d+)(\\.)(\\d+))/"))
                .average(getValFromString(lines.get(lines.size() - 1), "/((\\d+)(\\.)(\\d+))/"))
                .max(getValFromString(lines.get(lines.size() - 1), "/((\\d+)(\\.)(\\d+))/((\\d+)(\\.)(\\d+)) ms"))
                .standardDeviation(getValFromString(lines.get(lines.size() - 1), "/((\\d+)(\\.)(\\d+)) ms"))
                .timestamp(Instant.now())
                .address(host)
                .build();
    }

    private static BufferedReader runSystemCommand(String command) throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        return new BufferedReader(
                new InputStreamReader(p.getInputStream()));
    }

    private static float getValFromString(String inputLine, String patternS) {
        Pattern pattern = Pattern.compile(patternS);
        Matcher m = pattern.matcher(inputLine);
        if (m.find()) {
            return Float.parseFloat((m.group(1)));
        }
        return -1;
    }
}