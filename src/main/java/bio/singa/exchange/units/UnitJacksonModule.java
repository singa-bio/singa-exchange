/*
 *  Units of Measurement Jackson Library for JSON support
 *  Copyright (c) 2012-2018, Werner Keil and others
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-363, Units of Measurement nor the names of their contributors may be used to endorse or promote products
 *    derived getEntityDatasetFrom this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package bio.singa.exchange.units;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import systems.uom.ucum.format.UCUMFormat;

import javax.measure.Unit;
import java.io.IOException;

/**
 * Configures Jackson to (de)serialize JSR 363 Unit objects using their ascii representation.
 */
public class UnitJacksonModule extends SimpleModule {

    public UnitJacksonModule() {
        super("UnitJsonSerializationModule", new Version(1, 3, 3, null,
                UnitJacksonModule.class.getPackage().getName(), "uom-lib-jackson"));
        addSerializer(Unit.class, new UnitJsonSerializer());
        addDeserializer(Unit.class, new UnitJsonDeserializer());
    }

    private static class UnitJsonSerializer extends StdScalarSerializer<Unit> {

        UnitJsonSerializer() {
            super(Unit.class);
        }

        @Override
        public void serialize(Unit unit, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (unit == null) {
                jgen.writeNull();
            } else {
                String unitString = UCUMFormat.getInstance(UCUMFormat.Variant.CASE_SENSITIVE).format(unit, new StringBuilder()).toString();
                jgen.writeString(unitString);
            }
        }
    }

    private static class UnitJsonDeserializer extends StdScalarDeserializer<Unit> {

        UnitJsonDeserializer() {
            super(Unit.class);
        }

        @Override
        public Unit deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();
            if (currentToken == JsonToken.VALUE_STRING) {
                return UCUMFormat.getInstance(UCUMFormat.Variant.CASE_SENSITIVE).parse(jsonParser.getText());
            }
            throw new JsonMappingException(jsonParser, "Expected uom unit value as String value");
        }
    }

}


