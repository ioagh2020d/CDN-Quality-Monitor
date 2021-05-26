package pl.edu.agh.cqm.data.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;

import java.lang.reflect.Type;

public class RTTSampleDTOSerializer implements JsonSerializer<RTTSampleDTO> {

    @Override
    public JsonElement serialize(final RTTSampleDTO sample, final Type type,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObj = new JsonObject();
        jsonObj.add("timestamp", context.serialize(sample.getTimestamp().toString()));
        jsonObj.add("average", context.serialize(sample.getAverage()));
        jsonObj.add("min", context.serialize(sample.getMin()));
        jsonObj.add("max", context.serialize(sample.getMax()));
        jsonObj.add("standardDeviation", context.serialize(sample.getStandardDeviation()));
        jsonObj.add("packetLoss", context.serialize(sample.getPacketLoss()));
        jsonObj.add("type", context.serialize(sample.getType()));
        return jsonObj;
    }
}
