/*
 * Copyright 2008-2015 Aerospike, Inc.
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

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * Test class
 * 
 * @author akshay
 *
 */
@Setter
@ToString
@EqualsAndHashCode
public class Student implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String name;
    public String rollno;
    public String subject;

    public Student() {
        this.name = null;
        this.rollno = null;
        this.subject = null;
    }

}
