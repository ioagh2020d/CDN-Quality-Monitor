package pl.edu.agh.cqm.data.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

import java.lang.reflect.Type;

public class ThroughputSampleDTOSerializer implements JsonSerializer<ThroughputSampleDTO> {

    @Override
    public JsonElement serialize(final ThroughputSampleDTO sample, final Type type,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObj = new JsonObject();
        jsonObj.add("timestamp", context.serialize(sample.getTimestamp().toString()));
        jsonObj.add("throughput", context.serialize(sample.getThroughput()));
        return jsonObj;
    }
}
