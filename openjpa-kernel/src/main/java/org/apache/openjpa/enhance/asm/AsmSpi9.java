/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.enhance.asm;

import org.apache.xbean.asm9.ClassReader;
import org.apache.xbean.asm9.ClassVisitor;
import org.apache.xbean.asm9.ClassWriter;
import org.apache.xbean.asm9.Opcodes;
import serp.bytecode.BCClass;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import static java.util.Arrays.asList;

public class AsmSpi9 implements AsmSpi {
    private static final int Java7_MajorVersion = 51;

    @SuppressWarnings("deprecation")
    @Override
    public void write(BCClass bc) throws IOException {
        if (bc.getMajorVersion() < Java7_MajorVersion) {
            bc.write();
        } else {
            String name = bc.getName();
            int dotIndex = name.lastIndexOf('.') + 1;
            name = name.substring(dotIndex);
            Class<?> type = bc.getType();

            OutputStream out = new FileOutputStream(
                    URLDecoder.decode(type.getResource(name + ".class").getFile()));
            try {
                writeJava7(bc, out);
            } finally {
                out.flush();
                out.close();
            }
        }
    }

    @Override
    public void write(BCClass bc, File outFile) throws IOException {
        if (bc.getMajorVersion() < Java7_MajorVersion) {
            bc.write(outFile);
        } else {
            OutputStream out = new FileOutputStream(outFile);
            try {
                writeJava7(bc, out);
            } finally {
                out.flush();
                out.close();
            }
        }
    }

    @Override
    public void write(BCClass bc, OutputStream os) throws IOException {
        if (bc.getMajorVersion() < Java7_MajorVersion) {
            bc.write(os);
        }
        else {
            try {
                writeJava7(bc, os);
            } finally {
                os.flush();
                os.close();
            }
        }
    }

    @Override
    public byte[] toByteArray(BCClass bc, byte[] returnBytes) throws IOException {
        if (bc.getMajorVersion() >= Java7_MajorVersion) {
            returnBytes = toJava7ByteArray(bc, returnBytes);
        }
        return returnBytes;
    }

    private void writeJava7(BCClass bc, OutputStream out) throws IOException {
        byte[] java7Bytes = toJava7ByteArray(bc, bc.toByteArray());
        out.write(java7Bytes);
    }

    private byte[] toJava7ByteArray(BCClass bc, byte[] classBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(classBytes);
        BufferedInputStream bis = new BufferedInputStream(bais);

        ClassWriter cw = new BCClassWriter(ClassWriter.COMPUTE_FRAMES, bc.getClassLoader());
        ClassReader cr = new ClassReader(bis);
        cr.accept(cw, 0);
        return cw.toByteArray();
    }

    public boolean isEnhanced(final byte[] b)
    {
        if (b == null)
        {
            return false;
        }
        final ClassReader cr = new ClassReader(b);
        try
        {
            cr.accept(new ClassVisitor(Opcodes.ASM8)
            {
                @Override
                public void visit(final int i, final int i1,
                                  final String name, final String s,
                                  final String parent, final String[] interfaces)
                {
                    boolean enhanced = interfaces != null && interfaces.length > 0 &&
                            asList(interfaces).contains("org/apache/openjpa/enhance/PersistenceCapable");
                    if (!enhanced && name != null && parent != null &&
                            !"java/lang/Object".equals(parent) && !name.equals(parent)) {
                        enhanced = isEnhanced(bytes(parent));
                    }
                    throw new EnhancedStatusException(enhanced);
                }
            }, 0);
            return false;
        } catch (final EnhancedStatusException e) {
            return e.status;
        } catch (final Exception e) {
            return false;
        }
    }

    private byte[] bytes(final String type)
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        final InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(type + ".class");
        if (stream == null) {
            return null;
        }
        try {
            int c;
            byte[] buffer = new byte[1024];
            while ((c = stream.read(buffer)) >= 0) {
                baos.write(buffer, 0, c);
            }
        } catch (IOException e) {
            return null;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // no-op
            }
        }
        return baos.toByteArray();
    }

    private static class EnhancedStatusException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        private final boolean status;

        private EnhancedStatusException(final boolean status) {
            this.status = status;
        }
    }
}
