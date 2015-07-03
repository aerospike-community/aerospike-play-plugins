/*
 * Copyright (C) 2008-2015 Aerospike, Inc.
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

package com.aerospike.transcoder;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Transcodes Java objects values to and from bytes.
 *
 * @author ashish
 *
 */
public interface Transcoder {
    /**
     * Encode the object to bytes.
     *
     * @param value
     *            the value to encode.
     * @return the encoded value as bytes.
     * @throws IOException
     */
    byte[] encode(final Object value) throws TranscodeException, IOException;

    /**
     * Decode the object from bytes.
     *
     * @param encoded
     *            the encoded bytes.
     * @return the decoded bytes.
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws ClassNotFoundException
     */
    Object decode(final byte[] encoded) throws TranscodeException;
}
