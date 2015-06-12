/*
 * Copyright (C) 2015 Aeroshift Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aerospike.transcoder.jackson;

import java.io.IOException;

import javax.inject.Inject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.aerospike.transcoder.TranscodeException;
import com.aerospike.transcoder.Transcoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson based transcoder objects to and from json.
 *
 * @author ashish
 *
 */
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class JacksonTranscoder implements Transcoder {
    /**
     * The object mapper.
     */
    private final ObjectMapper mapper;

    /**
     * The classname.
     *
     * @author ashish
     */
    @RequiredArgsConstructor
    @Getter
    private static class ObjectWrapper {
        /**
         * The class name.
         */
        private final String fqcn;

        /**
         * The value.
         */
        private final byte[] value;

        /**
         *
         */
        @SuppressWarnings("unused")
        private ObjectWrapper() {
            this(null, null);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.aerospike.aeroshift.transcoder.Transcoder#encode(java.lang.Object)
     */
    @Override
    public byte[] encode(final Object value) throws TranscodeException {
        try {
            return mapper.writeValueAsBytes(new ObjectWrapper(value.getClass()
                    .getCanonicalName(), mapper.writeValueAsBytes(value)));
        } catch (final JsonProcessingException e) {
            throw new TranscodeException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.aeroshift.transcoder.Transcoder#decode(byte[])
     */
    @Override
    public Object decode(final byte[] encoded) throws TranscodeException {
        ObjectWrapper wrapped;
        try {
            wrapped = mapper.readValue(encoded, ObjectWrapper.class);
            String className = wrapped.fqcn;
            byte[] valueBytes = wrapped.value;
            Class<?> classType = null;

            classType = Class.forName(className);
            return mapper.readValue(valueBytes, classType);
        } catch (ClassNotFoundException | IOException e) {
            throw new TranscodeException("IOException", e);
        }

    }
}
