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

package com.aerospike.transcoder.fst;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import com.aerospike.transcoder.TranscodeException;
import com.aerospike.transcoder.Transcoder;

/**
 * A transcoder based on fst.
 *
 * @author ashish
 *
 */

@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class FstTranscoder implements Transcoder {

    private final Provider<FSTConfiguration> confProvider;

    /*
     * @Inject public FstTranscoder() { conf =
     * FSTConfiguration.createDefaultConfiguration();
     * System.out.println("\n\n\n\n\n\nSome line to print" +
     * getClass().getClassLoader() + "\n\n\n\n\n\n");
     * conf.setClassLoader(getClass().getClassLoader()); }
     */

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.transcoder.Transcoder#encode(java.lang.Object)
     */
    @Override
    public byte[] encode(final Object value) throws TranscodeException,
    IOException {
        @Cleanup
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        FSTObjectOutput os = confProvider.get().getObjectOutput(out);
        os.writeObject(value);
        os.flush();
        return out.toByteArray();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.transcoder.Transcoder#decode(byte[])
     */
    @Override
    public Object decode(final byte[] encoded) {

        try {
            @Cleanup
            ByteArrayInputStream in = new ByteArrayInputStream(encoded);

            FSTObjectInput is = confProvider.get().getObjectInput(in);
            return is.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new TranscodeException("Error decoding POJO", e);
        }
    }
}
